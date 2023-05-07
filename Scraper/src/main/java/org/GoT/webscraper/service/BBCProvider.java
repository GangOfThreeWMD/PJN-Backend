package org.GoT.webscraper.service;

import org.GoT.webscraper.exception.IncorrectLink;
import org.GoT.webscraper.model.News;
import org.GoT.webscraper.model.Source;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BBCProvider implements NewsProvider{

    @Override
    public Source getSource() {
        return Source.bbc;
    }

    @Override
    public Optional<News> getArticle(String link) {
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

            Element pictureElement = document.getElementsByTag("picture").first();

            if (pictureElement != null) {
                Element img = pictureElement.getElementsByTag("img").first();
                if (img != null) {
                    String src = img.attr("src");
                    if(!src.isBlank()) {
                       return Optional.of(new News(heading, sb.toString(), link, src));
                    }
                }
            }
            return Optional.of(new News(heading, sb.toString(), link));

        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
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

    @Override
    public String getBaseUrl() {
        return "https://www.bbc.com";
    }
}
