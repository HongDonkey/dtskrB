INSERT INTO `quest_post` (`id`, `title`, `category`, `source_url`, `sort_order`) VALUES
  (6, '아우터 던전 위치 정리', 'GUIDE', NULL, 60);

INSERT INTO `quest_translation`
    (`quest_post_id`, `language_code`, `title`, `summary`, `content`, `source_url`) VALUES
  (6, 'ko', '아우터 던전 위치 정리', '아우터 던전 20개의 위치를 던전 테마 순서대로 확인할 수 있습니다.', '원하는 던전 테마와 같은 번호의 이미지를 확인하세요.', NULL),
  (6, 'en', 'Outer Dungeon Locations', 'Find all 20 Outer Dungeons in dungeon-theme order.', 'Use the image with the same number as the dungeon theme you are looking for.', NULL),
  (6, 'jp', 'アウターダンジョンの場所まとめ', '全20か所のアウターダンジョンをテーマ順に確認できます。', '探しているダンジョンテーマと同じ番号の画像を確認してください。', NULL);

INSERT INTO `quest_image` (`quest_post_id`, `image_url`, `sort_order`) VALUES
  (6, '/digimon/captures/guide/outerDungeonLocations/1.jpg', 1),
  (6, '/digimon/captures/guide/outerDungeonLocations/2.jpg', 2),
  (6, '/digimon/captures/guide/outerDungeonLocations/3.jpg', 3),
  (6, '/digimon/captures/guide/outerDungeonLocations/4.jpg', 4),
  (6, '/digimon/captures/guide/outerDungeonLocations/5.jpg', 5),
  (6, '/digimon/captures/guide/outerDungeonLocations/6.jpg', 6),
  (6, '/digimon/captures/guide/outerDungeonLocations/7.jpg', 7),
  (6, '/digimon/captures/guide/outerDungeonLocations/8.jpg', 8),
  (6, '/digimon/captures/guide/outerDungeonLocations/9.jpg', 9),
  (6, '/digimon/captures/guide/outerDungeonLocations/10.jpg', 10),
  (6, '/digimon/captures/guide/outerDungeonLocations/11.jpg', 11),
  (6, '/digimon/captures/guide/outerDungeonLocations/12.jpg', 12),
  (6, '/digimon/captures/guide/outerDungeonLocations/13.jpg', 13),
  (6, '/digimon/captures/guide/outerDungeonLocations/14.jpg', 14),
  (6, '/digimon/captures/guide/outerDungeonLocations/15.jpg', 15),
  (6, '/digimon/captures/guide/outerDungeonLocations/16.jpg', 16),
  (6, '/digimon/captures/guide/outerDungeonLocations/17.jpg', 17),
  (6, '/digimon/captures/guide/outerDungeonLocations/18.jpg', 18),
  (6, '/digimon/captures/guide/outerDungeonLocations/19.jpg', 19),
  (6, '/digimon/captures/guide/outerDungeonLocations/20.jpg', 20);

INSERT INTO `quest_image_translation`
    (`quest_image_id`, `quest_post_id`, `language_code`, `location_name`, `location_note`, `image_url`)
SELECT image.id,
       image.quest_post_id,
       language.language_code,
       CASE language.language_code
         WHEN 'ko' THEN CASE
           WHEN image.sort_order BETWEEN 1 AND 4 THEN CONCAT('거대 디지고기를 지켜라 ', image.sort_order)
           WHEN image.sort_order BETWEEN 5 AND 8 THEN CONCAT('마지막까지 살아남아라 ', image.sort_order - 4)
           WHEN image.sort_order BETWEEN 9 AND 12 THEN CONCAT('스피드로 이겨라 ', image.sort_order - 8)
           WHEN image.sort_order BETWEEN 13 AND 16 THEN CONCAT('제한 시간 내에 보스를 쓰러뜨려라 ', image.sort_order - 12)
           WHEN image.sort_order BETWEEN 17 AND 20 THEN CONCAT('최강의 디지몬과 싸워라 ', image.sort_order - 16)
         END
         WHEN 'en' THEN CASE
           WHEN image.sort_order BETWEEN 1 AND 4 THEN CONCAT('Protect the Giant DigiMeat ', image.sort_order)
           WHEN image.sort_order BETWEEN 5 AND 8 THEN CONCAT('Survive Until the End ', image.sort_order - 4)
           WHEN image.sort_order BETWEEN 9 AND 12 THEN CONCAT('Win with Speed ', image.sort_order - 8)
           WHEN image.sort_order BETWEEN 13 AND 16 THEN CONCAT('Defeat the Boss Within the Time Limit ', image.sort_order - 12)
           WHEN image.sort_order BETWEEN 17 AND 20 THEN CONCAT('Fight the Strongest Digimon ', image.sort_order - 16)
         END
         WHEN 'jp' THEN CASE
           WHEN image.sort_order BETWEEN 1 AND 4 THEN CONCAT('巨大デジ肉を守れ ', image.sort_order)
           WHEN image.sort_order BETWEEN 5 AND 8 THEN CONCAT('最後まで生き残れ ', image.sort_order - 4)
           WHEN image.sort_order BETWEEN 9 AND 12 THEN CONCAT('スピードで勝て ', image.sort_order - 8)
           WHEN image.sort_order BETWEEN 13 AND 16 THEN CONCAT('制限時間内にボスを倒せ ', image.sort_order - 12)
           WHEN image.sort_order BETWEEN 17 AND 20 THEN CONCAT('最強のデジモンと戦え ', image.sort_order - 16)
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
WHERE image.quest_post_id = 6;
