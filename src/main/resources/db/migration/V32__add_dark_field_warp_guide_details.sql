ALTER TABLE `quest_image_translation`
  MODIFY COLUMN `location_note` text NULL;

UPDATE `quest_image_translation` translation
JOIN `quest_image` image ON image.id = translation.quest_image_id
SET translation.location_note = CASE translation.language_code
  WHEN 'ko' THEN CASE image.sort_order
    WHEN 1 THEN JSON_OBJECT('before', '20% · 30% · 20%', 'after', '120% · 160% · 130% · 180%', 'drop', '10~30 랜덤으로 획득', 'extra', NULL, 'chestRoute', '1→2→2→2→1→2', 'shortestRoute', '1→1→1→2', 'returnRule', '미표기 숫자는 처음으로 복귀')
    WHEN 2 THEN JSON_OBJECT('before', '20% · 30% · 20%', 'after', '120% · 160% · 130% · 45%', 'drop', '10~30 랜덤으로 획득', 'extra', '피에몬 5%', 'chestRoute', '1→1→2→1→2→2→2→1→1→1→2→1→1→2', 'shortestRoute', '1→2→1→1→2', 'returnRule', '미표기 숫자는 처음으로 복귀')
  END
  WHEN 'en' THEN CASE image.sort_order
    WHEN 1 THEN JSON_OBJECT('before', '20% · 30% · 20%', 'after', '120% · 160% · 130% · 180%', 'drop', 'Obtain 10–30 at random', 'extra', NULL, 'chestRoute', '1→2→2→2→1→2', 'shortestRoute', '1→1→1→2', 'returnRule', 'An unmarked choice returns you to the beginning')
    WHEN 2 THEN JSON_OBJECT('before', '20% · 30% · 20%', 'after', '120% · 160% · 130% · 45%', 'drop', 'Obtain 10–30 at random', 'extra', 'Piedmon 5%', 'chestRoute', '1→1→2→1→2→2→2→1→1→1→2→1→1→2', 'shortestRoute', '1→2→1→1→2', 'returnRule', 'An unmarked choice returns you to the beginning')
  END
  WHEN 'jp' THEN CASE image.sort_order
    WHEN 1 THEN JSON_OBJECT('before', '20% · 30% · 20%', 'after', '120% · 160% · 130% · 180%', 'drop', '10～30をランダムで獲得', 'extra', NULL, 'chestRoute', '1→2→2→2→1→2', 'shortestRoute', '1→1→1→2', 'returnRule', '数字のない選択肢は最初に戻る')
    WHEN 2 THEN JSON_OBJECT('before', '20% · 30% · 20%', 'after', '120% · 160% · 130% · 45%', 'drop', '10～30をランダムで獲得', 'extra', 'ピエモン 5%', 'chestRoute', '1→1→2→1→2→2→2→1→1→1→2→1→1→2', 'shortestRoute', '1→2→1→1→2', 'returnRule', '数字のない選択肢は最初に戻る')
  END
END
WHERE image.quest_post_id = 5;
