package org.GoT.webscraper.model;

import java.util.Objects;
import java.util.Optional;

public class News {
    private final String title;
    private final String content;
    private final String link;
    private final String urlToImage;

        // Constructor for the original version of the News record
    public News(String title, String content, String link) {
        this.title = title;
        this.content = content;
        this.link = link;
        this.urlToImage = null;
    }

        // Constructor for the new version of the News record
    public News(String title, String content, String link, String urlToImage) {
        this.title = title;
        this.content = content;
        this.link = link;
        this.urlToImage = urlToImage;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }

    public Optional<String> getUrlToImage() {
        if(this.urlToImage == null ){
            return Optional.empty();
        }
        return Optional.of(this.urlToImage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return Objects.equals(title, news.title) && Objects.equals(content, news.content) && Objects.equals(link, news.link) && Objects.equals(urlToImage, news.urlToImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, link, urlToImage);
    }
}
