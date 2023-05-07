package org.GoT.webscraper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(IncorrectSource.class)
    public ResponseEntity<String> handleIncorrectSource(IncorrectSource ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleConflict(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
