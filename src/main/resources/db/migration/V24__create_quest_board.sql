CREATE TABLE `quest_post` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `slug` varchar(100) NOT NULL,
  `category` varchar(30) NOT NULL,
  `source_url` varchar(500) NULL,
  `is_published` tinyint(1) NOT NULL DEFAULT 1,
  `sort_order` int NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_quest_post_slug` (`slug`),
  KEY `idx_quest_post_published_sort` (`is_published`, `sort_order`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `quest_translation` (
  `quest_post_id` bigint unsigned NOT NULL,
  `language_code` varchar(5) NOT NULL,
  `title` varchar(200) NOT NULL,
  `summary` varchar(500) NOT NULL,
  `content` text NULL,
  PRIMARY KEY (`quest_post_id`, `language_code`),
  CONSTRAINT `fk_quest_translation_post`
    FOREIGN KEY (`quest_post_id`) REFERENCES `quest_post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_quest_translation_language`
    CHECK (`language_code` IN ('ko', 'en', 'jp'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `quest_image` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `quest_post_id` bigint unsigned NOT NULL,
  `image_url` varchar(500) NOT NULL,
  `sort_order` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_quest_image_post_sort` (`quest_post_id`, `sort_order`),
  CONSTRAINT `fk_quest_image_post`
    FOREIGN KEY (`quest_post_id`) REFERENCES `quest_post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `quest_image_translation` (
  `quest_image_id` bigint unsigned NOT NULL,
  `language_code` varchar(5) NOT NULL,
  `location_name` varchar(200) NOT NULL,
  `location_note` varchar(300) NULL,
  PRIMARY KEY (`quest_image_id`, `language_code`),
  CONSTRAINT `fk_quest_image_translation_image`
    FOREIGN KEY (`quest_image_id`) REFERENCES `quest_image` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_quest_image_translation_language`
    CHECK (`language_code` IN ('ko', 'en', 'jp'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `quest_post` (`id`, `slug`, `category`, `source_url`, `sort_order`)
VALUES (1, 'all-eyes-on-me', 'SOUL', 'https://bbs.ruliweb.com/game/77673/read/9457703', 10);

INSERT INTO `quest_translation` (`quest_post_id`, `language_code`, `title`, `summary`, `content`) VALUES
  (1, 'ko', 'All Eyes on Me - 에테몬의 소울 찾기 위치', '에테몬의 소울을 찾는 순서와 위치를 이미지로 확인할 수 있습니다.', '해당 퀘스트는 8년 전 현실세계에서만 수주하고 공략할 수 있습니다.'),
  (1, 'en', 'All Eyes on Me - Etemon Soul Locations', 'Follow the screenshots to find Etemon''s Souls in order.', 'This quest can only be accepted and completed in the Real World eight years ago.'),
  (1, 'jp', 'All Eyes on Me - エテモンのソウルの場所', '画像を見ながらエテモンのソウルの場所を順番に確認できます。', 'このクエストは8年前の現実世界でのみ受注・攻略できます。');

INSERT INTO `quest_image` (`quest_post_id`, `image_url`, `sort_order`) VALUES
  (1, '/digimon/captures/quest/alleyesonme/1.png', 1),
  (1, '/digimon/captures/quest/alleyesonme/2.png', 2),
  (1, '/digimon/captures/quest/alleyesonme/3.png', 3),
  (1, '/digimon/captures/quest/alleyesonme/4.png', 4),
  (1, '/digimon/captures/quest/alleyesonme/5.png', 5),
  (1, '/digimon/captures/quest/alleyesonme/6.png', 6),
  (1, '/digimon/captures/quest/alleyesonme/7.png', 7),
  (1, '/digimon/captures/quest/alleyesonme/8.png', 8),
  (1, '/digimon/captures/quest/alleyesonme/9.png', 9),
  (1, '/digimon/captures/quest/alleyesonme/10.png', 10),
  (1, '/digimon/captures/quest/alleyesonme/11.png', 11),
  (1, '/digimon/captures/quest/alleyesonme/12.png', 12),
  (1, '/digimon/captures/quest/alleyesonme/13.png', 13),
  (1, '/digimon/captures/quest/alleyesonme/14.png', 14),
  (1, '/digimon/captures/quest/alleyesonme/15.png', 15),
  (1, '/digimon/captures/quest/alleyesonme/16.png', 16),
  (1, '/digimon/captures/quest/alleyesonme/17.png', 17);

INSERT INTO `quest_image_translation`
    (`quest_image_id`, `language_code`, `location_name`, `location_note`)
SELECT id, 'ko',
       CASE sort_order
         WHEN 1 THEN '히가시신주쿠 비전광장'
         WHEN 2 THEN '히가시신주쿠 타카스 가도 교차점'
         WHEN 3 THEN '신주쿠 지하상가 서브로드'
         WHEN 4 THEN '토에이마루노나카선 신주쿠역'
         WHEN 5 THEN '히가시신주쿠 타카스가도 교차점 카페'
         WHEN 6 THEN '니시신주쿠 철도교 교차점'
         WHEN 7 THEN '신주쿠 어느 뒷골목'
         WHEN 8 THEN '신주쿠역 동쪽 개찰구'
         WHEN 9 THEN '신주쿠 지하 산책로'
         WHEN 10 THEN '아키하바라역 전자거리 남쪽 출구'
         WHEN 11 THEN '아키하바라 어느 뒷골목'
         WHEN 12 THEN '도쿄도청 중앙본청사'
         WHEN 13 THEN '신주쿠 공원 폭포 광장'
         WHEN 14 THEN '신주쿠 동쪽 상점가'
         WHEN 15 THEN '신주쿠 지하수로 북쪽 블록'
         WHEN 16 THEN '신주쿠 지하수로 남쪽 블록'
         WHEN 17 THEN '신주쿠 지하 저수조'
       END,
       CASE sort_order
         WHEN 1 THEN '버스킹 공연중'
         WHEN 2 THEN '만남의 광장 랜드마크'
         WHEN 3 THEN '놀라울 만큼 그 누구도 관심을 주지 않았다'
         WHEN 4 THEN '노숙중'
         WHEN 5 THEN '에스프레소가 왜 이렇게 써!!'
         WHEN 6 THEN '콘크리트의 원숭이'
         WHEN 7 THEN '섹시도발'
         WHEN 8 THEN '팬미팅중'
         WHEN 9 THEN '길막중'
         WHEN 10 THEN '모에모에큥'
         WHEN 11 THEN '대낮까지 달린 에테몬'
         WHEN 12 THEN '도청 앞에서 불법 시위중'
         WHEN 13 THEN '나는 원숭이로소이다'
         WHEN 14 THEN '평범하게 장을 보고 있던 에테몬'
         WHEN 15 THEN '목욕중'
         WHEN 16 THEN '쓰레기봉투에 화풀이중'
         WHEN 17 THEN '나홀로 공연'
       END
FROM `quest_image`
WHERE `quest_post_id` = 1;
