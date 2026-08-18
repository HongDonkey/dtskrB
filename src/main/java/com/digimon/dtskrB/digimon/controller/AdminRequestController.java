package com.digimon.dtskrB.digimon.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/admin/requests")
public class AdminRequestController {

    private static final Logger log = LoggerFactory.getLogger(AdminRequestController.class);
    private static final Set<String> ALLOWED_STATUSES = Set.of("PENDING", "REVIEWING", "COMPLETED");

    private final JdbcTemplate jdbcTemplate;
    private final Path uploadDirectory;

    public AdminRequestController(
            JdbcTemplate jdbcTemplate,
            @Value("${app.request-board.upload-directory:uploads/requests}") String uploadDirectory) {
        this.jdbcTemplate = jdbcTemplate;
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    @GetMapping
    public List<AdminRequestSummary> getRequests() {
        List<AdminRequestSummary> requests = jdbcTemplate.query("""
                SELECT p.id, p.category, p.title, p.content, p.requester_name, p.requester_ip,
                       p.status, p.admin_response, p.responded_at, p.responded_by, p.language_code,
                       p.created_at, COUNT(a.id) AS attachment_count
                FROM request_post p
                LEFT JOIN request_attachment a ON a.request_post_id = p.id
                GROUP BY p.id, p.category, p.title, p.content, p.requester_name, p.requester_ip,
                         p.status, p.admin_response, p.responded_at, p.responded_by, p.language_code, p.created_at
                ORDER BY p.created_at DESC, p.id DESC
                """, (resultSet, rowNumber) -> new AdminRequestSummary(
                        resultSet.getLong("id"), resultSet.getString("category"),
                        resultSet.getString("title"), resultSet.getString("content"),
                        resultSet.getString("requester_name"),
                        resultSet.getString("requester_ip"),
                        resultSet.getString("status"), resultSet.getString("admin_response"),
                        resultSet.getObject("responded_at", LocalDateTime.class), resultSet.getString("responded_by"),
                        resultSet.getString("language_code"),
                        resultSet.getObject("created_at", LocalDateTime.class),
                        resultSet.getInt("attachment_count"), List.of()));
        Map<Long, List<AdminAttachmentSummary>> attachmentsByRequest = findAllAttachments();
        return requests.stream().map(request -> new AdminRequestSummary(
                request.id(), request.category(), request.title(), request.content(), request.requesterName(),
                request.requesterIp(), request.status(), request.adminResponse(), request.respondedAt(), request.respondedBy(),
                request.languageCode(), request.createdAt(), request.attachmentCount(),
                attachmentsByRequest.getOrDefault(request.id(), List.of()))).toList();
    }

    @PatchMapping("/{requestId}/status")
    public Map<String, Object> updateStatus(
            @PathVariable long requestId,
            @RequestBody UpdateStatusRequest request,
            Authentication authentication) {
        String status = request.status() == null ? "" : request.status().trim().toUpperCase();
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request status.");
        }
        int updated = jdbcTemplate.update(
                "UPDATE request_post SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                status, requestId);
        if (updated == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        log.info("Request status changed: requestId={}, status={}, administrator={}",
                requestId, status, authentication.getName());
        return Map.of("id", requestId, "status", status);
    }

    @PatchMapping("/{requestId}/response")
    public Map<String, Object> updateResponse(
            @PathVariable long requestId,
            @RequestBody UpdateResponseRequest request,
            Authentication authentication) {
        String response = request.response() == null ? "" : request.response().trim();
        if (!StringUtils.hasText(response)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Administrator response is required.");
        }
        if (response.length() > 10000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Administrator response must be 10000 characters or fewer.");
        }
        int updated = jdbcTemplate.update("""
                UPDATE request_post
                SET admin_response = ?, responded_at = CURRENT_TIMESTAMP, responded_by = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """, response, authentication.getName(), requestId);
        if (updated == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        LocalDateTime respondedAt = jdbcTemplate.queryForObject(
                "SELECT responded_at FROM request_post WHERE id = ?", LocalDateTime.class, requestId);
        log.info("Request response saved: requestId={}, administrator={}", requestId, authentication.getName());
        return Map.of("id", requestId, "response", response, "respondedAt", respondedAt,
                "respondedBy", authentication.getName());
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> getAttachment(@PathVariable long attachmentId) {
        List<AdminAttachmentFile> files = jdbcTemplate.query("""
                SELECT original_file_name, storage_type, storage_key, content_type
                FROM request_attachment
                WHERE id = ?
                """, (resultSet, rowNumber) -> new AdminAttachmentFile(
                        resultSet.getString("original_file_name"), resultSet.getString("storage_type"),
                        resultSet.getString("storage_key"), resultSet.getString("content_type")), attachmentId);
        if (files.isEmpty() || !"LOCAL".equals(files.getFirst().storageType())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        AdminAttachmentFile file = files.getFirst();
        Path path = resolveAttachmentPath(file.storageKey());
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(file.originalFileName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(new FileSystemResource(path));
    }

    private Path resolveAttachmentPath(String storageKey) {
        try {
            if (!Files.isDirectory(uploadDirectory)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            Path realUploadDirectory = uploadDirectory.toRealPath();
            Path realFile = Path.of(storageKey).toAbsolutePath().normalize().toRealPath();
            if (!realFile.startsWith(realUploadDirectory) || !Files.isRegularFile(realFile)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return realFile;
        } catch (java.io.IOException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private Map<Long, List<AdminAttachmentSummary>> findAllAttachments() {
        return jdbcTemplate.query("""
                SELECT request_post_id, id, original_file_name, content_type, file_size
                FROM request_attachment
                ORDER BY request_post_id, sort_order, id
                """, (resultSet, rowNumber) -> new AdminAttachmentRow(
                        resultSet.getLong("request_post_id"),
                        new AdminAttachmentSummary(
                                resultSet.getLong("id"), resultSet.getString("original_file_name"),
                                resultSet.getString("content_type"), resultSet.getLong("file_size"))))
                .stream()
                .collect(Collectors.groupingBy(
                        AdminAttachmentRow::requestId,
                        Collectors.mapping(AdminAttachmentRow::attachment, Collectors.toList())));
    }

    public record AdminRequestSummary(long id, String category, String title, String content, String requesterName,
            String requesterIp, String status, String adminResponse, LocalDateTime respondedAt, String respondedBy,
            String languageCode, LocalDateTime createdAt, int attachmentCount,
            List<AdminAttachmentSummary> attachments) { }
    public record AdminAttachmentSummary(long id, String originalFileName, String contentType, long fileSize) { }
    public record UpdateStatusRequest(String status) { }
    public record UpdateResponseRequest(String response) { }
    private record AdminAttachmentRow(long requestId, AdminAttachmentSummary attachment) { }
    private record AdminAttachmentFile(String originalFileName, String storageType, String storageKey,
            String contentType) { }
}
