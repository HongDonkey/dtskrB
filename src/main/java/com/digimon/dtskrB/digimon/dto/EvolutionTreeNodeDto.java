package com.digimon.dtskrB.digimon.dto;

import java.util.List;

public record EvolutionTreeNodeDto(
        int id,
        String name,
        String stage,
        String attribute,
        String digimonType,
        String personality,
        String imageUrl,
        String pixelImageUrl,
        String conditionText,
        Integer requiredAgentRank,
        Long requiredItemId,
        String requiredItemName,
        List<EvolutionTreeNodeDto> children
) {
}
