package org.got.summarizer.controller;

import org.got.summarizer.dto.ArticleDto;
import org.GoT.webscraper.model.Source;
import org.got.summarizer.service.SummarizeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/summarizer/api/v1")
public class SummarizerController {
    private final SummarizeService summarizeService;

    public SummarizerController(SummarizeService summarizeService) {
        this.summarizeService = summarizeService;
    }

    @GetMapping( value ={"", "/{src}"})
    public ResponseEntity<List<ArticleDto>> getSummaries(@PathVariable(required = false) String src) {
        if (src == null) {
           return new ResponseEntity<>(this.summarizeService.getSummaries(), HttpStatus.OK);
        }
        Source source = Source.valueOf(src.toLowerCase());

        return new ResponseEntity<>(this.summarizeService.getSummaries(source), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public void refresh() {
        this.summarizeService.forceRefresh();
    }

    @GetMapping("/sources")
    public ResponseEntity<List<Source>> getSourcesList() {
        return new ResponseEntity<>(this.summarizeService.getSources(), HttpStatus.OK);
    }

}
