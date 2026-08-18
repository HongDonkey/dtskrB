ALTER TABLE `admin_user`
  ADD COLUMN `failed_login_attempts` int unsigned NOT NULL DEFAULT 0 AFTER `is_active`,
  ADD COLUMN `locked_until` datetime DEFAULT NULL AFTER `failed_login_attempts`;

CREATE INDEX `idx_admin_user_locked_until` ON `admin_user` (`locked_until`);
