package com.ti.demo.domain.impl;

import org.springframework.stereotype.Component;

import com.ti.demo.domain.Coach;

@Component("cricket")
public class CricketCoach implements Coach {

    @Override
    public String getDailyWorkout() {
        return "practice fast bowling!";
    }
    
}
