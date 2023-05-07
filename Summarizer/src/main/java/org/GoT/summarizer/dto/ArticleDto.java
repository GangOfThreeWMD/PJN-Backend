package org.GoT.summarizer.dto;

import java.util.Objects;
import java.util.Optional;

public class ArticleDto {
    private final String title;
    private final String content;
    private final String link;
    private final String urlToImage;

    // Constructor for the original version of the News record
    public ArticleDto(String title, String content, String link) {
        this.title = title;
        this.content = content;
        this.link = link;
        this.urlToImage = null;
    }

    // Constructor for the new version of the News record
    public ArticleDto(String title, String content, String link, String urlToImage) {
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
        if (this.urlToImage == null) {
            return Optional.empty();
        }
        return Optional.of(this.urlToImage);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleDto that = (ArticleDto) o;
        return Objects.equals(title, that.title) && Objects.equals(content, that.content) && Objects.equals(link, that.link) && Objects.equals(urlToImage, that.urlToImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, link, urlToImage);
    }
}
