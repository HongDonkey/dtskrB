INSERT INTO `quest_image_translation`
    (`quest_image_id`, `quest_post_id`, `language_code`, `location_name`, `location_note`)
SELECT image.id,
       image.quest_post_id,
       'jp',
       CASE image.sort_order
         WHEN 1 THEN '東新宿ビジョン広場'
         WHEN 2 THEN '東新宿タカス通り交差点'
         WHEN 3 THEN '新宿地下街サブロード'
         WHEN 4 THEN '東京メトロ丸ノ内線 新宿駅'
         WHEN 5 THEN '東新宿タカス通り交差点 カフェ'
         WHEN 6 THEN '西新宿鉄道橋交差点'
         WHEN 7 THEN '新宿の路地裏'
         WHEN 8 THEN '新宿駅東口改札'
         WHEN 9 THEN '新宿地下散歩道'
         WHEN 10 THEN '秋葉原駅電気街口南側出口'
         WHEN 11 THEN '秋葉原の路地裏'
         WHEN 12 THEN '東京都庁中央本庁舎'
         WHEN 13 THEN '新宿公園 滝広場'
         WHEN 14 THEN '新宿東口商店街'
         WHEN 15 THEN '新宿地下水路 北ブロック'
         WHEN 16 THEN '新宿地下水路 南ブロック'
         WHEN 17 THEN '新宿地下貯水槽'
       END,
       CASE image.sort_order
         WHEN 5 THEN '北側のカフェ'
         WHEN 15 THEN '水路入口：新宿の路地裏北東側'
         WHEN 16 THEN '東京都庁中央本庁舎から西へ進入'
         ELSE NULL
       END
FROM `quest_image` image
WHERE image.quest_post_id = 1
ON DUPLICATE KEY UPDATE
  `quest_post_id` = VALUES(`quest_post_id`),
  `location_name` = VALUES(`location_name`),
  `location_note` = VALUES(`location_note`);
