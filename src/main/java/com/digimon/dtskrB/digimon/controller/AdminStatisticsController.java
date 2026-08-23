package com.digimon.dtskrB.digimon.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digimon.dtskrB.digimon.service.VisitorStatisticsService;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    private final VisitorStatisticsService visitorStatisticsService;

    public AdminStatisticsController(VisitorStatisticsService visitorStatisticsService) {
        this.visitorStatisticsService = visitorStatisticsService;
    }

    @GetMapping("/today")
    public TodayStatistics getTodayStatistics() {
        return new TodayStatistics(LocalDate.now(), visitorStatisticsService.getTodayVisitorCount());
    }

    public record TodayStatistics(LocalDate date, long uniqueVisitors) { }
}
