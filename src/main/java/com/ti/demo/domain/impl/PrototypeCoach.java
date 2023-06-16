package com.ti.demo.domain.impl;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ti.demo.domain.Coach;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component("prototype")
@Scope("prototype")
public class PrototypeCoach implements Coach, DisposableBean {

    private String name;

    PrototypeCoach() {
        log.debug("Prototype coach created");
    }

    @Override
    public String getDailyWorkout() {
        return "Do prototyping work!";
    }

    @Override
    public void destroy() throws Exception {
        log.debug("Prototype coach destroyed for: " + name);
    }

}
