package com.ti.demo.springsixstarter.reactive.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.ti.demo.domain.reactive.Student;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class StudentClientHandler {

    @Value("${app.internal.user}")
    private String internalUser;

    @Value("${app.internal.password}")
    private String internalPass;

    private WebClient webClient;

    @Autowired
    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ServerResponse> complexClientOperation(ServerRequest request) {
        // perform a save operation that goes parallel to both the below

        // get id=1 of student for call 1 and get lname=Bell for call 2
        
        // update bulk for call 2 result as lname = Bell2 and delete result of call 1 in parallel
        return null;
    }
    
}
