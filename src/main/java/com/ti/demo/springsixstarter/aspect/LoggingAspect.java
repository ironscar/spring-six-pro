package com.ti.demo.springsixstarter.aspect;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.ti.demo.springsixstarter.aspect.pointcut.CommonPointcuts;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(1)
@Aspect
@Component
public class LoggingAspect {

    /**
     * Aspect to add logging before execution of any student service method
     * Assumes that all params passed override the toStrimng method
     * 
     * @param jp - join point
     */
    @Before(CommonPointcuts.POINTCUT_EXPR_FOR_STUD_SERVICE)
    public void addLoggingBeforeStudentServiceMethods(JoinPoint jp) {
        List<String> paramList = Arrays.asList(jp.getArgs()).stream().map(Object::toString).collect(Collectors.toList());
        log.info("Aspect: Logging {} before execution with args: {}", CommonPointcuts.getMethodDetails(jp), paramList);
    }

    /**
     * Aspect to add logging after successful execution of any student service method
     * Assumes that return value object overrides the toString method
     * Skipped curently for getById to demonstrate combining pointcuts
     * 
     * @param jp - join point
     * @param returnVal - the return value
     */
    @AfterReturning(
        value = CommonPointcuts.POINTCUT_EXPR_FOR_STUD_SERVICE + " && !" + CommonPointcuts.POINTCUT_EXPR_FOR_GETTER_BY_ID, 
        returning = "returnVal"
    )
    public void addLoggingAfterReturnStudentServiceMethods(JoinPoint jp, Object returnVal) {
        if (returnVal != null) {
            log.info("Aspect: Logging {} after execution with return value {}", CommonPointcuts.getMethodDetails(jp), returnVal);
        } else {
            log.info("Aspect: Logging {} after execution with possibly updated args {}", CommonPointcuts.getMethodDetails(jp), CommonPointcuts.getMethodArgs(jp));
        }
    }

    /**
     * Aspect to add logging after throwing an exception
     * 
     * @param jp - join point
     */
    @AfterThrowing(
        value = CommonPointcuts.POINTCUT_EXPR_FOR_GETTER_BY_ID, 
        throwing = "ex"
    )
    public void addLoggingAfterThrowingForGetterById(JoinPoint jp, Exception ex) {
        log.info("Aspect: Loggin {} after throwing exception ({})", CommonPointcuts.getMethodDetails(jp), ex.getMessage());
    }

}
