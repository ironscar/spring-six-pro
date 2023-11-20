package com.ti.demo.springsixstarter.aspect.pointcut;

import org.aspectj.lang.annotation.Pointcut;

import lombok.experimental.UtilityClass;

/**
 * Allows referring to the same pointcut expression for multiple methods
 */
@UtilityClass
public class CommonPointcuts {

    /**
     * static strings to quickly use rather than copying package name + class name + method name
     */
    public static final String POINTCUT_CLASSPATH = "com.ti.demo.springsixstarter.aspect.pointcut.CommonPointcuts.";
    public static final String POINTCUT_EXPR_FOR_STUD_SERVICE = POINTCUT_CLASSPATH + "pointcutExprForStudService()";
    public static final String POINTCUT_EXPR_FOR_GETTER_BY_ID = POINTCUT_CLASSPATH + "pointcutExprForGetterById()";

    /**
     * Pointcut for all methods defined by student service
     */
    @Pointcut("execution(* com.ti.demo.springsixstarter.service.StudentService.*(..))")
    public static void pointcutExprForStudService() {}

    /**
     * Pointcut for all get methods with an Integer argument
     * Just there to show combining of exprs
     */
    @Pointcut("execution(* com.ti.demo.springsixstarter.service.StudentService.get*(Integer))")
    public static void pointcutExprForGetterById() {}

}
