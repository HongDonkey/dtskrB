ALTER TABLE `request_post`
  ADD COLUMN `requester_password_hash` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
    AFTER `requester_name`,
  DROP COLUMN `contact_email`,
  ADD KEY `idx_request_post_requester_name` (`requester_name`);
