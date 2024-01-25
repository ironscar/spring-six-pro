package com.ti.demo.springsixstarter.reactive.handler;

import java.util.Arrays;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.exception.GreetingException;
import com.ti.demo.domain.reactive.Greeting;
import com.ti.demo.springsixstarter.reactive.service.GreetingService;
import com.ti.demo.springsixstarter.util.SpringSixConstants;

import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

    private GreetingService greetingService;

    GreetingHandler(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public Mono<ServerResponse> getGreetings(ServerRequest request) {
        try {
            return greetingService.getGreetings(request.queryParam(SpringSixConstants.RECIPIENT_PARAM))
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
                ).onErrorMap(e -> new GreetingException(SpringSixConstants.CUSTOM_PREFIX + e.getMessage()));
        } catch (Exception e) {
            throw new GreetingException(e.getMessage());
        }
    }

    public Mono<ServerResponse> saveGreeting(ServerRequest request) {
        try {
            return request.bodyToMono(Greeting.class)
                .flatMap(greeting -> greetingService.saveGreeting(greeting))
                .flatMap(res -> ServerResponse.ok().build())
                .onErrorMap(e -> new GreetingException(SpringSixConstants.CUSTOM_PREFIX + e.getMessage()));
        } catch (Exception e) {
            throw new GreetingException(e.getMessage());
        }
    }

    public Mono<ServerResponse> updateGreeting(ServerRequest request) {
        try {
            return request.bodyToMono(Greeting.class)
                .flatMap(greeting -> greetingService.updateGreeting(
                    Integer.parseInt(request.pathVariable("id")), 
                    greeting
                )).flatMap(res -> ServerResponse.ok().build())
                .onErrorMap(e -> new GreetingException(SpringSixConstants.CUSTOM_PREFIX + e.getMessage()));
        } catch (Exception e) {
            throw new GreetingException(e.getMessage());
        }
    }

    public Mono<ServerResponse> updateGreetings(ServerRequest request) {
        try {
            return greetingService.updateGreetings(
                Arrays.asList(request.queryParam("ids").orElse("").split(","))
                    .stream()
                    .map(Integer::parseInt)
                    .toList(),
                request.queryParam(SpringSixConstants.RECIPIENT_PARAM).orElse(null)
            ).flatMap(res -> ServerResponse.ok().build())
            .onErrorMap(e -> new GreetingException(SpringSixConstants.CUSTOM_PREFIX + e.getMessage()));
        } catch (Exception e) {
            throw new GreetingException(e.getMessage());
        }
    }

    public Mono<ServerResponse> deleteGreetingById(ServerRequest request) {
        try {
            return greetingService.deleteGreetingById(Integer.parseInt(request.pathVariable("id")))
                .flatMap(res -> ServerResponse.ok().build())
                .onErrorMap(e -> new GreetingException(SpringSixConstants.CUSTOM_PREFIX + e.getMessage()));
        } catch (Exception e) {
            throw new GreetingException(e.getMessage());
        }
    }

    public Mono<ServerResponse> deleteGreetings(ServerRequest request) {
        try {
            return greetingService.deleteGreetings(
                Arrays.asList(request.queryParam("ids").orElse("").split(","))
                    .stream()
                    .map(Integer::parseInt)
                    .toList()
            ).flatMap(res -> ServerResponse.ok().build())
            .onErrorMap(e -> new GreetingException(SpringSixConstants.CUSTOM_PREFIX + e.getMessage()));
        } catch (Exception e) {
            throw new GreetingException(e.getMessage());
        }
    }
    
}
