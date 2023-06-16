package com.ti.demo.domain.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.ti.demo.domain.Coach;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("soccer")
@Primary
@Lazy
public class SoccerCoach implements Coach {

    SoccerCoach() {
        log.debug("Soccer coach created");
    }

    @Override
    public String getDailyWorkout() {
        return "Do fifty goal kicks!";
    }
    
}
