CREATE TABLE `visitor_statistics` (
  `id` tinyint unsigned NOT NULL,
  `total_visitors` bigint unsigned NOT NULL DEFAULT 0,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_visitor_statistics_singleton` CHECK (`id` = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `visitor_statistics` (`id`, `total_visitors`)
SELECT 1, COUNT(*) FROM `daily_visitor`;
