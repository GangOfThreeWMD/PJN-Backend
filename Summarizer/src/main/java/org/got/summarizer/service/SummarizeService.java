package org.got.summarizer.service;

import org.GoT.Algorithm;
import org.GoT.webscraper.service.NewsProvider;
import org.GoT.webscraper.service.Scraper;
import org.got.summarizer.dto.ArticleDto;
import org.GoT.webscraper.model.Source;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@Service
@CacheConfig(cacheNames={"allArticles", "selectedArticles", "limitArticles", "limitSelectedArticles"})
public class SummarizeService {
    private final ServiceLoader<NewsProvider> serviceLoader;

    private final Algorithm summarizeAlgorithm;

    private int lengthOfArticle;


    public SummarizeService(Scraper scraper, Algorithm algorithm) {
        this.serviceLoader = scraper.getAllProvider();
        this.summarizeAlgorithm = algorithm;
        this.lengthOfArticle = 192;
    }

    @Cacheable(value = "limitArticles", key = "#limit")
    public List<ArticleDto> getSummaries(long limit) {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        for(NewsProvider newsProvider: serviceLoader) {
            articleDtoList.addAll(getArticles(newsProvider, limit));
        }
        return articleDtoList;
    }

    @Cacheable(value = "allArticles")
    public List<ArticleDto> getSummaries() {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        for(NewsProvider newsProvider: serviceLoader) {
            articleDtoList.addAll(getArticles(newsProvider));
        }
        return articleDtoList;
    }

    @Cacheable(value = "selectedArticles", key = "#source")
    public List<ArticleDto> getSummaries(Source source) {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        for(NewsProvider newsProvider: serviceLoader) {
            if (!source.equals(newsProvider.getSource()))
                continue;
            articleDtoList.addAll(getArticles(newsProvider));
        }
        return articleDtoList;
    }

    @Cacheable(value = "limitSelectedArticles", key = "{#source, #limit}")
    public List<ArticleDto> getSummaries(Source source, long limit) {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        for(NewsProvider newsProvider: serviceLoader) {
            if (!source.equals(newsProvider.getSource()))
                continue;
            articleDtoList.addAll(getArticles(newsProvider, limit));
        }
        return articleDtoList;
    }

    private List<ArticleDto> getArticles(NewsProvider newsProvider) {
        return newsProvider.getAllArticles()
                .stream()
                .map(
                        a -> new ArticleDto(a.title(), summarizeAlgorithm.getSummarize(a.content()), a.link())
                ).toList();
    }

    private List<ArticleDto> getArticles(NewsProvider newsProvider, long limit) {
        return newsProvider.getArticles(limit)
                .stream()
                .map(
                        a -> new ArticleDto(a.title(), summarizeAlgorithm.getSummarize(a.content()), a.link())
                ).toList();
    }

    @CacheEvict(value = {"allArticles", "selectedArticles", "limitSelectedArticles", "limitArticles"}, allEntries = true)
    @Scheduled(fixedDelay = 1440000)
    public void forceRefresh() {

    }

    @Caching(evict = {
            @CacheEvict(value = "limitSelectedArticles", allEntries = true),
            @CacheEvict(value = "selectedArticles", key = "#source") })
    public void forceRefresh(Source source){

    }

    public List<Source> getSources() {
        return serviceLoader.stream()
                .map( p -> p.get().getSource())
                .toList();
    }

    @CacheEvict(value = {"allArticles", "selectedArticles", "limitSelectedArticles", "limitArticles"}, allEntries = true)
    public void setLength(int length) {
        this.lengthOfArticle = length;
    }

    private String shortenText(String text, int maxLength) {
        return text.length() <= maxLength ? text : String.format("%s...", text.substring(0, maxLength));
    }
}
