package com.digimon.dtskrB.digimon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CatalogLanguageTests {

    @Test
    void resolvesJapaneseLanguageHeader() {
        assertEquals(CatalogLanguage.JP, CatalogLanguage.fromHeader("jp"));
    }

    @Test
    void fallsBackToKoreanAndPreservesEnglish() {
        assertEquals(CatalogLanguage.KO, CatalogLanguage.fromHeader(null));
        assertEquals(CatalogLanguage.KO, CatalogLanguage.fromHeader("ko-KR"));
        assertEquals(CatalogLanguage.EN, CatalogLanguage.fromHeader("en-US"));
    }
}
