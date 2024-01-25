package com.ti.demo.springsixstarter.reactive.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ti.demo.domain.reactive.Greeting;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GreetingService {

    private static List<Greeting> greetings = new ArrayList<>();

    static {
        greetings.add(Greeting.builder().message("Hi 1").recipient("John").build());
        greetings.add(Greeting.builder().message("Hi 2").recipient("Amy").build());
    }

    public Mono<List<Greeting>> getGreetings(Optional<String> recipient) {
        return recipient.isPresent()
            ? Mono.just(greetings.stream().filter(greeting -> recipient.get().equals(greeting.getRecipient())).toList())
            : Mono.just(greetings);
    }

    public Mono<Greeting> getGreetingById(int id) {
        if (id <= 0 || id > greetings.size()) {
            return Mono.error(() -> new IllegalArgumentException("id doesn't exist"));
        }
        return Mono.just(greetings.get(id-1));
    }

    public Mono<Void> saveGreeting(Greeting greeting) {
        if (greeting == null || !StringUtils.hasText(greeting.getMessage())) {
            return Mono.error(() -> new IllegalArgumentException("greeting message cannot be empty"));
        }
        greetings.add(greeting);
        return Mono.empty();
    }

    public Mono<Void> updateGreeting(int id, Greeting updatedGreeting) {
        if (updatedGreeting == null || !StringUtils.hasText(updatedGreeting.getMessage())) {
            return Mono.error(() -> new IllegalArgumentException("greeting message cannot be empty"));
        }
        greetings.set(id - 1, updatedGreeting);
        return Mono.empty();
    }

    public Mono<Void> updateGreetings(List<Integer> ids, String recipient) {
        if (CollectionUtils.isEmpty(ids) || !StringUtils.hasText(recipient)) {
            return Mono.error(() -> new IllegalArgumentException("ids or recipient cannot be empty"));
        }
        List<Integer> failIds = new ArrayList<>();
        ids.stream()
            .forEach(id -> 
                getGreetingById(id)
                    .doOnError(e -> failIds.add(id))
                    .onErrorResume(e -> Mono.empty())
                    .subscribe(greeting -> greeting.setRecipient(recipient))
            );
        log.warn("Failed ids: {}", failIds);
        return Mono.empty();
    }

    public Mono<Void> deleteGreetingById(int id) {
        if (id <= 0 || id > greetings.size()) {
            return Mono.error(() -> new IllegalArgumentException("id doesn't exist"));
        }
        greetings.remove(id);
        return Mono.empty();
    }

    public Mono<Void> deleteGreetings(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Mono.error(() -> new IllegalArgumentException("ids cannot be empty"));
        }
        List<Integer> failIds = new ArrayList<>();
        ids.forEach(id -> greetings.remove(
            getGreetingById(id)
                .doOnError(e -> failIds.add(id))
                .onErrorResume(e -> Mono.empty())
                .block()
        ));
        log.warn("Failed ids: {}", failIds);
        return Mono.empty();
    }

}
