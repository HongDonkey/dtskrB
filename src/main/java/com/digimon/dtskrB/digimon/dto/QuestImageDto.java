package com.digimon.dtskrB.digimon.dto;

public record QuestImageDto(
        int sortOrder,
        String imageUrl,
        String locationName,
        String locationNote) {
}
