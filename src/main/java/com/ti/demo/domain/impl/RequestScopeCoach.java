package com.ti.demo.domain.impl;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.ti.demo.domain.Coach;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("request")
@RequestScope
public class RequestScopeCoach implements Coach, InitializingBean, DisposableBean {

    public static int COUNT = 0;

    private PrototypeCoach prototypeCoach;

    RequestScopeCoach(PrototypeCoach prototypeCoach) {
        this.prototypeCoach = prototypeCoach;
        prototypeCoach.setName("request proto " + (++COUNT));
        log.debug("Request scope coach created: " + COUNT);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("Request scope coach init");
    }

    @Override
    public String getDailyWorkout() {
        return "Do request scope workout!";
    }

    @Override
    public void destroy() throws Exception {
        log.debug("Request scope coach destroyed");
        prototypeCoach.destroy();
    }
    
}
