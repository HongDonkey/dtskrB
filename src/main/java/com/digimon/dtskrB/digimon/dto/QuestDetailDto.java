package com.digimon.dtskrB.digimon.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuestDetailDto(
        long id,
        String category,
        String title,
        String summary,
        String content,
        String sourceUrl,
        List<QuestImageDto> images,
        LocalDateTime updatedAt) {
}
