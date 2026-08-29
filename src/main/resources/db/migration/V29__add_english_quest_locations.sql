INSERT INTO `quest_image_translation`
    (`quest_image_id`, `quest_post_id`, `language_code`, `location_name`, `location_note`)
SELECT image.id,
       image.quest_post_id,
       'en',
       CASE image.sort_order
         WHEN 1 THEN 'Higashi-Shinjuku Vision Square'
         WHEN 2 THEN 'Higashi-Shinjuku Takasu Avenue Intersection'
         WHEN 3 THEN 'Shinjuku Underground Mall Subroad'
         WHEN 4 THEN 'Tokyo Metro Marunouchi Line Shinjuku Station'
         WHEN 5 THEN 'Higashi-Shinjuku Takasu Avenue Intersection Cafe'
         WHEN 6 THEN 'Nishi-Shinjuku Railway Bridge Intersection'
         WHEN 7 THEN 'A Back Alley in Shinjuku'
         WHEN 8 THEN 'Shinjuku Station East Exit Ticket Gate'
         WHEN 9 THEN 'Shinjuku Underground Walkway'
         WHEN 10 THEN 'Akihabara Station Electric Town South Exit'
         WHEN 11 THEN 'A Back Alley in Akihabara'
         WHEN 12 THEN 'Tokyo Metropolitan Government Main Building'
         WHEN 13 THEN 'Shinjuku Park Waterfall Square'
         WHEN 14 THEN 'Shinjuku East Shopping District'
         WHEN 15 THEN 'Shinjuku Underground Waterway: North Block'
         WHEN 16 THEN 'Shinjuku Underground Waterway: South Block'
         WHEN 17 THEN 'Shinjuku Underground Reservoir'
       END,
       CASE image.sort_order
         WHEN 5 THEN 'Cafe on the north side'
         WHEN 15 THEN 'Waterway entrance: northeast of a back alley in Shinjuku'
         WHEN 16 THEN 'Enter west from the Tokyo Metropolitan Government Main Building'
         ELSE NULL
       END
FROM `quest_image` image
WHERE image.quest_post_id = 1
ON DUPLICATE KEY UPDATE
  `quest_post_id` = VALUES(`quest_post_id`),
  `location_name` = VALUES(`location_name`),
  `location_note` = VALUES(`location_note`);
