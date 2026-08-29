ALTER TABLE `quest_translation` ADD COLUMN `source_url` varchar(500) NULL AFTER `content`;
ALTER TABLE `quest_image_translation` ADD COLUMN `image_url` varchar(500) NULL AFTER `location_note`;

INSERT INTO `quest_post` (`id`, `title`, `category`, `source_url`, `sort_order`) VALUES
  (2, '꿈인가 현실인가', 'SUB', 'https://bbs.ruliweb.com/game/77673/read/9457708', 20);

INSERT INTO `quest_translation`
    (`quest_post_id`, `language_code`, `title`, `summary`, `content`, `source_url`) VALUES
  (2, 'ko', '꿈인가 현실인가', '사기사카 히로코의 모니카 위치를 이미지 순서대로 확인할 수 있습니다.', NULL, NULL),
  (2, 'en', 'Dreams or Reality', 'Find all 13 Hiroko Sagisaka data monikers scattered across the Digital World.', 'Past and present versions of an area count as separate locations. Follow the locations below to collect every moniker.', 'https://raiderking.com/digimon-story-time-stranger-all-hiroko-sagisaka-moniker-locations/'),
  (2, 'jp', '夢か現実か', '鷺坂ひろこのモニカの場所を画像の順番で確認できます。', NULL, NULL);

INSERT INTO `quest_image` (`quest_post_id`, `image_url`, `sort_order`) VALUES
  (2, '/digimon/captures/quest/dreamsOrReality/1.png', 1),
  (2, '/digimon/captures/quest/dreamsOrReality/2.png', 2),
  (2, '/digimon/captures/quest/dreamsOrReality/3.png', 3),
  (2, '/digimon/captures/quest/dreamsOrReality/4.png', 4),
  (2, '/digimon/captures/quest/dreamsOrReality/5.png', 5),
  (2, '/digimon/captures/quest/dreamsOrReality/6.png', 6),
  (2, '/digimon/captures/quest/dreamsOrReality/7.png', 7),
  (2, '/digimon/captures/quest/dreamsOrReality/8.png', 8),
  (2, '/digimon/captures/quest/dreamsOrReality/9.png', 9),
  (2, '/digimon/captures/quest/dreamsOrReality/10.png', 10),
  (2, '/digimon/captures/quest/dreamsOrReality/11.png', 11),
  (2, '/digimon/captures/quest/dreamsOrReality/12.png', 12),
  (2, '/digimon/captures/quest/dreamsOrReality/13.png', 13);

INSERT INTO `quest_image_translation`
    (`quest_image_id`, `quest_post_id`, `language_code`, `location_name`, `location_note`, `image_url`)
SELECT image.id, image.quest_post_id, language.language_code,
       CASE language.language_code
         WHEN 'ko' THEN CONCAT('모니카 위치 ', LPAD(image.sort_order, 2, '0'))
         WHEN 'en' THEN CASE image.sort_order
           WHEN 1 THEN 'Central Tower: Steel Point (8 Years Ago)'
           WHEN 2 THEN 'Factorial Area (8 Years Ago)'
           WHEN 3 THEN 'Guardian Palace (8 Years Ago)'
           WHEN 4 THEN 'Gear Forest: Sanctuary (8 Years Ago)'
           WHEN 5 THEN 'Abyss Depths (8 Years Ago)'
           WHEN 6 THEN 'Rebellion Village'
           WHEN 7 THEN 'Dark Garden'
           WHEN 8 THEN 'Central Town: Spiral Waterway (Present Day)'
           WHEN 9 THEN 'Chill Cosmic: Throne Room'
           WHEN 10 THEN 'Gear Forest: Floating Island Plains (Present Day)'
           WHEN 11 THEN 'Abyss Sanctuary (Present Day)'
           WHEN 12 THEN 'Factorial Core (Present Day)'
           WHEN 13 THEN 'Guardian Palace: Throne Room (Present Day)'
         END
         WHEN 'jp' THEN CONCAT('モニカの場所 ', LPAD(image.sort_order, 2, '0'))
       END,
       CASE language.language_code
         WHEN 'ko' THEN CASE image.sort_order
           WHEN 5 THEN '고래몬을 소라고동으로 불러서 심해맵 7시'
           WHEN 6 THEN '현대 리벨리온 마을'
           WHEN 7 THEN '현대 다크가든 플루토몬 신전 입구'
           WHEN 8 THEN '우가몬 삼총사 있던 장소'
           WHEN 9 THEN '디아나몬 있던 곳의 뒤편'
           WHEN 10 THEN '기어 포레스트 로코몬역'
           WHEN 11 THEN '어비스 심해맵 감옥'
           WHEN 12 THEN '코쿠와랜드 티켓을 구매해 불카누스의 공방으로 들어가야 함'
           WHEN 13 THEN '벨제붑이 있는 장소에 서 있음'
           ELSE NULL
         END
         WHEN 'en' THEN CASE image.sort_order
           WHEN 1 THEN 'Return to the Parrotmon battle area. Hiroko is near the lower elevator, looking over the edge.'
           WHEN 2 THEN 'Continue toward the end of the main Factorial Area dungeon.'
           WHEN 3 THEN 'At Junomon''s Palace entrance, check the platform to the right of the entrance walkway.'
           WHEN 4 THEN 'Hiroko is lying in the middle of the sanctuary room.'
           WHEN 5 THEN 'Check the bottom-left corner of the dungeon, where Hiroko is taking a selfie.'
           WHEN 6 THEN 'Climb the ladder in Rebellion Village; Hiroko is visible immediately.'
           WHEN 7 THEN 'Hiroko is sitting outside Plutomon''s throne room.'
           WHEN 8 THEN 'Go to the room at the bottom of the waterway and check its upper-left corner.'
           WHEN 9 THEN 'Return to the throne room and look directly behind the throne.'
           WHEN 10 THEN 'Backtrack to the platform where Locomon''s train was during the first visit.'
           WHEN 11 THEN 'Go to the prison in the upper-right corner of the present-day Abyss dungeon.'
           WHEN 12 THEN 'Enter Vulcanusmon''s Forge and look beside the blacksmith statue.'
           WHEN 13 THEN 'At Junomon''s throne, find Hiroko admiring the priestess.'
         END
         WHEN 'jp' THEN CASE image.sort_order
           WHEN 5 THEN 'ホエーモンをほら貝で呼び、深海マップの7時方向'
           WHEN 6 THEN '現代のリベリオン村'
           WHEN 7 THEN '現代ダークガーデンのプルートモン神殿入口'
           WHEN 8 THEN 'オーガモン三人組がいた場所'
           WHEN 9 THEN 'ディアナモンがいた場所の裏側'
           WHEN 10 THEN 'ギアフォレスト・ロコモン駅'
           WHEN 11 THEN 'アビス深海マップの牢獄'
           WHEN 12 THEN 'コクワランドのチケットを購入し、ヴォルカヌスモンの工房へ入る'
           WHEN 13 THEN 'ベルゼブモンがいる場所に立つ'
           ELSE NULL
         END
       END,
       CASE WHEN language.language_code = 'en'
         THEN CONCAT('/digimon/captures/quest/dreamsOrReality/', image.sort_order, '_en.jpg')
         ELSE NULL
       END
FROM `quest_image` image
CROSS JOIN (
  SELECT 'ko' AS language_code
  UNION ALL SELECT 'en'
  UNION ALL SELECT 'jp'
) language
WHERE image.quest_post_id = 2;
