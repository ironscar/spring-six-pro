package com.ti.demo.springsixstarter.config;

import java.time.ZoneId;

import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.asyncer.r2dbc.mysql.MySqlConnectionFactoryProvider;

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

    /**
     * method to create the bean to customize timezone of mysql db connection
     * otherwise the connection fails during query
     * 
     * @return connection factory customizer bean
     */
    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer mysqlCustomizer() {
        return builder -> builder.option(MySqlConnectionFactoryProvider.SERVER_ZONE_ID, ZoneId.of("UTC"));
    }
    
}
