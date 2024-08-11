package com.ti.demo.springsixstarter.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class AppSecurityConfig {

    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_TEACHER = "TEACHER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STUDENT_URL = "/reactive/app2/student";

    private static String encoderType = null;

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
    @Autowired
    public SecurityWebFilterChain filterChain(Environment env, ReactiveAuthenticationManager authenticationManager, ServerHttpSecurity http) {
        http
            .authorizeExchange(exchange -> exchange
                .pathMatchers(HttpMethod.GET, "/actuator", "/actuator/*").hasAnyRole(ROLE_ADMIN)
                .pathMatchers(HttpMethod.GET, "/reactive/app3/greeting/client/*").hasAnyRole(ROLE_TEACHER, ROLE_ADMIN)
                .pathMatchers(HttpMethod.GET, "/reactive/app3/greeting/*").hasAnyRole(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
                .pathMatchers(HttpMethod.GET, STUDENT_URL, STUDENT_URL + "/*").hasAnyRole(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
                .pathMatchers(HttpMethod.POST, STUDENT_URL + "/client", STUDENT_URL + "/client/*").hasAnyRole(ROLE_ADMIN)
                .pathMatchers(HttpMethod.POST, STUDENT_URL, STUDENT_URL + "/*").hasAnyRole(ROLE_TEACHER, ROLE_ADMIN)
                .pathMatchers(HttpMethod.PUT, STUDENT_URL, STUDENT_URL + "/*").hasAnyRole(ROLE_TEACHER, ROLE_ADMIN)
                .pathMatchers(HttpMethod.DELETE, STUDENT_URL, STUDENT_URL + "/*").hasAnyRole(ROLE_ADMIN)
                .anyExchange().authenticated());

        if (Arrays.asList(env.getActiveProfiles()).contains("custom-r2dbc-security")) {
            http.authenticationManager(authenticationManager);
        }

        return http.httpBasic(Customizer.withDefaults())
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

    @Bean
    @Autowired
    @Profile("custom-r2dbc-security")
    public ReactiveAuthenticationManager authenticationManager(
        @Qualifier("customUserDetailsService") ReactiveUserDetailsService userDetailsService
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    public static String getEncoderType() {
        return encoderType;
    }

    private static PasswordEncoder passwordEncoder() {
        // used for removing prefix from encoded password stored in DB
        AppSecurityConfig.encoderType = encoderType == null ? "{bcrypt}" : encoderType;

        // return actual encoder object
        return new BCryptPasswordEncoder();
    }

}
