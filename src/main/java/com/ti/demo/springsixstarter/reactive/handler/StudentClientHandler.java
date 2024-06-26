package com.ti.demo.springsixstarter.reactive.handler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.reactivestreams.Subscription;
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
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class StudentClientHandler {

    @Value("${app.internal.user}")
    private String internalUser;

    @Value("${app.internal.password}")
    private String internalPass;

    private WebClient webClient;

    @Autowired
    public void setWebClient(WebClient webClient) throws InterruptedException {
        this.webClient = webClient;
        // simpleReactiveOperations();
        complexReactiveOperations();
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
    
    private void simpleReactiveOperations() {
        log.info("Started simple reactive operations");

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

        // reactive to blocking
        Integer b1 = flux4.blockFirst();
        log.info("blockfirst log: {}", b1);
        Integer b2 = flux4.blockLast();
        log.info("blockLast log: {}", b2);
        Iterator<Integer> it = flux3.toIterable(1).iterator();
        while(it.hasNext()) {
            log.info("flux to iterable: {}", it.next());
        }

        log.info("Ended simple reactive operations");
    }

    private void complexReactiveOperations() throws InterruptedException {
        log.info("Started complex reactive operations");

        // hot fluxes
        Flux<Long> coldTicks = Flux.interval(Duration.ofSeconds(1)).take(5);
        Flux<Long> hotTicks = coldTicks.share();
        hotTicks.subscribe(tick -> log.info("clock1 " + tick + "s"));
        Thread.sleep(2000);
        hotTicks.subscribe(tick -> log.info("\tclock2 " + tick + "s"));

        // normal main thread execution
        String s1 = " Hello";
        String s2 = " Hello2";
        Flux.just(1,2,3)
            .map(n -> (n.toString() + s1)) // blocking part (could be a long operation)
            .subscribe(v -> log.info("[NORMAL] {} executed first list with value {}", Thread.currentThread().getName(), v));
        Flux.just(4,5,6)
            .map(n -> (n.toString() + s2)) // blocking part (could be a long operation)
            .subscribe(v -> log.info("[NORMAL] {} executed second list with value {}", Thread.currentThread().getName(), v));

        // publishOn scheduler execution
        Scheduler scheduler1 = Schedulers.newBoundedElastic(2, 5, "boundedElasticPublish");
        Flux.just(1,2,3)
            .publishOn(scheduler1)
            .map(n -> (n.toString() + s1)) // blocking part (could be a long operation)
            .subscribe(v -> log.info("[PUBLISH] {} executed first list with value {}", Thread.currentThread().getName(), v));
        Flux.just(4,5,6)
            .publishOn(scheduler1)
            .map(n -> (n.toString() + s2)) // blocking part (could be a long operation)
            .subscribe(v -> log.info("[PUBLISH] {} executed second list with value {}", Thread.currentThread().getName(), v));

        // subscribeOn scheduler execution
        Scheduler scheduler2 = Schedulers.newBoundedElastic(2, 5, "boundedElasticSubscribe");
        Flux.just(1,2,3)
            .map(n -> (n.toString() + s1)) // blocking part (could be a long operation)
            .subscribeOn(scheduler2)
            .subscribe(v -> log.info("[SUBSCRIBE] {} executed first list with value {}", Thread.currentThread().getName(), v));
        Flux.just(4,5,6)
            .map(n -> (n.toString() + s2)) // blocking part (could be a long operation)
            .subscribeOn(scheduler2)
            .subscribe(v -> log.info("[SUBSCRIBE] {} executed second list with value {}", Thread.currentThread().getName(), v));

        // backpressure request 10 at a time
        Flux.range(1,20)
            .delayElements(Duration.ofMillis(200))
            .doOnEach(v -> log.info("Source stream value = {}", v))
            .subscribe(new BaseSubscriber<Integer>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    // get first 10 values on subscribe
                    request(5);
                }

                @Override
                protected void hookOnNext(Integer value) {
                    // simulate processing
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("Backpressure log value = {}", value);

                    // get next 10 values when tenth value
                    if (value % 5 == 0) {
                        request(5);
                    }
                }
            });
    }

}
