package com.digimon.dtskrB.digimon.dto;

import java.util.List;

public record EvolutionRoutesDto(
        EvolutionTreeNodeDto current,
        List<EvolutionTreeNodeDto> previous,
        List<EvolutionTreeNodeDto> next
) {
}
