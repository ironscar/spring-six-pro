package com.ti.demo.springsixstarter.reactive.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.springsixstarter.reactive.handler.GreetingHandler;

@Configuration
public class GreetingRouter {

    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler handler) {
        return RouterFunctions
            .route(
                RequestPredicates
                    .GET("/app3/reactive")
                    .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                handler::hello
            ).andRoute(
                RequestPredicates
                    .GET("/app3/reactive-client")
                    .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), 
                handler::helloClient
            );
    }
    
}
