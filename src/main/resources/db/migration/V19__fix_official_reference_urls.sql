-- Replace Game8/GameFAQs fallback links with verified Digimon Encyclopedia URLs.
-- Verification source: each language's official reference/request.php catalog (1,316 rows).
-- 199 fallback records have a capture basename identical to the official directory_name.
UPDATE digimon_kr
SET source_url = CONCAT(
    'https://digimon.net/reference_ko/detail.php?directory_name=',
    SUBSTRING_INDEX(SUBSTRING_INDEX(game_capture_url, '/', -1), '.', 1)
)
WHERE source_url LIKE 'https://game8.co/%'
   OR source_url LIKE 'https://gamefaqs.gamespot.com/%';

-- 31 records use a local filename that differs from the verified official directory_name.
UPDATE digimon_kr
SET source_url = CONCAT(
    'https://digimon.net/reference_ko/detail.php?directory_name=',
    CASE id
        WHEN 1 THEN 'ex-tyranomon'
        WHEN 33 THEN 'triceramon'
        WHEN 45 THEN 'chronomon_holy'
        WHEN 46 THEN 'chronomon_destroy'
        WHEN 98 THEN 'jumbogamemon'
        WHEN 106 THEN 'armagemon'
        WHEN 140 THEN 'weregarrumon'
        WHEN 169 THEN 'jupitermmon_wrath'
        WHEN 171 THEN 'junomon_hysteric'
        WHEN 182 THEN 'metalgarurumon_black'
        WHEN 193 THEN 'ouryumon'
        WHEN 224 THEN 'cerberumon_werewolfmode'
        WHEN 302 THEN 'metalgreymon-v'
        WHEN 327 THEN 'metalgreymon-web'
        WHEN 365 THEN 'xv-mon'
        WHEN 394 THEN 'orgemon'
        WHEN 400 THEN 'earov-dramon'
        WHEN 401 THEN 'okuwamon'
        WHEN 489 THEN 'agumon_kizuna'
        WHEN 490 THEN 'gabumon_kizuna'
        WHEN 494 THEN 'craniummon_enbarrmon'
        WHEN 715 THEN 'weregarurumon_black'
        WHEN 737 THEN 'justimon_blitzarm'
        WHEN 751 THEN 'rapidmon_gold'
        WHEN 757 THEN 'omegamon_zwart'
        WHEN 760 THEN 'gardromon_gold'
        WHEN 766 THEN 'alphamon:ouryuken'
        WHEN 773 THEN 'valkyrimon'
        WHEN 774 THEN 'cherubimon-vice'
        WHEN 777 THEN 'miragegaogamon:burstmode'
        WHEN 915 THEN 'titamon_skullbaluchimon'
    END
)
WHERE id IN (
    1, 33, 45, 46, 98, 106, 140, 169, 171, 182, 193, 224, 302, 327,
    365, 394, 400, 401, 489, 490, 494, 715, 737, 751, 757, 760, 766,
    773, 774, 777, 915
);

-- These game-specific forms have no separate entry in the official catalog.
UPDATE digimon_kr SET source_url = NULL WHERE id IN (189, 767);

-- Correct two pre-existing digimon.net URLs whose directory_name was invalid.
UPDATE digimon_kr
SET source_url = CASE id
    WHEN 129 THEN 'https://digimon.net/reference_ko/detail.php?directory_name=dagomon'
    WHEN 407 THEN 'https://digimon.net/reference_ko/detail.php?directory_name=doruguremon'
END
WHERE id IN (129, 407);

-- The localized official catalogs use the same verified directory_name values.
UPDATE digimon_en en
JOIN digimon_kr kr ON kr.id = en.id
SET en.source_url = CASE
    WHEN kr.source_url IS NULL THEN NULL
    ELSE REPLACE(kr.source_url, 'reference_ko/', 'reference_en/')
END;

UPDATE digimon_jp jp
JOIN digimon_kr kr ON kr.id = jp.id
SET jp.source_url = CASE
    WHEN kr.source_url IS NULL THEN NULL
    ELSE REPLACE(kr.source_url, 'reference_ko/', 'reference/')
END;
