package org.GoT.webscraper.controller;


import org.GoT.webscraper.model.News;
import org.GoT.webscraper.exception.NotFoundArticle;
import org.GoT.webscraper.service.Scraper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/scraper/api")
public class ScraperController {

    private final Scraper scraper;

    public ScraperController(Scraper scraper) {
        this.scraper = scraper;
    }

    @GetMapping("/bbc")
    public List<News> getBBCNewsArticles(@RequestParam(name = "limit", required = false, defaultValue = "191") int charsLimit) {
        return scraper.getBBCNewsArticles("https://www.bbc.com", charsLimit);
    }

    @GetMapping("/bbc/links")
    public Set<String> getBBCNewsLinks() {
        return scraper.getLinksToArticles("https://www.bbc.com");
    }

    @GetMapping
    public News getArticleContent(@RequestParam String link, @RequestParam String source) {
        return scraper.retrieveArticle(link, source)
                .orElseThrow(() -> new NotFoundArticle(
                        String.format("Not found article with link: %s and source: %s", link, source)));
    }
}