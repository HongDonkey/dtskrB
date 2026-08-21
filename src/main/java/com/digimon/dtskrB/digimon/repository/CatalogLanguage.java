package com.digimon.dtskrB.digimon.repository;

public enum CatalogLanguage {
    KO("digimon_kr", "digimon_stage", "evolution_kr", "item_kr",
            "digimon_special_skill_kr", "digimon_attachment_skill_kr", "전체"),
    EN("digimon_en", "digimon_stage_en", "evolution_en", "item_en",
            "digimon_special_skill_en", "digimon_attachment_skill_en", "All"),
    JP("digimon_jp", "digimon_stage_jp", "evolution_jp", "item_jp",
            "digimon_special_skill_jp", "digimon_attachment_skill_jp", "すべて");

    private final String digimonTable;
    private final String stageTable;
    private final String evolutionTable;
    private final String itemTable;
    private final String specialSkillTable;
    private final String attachmentSkillTable;
    private final String allLabel;

    CatalogLanguage(String digimonTable, String stageTable, String evolutionTable,
                    String itemTable, String specialSkillTable, String attachmentSkillTable,
                    String allLabel) {
        this.digimonTable = digimonTable;
        this.stageTable = stageTable;
        this.evolutionTable = evolutionTable;
        this.itemTable = itemTable;
        this.specialSkillTable = specialSkillTable;
        this.attachmentSkillTable = attachmentSkillTable;
        this.allLabel = allLabel;
    }

    public static CatalogLanguage fromHeader(String value) {
        if (value == null) return KO;
        String normalized = value.toLowerCase();
        if (normalized.startsWith("en")) return EN;
        if (normalized.startsWith("jp")) return JP;
        return KO;
    }

    public String digimonTable() { return digimonTable; }
    public String stageTable() { return stageTable; }
    public String evolutionTable() { return evolutionTable; }
    public String itemTable() { return itemTable; }
    public String specialSkillTable() { return specialSkillTable; }
    public String attachmentSkillTable() { return attachmentSkillTable; }
    public String allLabel() { return allLabel; }
}
