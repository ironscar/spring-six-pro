package com.ti.demo.springsixstarter.reactive.handler;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.reactive.Greeting;

import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

    private WebClient client;

    GreetingHandler(WebClient client) {
        this.client = client;
    }

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new Greeting("Hello Reactive Greeting!")));
    }

    public Mono<ServerResponse> helloClient(ServerRequest request) {
        return ReactiveSecurityContextHolder
            .getContext()
            .flatMap(context -> Mono.just(context.getAuthentication()))
            .flatMap(auth -> client
                .get()
                .uri("/app3/reactive")
                .headers(headers -> headers.setBasicAuth(auth.getName(), "johnpass"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Greeting.class))
            .flatMap(greeting -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(
                    new Greeting(greeting.getMessage().replace("Reactive", "Reactive Client"))
                )));
    }
    
}
