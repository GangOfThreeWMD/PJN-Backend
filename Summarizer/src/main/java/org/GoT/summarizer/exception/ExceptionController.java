package org.GoT.summarizer.exception;

import jep.JepException;
import org.GoT.webscraper.exception.IncorrectLink;
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format(ex.getMessage()));
    }

    @ExceptionHandler(IncorrectLink.class)
    public ResponseEntity<String> handleWrongLink(IncorrectLink ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Problem with link: %s", ex.getMessage()));
    }

    @ExceptionHandler(JepException.class)
    public ResponseEntity<String> handleAlgorithmError(JepException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Problem with summarize: %s", ex.getMessage()));
    }
}
