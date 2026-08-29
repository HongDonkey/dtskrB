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

INSERT INTO `quest_post` (`id`, `title`, `category`, `source_url`, `sort_order`) VALUES
  (3, '조용한 뒤틀림', 'SUB', 'https://gall.dcinside.com/mgallery/board/view/?id=digimontime&no=3256&search_head=10&page=3', 30);

INSERT INTO `quest_translation`
    (`quest_post_id`, `language_code`, `title`, `summary`, `content`, `source_url`) VALUES
  (3, 'ko', '조용한 뒤틀림', '현대 신주쿠와 아키하바라에 나타난 쿠라몬 13마리의 위치를 확인할 수 있습니다.', '각 이미지의 지역명과 지도에 표시된 위치를 따라 쿠라몬을 쓰러뜨리고 코어 파편을 모두 모으세요.', NULL),
  (3, 'en', 'Quiet Distortion', 'Find the 13 Kuramon appearing across present-day Shinjuku and Akihabara.', 'Follow each area name and map marker to defeat every Kuramon and collect all of the core fragments.', NULL),
  (3, 'jp', '静かな歪み', '現代の新宿と秋葉原に現れたクラモン13体の場所を確認できます。', '各画像のエリア名とマップ上の位置をたどり、クラモンを倒してコアの欠片をすべて集めてください。', NULL);

INSERT INTO `quest_image` (`quest_post_id`, `image_url`, `sort_order`) VALUES
  (3, '/digimon/captures/quest/quietDistortion/1.jpg', 1),
  (3, '/digimon/captures/quest/quietDistortion/2.jpg', 2),
  (3, '/digimon/captures/quest/quietDistortion/3.jpg', 3),
  (3, '/digimon/captures/quest/quietDistortion/4.jpg', 4),
  (3, '/digimon/captures/quest/quietDistortion/5.jpg', 5),
  (3, '/digimon/captures/quest/quietDistortion/6.jpg', 6),
  (3, '/digimon/captures/quest/quietDistortion/7.jpg', 7),
  (3, '/digimon/captures/quest/quietDistortion/8.jpg', 8),
  (3, '/digimon/captures/quest/quietDistortion/9.jpg', 9),
  (3, '/digimon/captures/quest/quietDistortion/10.jpg', 10),
  (3, '/digimon/captures/quest/quietDistortion/11.jpg', 11),
  (3, '/digimon/captures/quest/quietDistortion/12.jpg', 12),
  (3, '/digimon/captures/quest/quietDistortion/13.jpg', 13);

INSERT INTO `quest_image_translation`
    (`quest_image_id`, `quest_post_id`, `language_code`, `location_name`, `location_note`, `image_url`)
SELECT image.id, image.quest_post_id, language.language_code,
       CASE language.language_code
         WHEN 'ko' THEN CASE image.sort_order
           WHEN 1 THEN '니시신주쿠 철도교 교차점'
           WHEN 2 THEN '신주쿠 가부키초 극장 광장'
           WHEN 3 THEN '신주쿠 동쪽 상점가'
           WHEN 4 THEN '신주쿠 어느 뒷골목'
           WHEN 5 THEN '신주쿠역 동쪽 개찰구'
           WHEN 6 THEN '신주쿠 지하 산책로'
           WHEN 7 THEN '신주쿠 지하상가 서브로드'
           WHEN 8 THEN '아키하바라 어느 뒷골목'
           WHEN 9 THEN '아키하바라역 전자거리 남쪽 출구'
           WHEN 10 THEN '토에이마루노나카선 신주쿠역'
           WHEN 11 THEN '희망의 벽'
           WHEN 12 THEN '히가시신주쿠 비전 광장'
           WHEN 13 THEN '히가시신주쿠 타카스 가도 교차점'
         END
         WHEN 'en' THEN CASE image.sort_order
           WHEN 1 THEN 'Nishi-Shinjuku Railway Bridge Intersection'
           WHEN 2 THEN 'Shinjuku Kabukicho Theater Square'
           WHEN 3 THEN 'Shinjuku East Shopping District'
           WHEN 4 THEN 'A Back Alley in Shinjuku'
           WHEN 5 THEN 'Shinjuku Station East Exit Ticket Gate'
           WHEN 6 THEN 'Shinjuku Underground Walkway'
           WHEN 7 THEN 'Shinjuku Underground Mall Subroad'
           WHEN 8 THEN 'A Back Alley in Akihabara'
           WHEN 9 THEN 'Akihabara Station Electric Town South Exit'
           WHEN 10 THEN 'Toei Marunouchi Line Shinjuku Station'
           WHEN 11 THEN 'Wall of Hope'
           WHEN 12 THEN 'Higashi-Shinjuku Vision Square'
           WHEN 13 THEN 'Higashi-Shinjuku Takasu Avenue Intersection'
         END
         WHEN 'jp' THEN CASE image.sort_order
           WHEN 1 THEN '西新宿鉄道橋交差点'
           WHEN 2 THEN '新宿歌舞伎町劇場広場'
           WHEN 3 THEN '新宿東口商店街'
           WHEN 4 THEN '新宿のとある裏路地'
           WHEN 5 THEN '新宿駅東口改札'
           WHEN 6 THEN '新宿地下歩道'
           WHEN 7 THEN '新宿地下街サブロード'
           WHEN 8 THEN '秋葉原のとある裏路地'
           WHEN 9 THEN '秋葉原駅電気街南口'
           WHEN 10 THEN '都営丸ノ内線新宿駅'
           WHEN 11 THEN '希望の壁'
           WHEN 12 THEN '東新宿ビジョン広場'
           WHEN 13 THEN '東新宿タカス街道交差点'
         END
       END,
       CASE language.language_code
         WHEN 'ko' THEN CASE image.sort_order
           WHEN 1 THEN '철도교 교차점 안쪽 통로'
           WHEN 2 THEN '극장 광장 중앙'
           WHEN 3 THEN '동쪽 상점가 교차로'
           WHEN 4 THEN '뒷골목 안쪽 공터'
           WHEN 5 THEN '동쪽 개찰구 앞'
           WHEN 6 THEN '10~14번 출구 방향 통로'
           WHEN 7 THEN '서브로드 중앙 통로'
           WHEN 8 THEN '뒷골목 입구 왼쪽'
           WHEN 9 THEN '전자거리 남쪽 출구 광장'
           WHEN 10 THEN '신주쿠역 개찰구 통로'
           WHEN 11 THEN '희망의 벽 중앙'
           WHEN 12 THEN '비전 광장 화단 옆'
           WHEN 13 THEN '타카스 가도 교차점 인도'
         END
         WHEN 'en' THEN CASE image.sort_order
           WHEN 1 THEN 'Inside the railway bridge intersection passage'
           WHEN 2 THEN 'In the middle of the theater square'
           WHEN 3 THEN 'At the east shopping district intersection'
           WHEN 4 THEN 'In the open area at the back of the alley'
           WHEN 5 THEN 'In front of the east ticket gate'
           WHEN 6 THEN 'Along the passage toward exits 10–14'
           WHEN 7 THEN 'In the center passage of Subroad'
           WHEN 8 THEN 'To the left of the alley entrance'
           WHEN 9 THEN 'In the plaza outside the Electric Town South Exit'
           WHEN 10 THEN 'Along the Shinjuku Station ticket gate passage'
           WHEN 11 THEN 'In the center of the Wall of Hope'
           WHEN 12 THEN 'Beside the planter in Vision Square'
           WHEN 13 THEN 'On the sidewalk at the Takasu Avenue intersection'
         END
         WHEN 'jp' THEN CASE image.sort_order
           WHEN 1 THEN '鉄道橋交差点の内側通路'
           WHEN 2 THEN '劇場広場の中央'
           WHEN 3 THEN '東口商店街の交差点'
           WHEN 4 THEN '裏路地の奥にある空き地'
           WHEN 5 THEN '東口改札の前'
           WHEN 6 THEN '10～14番出口方面の通路'
           WHEN 7 THEN 'サブロード中央通路'
           WHEN 8 THEN '裏路地入口の左側'
           WHEN 9 THEN '電気街南口前の広場'
           WHEN 10 THEN '新宿駅改札通路'
           WHEN 11 THEN '希望の壁の中央'
           WHEN 12 THEN 'ビジョン広場の植え込み横'
           WHEN 13 THEN 'タカス街道交差点の歩道'
         END
       END,
       NULL
FROM `quest_image` image
CROSS JOIN (
  SELECT 'ko' AS language_code
  UNION ALL SELECT 'en'
  UNION ALL SELECT 'jp'
) language
WHERE image.quest_post_id = 3;

INSERT INTO `quest_post` (`id`, `title`, `category`, `source_url`, `sort_order`) VALUES
  (4, '깨끗한 물 찾기', 'SUB', 'https://raiderking.com/digimon-story-time-stranger-where-to-find-clean-seawater-marineangemon-sidequest/', 40);

INSERT INTO `quest_translation`
    (`quest_post_id`, `language_code`, `title`, `summary`, `content`, `source_url`) VALUES
  (4, 'ko', '깨끗한 물 찾기', '마린엔젤몬에게 가져다줄 깨끗한 바닷물의 정확한 채집 위치를 확인할 수 있습니다.', '어비스 해변의 대부분 채집 지점에서는 이상한 바닷물을 얻게 됩니다. 지도의 왼쪽 위, 낮은 지대에 있는 커다란 황금색 기어 위에서 깨끗한 바닷물을 채집하세요.', NULL),
  (4, 'en', 'Finding Clean Water', 'Find the correct place to gather clean seawater for MarineAngemon.', 'Most gathering points at Abyss Beach give Funky Seawater. Go to the upper-left of the map and collect water from the large golden gear on the lower ledge.', 'https://raiderking.com/digimon-story-time-stranger-where-to-find-clean-seawater-marineangemon-sidequest/'),
  (4, 'jp', 'きれいな水探し', 'マリンエンジェモンに渡すきれいな海水の正しい採取場所を確認できます。', 'アビス海岸のほとんどの採取地点では変な海水を入手します。マップ左上の低い場所にある大きな金色の歯車で、きれいな海水を採取してください。', NULL);

INSERT INTO `quest_image` (`quest_post_id`, `image_url`, `sort_order`) VALUES
  (4, '/digimon/captures/quest/cleanWater/1.jpg', 1),
  (4, '/digimon/captures/quest/cleanWater/2.jpg', 2);

INSERT INTO `quest_image_translation`
    (`quest_image_id`, `quest_post_id`, `language_code`, `location_name`, `location_note`, `image_url`)
SELECT image.id, image.quest_post_id, language.language_code,
       CASE language.language_code
         WHEN 'ko' THEN CASE image.sort_order
           WHEN 1 THEN '마린엔젤몬 퀘스트 수주 위치'
           WHEN 2 THEN '어비스 해변(현대)'
         END
         WHEN 'en' THEN CASE image.sort_order
           WHEN 1 THEN 'Warrior''s Watering Hole'
           WHEN 2 THEN 'Abyss Surface (Present Day)'
         END
         WHEN 'jp' THEN CASE image.sort_order
           WHEN 1 THEN '戦士の水飲み場'
           WHEN 2 THEN 'アビス地上（現代）'
         END
       END,
       CASE language.language_code
         WHEN 'ko' THEN CASE image.sort_order
           WHEN 1 THEN '메인 스토리 「저승의 왕좌」 진행 중 마린엔젤몬에게 말을 겁니다.'
           WHEN 2 THEN '지도 왼쪽 위의 낮은 지대로 내려가 커다란 황금색 기어에서 물을 채집합니다.'
         END
         WHEN 'en' THEN CASE image.sort_order
           WHEN 1 THEN 'Talk to MarineAngemon during the main story quest “Throne of the Underworld.”'
           WHEN 2 THEN 'Descend to the lower ledge in the upper-left of the map and gather water from the large golden gear.'
         END
         WHEN 'jp' THEN CASE image.sort_order
           WHEN 1 THEN 'メインストーリー「冥界の玉座」の進行中にマリンエンジェモンに話しかけます。'
           WHEN 2 THEN 'マップ左上の低い足場へ降り、大きな金色の歯車から水を採取します。'
         END
       END,
       NULL
FROM `quest_image` image
CROSS JOIN (
  SELECT 'ko' AS language_code
  UNION ALL SELECT 'en'
  UNION ALL SELECT 'jp'
) language
WHERE image.quest_post_id = 4;
