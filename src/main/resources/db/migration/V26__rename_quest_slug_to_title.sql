ALTER TABLE `quest_post`
  DROP INDEX `uk_quest_post_slug`,
  CHANGE COLUMN `slug` `title` varchar(200) NOT NULL,
  ADD UNIQUE KEY `uk_quest_post_title` (`title`);

UPDATE `quest_post`
SET `title` = 'All eyes one me!!';

UPDATE `quest_translation`
SET `title` = 'All eyes one me!!';
