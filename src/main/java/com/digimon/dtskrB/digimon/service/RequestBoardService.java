package com.digimon.dtskrB.digimon.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RequestBoardService {

    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L;
    private static final long MAX_TOTAL_FILE_SIZE = 25L * 1024L * 1024L;
    private static final int MAX_FILE_COUNT = 5;
    private static final int MAX_IMAGE_DIMENSION = 12_000;
    private static final long MAX_IMAGE_PIXELS = 50_000_000L;
    private static final Set<String> CATEGORIES = Set.of("DATA_CORRECTION", "FEATURE", "BUG", "ETC");
    private static final Set<String> LANGUAGES = Set.of("ko", "en", "jp");

    private final JdbcTemplate jdbcTemplate;
    private final Path uploadDirectory;
    private final PasswordEncoder passwordEncoder;

    public RequestBoardService(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            @Value("${app.request-board.upload-directory:uploads/requests}") String uploadDirectory,
            Environment environment) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        Path configuredDirectory = Path.of(uploadDirectory);
        if (environment.matchesProfiles("prod") && !configuredDirectory.isAbsolute()) {
            throw new IllegalStateException("REQUEST_UPLOAD_DIR must be an absolute path in the prod profile.");
        }
        this.uploadDirectory = configuredDirectory.toAbsolutePath().normalize();
    }

    @Transactional
    public long createRequest(
            String category,
            String title,
            String content,
            String requesterName,
            String requesterPassword,
            String languageCode,
            Integer relatedDigimonId,
            List<MultipartFile> files,
            String requesterIp) {
        validatePost(category, title, content, requesterName, requesterPassword, languageCode);
        List<MultipartFile> images = files == null ? List.of() : files.stream().filter(file -> !file.isEmpty()).toList();
        if (images.size() > MAX_FILE_COUNT) throw new IllegalArgumentException("A maximum of 5 images can be uploaded.");
        long totalFileSize = images.stream().mapToLong(MultipartFile::getSize).sum();
        if (totalFileSize > MAX_TOTAL_FILE_SIZE) {
            throw new IllegalArgumentException("The total image size must be 25 MB or smaller.");
        }

        List<StoredImage> storedImages = new ArrayList<>();
        try {
            for (MultipartFile image : images) storedImages.add(storeImage(image));
            long postId = insertPost(category, title.trim(), content.trim(), requesterName.trim(),
                    passwordEncoder.encode(requesterPassword), normalizeIp(requesterIp), languageCode,
                    relatedDigimonId);
            for (int index = 0; index < storedImages.size(); index++) insertAttachment(postId, storedImages.get(index), index + 1);
            registerRollbackCleanup(storedImages);
            return postId;
        } catch (RuntimeException | IOException exception) {
            storedImages.forEach(image -> deleteQuietly(image.path()));
            if (exception instanceof IllegalArgumentException illegalArgumentException) throw illegalArgumentException;
            throw new IllegalStateException("Failed to save the request.", exception);
        }
    }

    private void validatePost(String category, String title, String content, String requesterName,
            String requesterPassword, String languageCode) {
        if (!CATEGORIES.contains(category)) throw new IllegalArgumentException("Invalid category.");
        if (!StringUtils.hasText(title) || title.trim().length() > 200) throw new IllegalArgumentException("Title must be between 1 and 200 characters.");
        if (!StringUtils.hasText(content) || content.trim().length() > 10000) throw new IllegalArgumentException("Content must be between 1 and 10000 characters.");
        if (!StringUtils.hasText(requesterName) || requesterName.trim().length() > 50) throw new IllegalArgumentException("Requester name is required and must be 50 characters or fewer.");
        if (!StringUtils.hasText(requesterPassword) || requesterPassword.length() < 4 || requesterPassword.length() > 72) {
            throw new IllegalArgumentException("Password must be between 4 and 72 characters.");
        }
        if (!LANGUAGES.contains(languageCode)) throw new IllegalArgumentException("Invalid language.");
    }

    private long insertPost(String category, String title, String content, String requesterName,
            String requesterPasswordHash, String requesterIp, String languageCode, Integer relatedDigimonId) {
        String sql = """
                INSERT INTO request_post
                    (category, title, content, requester_name, requester_password_hash, requester_ip,
                     language_code, related_digimon_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, category);
            statement.setString(2, title);
            statement.setString(3, content);
            statement.setString(4, requesterName);
            statement.setString(5, requesterPasswordHash);
            statement.setString(6, requesterIp);
            statement.setString(7, languageCode);
            if (relatedDigimonId == null) statement.setNull(8, java.sql.Types.INTEGER);
            else statement.setInt(8, relatedDigimonId);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("Failed to obtain the request ID.");
        return key.longValue();
    }

    public List<RequesterPost> findRequesterPosts(String requesterName, String requesterPassword) {
        if (!StringUtils.hasText(requesterName) || requesterName.trim().length() > 50) {
            throw new IllegalArgumentException("Requester name is required and must be 50 characters or fewer.");
        }
        if (!StringUtils.hasText(requesterPassword)
                || requesterPassword.length() < 4 || requesterPassword.length() > 72) {
            throw new IllegalArgumentException("Password must be between 4 and 72 characters.");
        }
        List<RequesterPostWithPassword> candidates = jdbcTemplate.query("""
                SELECT p.id, p.category, p.title, p.content, p.status, p.admin_response, p.responded_at, p.created_at,
                       p.requester_password_hash, COUNT(a.id) AS attachment_count
                FROM request_post p
                LEFT JOIN request_attachment a ON a.request_post_id = p.id
                WHERE p.requester_name = ? AND p.requester_password_hash IS NOT NULL
                GROUP BY p.id, p.category, p.title, p.content, p.status, p.admin_response, p.responded_at,
                         p.created_at, p.requester_password_hash
                ORDER BY p.created_at DESC, p.id DESC
                """, (resultSet, rowNumber) -> new RequesterPostWithPassword(
                        resultSet.getLong("id"), resultSet.getString("category"),
                        resultSet.getString("title"), resultSet.getString("content"),
                        resultSet.getString("status"), resultSet.getString("admin_response"),
                        resultSet.getObject("responded_at", java.time.LocalDateTime.class),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getInt("attachment_count"), resultSet.getString("requester_password_hash")),
                requesterName.trim());
        return candidates.stream()
                .filter(post -> passwordEncoder.matches(requesterPassword, post.passwordHash()))
                .map(post -> new RequesterPost(post.id(), post.category(), post.title(), post.content(),
                        post.status(), post.adminResponse(), post.respondedAt(), post.createdAt(), post.attachmentCount()))
                .toList();
    }

    private void insertAttachment(long postId, StoredImage image, int sortOrder) {
        jdbcTemplate.update("""
                INSERT INTO request_attachment
                    (request_post_id, original_file_name, stored_file_name, storage_type, storage_key,
                     content_type, file_size, image_width, image_height, sort_order)
                VALUES (?, ?, ?, 'LOCAL', ?, ?, ?, ?, ?, ?)
                """, postId, image.originalName(), image.storedName(), image.path().toString(),
                image.contentType(), image.size(), image.width(), image.height(), sortOrder);
    }

    private StoredImage storeImage(MultipartFile file) throws IOException {
        if (file.getSize() <= 0 || file.getSize() > MAX_FILE_SIZE) throw new IllegalArgumentException("Each image must be 10 MB or smaller.");
        byte[] header = file.getInputStream().readNBytes(12);
        ImageType type = detectImageType(header);
        if (type == null) throw new IllegalArgumentException("Only JPEG, PNG, and WebP images are allowed.");

        Files.createDirectories(uploadDirectory);
        String storedName = UUID.randomUUID() + type.extension();
        Path destination = uploadDirectory.resolve(storedName).normalize();
        if (!destination.startsWith(uploadDirectory)) throw new IllegalArgumentException("Invalid file path.");
        try {
            try (var input = file.getInputStream()) {
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            Integer width = null;
            Integer height = null;
            BufferedImage image = ImageIO.read(destination.toFile());
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
                if (width <= 0 || height <= 0
                        || width > MAX_IMAGE_DIMENSION || height > MAX_IMAGE_DIMENSION
                        || width * (long) height > MAX_IMAGE_PIXELS) {
                    throw new IllegalArgumentException("The image dimensions are too large.");
                }
            } else if (!".webp".equals(type.extension())) {
                throw new IllegalArgumentException("The uploaded file is not a valid image.");
            }
            return new StoredImage(safeOriginalName(file.getOriginalFilename()), storedName, destination,
                    type.contentType(), file.getSize(), width, height);
        } catch (IOException | RuntimeException exception) {
            deleteQuietly(destination);
            throw exception;
        }
    }

    private ImageType detectImageType(byte[] header) {
        String hex = HexFormat.of().formatHex(header).toLowerCase(Locale.ROOT);
        if (hex.startsWith("ffd8ff")) return new ImageType(".jpg", "image/jpeg");
        if (hex.startsWith("89504e470d0a1a0a")) return new ImageType(".png", "image/png");
        if (header.length >= 12 && new String(header, 0, 4).equals("RIFF") && new String(header, 8, 4).equals("WEBP")) {
            return new ImageType(".webp", "image/webp");
        }
        return null;
    }

    private String safeOriginalName(String name) {
        if (!StringUtils.hasText(name)) return "image";
        String normalized = name.replace('\\', '/');
        String basename = normalized.substring(normalized.lastIndexOf('/') + 1);
        return basename.length() > 255 ? basename.substring(basename.length() - 255) : basename;
    }

    private String normalizeIp(String requesterIp) {
        if (!StringUtils.hasText(requesterIp)) return null;
        String normalized = requesterIp.trim();
        int scopeIndex = normalized.indexOf('%');
        if (scopeIndex >= 0) normalized = normalized.substring(0, scopeIndex);
        if (normalized.length() > 45) throw new IllegalArgumentException("Invalid requester IP address.");
        return normalized;
    }

    private void deleteQuietly(Path path) {
        try { Files.deleteIfExists(path); } catch (IOException ignored) { }
    }

    private void registerRollbackCleanup(List<StoredImage> storedImages) {
        if (storedImages.isEmpty() || !TransactionSynchronizationManager.isSynchronizationActive()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    storedImages.forEach(image -> deleteQuietly(image.path()));
                }
            }
        });
    }

    private record ImageType(String extension, String contentType) { }
    private record StoredImage(String originalName, String storedName, Path path, String contentType,
            long size, Integer width, Integer height) { }
    public record RequesterPost(long id, String category, String title, String content, String status,
            String adminResponse, java.time.LocalDateTime respondedAt,
            java.time.LocalDateTime createdAt, int attachmentCount) { }
    private record RequesterPostWithPassword(long id, String category, String title, String content,
            String status, String adminResponse, java.time.LocalDateTime respondedAt,
            java.time.LocalDateTime createdAt, int attachmentCount, String passwordHash) { }
}
