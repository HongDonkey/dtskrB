ALTER TABLE `admin_user`
  ADD COLUMN `password_change_required` tinyint(1) NOT NULL DEFAULT 1 AFTER `locked_until`,
  ADD COLUMN `credential_version` bigint unsigned NOT NULL DEFAULT 1 AFTER `password_change_required`;

-- Disable the publicly known V11 bootstrap credential. A deployment bootstrap secret
-- can reactivate this account with a new BCrypt hash on the next application start.
UPDATE `admin_user`
SET `is_active` = 0,
    `password_change_required` = 1,
    `credential_version` = `credential_version` + 1
WHERE `login_id` = 'digimon_admin'
  AND `password_hash` = '$2a$12$hDAR4lQkNUKXXd8OGYw4mOcyK8MKuClLYFI0qYici2czdVEU69c.K';
