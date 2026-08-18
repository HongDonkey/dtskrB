package com.digimon.dtskrB.digimon.dto;

public record EvolutionEdgeDto(
        DigimonDto fromDigimon,
        DigimonDto toDigimon,
        String fromDigimonEvolutionCondition,
        String toDigimonEvolutionCondition,
        Integer requiredAgentRank,
        Long requiredItemId,
        String requiredItemName
) {
}
