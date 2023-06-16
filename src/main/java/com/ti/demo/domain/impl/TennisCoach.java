package com.ti.demo.domain.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.ti.demo.domain.Coach;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("tennis")
@Lazy
public class TennisCoach implements Coach {

    TennisCoach() {
        log.debug("Tennis coach created");
    }
    
    @Override
    public String getDailyWorkout() {
        return "Practice 30 serves!";
    }

}
