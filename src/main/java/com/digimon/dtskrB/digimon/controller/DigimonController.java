package com.digimon.dtskrB.digimon.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digimon.dtskrB.digimon.dto.DigimonDto;
import com.digimon.dtskrB.digimon.dto.DigimonDetailDto;
import com.digimon.dtskrB.digimon.dto.EvolutionRoutesDto;
import com.digimon.dtskrB.digimon.service.DigimonService;
import com.digimon.dtskrB.digimon.repository.CatalogLanguage;

@RestController
@RequestMapping("/api/digimons")
public class DigimonController {

    private final DigimonService digimonService;

    public DigimonController(DigimonService digimonService) {
        this.digimonService = digimonService;
    }

    @GetMapping("/counts")
    public Map<String, Long> getCounts(@RequestHeader(name = "Accept-Language", defaultValue = "ko") String language) {
        return digimonService.getCounts(CatalogLanguage.fromHeader(language));
    }

    @GetMapping("/evolution-count")
    public Map<String, Long> getEvolutionCount(
            @RequestHeader(name = "Accept-Language", defaultValue = "ko") String language) {
        return Map.of("count", digimonService.getEvolutionCount(CatalogLanguage.fromHeader(language)));
    }

    @GetMapping("/getAll")
    public List<DigimonDto> getAll(@RequestHeader(name = "Accept-Language", defaultValue = "ko") String language) {
        return digimonService.getAll(CatalogLanguage.fromHeader(language));
    }

    @GetMapping("/by-stage")
    public List<DigimonDto> getByStage(@RequestParam String stageCode, @RequestHeader(name = "Accept-Language", defaultValue = "ko") String language) {
        return digimonService.getByStageCode(stageCode, CatalogLanguage.fromHeader(language));
    }

    @GetMapping("/search")
    public List<DigimonDto> search(@RequestParam String query, @RequestHeader(name = "Accept-Language", defaultValue = "ko") String language) {
        return digimonService.searchByName(query.trim(), CatalogLanguage.fromHeader(language));
    }

    @GetMapping("/{id}/evolution-routes")
    public EvolutionRoutesDto getEvolutionRoutes(@PathVariable int id, @RequestHeader(name = "Accept-Language", defaultValue = "ko") String language) {
        return digimonService.getEvolutionRoutes(id, CatalogLanguage.fromHeader(language));
    }

    @GetMapping("/{id}")
    public DigimonDetailDto getDetail(@PathVariable int id, @RequestHeader(name = "Accept-Language", defaultValue = "ko") String language) {
        return digimonService.getDetail(id, CatalogLanguage.fromHeader(language));
    }
}
