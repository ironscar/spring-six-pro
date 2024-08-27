package com.ti.demo.springsixstarter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

    /**
     * method to create bean for object mapper
     * @return the bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * method to create a bean for the WebClient instance
     *  
     * @return web client bean
     */
    @Bean
    public WebClient getWebClient() {
        return WebClient.builder().baseUrl("http://localhost:8081").build();
    }
    
}
