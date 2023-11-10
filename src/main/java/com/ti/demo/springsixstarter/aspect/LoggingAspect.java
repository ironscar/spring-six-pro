package com.ti.demo.springsixstarter.aspect;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Aspect to add logging before execution of any student service method
     * Assumes that all params passed override the toString method
     * 
     * @param jp - join point
     */
    @Before("execution(* com.ti.demo.springsixstarter.service.StudentService.*(..))")
    public void addLoggingBeforeStudentServiceMethods(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        String returnType = ((MethodSignature) jp.getSignature()).getReturnType().getName();
        List<String> paramList = Arrays.asList(jp.getArgs()).stream().map(Object::toString).collect(Collectors.toList());
        log.info("Aspect: Logging {} {} before execution with args: {}", returnType, methodName, paramList);  
    }

    /**
     * Aspect to add logging after execution of any student service method
     * Assumes that return value object overrides the toString method
     * 
     * @param jp - join point
     * @param returnVal - the return value
     */
    @AfterReturning(value = "execution(* com.ti.demo.springsixstarter.service.StudentService.*(..))", returning = "returnVal")
    public void addLoggingAfterReturnStudentServiceMethods(JoinPoint jp, Object returnVal) {
        String methodName = jp.getSignature().getName();
        String returnType = ((MethodSignature) jp.getSignature()).getReturnType().getName();
        if (returnVal != null) {
            log.info("Aspect: Logging {} {} after execution with return value {}", returnType, methodName, returnVal);
        } else {
            List<String> paramList = Arrays.asList(jp.getArgs()).stream().map(Object::toString).collect(Collectors.toList());
            log.info("Aspect: Logging {} {} after execution with possibly updated args {}", returnType, methodName, paramList);
        }
    }

}
