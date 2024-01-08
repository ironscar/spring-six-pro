package com.ti.demo.springsixstarter.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration
public class AppSecurityConfig {

    private static final String STUDENT_BASE_PATH = "/app2/student";
    private static final String STUDENT_BASE_PATH2 = "/app2/student/*";
    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_TEACHER = "TEACHER";
    private static final String ROLE_ADMIN = "ADMIN";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(authz -> authz // setup role access
                .requestMatchers(HttpMethod.GET, "/actuator", "/actuator/*").hasAnyRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.GET, STUDENT_BASE_PATH, STUDENT_BASE_PATH2).hasAnyRole(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
                .requestMatchers(HttpMethod.POST, STUDENT_BASE_PATH, STUDENT_BASE_PATH2).hasAnyRole(ROLE_TEACHER, ROLE_ADMIN)
                .requestMatchers(HttpMethod.PUT, STUDENT_BASE_PATH, STUDENT_BASE_PATH2).hasAnyRole(ROLE_TEACHER)
                .requestMatchers(HttpMethod.DELETE, STUDENT_BASE_PATH, STUDENT_BASE_PATH2).hasAnyRole(ROLE_ADMIN)
                .anyRequest().permitAll())
            .httpBasic(Customizer.withDefaults()) // use basic auth
            .csrf(CsrfConfigurer::disable) // disable CSRF
            .build();
    }

    @Bean
    @Profile("memory-security")
    public UserDetailsManager memoryUserDetailsManager() {
        UserDetails user1 = User.builder().username("john").password("{noop}johnpass").roles(ROLE_STUDENT).build();
        UserDetails user2 = User.builder().username("amy").password("{noop}amypass").roles(ROLE_TEACHER).build();
        UserDetails user3 = User.builder().username("prince").password("{noop}princepass").roles(ROLE_TEACHER, ROLE_ADMIN).build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }

    @Bean
    @Profile("norm-jdbc-security")
    public UserDetailsManager jdbcUserDetailsManager(DataSource ds) {
        return new JdbcUserDetailsManager(ds);
    }

    @Bean
    @Profile("custom-jdbc-security")
    public UserDetailsManager customJdbcUserDetailsManager(DataSource ds) {
        JdbcUserDetailsManager judm = new JdbcUserDetailsManager(ds);
        judm.setUsersByUsernameQuery("SELECT userid, pwd, CASE WHEN enabled = 'Y' THEN 1 ELSE 0 END enabled FROM custom_users WHERE userid = ?");
        judm.setAuthoritiesByUsernameQuery("SELECT userid, CONCAT('ROLE_', role) role FROM custom_authorities WHERE userid = ?");
        return judm;
    }
    
}
