package com.example.webscraper;

public class NotFoundArticle extends RuntimeException {
    public NotFoundArticle(String message) {
        super(message);
    }
}
