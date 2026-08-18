/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `digimon_stage` (
  `stage_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stage_name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` smallint unsigned NOT NULL,
  PRIMARY KEY (`stage_code`),
  UNIQUE KEY `uk_digimon_stage_name` (`stage_name`),
  UNIQUE KEY `uk_digimon_stage_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_kr` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `digimon_kr` (
  `id` int unsigned NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stage_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `attribute` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `digimon_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `personality` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pixel_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `game_capture_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `skill_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `evolution_condition` text COLLATE utf8mb4_unicode_ci,
  `source_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_digimon_attribute` (`attribute`),
  KEY `idx_digimon_type` (`digimon_type`),
  KEY `idx_digimon_personality` (`personality`),
  KEY `idx_digimon_stage_code` (`stage_code`),
  CONSTRAINT `fk_digimon_stage` FOREIGN KEY (`stage_code`) REFERENCES `digimon_stage` (`stage_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evolution_kr` (
  `evolution_id` int unsigned NOT NULL AUTO_INCREMENT,
  `from_digimon_id` int unsigned NOT NULL,
  `to_digimon_id` int unsigned NOT NULL,
  `condition_text` text COLLATE utf8mb4_unicode_ci,
  `required_agent_rank` smallint unsigned DEFAULT NULL,
  `required_item_id` bigint unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`evolution_id`),
  UNIQUE KEY `uq_evolution_route` (`from_digimon_id`,`to_digimon_id`),
  KEY `fk_evolution_item` (`required_item_id`),
  KEY `idx_evolution_from` (`from_digimon_id`),
  KEY `idx_evolution_to` (`to_digimon_id`),
  CONSTRAINT `fk_evolution_from` FOREIGN KEY (`from_digimon_id`) REFERENCES `digimon_kr` (`id`),
  CONSTRAINT `fk_evolution_item` FOREIGN KEY (`required_item_id`) REFERENCES `item_kr` (`id`),
  CONSTRAINT `fk_evolution_to` FOREIGN KEY (`to_digimon_id`) REFERENCES `digimon_kr` (`id`),
  CONSTRAINT `chk_evolution_not_same` CHECK ((`from_digimon_id` <> `to_digimon_id`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `digimon_special_skill_kr` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `digimon_id` int unsigned NOT NULL,
  `skill_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `skill_details` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `effect_text` text COLLATE utf8mb4_unicode_ci,
  `sort_order` smallint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_special_skill_digimon` (`digimon_id`),
  CONSTRAINT `fk_special_skill_digimon` FOREIGN KEY (`digimon_id`) REFERENCES `digimon_kr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `digimon_attachment_skill_kr` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `digimon_id` int unsigned NOT NULL,
  `required_level` smallint unsigned DEFAULT NULL,
  `skill_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` smallint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_attachment_skill_digimon` (`digimon_id`),
  CONSTRAINT `fk_attachment_skill_digimon` FOREIGN KEY (`digimon_id`) REFERENCES `digimon_kr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


