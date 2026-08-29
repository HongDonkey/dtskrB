package com.digimon.dtskrB.digimon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digimon.dtskrB.digimon.dto.QuestDetailDto;
import com.digimon.dtskrB.digimon.dto.QuestSummaryDto;
import com.digimon.dtskrB.digimon.service.QuestBoardService;

@RestController
@RequestMapping("/api/quests")
public class QuestBoardController {

    private final QuestBoardService questBoardService;

    public QuestBoardController(QuestBoardService questBoardService) {
        this.questBoardService = questBoardService;
    }

    @GetMapping
    public List<QuestSummaryDto> getQuests(@RequestParam(defaultValue = "ko") String lang) {
        return questBoardService.findPublishedQuests(lang);
    }

    @GetMapping("/{questId}")
    public ResponseEntity<QuestDetailDto> getQuest(
            @PathVariable long questId,
            @RequestParam(defaultValue = "ko") String lang) {
        return questBoardService.findPublishedQuest(questId, lang)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
