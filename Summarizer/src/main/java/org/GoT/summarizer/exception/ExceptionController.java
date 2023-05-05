package org.GoT.summarizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleFailParse(NumberFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Wrong request param: %s", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleWrongProvider(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Wrong provider: %s", ex.getMessage()));
    }
}
