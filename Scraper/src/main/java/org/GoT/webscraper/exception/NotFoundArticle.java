package org.GoT.webscraper.exception;

public class NotFoundArticle extends RuntimeException {
    public NotFoundArticle(String message) {
        super(message);
    }
}
