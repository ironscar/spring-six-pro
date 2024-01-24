package com.ti.demo.springsixstarter.reactive.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.springsixstarter.reactive.handler.GreetingClientHandler;
import com.ti.demo.springsixstarter.reactive.handler.GreetingHandler;

@Configuration
public class GreetingRouter {

    @Bean
    @Order(2)
    public RouterFunction<ServerResponse> apiRoutes(GreetingHandler handler) {
        return nest(
            path("/app3/reactive"),
            nest(
                accept(MediaType.APPLICATION_JSON),
                route()
                    .GET(path("").or(path("/")), handler::getGreetings)
                    .GET(path("/{id}").or(path("/{id}/")), handler::getGreetingById)
                    .build()
            ));
    }

    /**
     * Method to define routes for accessing via web client
     * Has Order(1) as some routes in the main api like getGreetingsById tries to override the greetingByClient
     * 
     * @param clientHandler - the client handler
     * @return - router function
     */
    @Bean
    @Order(1)
    public RouterFunction<ServerResponse> clientRoutes(GreetingClientHandler clientHandler) {
        return nest(
            path("/app3/reactive/client"),
            nest(
                accept(MediaType.APPLICATION_JSON),
                route()
                    .GET(path("").or(path("/")), clientHandler::greetingByClient)
                    .build()
            ));
    }

}
