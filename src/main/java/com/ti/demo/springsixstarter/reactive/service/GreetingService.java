package com.ti.demo.springsixstarter.reactive.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ti.demo.domain.reactive.Greeting;

@Service
public class GreetingService {

    private static List<Greeting> greetings = new ArrayList<>();

    static {
        greetings.add(Greeting.builder().message("Hi 1").recipient("John").build());
        greetings.add(Greeting.builder().message("Hi 2").recipient("Amy").build());
    }

    public List<Greeting> getGreetings(Optional<String> recipient) {
        return recipient.isPresent()
            ? greetings.stream().filter(greeting -> recipient.get().equals(greeting.getRecipient())).toList()
            : greetings;
    }

    public Greeting getGreetingById(int id) {
        return greetings.get(id);
    }
    
}
