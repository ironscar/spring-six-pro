package com.ti.demo.springsixstarter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${app.custom.value1}")
    private String customValue1;

    /**
     * method to create a bean to get the custom value from property file
     * showing how bean methods can be defined in configuration classes
     * 
     * @return the bean
     */
    @Bean(name = "customValue1")
    public String getCustomValue1() {
        return "custom " + customValue1;
    }
    
}
