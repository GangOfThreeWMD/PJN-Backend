package org.GoT.webscraper.service;

import org.GoT.Algorithm;
import org.GoT.webscraper.exception.IncorrectLink;
import org.GoT.webscraper.model.News;
import org.GoT.webscraper.model.Source;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class Scraper {
    private final Algorithm algorithm;

    private final ServiceLoader<NewsProvider> serviceLoader;

    public Scraper(Algorithm algorithm) {
        this.algorithm = algorithm;
        this.serviceLoader = getAllProvider();
    }

    public ServiceLoader<NewsProvider> getAllProvider() {
        return ServiceLoader.load(NewsProvider.class);
    }

    public List<News> getBBCNewsArticles(String baseUrl, int charsLimit) {
        Optional<NewsProvider> bbcProvider = this.serviceLoader.stream().filter(p -> p.get().getSource().equals(Source.bbc)).map(ServiceLoader.Provider::get).findFirst();
        if(bbcProvider.isPresent()) {
           return bbcProvider.get().getArticles();
        }
        var links = getLinksToArticles(baseUrl);

        List<News> news = new ArrayList<>();
        for (String link : links) {
            Optional<News> possibleArticle = getBBCArticle(link, charsLimit);
            possibleArticle.ifPresent(news::add);
        }

        return news;
    }

    public Set<String> getLinksToArticles(String baseUrl) {
        baseUrl = baseUrl.replaceFirst("/*$", "");
        try {
            Document document = Jsoup.connect(baseUrl + "/news").get();

            Elements aElements = document.getElementsByTag("a");

            Set<String> links = new HashSet<>();
            for (Element e : aElements) {
                String link = e.attr("href");

                if (!link.contains("/news/")) continue;

                if (!link.contains(baseUrl)) {
                    link = baseUrl + link;
                }

                links.add(link);
            }

            return links;

        } catch (HttpStatusException e) {
            throw new IncorrectLink(e.getUrl());
        } catch (IOException e) {
            throw new IncorrectLink(e.getMessage());
        }
    }

    public Optional<News> retrieveArticle(String link, String source) {
        if ("wikipedia".equalsIgnoreCase(source)) {
            return getWikipediaArticle(link);
        } else if ("bbc".equalsIgnoreCase(source)) {
            return getBBCArticle(link, 191);
        }

        return Optional.empty();
    }

    public Optional<News> getBBCArticle(String link, int charsLimit) {
        System.out.println("link: " + link);
        try {
            Document document = Jsoup.connect(link).get();

            Element possibleHeading = document.getElementById("main-heading");
            if(possibleHeading == null)
                return Optional.empty();
            String heading = possibleHeading.text();

            Elements textBlocks = document.getElementsByAttributeValue("data-component", "text-block");

            StringBuilder sb = new StringBuilder();
            for (Element textBlock : textBlocks) {
                sb.append(textBlock.getElementsByTag("p").get(0).text()).append(" ");
            }

            if(sb.isEmpty())
                return Optional.empty();
            sb.deleteCharAt(sb.length() - 1); // remove space in end of content
            var summarize = algorithm.getSummarize(sb.toString());
            summarize = shortenText(summarize, charsLimit);
            return Optional.of(new News(heading, summarize, link));

        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private String shortenText(String text, int maxLength) {
        return text.length() <= maxLength ? text : String.format("%s...", text.substring(0, maxLength));
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
