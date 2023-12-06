package com.ti.demo.springsixstarter.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.ti.demo.springsixstarter.aspect.pointcut.CommonPointcuts;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(2)
@Aspect
@Component
public class SecondOrderAspect {

    /**
     * Just an aspect that exists to show how to order aspects using @Order
     */
    @Before(CommonPointcuts.POINTCUT_EXPR_FOR_STUD_SERVICE)
    public void addSecondOrderBeforeLog() {
        log.info("Aspect: second order aspect before log");
    }

    /**
     * Just an aspect that exists to show difference of after throwing and after returning
     * after throwing aspect in LoggingAspect
     */
    @AfterReturning(CommonPointcuts.POINTCUT_EXPR_FOR_GETTER_BY_ID)
    public void addSecondOrderAfterReturningLog() {
        log.info("Aspect: second order aspect after returning log");
    }

    /**
     * Just an aspect that exists to show difference of after throwing and after
     * after throwing aspect in LoggingAspect
     */
    @After(CommonPointcuts.POINTCUT_EXPR_FOR_GETTER_BY_ID)
    public void addSecondOrderAfterLog() {
        log.info("Aspect: second order aspect after log");
    }

}
