UPDATE `request_post`
SET `status` = 'COMPLETED'
WHERE `status` = 'REJECTED';

ALTER TABLE `request_post`
  DROP CHECK `chk_request_post_status`;

ALTER TABLE `request_post`
  ADD CONSTRAINT `chk_request_post_status`
    CHECK (`status` IN ('PENDING', 'REVIEWING', 'COMPLETED'));
