package com.digimon.dtskrB.digimon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeoController {

    private static final List<LocalizedPage> LANGUAGES = List.of(
            new LocalizedPage("ko", "ko"),
            new LocalizedPage("en", "en"),
            new LocalizedPage("ja", "ja"));

    private final JdbcTemplate jdbcTemplate;
    private final String siteBaseUrl;

    public SeoController(JdbcTemplate jdbcTemplate, @Value("${app.site.base-url}") String siteBaseUrl) {
        this.jdbcTemplate = jdbcTemplate;
        this.siteBaseUrl = siteBaseUrl.replaceAll("/+$", "");
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        List<Integer> digimonIds = jdbcTemplate.queryForList(
                "SELECT id FROM digimon_kr ORDER BY id",
                Integer.class);
        List<Long> questIds = jdbcTemplate.queryForList(
                "SELECT id FROM quest_post WHERE is_published = 1 ORDER BY sort_order, id",
                Long.class);

        StringBuilder xml = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
                        xmlns:xhtml="http://www.w3.org/1999/xhtml">
                """);
        appendLocalizedUrls(xml, "/");
        appendLocalizedUrls(xml, "/quests");
        digimonIds.forEach(id -> appendLocalizedUrls(xml, "/digimons/" + id));
        questIds.forEach(id -> appendLocalizedUrls(xml, "/quests/" + id));
        xml.append("</urlset>\n");

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(xml.toString());
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> robots() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body("User-agent: *\nAllow: /\nSitemap: " + siteBaseUrl + "/sitemap.xml\n");
    }

    private void appendLocalizedUrls(StringBuilder xml, String path) {
        for (LocalizedPage language : LANGUAGES) {
            xml.append("  <url>\n")
                    .append("    <loc>").append(xmlEscape(localizedUrl(path, language.queryValue()))).append("</loc>\n");
            for (LocalizedPage alternate : LANGUAGES) {
                appendAlternate(xml, alternate.hreflang(), localizedUrl(path, alternate.queryValue()));
            }
            appendAlternate(xml, "x-default", localizedUrl(path, "ko"));
            xml.append("  </url>\n");
        }
    }

    private void appendAlternate(StringBuilder xml, String hreflang, String url) {
        xml.append("    <xhtml:link rel=\"alternate\" hreflang=\"")
                .append(hreflang)
                .append("\" href=\"")
                .append(xmlEscape(url))
                .append("\" />\n");
    }

    private String localizedUrl(String path, String language) {
        return siteBaseUrl + path + "?lang=" + language;
    }

    private static String xmlEscape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private record LocalizedPage(String hreflang, String queryValue) {
    }
}
