UPDATE `quest_translation`
SET `content` = CASE `language_code`
  WHEN 'ko' THEN '해당 위치에서 아우터 던전이 보이지 않으면 디지어택으로 상호작용하세요.'
  WHEN 'en' THEN 'If the Outer Dungeon is not visible at the listed location, use DigiAttack to interact with it.'
  WHEN 'jp' THEN '該当する場所でアウターダンジョンが見えない場合は、デジアタックでインタラクトしてください。'
END
WHERE `quest_post_id` = 6;

UPDATE `quest_image_translation` translation
JOIN `quest_image` image ON image.id = translation.quest_image_id
SET translation.location_note = CASE translation.language_code
  WHEN 'ko' THEN CASE image.sort_order
    WHEN 1 THEN '기어 포레스트의 마을'
    WHEN 2 THEN '기묘한 지하도'
    WHEN 3 THEN '다크 캐슬'
    WHEN 4 THEN '칠 코즈믹 옥좌의 방'
    WHEN 5 THEN '신주쿠 어느 뒷골목'
    WHEN 6 THEN '토에이마루노나카선 신주쿠역'
    WHEN 7 THEN '어비스 생크추어리 · 현대 전용'
    WHEN 8 THEN '히트 코즈믹 화염 에어리어'
    WHEN 9 THEN '팩토리얼 에어리어'
    WHEN 10 THEN '팩토리얼 타운'
    WHEN 11 THEN '어비스 비치 · 현대 전용'
    WHEN 12 THEN '하이라이즈 콜로세움'
    WHEN 13 THEN '아키하바라 어느 뒷골목'
    WHEN 14 THEN '어비스 비치'
    WHEN 15 THEN '신주쿠 동쪽 상점가'
    WHEN 16 THEN '기어 포레스트 나무 그늘 부두'
    WHEN 17 THEN '센트럴 타운 나선형 지하 수로'
    WHEN 18 THEN '기어 포레스트 숲의 나무 그늘'
    WHEN 19 THEN '센트럴 다운타운'
    WHEN 20 THEN '칠 코즈믹 초승달 에어리어'
  END
  WHEN 'en' THEN CASE image.sort_order
    WHEN 1 THEN 'Gear Forest Village'
    WHEN 2 THEN 'Mysterious Underground Passage'
    WHEN 3 THEN 'Dark Castle'
    WHEN 4 THEN 'Chill Cosmic Throne Room'
    WHEN 5 THEN 'A Back Alley in Shinjuku'
    WHEN 6 THEN 'Toei Marunouchi Line Shinjuku Station'
    WHEN 7 THEN 'Abyss Sanctuary · Present Day only'
    WHEN 8 THEN 'Heat Cosmic Flame Area'
    WHEN 9 THEN 'Factorial Area'
    WHEN 10 THEN 'Factorial Town'
    WHEN 11 THEN 'Abyss Beach · Present Day only'
    WHEN 12 THEN 'Highrise Colosseum'
    WHEN 13 THEN 'A Back Alley in Akihabara'
    WHEN 14 THEN 'Abyss Beach'
    WHEN 15 THEN 'Shinjuku East Shopping District'
    WHEN 16 THEN 'Gear Forest Shaded Pier'
    WHEN 17 THEN 'Central Town Spiral Underground Waterway'
    WHEN 18 THEN 'Gear Forest Shaded Woods'
    WHEN 19 THEN 'Central Downtown'
    WHEN 20 THEN 'Chill Cosmic Crescent Area'
  END
  WHEN 'jp' THEN CASE image.sort_order
    WHEN 1 THEN 'ギア・フォレストの村'
    WHEN 2 THEN '奇妙な地下道'
    WHEN 3 THEN 'ダークキャッスル'
    WHEN 4 THEN 'チル・コズミック 玉座の間'
    WHEN 5 THEN '新宿のとある路地裏'
    WHEN 6 THEN '都営丸ノ内線 新宿駅'
    WHEN 7 THEN 'アビス・サンクチュアリ · 現代限定'
    WHEN 8 THEN 'ヒート・コズミック 炎エリア'
    WHEN 9 THEN 'ファクトリアル・エリア'
    WHEN 10 THEN 'ファクトリアル・タウン'
    WHEN 11 THEN 'アビス・ビーチ · 現代限定'
    WHEN 12 THEN 'ハイライズ・コロシアム'
    WHEN 13 THEN '秋葉原のとある路地裏'
    WHEN 14 THEN 'アビス・ビーチ'
    WHEN 15 THEN '新宿東ショッピング街'
    WHEN 16 THEN 'ギア・フォレスト 木陰の船着き場'
    WHEN 17 THEN 'セントラルタウン 螺旋地下水路'
    WHEN 18 THEN 'ギア・フォレスト 森の木陰'
    WHEN 19 THEN 'セントラル・ダウンタウン'
    WHEN 20 THEN 'チル・コズミック 三日月エリア'
  END
END
WHERE image.quest_post_id = 6;
