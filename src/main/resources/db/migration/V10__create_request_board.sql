CREATE TABLE `request_post` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `category` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `requester_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `language_code` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ko',
  `related_digimon_id` int unsigned DEFAULT NULL,
  `admin_note` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_request_post_status_created_at` (`status`, `created_at`),
  KEY `idx_request_post_category` (`category`),
  KEY `idx_request_post_language` (`language_code`),
  KEY `idx_request_post_digimon` (`related_digimon_id`),
  CONSTRAINT `fk_request_post_digimon`
    FOREIGN KEY (`related_digimon_id`) REFERENCES `digimon_kr` (`id`) ON DELETE SET NULL,
  CONSTRAINT `chk_request_post_category`
    CHECK (`category` IN ('DATA_CORRECTION', 'FEATURE', 'BUG', 'ETC')),
  CONSTRAINT `chk_request_post_status`
    CHECK (`status` IN ('PENDING', 'REVIEWING', 'COMPLETED', 'REJECTED')),
  CONSTRAINT `chk_request_post_language`
    CHECK (`language_code` IN ('ko', 'en', 'jp'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `request_attachment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `request_post_id` bigint unsigned NOT NULL,
  `original_file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stored_file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `storage_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'LOCAL',
  `storage_key` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_size` bigint unsigned NOT NULL,
  `image_width` int unsigned DEFAULT NULL,
  `image_height` int unsigned DEFAULT NULL,
  `sort_order` smallint unsigned NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_request_attachment_stored_name` (`stored_file_name`),
  KEY `idx_request_attachment_post_sort` (`request_post_id`, `sort_order`),
  CONSTRAINT `fk_request_attachment_post`
    FOREIGN KEY (`request_post_id`) REFERENCES `request_post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_request_attachment_storage_type`
    CHECK (`storage_type` IN ('LOCAL', 'S3', 'R2')),
  CONSTRAINT `chk_request_attachment_file_size`
    CHECK (`file_size` > 0),
  CONSTRAINT `chk_request_attachment_sort_order`
    CHECK (`sort_order` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
