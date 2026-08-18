CREATE TABLE digimon_stage_en LIKE digimon_stage;
CREATE TABLE item_en LIKE item_kr;
CREATE TABLE digimon_en LIKE digimon_kr;
CREATE TABLE evolution_en LIKE evolution_kr;
CREATE TABLE digimon_special_skill_en LIKE digimon_special_skill_kr;
CREATE TABLE digimon_attachment_skill_en LIKE digimon_attachment_skill_kr;

ALTER TABLE digimon_en
    ADD CONSTRAINT fk_digimon_stage_en
        FOREIGN KEY (stage_code) REFERENCES digimon_stage_en(stage_code);

ALTER TABLE evolution_en
    ADD CONSTRAINT fk_evolution_from_en
        FOREIGN KEY (from_digimon_id) REFERENCES digimon_en(id),
    ADD CONSTRAINT fk_evolution_to_en
        FOREIGN KEY (to_digimon_id) REFERENCES digimon_en(id),
    ADD CONSTRAINT fk_evolution_item_en
        FOREIGN KEY (required_item_id) REFERENCES item_en(id);

ALTER TABLE digimon_special_skill_en
    ADD CONSTRAINT fk_special_skill_digimon_en
        FOREIGN KEY (digimon_id) REFERENCES digimon_en(id);

ALTER TABLE digimon_attachment_skill_en
    ADD CONSTRAINT fk_attachment_skill_digimon_en
        FOREIGN KEY (digimon_id) REFERENCES digimon_en(id);

INSERT INTO digimon_stage_en (stage_code, stage_name, sort_order) VALUES
    ('BABY_1', 'Baby I', 10),
    ('BABY_2', 'Baby II', 20),
    ('ROOKIE', 'Rookie', 30),
    ('CHAMPION', 'Champion', 40),
    ('ULTIMATE', 'Ultimate', 50),
    ('MEGA', 'Mega', 60),
    ('MEGA_PLUS', 'Ultra', 70),
    ('ARMOR', 'Armor', 80),
    ('HYBRID', 'Hybrid', 90);

INSERT INTO item_en (id, name, description, image_url, created_at, updated_at)
SELECT id, name, description, image_url, created_at, updated_at
FROM item_kr;

INSERT INTO digimon_en (
    id, name, stage_code, attribute, digimon_type, personality,
    image_url, pixel_image_url, game_capture_url, skill_image_url,
    description, evolution_condition, source_url, created_at, updated_at
)
SELECT
    id,
    CASE SUBSTRING_INDEX(SUBSTRING_INDEX(game_capture_url, '/', -1), '.', 1)
        WHEN 'nanomon' THEN 'Datamon'
        WHEN 'tailmon' THEN 'Gatomon'
        WHEN 'plotmon' THEN 'Salamon'
        WHEN 'vamdemon' THEN 'Myotismon'
        WHEN 'holydramon' THEN 'Magnadramon'
        WHEN 'dukemon' THEN 'Gallantmon'
        WHEN 'omegamon' THEN 'Omnimon'
        WHEN 'lordknightmon' THEN 'Crusadermon'
        WHEN 'mugendramon' THEN 'Machinedramon'
        ELSE CONCAT(
            UPPER(LEFT(SUBSTRING_INDEX(SUBSTRING_INDEX(game_capture_url, '/', -1), '.', 1), 1)),
            SUBSTRING(SUBSTRING_INDEX(SUBSTRING_INDEX(game_capture_url, '/', -1), '.', 1), 2)
        )
    END,
    stage_code,
    CASE attribute
        WHEN '백신' THEN 'Vaccine'
        WHEN '데이터' THEN 'Data'
        WHEN '바이러스' THEN 'Virus'
        WHEN '프리' THEN 'Free'
        WHEN '베리어블' THEN 'Variable'
        WHEN '불명' THEN 'Unknown'
        ELSE attribute
    END,
    digimon_type,
    CASE personality
        WHEN '계시' THEN 'Revelation'
        WHEN '과보호' THEN 'Overprotective'
        WHEN '기회주의자' THEN 'Opportunist'
        WHEN '대담' THEN 'Bold'
        WHEN '만용' THEN 'Reckless'
        WHEN '사교적' THEN 'Sociable'
        WHEN '열혈' THEN 'Passionate'
        WHEN '잔머리' THEN 'Cunning'
        WHEN '전략가' THEN 'Strategist'
        WHEN '친근함' THEN 'Friendly'
        WHEN '포용력' THEN 'Tolerant'
        WHEN '헌신적' THEN 'Devoted'
        ELSE personality
    END,
    image_url,
    pixel_image_url,
    game_capture_url,
    skill_image_url,
    description,
    condition_text,
    REPLACE(source_url, 'reference_ko/', 'reference_en/'),
    created_at,
    updated_at
FROM (
    SELECT d.*, d.evolution_condition AS condition_text
    FROM digimon_kr d
) source;

INSERT INTO evolution_en (
    evolution_id, from_digimon_id, to_digimon_id, condition_text,
    required_agent_rank, required_item_id, created_at, updated_at
)
SELECT evolution_id, from_digimon_id, to_digimon_id,
       REPLACE(
           REPLACE(
               REPLACE(
                   REPLACE(
                       REPLACE(
                           REPLACE(condition_text, '에이전트 랭크', 'Agent Rank'),
                           '공격력', 'ATK'),
                       '방어력', 'DEF'),
                   '정신력', 'SPI'),
               '민첩', 'SPD'),
           '지력', 'INT'),
       required_agent_rank, required_item_id, created_at, updated_at
FROM evolution_kr;

INSERT INTO digimon_special_skill_en (
    id, digimon_id, skill_name, skill_details, effect_text, sort_order
)
SELECT id, digimon_id, skill_name, skill_details, effect_text, sort_order
FROM digimon_special_skill_kr;

INSERT INTO digimon_attachment_skill_en (
    id, digimon_id, required_level, skill_name, sort_order
)
SELECT id, digimon_id, required_level, skill_name, sort_order
FROM digimon_attachment_skill_kr;
