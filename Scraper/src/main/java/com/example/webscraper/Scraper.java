package com.example.webscraper;

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

    public List<News> getBBCNewsArticles(String baseUrl) {
        var links = getLinksToArticles(baseUrl);

        List<News> news = new ArrayList<>();
        for (String link : links) {
            Optional<News> possibleArticle = getBBCArticle(link);
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
            return getBBCArticle(link);
        }

        return Optional.empty();
    }

    public Optional<News> getBBCArticle(String link) {
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

            return Optional.of(new News(heading, sb.toString(), link));

        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            return Optional.empty();
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
