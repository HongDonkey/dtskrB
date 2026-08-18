package com.digimon.dtskrB.digimon.dto;

public record EvolutionCandidateDto(
        DigimonDto digimon,
        String conditionText,
        Integer requiredAgentRank,
        Long requiredItemId,
        String requiredItemName
) {
}
