package com.ti.demo.domain.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.ti.demo.domain.Coach;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("cricket")
@Lazy
public class CricketCoach implements Coach {

    CricketCoach() {
        log.debug("Cricket coach created");
    }

    @Override
    public String getDailyWorkout() {
        return "practice fast bowling!";
    }
    
}
