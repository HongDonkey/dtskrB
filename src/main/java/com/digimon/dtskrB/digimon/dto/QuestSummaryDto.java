package com.digimon.dtskrB.digimon.dto;

import java.time.LocalDateTime;

public record QuestSummaryDto(
        long id,
        String category,
        String title,
        String summary,
        String thumbnailUrl,
        int imageCount,
        LocalDateTime updatedAt) {
}
