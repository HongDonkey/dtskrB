-- Correct the non-official links not covered by V19.
UPDATE digimon_kr
SET source_url = CASE id
    WHEN 242 THEN 'https://digimon.net/reference_ko/detail.php?directory_name=capromon'
    WHEN 677 THEN 'https://digimon.net/reference_ko/detail.php?directory_name=kaisergreymon'
    WHEN 678 THEN 'https://digimon.net/reference_ko/detail.php?directory_name=magnagarurumon'
END
WHERE id IN (242, 677, 678);

-- MagnaGarurumon (Separated) has no separate official encyclopedia entry.
UPDATE digimon_kr SET source_url = NULL WHERE id = 675;

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
