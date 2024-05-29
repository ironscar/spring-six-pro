package com.ti.demo.springsixstarter.reactive.handler;

import java.util.Date;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.exception.GreetingErrorResponse;
import com.ti.demo.domain.reactive.Greeting;

import reactor.core.publisher.Mono;

@Component
public class GreetingClientHandler {

    private WebClient client;

    GreetingClientHandler(WebClient client) {
        this.client = client;
    }

    public Mono<ServerResponse> greetingByClient(ServerRequest request) {
        return client
            .get()
            .uri(
                "/reactive/app3/greeting" +
                request.queryParam("recipient")
                    .map(val -> {
                        return "?recipient=" + val;
                    }).orElse("")
            ).accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Greeting>>() {})
            .flatMap(greetings -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(
                    greetings.stream()
                        .map(greeting -> {
                            greeting.setMessage("From Client: " + greeting.getMessage());
                            return greeting;
                        }).toList()
                ))
            ).onErrorResume(e -> ServerResponse
            .badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(GreetingErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .timestamp(new Date())
                .build())
            );
    }
    
}
