package com.example.webscraper;

import org.GoT.Algorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class WebscraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebscraperApplication.class, args);
    }

    @Bean
    public Algorithm getAlgorithm() throws IOException {
        return new Algorithm();
    }

}
