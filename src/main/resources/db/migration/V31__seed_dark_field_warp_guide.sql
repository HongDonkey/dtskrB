INSERT INTO `quest_post` (`id`, `title`, `category`, `source_url`, `sort_order`) VALUES
  (5, '다크 필드 워프 정리', 'GUIDE', NULL, 50);

INSERT INTO `quest_translation`
    (`quest_post_id`, `language_code`, `title`, `summary`, `content`, `source_url`) VALUES
  (5, 'ko', '다크 필드 워프 정리', '다크 필드의 워프 경로와 상자 위치를 이미지로 확인할 수 있습니다.', NULL, NULL),
  (5, 'en', 'Dark Field Warp Guide', 'Check the warp routes and treasure chest locations in the Dark Field images.', NULL, NULL),
  (5, 'jp', 'ダークフィールド ワープ案内', 'ダークフィールドのワープ経路と宝箱の位置を画像で確認できます。', NULL, NULL);

INSERT INTO `quest_image` (`quest_post_id`, `image_url`, `sort_order`) VALUES
  (5, '/digimon/captures/guide/darkFieldWarp/1.jpg', 1),
  (5, '/digimon/captures/guide/darkFieldWarp/2.jpg', 2);

INSERT INTO `quest_image_translation`
    (`quest_image_id`, `quest_post_id`, `language_code`, `location_name`, `location_note`, `image_url`)
SELECT image.id,
       image.quest_post_id,
       language.language_code,
       CASE language.language_code
         WHEN 'ko' THEN CASE image.sort_order
           WHEN 1 THEN '다크 크레바스 현대'
           WHEN 2 THEN '다크 캐슬 현대'
         END
         WHEN 'en' THEN CASE image.sort_order
           WHEN 1 THEN 'Dark Crevasse (Present Day)'
           WHEN 2 THEN 'Dark Castle (Present Day)'
         END
         WHEN 'jp' THEN CASE image.sort_order
           WHEN 1 THEN 'ダーククレバス・現代'
           WHEN 2 THEN 'ダークキャッスル・現代'
         END
       END,
       NULL,
       NULL
FROM `quest_image` image
CROSS JOIN (
  SELECT 'ko' AS language_code
  UNION ALL SELECT 'en'
  UNION ALL SELECT 'jp'
) language
WHERE image.quest_post_id = 5;
