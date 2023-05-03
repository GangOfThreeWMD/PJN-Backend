package org.got.summarizer.service;

import org.got.summarizer.dto.ArticleDto;
import org.got.summarizer.dto.Source;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummarizeService {
    public List<ArticleDto> getSummaries() {
        return null;
    }

    public List<ArticleDto> getSummaries(Source source) {
        return null;
    }

    public void forceRefresh() {

    }

    public void forceRefresh(List<Source> sources) {

    }

    public List<Source> getSources() {
        return List.of(Source.bbc, Source.wikipedia);
    }
}
