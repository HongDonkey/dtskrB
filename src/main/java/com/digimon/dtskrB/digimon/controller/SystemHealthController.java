package com.digimon.dtskrB.digimon.controller;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class SystemHealthController {

    private final JdbcTemplate jdbcTemplate;

    public SystemHealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public Map<String, String> getHealth() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return Map.of("server", "UP", "database", "UP");
        } catch (DataAccessException exception) {
            return Map.of("server", "UP", "database", "DOWN");
        }
    }
}
