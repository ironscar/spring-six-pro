package com.ti.demo.springsixstarter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class AppSecurityConfig {

    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_TEACHER = "TEACHER";
    private static final String ROLE_ADMIN = "ADMIN";

    @Value("${app.internal.user}")
    private String internalUser;

    @Value("${app.internal.password}")
    private String internalPass;

    /**
     * this is used for reactive auth
     * 
     * @param http - server http security
     * @return - the security filter chain
     */
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchange -> exchange
                .pathMatchers(HttpMethod.GET, "/actuator", "/actuator/*").hasAnyRole(ROLE_ADMIN)
                .pathMatchers(HttpMethod.GET, "/app3/reactive/client/*").hasAnyRole(ROLE_TEACHER, ROLE_ADMIN)
                .pathMatchers(HttpMethod.GET, "/app3/reactive/*").hasAnyRole(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
                .pathMatchers(HttpMethod.GET, "/app2/student", "/app2/student/*").hasAnyRole(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
                .anyExchange().authenticated())
            .httpBasic(Customizer.withDefaults())
            .csrf(CsrfSpec::disable)
            .build();
    }

    @Bean
    @Profile("memory-security")
    public ReactiveUserDetailsService memoryUserDetailsService() {
        UserDetails user1 = User.builder().username("john").password("{noop}johnpass").roles(ROLE_STUDENT).build();
        UserDetails user2 = User.builder().username("amy").password("{noop}amypass").roles(ROLE_TEACHER).build();
        UserDetails user3 = User.builder().username("prince").password("{noop}princepass").roles(ROLE_TEACHER, ROLE_ADMIN).build();
        UserDetails user4 = User.builder().username(internalUser).password("{noop}" + internalPass).roles(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN).build();
        return new MapReactiveUserDetailsService(user1, user2, user3, user4);
    }

}
