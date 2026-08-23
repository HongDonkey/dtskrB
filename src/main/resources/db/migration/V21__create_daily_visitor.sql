CREATE TABLE `daily_visitor` (
  `visit_date` date NOT NULL,
  `visitor_hash` char(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `first_visited_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_visited_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`visit_date`, `visitor_hash`),
  KEY `idx_daily_visitor_last_visited_at` (`last_visited_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
