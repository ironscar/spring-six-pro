package com.ti.demo.springsixstarter.reactive.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.exception.StudentErrorResponse;
import com.ti.demo.domain.exception.StudentException;
import com.ti.demo.domain.reactive.Greeting;

import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

    private WebClient client;

    GreetingHandler(WebClient client) {
        this.client = client;
    }

    public Mono<ServerResponse> hello(ServerRequest request) {
        try {
            return Mono.just(
                new Greeting(
                    "Hello Reactive Greeting! " + 
                    request.pathVariable("param1") + "," +
                    request.queryParam("param2").orElse("default2"))
            ).flatMap(greeting -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(greeting)
            );
        } catch (Exception e) {
            throw new StudentException("incorrect usage");
        }
    }

    public Mono<ServerResponse> helloClient(ServerRequest request) {
        return ReactiveSecurityContextHolder
            .getContext()
            .flatMap(context -> Mono.just(context.getAuthentication())
            ).flatMap(auth -> client
                .get()
                .uri(
                    "/app3/" + 
                    request.pathVariable("path") + "/" +
                    request.pathVariable("param1") +
                    request.queryParam("param2").map(val -> "?param2=" + val).orElse("")
                ).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Greeting.class)
            ).flatMap(greeting -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(
                    new Greeting(greeting.getMessage().replace("Reactive", "Reactive Client"))
                ))
            ).onErrorResume(e -> ServerResponse
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(StudentErrorResponse
                    .builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .timestamp(new Date())
                    .build())
            );
    }
    
}
