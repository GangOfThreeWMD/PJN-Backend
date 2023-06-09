package org.GoT.summarizer;

import org.GoT.Algorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SummarizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SummarizerApplication.class, args);
	}

	@Bean
	public Algorithm getSummarizeAlgorithm() throws IOException {
		return new Algorithm();
	}

}
