package com.ti.demo.springsixstarter.reactive.handler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.ti.demo.domain.reactive.Student;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
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
        simpleReactiveOperations();
    }

    private void simpleReactiveOperations() {
        // create a flux
        Flux<Integer> flux = Flux.just(1,2,3,4);

        // map, zip, interval, take
        flux.map(i -> i * 2)
            .zipWith(Flux.interval(Duration.ofMillis(1000L)), (i,j) -> i + j)
            .take(2L)
            .subscribe(i -> log.info("flux value = {}", i));

        // transform with map(sync) & flatmap(async delay merge) operations 
        Flux<Student> flux2 = Flux.fromIterable(Arrays.asList(
            Student.builder().firstName("john").lastName("gell").build(),
            Student.builder().firstName("sarah").lastName("bell").build()
        ));
        flux2
            .map(student -> Student
                .builder()
                .firstName(student.getFirstName().toUpperCase())
                .lastName(student.getLastName().toUpperCase())
                .build())
            .subscribe(s -> log.info("flux2 map value = {}", s));
        flux2
            .flatMap(student -> Flux.just(
                Student.builder().firstName(student.getFirstName().toUpperCase()).build(),
                Student.builder().firstName(student.getLastName().toUpperCase()).build()
            ).delayElements(Duration.ofSeconds(1L)))
            .subscribe(s -> log.info("flux2 flatMap value = {}", s));

        // concat, combineLatest, merge
        Flux<Integer> flux3 = Flux
            .range(1,5)
            .filter(x -> x % 2 == 0)
            .delayElements(Duration.ofMillis(500L));
        Flux<Integer> flux4 = Flux
            .range(1,5)
            .filter(x -> x % 2 != 0)
            .delayElements(Duration.ofMillis(300L));
        Flux.concat(flux3, flux4)
            .subscribe(s -> log.info("flux3&4 concat value = {}", s));
        Flux.combineLatest(flux4, flux3, (a, b) -> a.toString() + b.toString())
            .subscribe(s -> log.info("flux3&4 combineLatest value = {}", s));
        Flux.merge(flux3, flux4)
            .subscribe(s -> log.info("flux3&4 merge value = {}", s));
    }

    public Mono<ServerResponse> complexClientOperation(ServerRequest request) {
        log.info("Started complex operation");
        List<String> errors = new ArrayList<>();

        // perform a save operation that goes parallel to both the below
        log.info("Start save operation");
        Mono<Void> m1 =  request
            .bodyToMono(Student.class)
            .flatMap(student -> webClient
            .post()
            .uri("/reactiv/app2/student")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(student)
            .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
            .retrieve()
            .bodyToMono(Void.class)
        ).doOnSuccess(res -> log.info("completed save operation"))
        .onErrorResume(e -> {
            log.error(e.getMessage());
            errors.add("save operation failed: " + e.getMessage());
            return Mono.empty();
        });

        // get id=1 and delete the student
        log.info("Start get1 + delete operation");
        Mono<Void> m2 = webClient
            .get()
            .uri("/reactive/app2/student/" + request.queryParam("id").orElse(""))
            .accept(MediaType.APPLICATION_JSON)
            .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
            .retrieve()
            .bodyToMono(Student.class)
            .flatMap(student -> {
                log.info("completed get1 operation & start delete operation");
                if (student == null) {
                    return Mono.error(new Throwable("no students found for get1"));
                }
                return Mono.just(student.getId());
            }).flatMap(id -> webClient
                .delete()
                .uri("/reactive/app2/student/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
                .retrieve()
                .bodyToMono(Void.class)
            ).doOnSuccess(res -> log.info("completed delete operation"))
            .onErrorResume(e -> {
                log.error(e.getMessage());
                errors.add("get1 + delete operation failed: " + e.getMessage());
                return Mono.empty();
            });

        // get lname=Bell and update last name to Bell2 for both
        log.info("Start get2 + update operation");
        Mono<Void> m3 = webClient
            .get()
            .uri("/reactive/app2/student?lname=" + request.queryParam("lname1").orElse(""))
            .accept(MediaType.APPLICATION_JSON)
            .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Student>>() {})
            .flatMap(students -> {
                log.info("completed get2 operation & start update operation");
                if (CollectionUtils.isEmpty(students)) {
                    return Mono.error(new Throwable("no students found for get1"));
                }
                String ids = students.stream()
                    .map(student -> student.getId().toString())
                    .reduce("", (tempIds, studentId) -> tempIds.equals("") ? studentId : tempIds + "," + studentId);
                return Mono.just(ids);
            }).flatMap(ids -> webClient
                .put()
                .uri("/reactive/app2/student?lname=" + request.queryParam("lname2").orElse("") + "&ids=" + ids)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBasicAuth(internalUser, internalPass))
                .retrieve()
                .bodyToMono(Void.class)
            ).doOnSuccess(res -> log.info("completed update operation"))
            .onErrorResume(e -> {
                log.error(e.getMessage());
                errors.add("get2 + update operation failed: " + e.getMessage());
                return Mono.empty();
            });

        log.info("Log at end of complex operation");

        return Flux.merge(m1, m2, m3)
            .doFinally(f -> {
                if (CollectionUtils.isEmpty(errors)) {
                    log.info("complex operation succeeded");
                } else {
                    log.info("complex operation failed: " + errors);
                }
            }).then(CollectionUtils.isEmpty(errors) 
                ? ServerResponse.ok().build() 
                : ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
}
