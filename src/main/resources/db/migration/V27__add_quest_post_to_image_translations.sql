ALTER TABLE `quest_image_translation`
  ADD COLUMN `quest_post_id` bigint unsigned NULL AFTER `quest_image_id`;

UPDATE `quest_image_translation` translation
JOIN `quest_image` image ON image.id = translation.quest_image_id
SET translation.quest_post_id = image.quest_post_id;

ALTER TABLE `quest_image_translation`
  MODIFY COLUMN `quest_post_id` bigint unsigned NOT NULL,
  ADD KEY `idx_quest_image_translation_post` (`quest_post_id`),
  ADD CONSTRAINT `fk_quest_image_translation_post`
    FOREIGN KEY (`quest_post_id`) REFERENCES `quest_post` (`id`) ON DELETE CASCADE;

UPDATE `quest_image_translation`
SET `location_note` = '북쪽 카페'
WHERE `quest_image_id` = 5
  AND `language_code` = 'ko';

UPDATE `quest_image_translation`
SET `location_note` = '수로입구 : 신주쿠 어느 뒷골목 북동쪽'
WHERE `quest_image_id` = 15
  AND `language_code` = 'ko';

UPDATE `quest_image_translation`
SET `location_note` = '도쿄도청 중앙본청사에서 서쪽으로 입장'
WHERE `quest_image_id` = 16
  AND `language_code` = 'ko';
