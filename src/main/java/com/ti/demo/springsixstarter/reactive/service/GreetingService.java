package com.ti.demo.springsixstarter.reactive.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ti.demo.domain.reactive.Greeting;

import reactor.core.publisher.Mono;

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
        if (id <= 0) {
            return Mono.error(() -> new IllegalArgumentException("id must be greater than zero"));
        }
        return Mono.just(greetings.get(id-1));
    }
    
}
