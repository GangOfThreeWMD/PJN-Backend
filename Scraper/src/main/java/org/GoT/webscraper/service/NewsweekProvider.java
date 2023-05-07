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

public class NewsweekProvider implements NewsProvider{

    @Override
    public Source getSource() {
        return Source.newsweek;
    }

    @Override
    public Optional<News> getArticle(String link) {
        try {
            Document document = Jsoup.connect(link).get();

            String heading = document.getElementsByClass("article-header").get(0).getElementsByTag("h1").get(0).text();

            Element articleBody = document.getElementsByClass("article-body").get(0);

            Elements paragraphs = articleBody.getElementsByTag("p");

            StringBuilder sb = new StringBuilder();
            for (Element paragraph : paragraphs) {
                sb.append(paragraph.text()).append(" ");
            }

            if(sb.isEmpty())
                return Optional.empty();
            sb.deleteCharAt(sb.length() - 1); // remove space in end of content

            Element pictureElement = articleBody.getElementsByTag("picture").first();

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

        } catch (IOException | NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public Set<String> getLinksToArticles(String baseUrl) {
        baseUrl = baseUrl.replaceFirst("/*$", "");
        try {
            Document document = Jsoup.connect(baseUrl).get();

            Elements aWrapperElements = document.getElementsByClass("news-title");

            Set<String> links = new HashSet<>();
            for (Element e : aWrapperElements) {
                Elements aElements = e.getElementsByTag("a");
                if (aElements.isEmpty()) continue;

                String link = aElements.attr("href");
                if(!link.contains(getBaseUrl())) continue;

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
        return "https://www.newsweek.com";
    }
}
