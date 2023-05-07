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

public class TheGuardianProvider implements NewsProvider{

    @Override
    public Source getSource() {
        return Source.guardian;
    }

    @Override
    public Optional<News> getArticle(String link) {
        try {
            Document document = Jsoup.connect(link).get();

            Elements possibleHeading = document.getElementsByAttributeValue("data-gu-name", "headline");
            if(possibleHeading.isEmpty())
                return Optional.empty();
            possibleHeading = possibleHeading.get(0).getElementsByTag("h1");
            if (possibleHeading.isEmpty())
                return Optional.empty();

            String heading = possibleHeading.get(0).text();
            Elements paragraphs = document.getElementsByAttributeValue("data-gu-name", "body").get(0).getElementsByTag("p");

            StringBuilder sb = new StringBuilder();
            for (Element paragraph : paragraphs) {
                sb.append(paragraph.text()).append(" ");
            }

            if(sb.isEmpty())
                return Optional.empty();
            sb.deleteCharAt(sb.length() - 1); // remove space in end of content

            Element pictureWrapper = document.getElementsByAttributeValue("data-gu-name", "media").first();
            Element pictureElement = null;
            if (pictureWrapper != null) {
                pictureElement = pictureWrapper.getElementsByTag("picture").first();
            }

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
            Document document = Jsoup.connect(baseUrl + "/world").get();

            Elements aElements = document.getElementsByTag("a");

            Set<String> links = new HashSet<>();
            for (Element e : aElements) {
                if (e.hasAttr("data-link-name")) {
                    if (!"article".equals(e.attr("data-link-name"))) continue;

                    String link = e.attr("href");
                    links.add(link);
                }
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
        return "https://www.theguardian.com";
    }
}
