ALTER TABLE `request_post`
  ADD COLUMN `requester_ip` varchar(45) COLLATE ascii_general_ci DEFAULT NULL
    AFTER `requester_password_hash`,
  ADD KEY `idx_request_post_requester_ip_created_at` (`requester_ip`, `created_at`);
