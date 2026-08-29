package com.digimon.dtskrB.digimon.service;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.digimon.dtskrB.digimon.dto.QuestDetailDto;
import com.digimon.dtskrB.digimon.dto.QuestImageDto;
import com.digimon.dtskrB.digimon.dto.QuestSummaryDto;
import com.digimon.dtskrB.digimon.repository.CatalogLanguage;

@Service
public class QuestBoardService {

    private final JdbcTemplate jdbcTemplate;

    public QuestBoardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<QuestSummaryDto> findPublishedQuests(CatalogLanguage catalogLanguage) {
        String language = catalogLanguage.languageCode();
        return jdbcTemplate.query("""
                SELECT q.id, q.category,
                       COALESCE(t.title, ko.title) AS title,
                       COALESCE(t.summary, ko.summary) AS summary,
                       (SELECT image_url FROM quest_image WHERE quest_post_id = q.id ORDER BY sort_order LIMIT 1) AS thumbnail_url,
                       (SELECT COUNT(*) FROM quest_image WHERE quest_post_id = q.id) AS image_count,
                       q.updated_at
                FROM quest_post q
                JOIN quest_translation ko ON ko.quest_post_id = q.id AND ko.language_code = 'ko'
                LEFT JOIN quest_translation t ON t.quest_post_id = q.id AND t.language_code = ?
                WHERE q.is_published = 1
                ORDER BY q.sort_order, q.id DESC
                """, (resultSet, rowNumber) -> new QuestSummaryDto(
                        resultSet.getLong("id"),
                        resultSet.getString("category"),
                        resultSet.getString("title"),
                        resultSet.getString("summary"),
                        resultSet.getString("thumbnail_url"),
                        resultSet.getInt("image_count"),
                        resultSet.getTimestamp("updated_at").toLocalDateTime()), language);
    }

    public Optional<QuestDetailDto> findPublishedQuest(long questId, CatalogLanguage catalogLanguage) {
        String language = catalogLanguage.languageCode();
        List<QuestDetailRow> quests = jdbcTemplate.query("""
                SELECT q.id, q.category,
                       COALESCE(t.title, ko.title) AS title,
                       COALESCE(t.summary, ko.summary) AS summary,
                       COALESCE(t.content, ko.content) AS content,
                       t.source_url,
                       q.updated_at
                FROM quest_post q
                JOIN quest_translation ko ON ko.quest_post_id = q.id AND ko.language_code = 'ko'
                LEFT JOIN quest_translation t ON t.quest_post_id = q.id AND t.language_code = ?
                WHERE q.id = ? AND q.is_published = 1
                """, (resultSet, rowNumber) -> new QuestDetailRow(
                        resultSet.getLong("id"),
                        resultSet.getString("category"),
                        resultSet.getString("title"),
                        resultSet.getString("summary"),
                        resultSet.getString("content"),
                        resultSet.getString("source_url"),
                        resultSet.getTimestamp("updated_at").toLocalDateTime()), language, questId);
        return quests.stream().findFirst().map(quest -> new QuestDetailDto(
                quest.id(), quest.category(), quest.title(), quest.summary(), quest.content(), quest.sourceUrl(),
                findImages(quest.id(), language), quest.updatedAt()));
    }

    private List<QuestImageDto> findImages(long questId, String language) {
        return jdbcTemplate.query("""
                SELECT i.sort_order, COALESCE(t.image_url, i.image_url) AS image_url,
                       COALESCE(t.location_name, ko.location_name) AS location_name,
                       COALESCE(t.location_note, ko.location_note) AS location_note
                FROM quest_image i
                JOIN quest_image_translation ko
                  ON ko.quest_image_id = i.id AND ko.language_code = 'ko'
                LEFT JOIN quest_image_translation t
                  ON t.quest_image_id = i.id AND t.language_code = ?
                WHERE i.quest_post_id = ?
                ORDER BY i.sort_order, i.id
                """, (resultSet, rowNumber) -> new QuestImageDto(
                        resultSet.getInt("sort_order"),
                        resultSet.getString("image_url"),
                        resultSet.getString("location_name"),
                        resultSet.getString("location_note")), language, questId);
    }

    private record QuestDetailRow(
            long id,
            String category,
            String title,
            String summary,
            String content,
            String sourceUrl,
            java.time.LocalDateTime updatedAt) {
    }
}
