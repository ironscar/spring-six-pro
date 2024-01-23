package com.ti.demo.springsixstarter.reactive.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.springsixstarter.reactive.handler.GreetingHandler;

@Configuration
public class GreetingRouter {

    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler handler) {
        return RouterFunctions.route()
            .GET((
                path("/app3/reactive")
                    .or(path("/app3/reactive/")))
                .and(accept(MediaType.APPLICATION_JSON)), 
                handler::getGreetings
            ).GET((
                path("/app3/reactive/{id}")
                    .or(path("/app3/reactive/{id}/")))
                .and(accept(MediaType.APPLICATION_JSON)), 
                handler::getGreetingById
            ).GET((
                path("/app3/reactive/client")
                    .or(path("/app3/reactive/client/")))
                .and(accept(MediaType.APPLICATION_JSON)), 
                handler::helloClient
            ).build();
    }

}
