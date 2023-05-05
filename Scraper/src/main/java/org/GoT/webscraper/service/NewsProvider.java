package org.GoT.webscraper.service;

import org.GoT.webscraper.model.News;
import org.GoT.webscraper.model.Source;

import java.util.*;

public interface NewsProvider {
    Optional<News> getArticle(String link);

    String getBaseUrl();

    Set<String> getLinksToArticles(String baseUrl);

    Source getSource();

    default List<News> getArticles(long limit) {
        var links = getLinksToArticles(getBaseUrl());
        return getArticles(links, limit);
    }

    default List<News> getAllArticles() {
        var links = getLinksToArticles(getBaseUrl());
        return getArticles(links);
    }

    private List<News> getArticles(Set<String> links, long limit) {
        List<News> news = new ArrayList<>();
        for (String link : links) {
            Optional<News> possibleArticle = getArticle(link);
            possibleArticle.ifPresent(news::add);
            if (news.size() == limit) {
                break;
            }
        }
        return news;
    }

    private List<News> getArticles(Set<String> links) {
        List<News> news = new ArrayList<>();
        for (String link : links) {
            Optional<News> possibleArticle = getArticle(link);
            possibleArticle.ifPresent(news::add);
        }
        return news;
    }
}
