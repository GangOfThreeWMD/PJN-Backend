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
    public ResponseEntity<List<ArticleDto>> getSummaries(@PathVariable(required = false) String src,
                                                         @RequestParam(required = false) String limit) {
        if (src == null && limit == null) {
           return new ResponseEntity<>(this.summarizeService.getSummaries(), HttpStatus.OK);
        }

        if (src != null) {
            Source source = Source.valueOf(src.toLowerCase());
            if (limit == null) {
                return new ResponseEntity<>(this.summarizeService.getSummaries(source), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(this.summarizeService.getSummaries(source, Long.parseLong(limit)), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(this.summarizeService.getSummaries(Long.parseLong(limit)), HttpStatus.OK);
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

}
