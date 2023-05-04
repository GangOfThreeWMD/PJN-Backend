package org.got.summarizer;

import org.GoT.webscraper.service.Scraper;
import org.GoT.Algorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class SummarizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SummarizerApplication.class, args);
	}

	@Bean
	public Scraper getScrapper() throws IOException {
		return new Scraper(getSummarizeAlgorithm());
	}

	@Bean
	public Algorithm getSummarizeAlgorithm() throws IOException {
		return new Algorithm();
	}

}
