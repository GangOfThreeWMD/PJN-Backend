package org.GoT.webscraper.service;

import org.GoT.webscraper.model.News;
import org.GoT.webscraper.model.Source;

import java.util.*;

public interface NewsProvider {
    Optional<News> getArticle(String link);

    String getBaseUrl();

    Set<String> getLinksToArticles(String baseUrl);

    Source getSource();

    default List<News> getArticles() {
        var links = getLinksToArticles(getBaseUrl());

        List<News> news = new ArrayList<>();
        for (String link : links) {
            Optional<News> possibleArticle = getArticle(link);
            possibleArticle.ifPresent(news::add);
        }

        return news;
    }
}
