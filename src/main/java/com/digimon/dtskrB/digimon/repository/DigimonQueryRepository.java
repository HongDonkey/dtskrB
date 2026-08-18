package com.digimon.dtskrB.digimon.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.digimon.dtskrB.digimon.dto.DigimonDto;
import com.digimon.dtskrB.digimon.dto.DigimonDetailDto;
import com.digimon.dtskrB.digimon.dto.EvolutionEdgeDto;
import com.digimon.dtskrB.digimon.dto.SpecialSkillDto;
import com.digimon.dtskrB.digimon.dto.AttachmentSkillDto;

@Repository
public class DigimonQueryRepository {

    private static final String DIGIMON_SUMMARY_SELECT = """
            SELECT d.id, d.name, s.stage_name AS stage, d.attribute, d.digimon_type,
                   d.personality, d.image_url, d.pixel_image_url
            FROM %s d
            JOIN %s s ON s.stage_code = d.stage_code
            """;

    private final JdbcTemplate jdbcTemplate;

    public DigimonQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Long> getCountsByStage(CatalogLanguage language) {
        Map<String, Long> counts = new LinkedHashMap<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                """
                        SELECT stage_name, digimon_count
                        FROM (
                            SELECT ? AS stage_name, 0 AS sort_order, COUNT(*) AS digimon_count
                            FROM %s
                            UNION ALL
                            SELECT s.stage_name, s.sort_order, COUNT(d.id)
                            FROM %s s
                            LEFT JOIN %s d ON d.stage_code = s.stage_code
                            GROUP BY s.stage_code, s.stage_name, s.sort_order
                        ) counts
                        ORDER BY sort_order
                        """.formatted(language.digimonTable(), language.stageTable(), language.digimonTable()),
                language.allLabel()
        );
        rows.forEach(row -> counts.put(
                (String) row.get("stage_name"),
                ((Number) row.get("digimon_count")).longValue()
        ));
        return counts;
    }

    public long getEvolutionCount(CatalogLanguage language) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM %s".formatted(language.evolutionTable()),
                Long.class
        );
        return count == null ? 0L : count;
    }

    public List<DigimonDto> getAll(CatalogLanguage language) {
        return jdbcTemplate.query(
                summarySelect(language) + "ORDER BY s.sort_order, d.name ASC",
                this::mapDigimonSummary
        );
    }

    public List<DigimonDto> getByStageCode(String stageCode, CatalogLanguage language) {
        return jdbcTemplate.query(
                summarySelect(language) + "WHERE s.stage_code = ? ORDER BY d.name ASC",
                this::mapDigimonSummary,
                stageCode
        );
    }

    public List<DigimonDto> searchByName(String query, CatalogLanguage language) {
        return jdbcTemplate.query(
                summarySelect(language) + "WHERE d.name LIKE ? ORDER BY s.sort_order, d.name ASC",
                this::mapDigimonSummary,
                '%' + query + '%'
        );
    }

    public Optional<DigimonDto> findById(int id, CatalogLanguage language) {
        List<DigimonDto> digimons = jdbcTemplate.query(
                summarySelect(language) + "WHERE d.id = ?",
                this::mapDigimonSummary,
                id
        );

        return digimons.stream().findFirst();
    }

    public Optional<DigimonDetailDto> findDetailById(int id, CatalogLanguage language) {
        List<DigimonDetailDto> digimons = jdbcTemplate.query(
                """
                        SELECT d.id, d.name, s.stage_name AS stage, d.attribute, d.digimon_type,
                               d.personality, d.image_url, d.pixel_image_url, d.game_capture_url,
                               d.skill_image_url,
                               d.description,
                               d.evolution_condition, d.source_url
                        FROM %s d
                        JOIN %s s ON s.stage_code = d.stage_code
                        WHERE d.id = ?
                        """.formatted(language.digimonTable(), language.stageTable()),
                (resultSet, rowNum) -> new DigimonDetailDto(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("stage"),
                        resultSet.getString("attribute"),
                        resultSet.getString("digimon_type"),
                        resultSet.getString("personality"),
                        resultSet.getString("image_url"),
                        resultSet.getString("pixel_image_url"),
                        resultSet.getString("game_capture_url"),
                        resultSet.getString("skill_image_url"),
                        resultSet.getString("description"),
                        resultSet.getString("evolution_condition"),
                        resultSet.getString("source_url"),
                        List.of(),
                        List.of()
                ),
                id
        );

        return digimons.stream().findFirst();
    }

    public List<SpecialSkillDto> findSpecialSkills(int digimonId, CatalogLanguage language) {
        return jdbcTemplate.query(
                "SELECT skill_name, skill_details, effect_text FROM %s WHERE digimon_id = ? ORDER BY sort_order".formatted(language.specialSkillTable()),
                (resultSet, rowNum) -> new SpecialSkillDto(
                        resultSet.getString("skill_name"),
                        resultSet.getString("skill_details"),
                        resultSet.getString("effect_text")
                ),
                digimonId
        );
    }

    public List<AttachmentSkillDto> findAttachmentSkills(int digimonId, CatalogLanguage language) {
        return jdbcTemplate.query(
                "SELECT required_level, skill_name FROM %s WHERE digimon_id = ? ORDER BY sort_order".formatted(language.attachmentSkillTable()),
                (resultSet, rowNum) -> new AttachmentSkillDto(
                        resultSet.getObject("required_level", Integer.class),
                        resultSet.getString("skill_name")
                ),
                digimonId
        );
    }

    public List<EvolutionEdgeDto> findAllEvolutionEdges(CatalogLanguage language) {
        return jdbcTemplate.query(
                """
                        SELECT source.id AS source_id, source.name AS source_name,
                               source_stage.stage_name AS source_stage, source.attribute AS source_attribute,
                               source.digimon_type AS source_digimon_type,
                               source.personality AS source_personality,
                               source.image_url AS source_image_url,
                               source.pixel_image_url AS source_pixel_image_url,
                               destination.id AS destination_id, destination.name AS destination_name,
                               destination_stage.stage_name AS destination_stage,
                               destination.attribute AS destination_attribute,
                               destination.digimon_type AS destination_digimon_type,
                               destination.personality AS destination_personality,
                               destination.image_url AS destination_image_url,
                               destination.pixel_image_url AS destination_pixel_image_url,
                               source.evolution_condition AS source_evolution_condition,
                               destination.evolution_condition AS destination_evolution_condition,
                               e.required_agent_rank,
                               e.required_item_id, i.name AS required_item_name
                        FROM %s e
                        JOIN %s source ON source.id = e.from_digimon_id
                        JOIN %s source_stage ON source_stage.stage_code = source.stage_code
                        JOIN %s destination ON destination.id = e.to_digimon_id
                        JOIN %s destination_stage ON destination_stage.stage_code = destination.stage_code
                        LEFT JOIN %s i ON i.id = e.required_item_id
                        ORDER BY source_stage.sort_order, source.name,
                                 destination_stage.sort_order, destination.name
                        """.formatted(
                                language.evolutionTable(), language.digimonTable(), language.stageTable(),
                                language.digimonTable(), language.stageTable(), language.itemTable()
                        ),
                this::mapEvolutionEdge
        );
    }

    private EvolutionEdgeDto mapEvolutionEdge(java.sql.ResultSet resultSet, int rowNum)
            throws java.sql.SQLException {
        return new EvolutionEdgeDto(
                mapDigimonSummary(resultSet, "source_"),
                mapDigimonSummary(resultSet, "destination_"),
                resultSet.getString("source_evolution_condition"),
                resultSet.getString("destination_evolution_condition"),
                resultSet.getObject("required_agent_rank", Integer.class),
                resultSet.getObject("required_item_id", Long.class),
                resultSet.getString("required_item_name")
        );
    }

    private DigimonDto mapDigimonSummary(java.sql.ResultSet resultSet, String prefix)
            throws java.sql.SQLException {
        return new DigimonDto(
                resultSet.getInt(prefix + "id"),
                resultSet.getString(prefix + "name"),
                resultSet.getString(prefix + "stage"),
                resultSet.getString(prefix + "attribute"),
                resultSet.getString(prefix + "digimon_type"),
                resultSet.getString(prefix + "personality"),
                resultSet.getString(prefix + "image_url"),
                resultSet.getString(prefix + "pixel_image_url")
        );
    }

    private DigimonDto mapDigimonSummary(java.sql.ResultSet resultSet, int rowNum)
            throws java.sql.SQLException {
        return new DigimonDto(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("stage"),
                resultSet.getString("attribute"),
                resultSet.getString("digimon_type"),
                resultSet.getString("personality"),
                resultSet.getString("image_url"),
                resultSet.getString("pixel_image_url")
        );
    }

    private String summarySelect(CatalogLanguage language) {
        return DIGIMON_SUMMARY_SELECT.formatted(language.digimonTable(), language.stageTable());
    }
}
