package org.GoT.summarizer.controller;

import io.micrometer.common.util.StringUtils;
import org.GoT.summarizer.dto.ArticleDto;
import org.GoT.summarizer.service.SummarizeService;
import org.GoT.webscraper.model.Source;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/summarizer/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SummarizerController {
    private final SummarizeService summarizeService;

    public SummarizerController(SummarizeService summarizeService) {
        this.summarizeService = summarizeService;
    }

    @GetMapping( value ={"", "/{src}"})
    public ResponseEntity<List<ArticleDto>> getSummaries(@PathVariable(required = false) Optional<Source> src,
                                                         @RequestParam(required = false) Optional<Long> limit) {
        if (src.isEmpty() && limit.isEmpty()) {
           return new ResponseEntity<>(this.summarizeService.getSummaries(), HttpStatus.OK);
        }

        if (src.isPresent()) {
            if (limit.isEmpty()) {
                return new ResponseEntity<>(this.summarizeService.getSummaries(src.get()), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(this.summarizeService.getSummaries(src.get(), limit.get()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(this.summarizeService.getSummaries(limit.get()), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public void refresh() {
        this.summarizeService.forceRefresh();
    }

    @GetMapping("/refresh/{source}")
    public void refresh(@PathVariable Source source) {
        this.summarizeService.forceRefresh(source);
    }

    @GetMapping("/sources")
    public ResponseEntity<List<Source>> getSourcesList() {
        return new ResponseEntity<>(this.summarizeService.getSources(), HttpStatus.OK);
    }

    @GetMapping("/length/{length}")
    public void setLength(@PathVariable(value = "length") int length) {
        this.summarizeService.setLength(length);
    }


    @PostMapping()
    public ResponseEntity<String> summarizeText(@RequestBody String text) {
        if(StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("Text is empty!");
        }
        return new ResponseEntity<>(this.summarizeService.summarizeText(text), HttpStatus.CREATED);
    }
}
