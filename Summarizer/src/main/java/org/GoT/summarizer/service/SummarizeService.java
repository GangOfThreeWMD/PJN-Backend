package org.GoT.summarizer.service;

import jep.JepException;
import org.GoT.Algorithm;
import org.GoT.summarizer.dto.ArticleDto;
import org.GoT.webscraper.exception.IncorrectLink;
import org.GoT.webscraper.model.News;
import org.GoT.webscraper.service.NewsProvider;
import org.GoT.webscraper.service.Scraper;
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
            try {
                articleDtoList.addAll(getArticles(newsProvider));
            } catch (IncorrectLink ex) {
                System.err.printf("Problem with link: %s%n", ex.getMessage());
            }
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
        List<News> newsList = newsProvider.getAllArticles();
        return transformNewsToArticles(newsList);
    }

    private List<ArticleDto> getArticles(NewsProvider newsProvider, long limit) {
        List<News> newsList = newsProvider.getArticles(limit);
        return transformNewsToArticles(newsList);
    }

    private List<ArticleDto> transformNewsToArticles(List<News> newsList) {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        for (News news: newsList) {
            try {
                articleDtoList.add(getArticle(news));
            } catch (JepException jepException) {
                System.err.printf("Problem with article: %s%n", jepException.getMessage());
            }
        }
        return articleDtoList;
    }

    private ArticleDto getArticle(News news) {
        return new ArticleDto(
                news.title(),
                shortenText(summarizeAlgorithm.getSummarize(news.content()), this.lengthOfArticle),
                news.link());
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

    public String summarizeText(String textToSummarize) {
        return this.summarizeAlgorithm.getSummarize(textToSummarize);
    }

    private String shortenText(String text, int maxLength) {
        return text.length() <= maxLength ? text : String.format("%s...", text.substring(0, maxLength));
    }
}
