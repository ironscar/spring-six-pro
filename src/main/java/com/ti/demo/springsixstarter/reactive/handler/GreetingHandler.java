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

import com.ti.demo.domain.exception.StudentErrorResponse;
import com.ti.demo.domain.exception.StudentException;
import com.ti.demo.domain.reactive.Greeting;
import com.ti.demo.springsixstarter.reactive.service.GreetingService;

import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

    private WebClient client;
    private GreetingService greetingService;

    GreetingHandler(WebClient client, GreetingService greetingService) {
        this.client = client;
        this.greetingService = greetingService;
    }

    public Mono<ServerResponse> getGreetings(ServerRequest request) {
        try {
            return Mono.just(
                greetingService.getGreetings(request.queryParam("recipient"))
            ).flatMap(greetings -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(greetings)
            );
        } catch (Exception e) {
            throw new StudentException("incorrect usage");
        }
    }

    public Mono<ServerResponse> getGreetingById(ServerRequest request) {
        try {
            return Mono.just(
                greetingService.getGreetingById(Integer.parseInt(request.pathVariable("id")))
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
        return client
            .get()
            .uri(
                "/app3/reactive" +
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
            .bodyValue(StudentErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .timestamp(new Date())
                .build())
        );
    }
    
}
