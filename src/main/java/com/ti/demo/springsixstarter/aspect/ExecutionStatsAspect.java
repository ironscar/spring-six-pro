package com.ti.demo.springsixstarter.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.ti.demo.springsixstarter.aspect.pointcut.CommonPointcuts;

@Slf4j
@Order(3)
@Aspect
@Component
public class ExecutionStatsAspect {

    /**
     * Method to calculate the execution time for methods
     * 
     * @param pjp - pproceeding join point
     * @throws Throwable
     * @returns result
     */
    @Around(value = CommonPointcuts.POINTCUT_EXPR_FOR_STUD_SERVICE)
    public Object executionStatsAspect(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.currentTimeMillis();

        // make sure the method gets implemented here and return value is stored
        Object result = pjp.proceed();

        long end = System.currentTimeMillis();
        long duration = end - begin;
        log.info("Aspect: Execution status for {} took {} ms", CommonPointcuts.getMethodDetails(pjp), duration);

        return result;
    }

    /**
     * Method to catch and block all exceptions
     * 
     * @param pjp - pproceeding join point
     * @throws Throwable
     * @returns result
     */
    @Around(value = CommonPointcuts.POINTCUT_EXPR_FOR_GETTER_BY_ID)
    public Object blockExceptionsAspect(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        try {
            // make sure the method gets implemented here and return value is stored
            result = pjp.proceed();
        } catch (Exception e) {
            log.error("Exception occurred", e);
        }
        return result;
    }
    
}
