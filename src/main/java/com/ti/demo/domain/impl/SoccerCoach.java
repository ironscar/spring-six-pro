package com.ti.demo.domain.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.ti.demo.domain.Coach;

@Component("soccer")
@Primary
public class SoccerCoach implements Coach {

    @Override
    public String getDailyWorkout() {
        return "Do fifty goal kicks!";
    }
    
}
