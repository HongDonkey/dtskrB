package com.digimon.dtskrB.digimon.dto;

import java.util.List;

public record DigimonDetailDto(
        int id,
        String name,
        String stage,
        String attribute,
        String digimonType,
        String personality,
        String imageUrl,
        String pixelImageUrl,
        String gameCaptureUrl,
        String skillImageUrl,
        String description,
        String evolutionCondition,
        String sourceUrl,
        List<SpecialSkillDto> specialSkills,
        List<AttachmentSkillDto> attachmentSkills
) {
}
