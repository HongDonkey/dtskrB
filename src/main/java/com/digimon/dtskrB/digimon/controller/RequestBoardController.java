package com.digimon.dtskrB.digimon.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digimon.dtskrB.digimon.service.RequestBoardService;
import com.digimon.dtskrB.request.RateLimitExceededException;
import com.digimon.dtskrB.request.RequestBoardRateLimiter;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/requests")
public class RequestBoardController {

    private final RequestBoardService requestBoardService;
    private final RequestBoardRateLimiter rateLimiter;

    public RequestBoardController(RequestBoardService requestBoardService, RequestBoardRateLimiter rateLimiter) {
        this.requestBoardService = requestBoardService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createRequest(
            @RequestParam String category,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String requesterName,
            @RequestParam String requesterPassword,
            @RequestParam(defaultValue = "ko") String languageCode,
            @RequestParam(required = false) Integer relatedDigimonId,
            @RequestParam(required = false, name = "images") List<MultipartFile> images,
            HttpServletRequest servletRequest) {
        rateLimiter.checkCreate(servletRequest.getRemoteAddr());
        long id = requestBoardService.createRequest(category, title, content, requesterName, requesterPassword,
                languageCode, relatedDigimonId, images, servletRequest.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id, "status", "PENDING"));
    }

    @PostMapping("/mine")
    public List<RequestBoardService.RequesterPost> getMyRequests(
            @RequestBody MyRequestLookup request,
            HttpServletRequest servletRequest) {
        rateLimiter.checkLookup(servletRequest.getRemoteAddr(), request.requesterName());
        return requestBoardService.findRequesterPosts(request.requesterName(), request.requesterPassword());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, Long.toString(exception.getRetryAfterSeconds()))
                .body(Map.of(
                        "code", "RATE_LIMITED",
                        "message", exception.getMessage(),
                        "retryAfterSeconds", exception.getRetryAfterSeconds()));
    }

    public record MyRequestLookup(String requesterName, String requesterPassword) { }
}
