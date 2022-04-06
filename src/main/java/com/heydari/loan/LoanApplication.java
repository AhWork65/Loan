package com.heydari.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class LoanApplication {
    @Bean
    public WebClient.Builder getWebClientBuilder(){
        return  WebClient.builder();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
    public static void main(String[] args) {
        SpringApplication.run(LoanApplication.class, args);
    }

}
