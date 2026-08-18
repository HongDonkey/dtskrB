ALTER TABLE `request_post`
  ADD COLUMN `admin_response` text NULL AFTER `status`,
  ADD COLUMN `responded_at` datetime NULL AFTER `admin_response`,
  ADD COLUMN `responded_by` varchar(100) NULL AFTER `responded_at`;
