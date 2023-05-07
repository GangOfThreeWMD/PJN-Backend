package org.GoT.webscraper.controller;


import org.GoT.webscraper.exception.IncorrectSource;
import org.GoT.webscraper.model.News;
import org.GoT.webscraper.exception.NotFoundArticle;
import org.GoT.webscraper.model.Source;
import org.GoT.webscraper.service.Scraper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/scraper/api")
public class ScraperController {

    private final Scraper scraper;

    public ScraperController(Scraper scraper) {
        this.scraper = scraper;
    }

    @GetMapping("/{source}/links")
    public Set<String> getBBCNewsLinks(@PathVariable Optional<Source> source) {
        if(source.isEmpty()) {
            throw new IncorrectSource("Insert wrong provider");
        }

        return scraper.getLinksToArticles(source.get());
    }

    @GetMapping
    public News getArticleContent(@RequestParam String link, @RequestParam String source) {
        return scraper.retrieveArticle(link, source)
                .orElseThrow(() -> new NotFoundArticle(
                        String.format("Not found article with link: %s and source: %s", link, source)));
    }

    @GetMapping("/{source}")
    public List<News> getNewsArticles(@PathVariable Optional<Source> source) {
        if(source.isEmpty()) {
            throw new IncorrectSource("Please provide correct source");
        }

        return this.scraper.getNews(source.get());
    }

    @GetMapping("/all")
    public List<News> getNewsArticles() {
        return this.scraper.getNews();
    }
}
