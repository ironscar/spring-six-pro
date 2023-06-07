package com.ti.demo.domain.impl;

import org.springframework.stereotype.Component;

import com.ti.demo.domain.Coach;

@Component("tennis")
public class TennisCoach implements Coach {
    
    @Override
    public String getDailyWorkout() {
        return "Practice 30 serves!";
    }

}
