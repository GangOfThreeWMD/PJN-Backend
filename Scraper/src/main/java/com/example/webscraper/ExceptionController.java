package com.example.webscraper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NotFoundArticle.class)
    public ResponseEntity<String> handleNoNewsFound(NotFoundArticle ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IncorrectLink.class)
    public ResponseEntity<String> handleIncorrectLink(IncorrectLink ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Incorrect link - %s", ex.getMessage()));
    }
}
