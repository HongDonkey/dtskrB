package com.digimon.dtskrB.digimon.dto;

public record DigimonDto(
        int id,
        String name,
        String stage,
        String attribute,
        String digimonType,
        String personality,
        String imageUrl,
        String pixelImageUrl
) {
}
