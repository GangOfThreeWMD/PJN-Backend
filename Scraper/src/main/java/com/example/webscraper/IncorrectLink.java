package com.example.webscraper;

public class IncorrectLink extends RuntimeException {
    public IncorrectLink(String message) {
        super(message);
    }
}
