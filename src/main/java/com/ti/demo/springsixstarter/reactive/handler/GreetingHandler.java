package com.ti.demo.springsixstarter.reactive.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.exception.GreetingException;
import com.ti.demo.springsixstarter.reactive.service.GreetingService;

import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

    private GreetingService greetingService;

    GreetingHandler(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public Mono<ServerResponse> getGreetings(ServerRequest request) {
        try {
            return greetingService.getGreetings(request.queryParam("recipient"))
                .flatMap(greetings -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(greetings)
                );
        } catch (Exception e) {
            throw new GreetingException("incorrect usage");
        }
    }

    public Mono<ServerResponse> getGreetingById(ServerRequest request) {
        try {
            return greetingService.getGreetingById(Integer.parseInt(request.pathVariable("id")))
                .flatMap(greeting -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(greeting)
                ).onErrorMap(e -> new GreetingException("custom: " + e.getMessage()));
        } catch (Exception e) {
            throw new GreetingException(e.getMessage());
        }
    }
    
}
