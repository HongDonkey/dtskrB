CREATE TABLE `admin_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `login_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ADMIN',
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `last_login_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_user_login_id` (`login_id`),
  CONSTRAINT `chk_admin_user_role` CHECK (`role` IN ('ADMIN', 'MANAGER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `admin_user` (`login_id`, `password_hash`, `display_name`, `role`)
VALUES (
  'digimon_admin',
  '$2a$12$hDAR4lQkNUKXXd8OGYw4mOcyK8MKuClLYFI0qYici2czdVEU69c.K',
  'Digimon Administrator',
  'ADMIN'
);
