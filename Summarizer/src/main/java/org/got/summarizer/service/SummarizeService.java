package org.got.summarizer.service;

import org.GoT.Algorithm;
import org.GoT.webscraper.service.NewsProvider;
import org.GoT.webscraper.service.Scraper;
import org.got.summarizer.dto.ArticleDto;
import org.GoT.webscraper.model.Source;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@Service
public class SummarizeService {
    private final ServiceLoader<NewsProvider> serviceLoader;

    private final Algorithm summarizeAlgorithm;


    public SummarizeService(Scraper scraper, Algorithm algorithm) {
        this.serviceLoader = scraper.getAllProvider();
        this.summarizeAlgorithm = algorithm;
    }

    public List<ArticleDto> getSummaries() {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        for(NewsProvider newsProvider: serviceLoader) {
            articleDtoList.addAll(newsProvider.getArticles()
                    .stream()
                    .map(
                    a -> new ArticleDto(a.title(), summarizeAlgorithm.getSummarize(a.title()), a.link())
            ).toList());
        }

        return articleDtoList;
    }

    public List<ArticleDto> getSummaries(Source source) {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        for(NewsProvider newsProvider: serviceLoader) {
            if (! source.toString().equals(newsProvider.getSource().toString()))
                continue;
            articleDtoList.addAll(newsProvider.getArticles()
                    .stream()
                    .map(
                            a -> new ArticleDto(a.title(), summarizeAlgorithm.getSummarize(a.title()), a.link())
                    ).toList());
        }
        return articleDtoList;
    }

    public void forceRefresh() {

    }

    public void forceRefresh(List<Source> sources) {

    }

    public List<Source> getSources() {
        return serviceLoader.stream()
                .map( p -> p.get().getSource())
                .toList();
    }
}
