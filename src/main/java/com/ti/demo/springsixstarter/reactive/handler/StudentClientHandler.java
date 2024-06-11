package com.ti.demo.springsixstarter.reactive.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
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
        Mono<Void> m1 = webClient
            .post()
            .uri("/reactive/app2/student")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(Student.builder().firstName("Fandom").lastName("Landom").email("flandom@rand.com").build())
            .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
            .retrieve()
            .bodyToMono(Void.class)
            .onErrorComplete(e -> {
                log.error(e.getMessage());
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "save operation failed: " + e.getMessage());
            });

        // get id=1 of student for call 1 and get lname=Bell for call 2
        Mono<List<Student>> ms1 = webClient
            .get()
            .uri("/reactive/app2/student/1")
            .accept(MediaType.APPLICATION_JSON)
            .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Student>>() {})
            .onErrorComplete(e -> {
                log.error(e.getMessage());
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "get1 operation failed: " + e.getMessage());
            });
        Mono<List<Student>> ms2 = webClient
            .get()
            .uri("/reactive/app2/student?lname=Bell")
            .accept(MediaType.APPLICATION_JSON)
            .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Student>>() {})
            .onErrorComplete(e -> {
                log.error(e.getMessage());
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "get2 operation failed: " + e.getMessage());
            });
        // complete these two in parallel
        List<Student> s1 = ms1.block();
        List<Student> s2 = ms2.block();
        
        // update bulk for call 2 result as lname = Bell2 and delete result of call 1 in parallel
        Mono<Void> m3 = null;
        Mono<Void> m4 = null;
        if (!CollectionUtils.isEmpty(s2)) {
            String ids = s2.stream()
                .map(student -> student.getId().toString())
                .reduce(null, (tempIds, studentId) -> (tempIds != null ? "," : "") + studentId);
            m3 = webClient
            .put()
            .uri("/reactive/app2/student?lname=Bell2&ids=" + ids)
            .accept(MediaType.APPLICATION_JSON)
            .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
            .retrieve()
            .bodyToMono(Void.class)
            .onErrorComplete(e -> {
                log.error(e.getMessage());
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "update operation failed: " + e.getMessage());
            });
        }
        if (!CollectionUtils.isEmpty(s1)) {
            String ids = s1.stream()
                .map(student -> student.getId().toString())
                .reduce(null, (tempIds, studentId) -> (tempIds != null ? "," : "") + studentId);
            m4 = webClient
                .delete()
                .uri("/reactive/app2/student/" + ids)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorComplete(e -> {
                    log.error(e.getMessage());
                    throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, 
                        "delete operation failed: " + e.getMessage());
                });
        }
        if (m3 != null && m4 != null) {
            m3.and(m4).block();
        }

        // complete the save parallel to all this
        m1.block();

        return ServerResponse.ok().build();
    }
    
}
