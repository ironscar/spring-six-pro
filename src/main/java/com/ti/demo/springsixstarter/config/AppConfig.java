package com.ti.demo.springsixstarter.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AppConfig {

    @Bean
    @Profile("!test")
    public DataSource getDataSource(
        @Value("${app_sql_db_url}") String url,
        @Value("${app_sql_db_user}") String user,
        @Value("${app_sql_db_pass}") String pass
    ) {
        return DataSourceBuilder.create().url(url).username(user).password(pass).build();
    }

    @Bean
    @Profile("test")
    public DataSource getTestDataSource(
        @Value("jdbc:postgresql://test-db") String url,
        @Value("test-user") String user,
        @Value("test-pass") String pass
    ) {
        return DataSourceBuilder.create().url(url).username(user).password(pass).build();
    }
    
}
