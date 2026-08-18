package com.digimon.dtskrB.digimon.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.digimon.dtskrB.digimon.dto.DigimonDto;
import com.digimon.dtskrB.digimon.dto.DigimonDetailDto;
import com.digimon.dtskrB.digimon.dto.EvolutionCandidateDto;
import com.digimon.dtskrB.digimon.dto.EvolutionEdgeDto;
import com.digimon.dtskrB.digimon.dto.EvolutionRoutesDto;
import com.digimon.dtskrB.digimon.dto.EvolutionTreeNodeDto;
import com.digimon.dtskrB.digimon.repository.DigimonQueryRepository;
import com.digimon.dtskrB.digimon.repository.CatalogLanguage;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class DigimonService {

    private final DigimonQueryRepository digimonQueryRepository;
    private final Map<CatalogLanguage, List<EvolutionEdgeDto>> evolutionEdgesByLanguage = new ConcurrentHashMap<>();

    public DigimonService(DigimonQueryRepository digimonQueryRepository) {
        this.digimonQueryRepository = digimonQueryRepository;
    }

    public Map<String, Long> getCounts(CatalogLanguage language) {
        return digimonQueryRepository.getCountsByStage(language);
    }

    public long getEvolutionCount(CatalogLanguage language) {
        return digimonQueryRepository.getEvolutionCount(language);
    }

    public List<DigimonDto> getAll(CatalogLanguage language) {
        return digimonQueryRepository.getAll(language);
    }

    public List<DigimonDto> getByStageCode(String stageCode, CatalogLanguage language) {
        return digimonQueryRepository.getByStageCode(stageCode, language);
    }

    public List<DigimonDto> searchByName(String query, CatalogLanguage language) {
        return digimonQueryRepository.searchByName(query, language);
    }

    public DigimonDetailDto getDetail(int id, CatalogLanguage language) {
        DigimonDetailDto detail = digimonQueryRepository.findDetailById(id, language)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Digimon not found"));
        return new DigimonDetailDto(
                detail.id(), detail.name(), detail.stage(), detail.attribute(), detail.digimonType(),
                detail.personality(), detail.imageUrl(), detail.pixelImageUrl(), detail.gameCaptureUrl(),
                detail.skillImageUrl(), detail.description(), detail.evolutionCondition(), detail.sourceUrl(),
                digimonQueryRepository.findSpecialSkills(id, language),
                digimonQueryRepository.findAttachmentSkills(id, language)
        );
    }

    public EvolutionRoutesDto getEvolutionRoutes(int digimonId, CatalogLanguage language) {
        DigimonDto current = digimonQueryRepository.findById(digimonId, language)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Digimon not found"));
        List<EvolutionEdgeDto> edges = evolutionEdgesByLanguage.computeIfAbsent(
                language, digimonQueryRepository::findAllEvolutionEdges);
        Map<Integer, List<EvolutionEdgeDto>> nextEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.fromDigimon().id()));
        Map<Integer, List<EvolutionEdgeDto>> previousEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.toDigimon().id()));

        List<EvolutionTreeNodeDto> previous = buildPreviousEvolutionTree(
                digimonId, Set.of(digimonId), previousEdges
        );

        List<EvolutionTreeNodeDto> next = buildNextEvolutionTree(
                digimonId, Set.of(digimonId), nextEdges
        );

        return new EvolutionRoutesDto(toEvolutionTreeNode(current, null, List.of()), previous, next);
    }

    private List<EvolutionTreeNodeDto> buildNextEvolutionTree(
            int digimonId,
            Set<Integer> ancestorIds,
            Map<Integer, List<EvolutionEdgeDto>> edgesBySource
    ) {
        return edgesBySource.getOrDefault(digimonId, List.of()).stream()
                .map(edge -> {
                    DigimonDto digimon = edge.toDigimon();
                    EvolutionCandidateDto candidate = toNextCandidate(digimon, edge);
                    Set<Integer> nextAncestors = new java.util.HashSet<>(ancestorIds);
                    nextAncestors.add(digimon.id());
                    List<EvolutionTreeNodeDto> children = ancestorIds.contains(digimon.id())
                            ? List.of()
                            : buildNextEvolutionTree(digimon.id(), nextAncestors, edgesBySource);

                    return toEvolutionTreeNode(digimon, candidate, children);
                })
                .toList();
    }

    private List<EvolutionTreeNodeDto> buildPreviousEvolutionTree(
            int digimonId,
            Set<Integer> descendantIds,
            Map<Integer, List<EvolutionEdgeDto>> edgesByDestination
    ) {
        return edgesByDestination.getOrDefault(digimonId, List.of()).stream()
                .map(edge -> {
                    DigimonDto digimon = edge.fromDigimon();
                    EvolutionCandidateDto candidate = toPreviousCandidate(digimon, edge);
                    Set<Integer> nextDescendants = new java.util.HashSet<>(descendantIds);
                    nextDescendants.add(digimon.id());
                    List<EvolutionTreeNodeDto> children = descendantIds.contains(digimon.id())
                            ? List.of()
                            : buildPreviousEvolutionTree(
                                    digimon.id(), nextDescendants, edgesByDestination
                            );

                    return toEvolutionTreeNode(digimon, candidate, children);
                })
                .toList();
    }

    private EvolutionCandidateDto toNextCandidate(DigimonDto digimon, EvolutionEdgeDto edge) {
        return new EvolutionCandidateDto(
                digimon,
                edge.toDigimonEvolutionCondition(),
                edge.requiredAgentRank(),
                edge.requiredItemId(),
                edge.requiredItemName()
        );
    }

    private EvolutionCandidateDto toPreviousCandidate(DigimonDto digimon, EvolutionEdgeDto edge) {
        return new EvolutionCandidateDto(
                digimon,
                edge.fromDigimonEvolutionCondition(),
                edge.requiredAgentRank(),
                edge.requiredItemId(),
                edge.requiredItemName()
        );
    }

    private EvolutionTreeNodeDto toEvolutionTreeNode(
            DigimonDto digimon,
            EvolutionCandidateDto candidate,
            List<EvolutionTreeNodeDto> children
    ) {
        return new EvolutionTreeNodeDto(
                digimon.id(),
                digimon.name(),
                digimon.stage(),
                digimon.attribute(),
                digimon.digimonType(),
                digimon.personality(),
                digimon.imageUrl(),
                digimon.pixelImageUrl(),
                candidate == null ? null : candidate.conditionText(),
                candidate == null ? null : candidate.requiredAgentRank(),
                candidate == null ? null : candidate.requiredItemId(),
                candidate == null ? null : candidate.requiredItemName(),
                children
        );
    }
}
