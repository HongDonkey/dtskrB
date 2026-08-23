package com.digimon.dtskrB.digimon.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.digimon.dtskrB.digimon.service.VisitorStatisticsService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/visits")
public class VisitorStatisticsController {

    private final VisitorStatisticsService visitorStatisticsService;

    public VisitorStatisticsController(VisitorStatisticsService visitorStatisticsService) {
        this.visitorStatisticsService = visitorStatisticsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Boolean> recordVisit(HttpServletRequest request) {
        visitorStatisticsService.recordVisit(request.getRemoteAddr());
        return Map.of("recorded", true);
    }
}
