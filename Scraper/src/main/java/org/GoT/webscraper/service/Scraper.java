package org.GoT.webscraper.service;

import org.GoT.webscraper.exception.IncorrectSource;
import org.GoT.webscraper.model.News;
import org.GoT.webscraper.model.Source;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class Scraper {
    private final ServiceLoader<NewsProvider> serviceLoader;

    public Scraper() {
        this.serviceLoader = ServiceLoader.load(NewsProvider.class);
    }

    public List<News> getNews(Source source) {
        Optional<NewsProvider> newsProvider = this.serviceLoader.stream()
                .filter(p -> p.get().getSource().equals(source))
                .map(ServiceLoader.Provider::get)
                .findFirst();
        if (newsProvider.isPresent()) {
            return newsProvider.get().getAllArticles();
        }
        return Collections.emptyList();
    }

    public List<News> getNews() {
        List<News> articleDtoList = new ArrayList<>();
        for(NewsProvider newsProvider: serviceLoader) {
            articleDtoList.addAll(newsProvider.getAllArticles());
        }
        return articleDtoList;
    }

    public Set<String> getLinksToArticles(Source source) {
        Optional<ServiceLoader.Provider<NewsProvider>> possibleNewsProviderProvider = this.serviceLoader.stream()
                .filter(p -> p.get().getSource().equals(source))
                .findFirst();

        if (possibleNewsProviderProvider.isPresent()) {
            ServiceLoader.Provider<NewsProvider> newsProvider = possibleNewsProviderProvider.get();
            return newsProvider.get().getLinksToArticles(newsProvider.get().getBaseUrl());
        } else {
            throw new IncorrectSource(String.format("Source %s doesn't exist", source.toString()));
        }
    }

    public Optional<News> retrieveArticle(String link, String sourceString) {
        if ("wikipedia".equalsIgnoreCase(sourceString)) {
            return getWikipediaArticle(link);
        }
        try {
            Source source = Source.valueOf(sourceString);
            return getArticle(link, source);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public Optional<News> getArticle(String link, Source source) {
        Optional<ServiceLoader.Provider<NewsProvider>> possibleNewsProviderProvider = this.serviceLoader.stream()
                .filter(p -> p.get().getSource().equals(source))
                .findFirst();

        if (possibleNewsProviderProvider.isPresent()) {
            ServiceLoader.Provider<NewsProvider> newsProvider = possibleNewsProviderProvider.get();
            return newsProvider.get().getArticle(link);
        } else {
            throw new IncorrectSource(String.format("Source %s doesn't exist", source.toString()));
        }
    }

    public Optional<News> getWikipediaArticle(String link){
        try {
            Document document = Jsoup.connect(link).get();

            Element possibleHeading = document.getElementById("firstHeading");
            if(possibleHeading == null)
                return Optional.empty();
            String heading = possibleHeading.child(0).text();

            Elements paragraphs = document.getElementsByTag("p");

            StringBuilder sb = new StringBuilder();
            for (Element e : paragraphs) {
                sb.append(e.text()).append("\n");
            }

            return Optional.of(new News(heading, sb.toString(), link));

        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
