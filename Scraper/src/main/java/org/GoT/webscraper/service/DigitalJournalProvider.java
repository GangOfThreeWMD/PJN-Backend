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

public class DigitalJournalProvider implements NewsProvider{

    @Override
    public Source getSource() {
        return Source.digital_journal;
    }

    @Override
    public Optional<News> getArticle(String link) {
        try {
            Document document = Jsoup.connect(link).get();

            String heading = document.getElementsByAttributeValue("itemprop", "headline").get(0).text();

            Elements paragraphs = document.getElementsByClass("zox-post-body").get(0).getElementsByTag("p");

            StringBuilder sb = new StringBuilder();
            for (Element paragraph : paragraphs) {
                sb.append(paragraph.text()).append(" ");
            }

            if(sb.isEmpty())
                return Optional.empty();
            sb.deleteCharAt(sb.length() - 1); // remove space in end of content
            return Optional.of(new News(heading, sb.toString(), link));

        } catch (IOException | NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public Set<String> getLinksToArticles(String baseUrl) {
        baseUrl = baseUrl.replaceFirst("/*$", "");
        try {
            Document document = Jsoup.connect(baseUrl).get();

            Elements aElements = document.getElementsByTag("a");

            Set<String> links = new HashSet<>();
            for (Element e : aElements) {
                if (e.hasAttr("rel")) {
                    if (!"bookmark".equals(e.attr("rel"))) continue;

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
        return "https://www.digitaljournal.com";
    }
}
