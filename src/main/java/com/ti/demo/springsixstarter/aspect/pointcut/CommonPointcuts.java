package com.ti.demo.springsixstarter.aspect.pointcut;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

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
    public static final String POINTCUT_CUSTOM_CLASSPATH = "com.ti.demo.springsixstarter.aspect.custom.";
    public static final String POINTCUT_EXPR_FOR_STUD_SERVICE = POINTCUT_CLASSPATH + "pointcutExprForStudService()";
    public static final String POINTCUT_EXPR_FOR_GETTER_BY_ID = POINTCUT_CLASSPATH + "pointcutExprForGetterById()";
    public static final String POINTCUT_EXPR_FOR_CUSTOM_EXEC_STATS = "@annotation(" + POINTCUT_CUSTOM_CLASSPATH + "ExecutionStatsCustomAspect)";

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

    /**
     * Utiltity method to get method details as string for logging
     * 
     * @param jp - join point
     * @return - stringified method details
     */
    public static String getMethodDetails(JoinPoint jp) {
        String methodName = jp.getSignature().getDeclaringType().getPackageName() + "." + jp.getSignature().getName();
        String returnType = ((MethodSignature) jp.getSignature()).getReturnType().getName();
        return methodName + " " + returnType;
    }

    /**
     * Utiltity method to get method args as list of strings
     * 
     * @param jp - join point
     * @return - list of stringified method args
     */
    public static List<String> getMethodArgs(JoinPoint jp) {
        return Arrays.asList(jp.getArgs()).stream().map(Object::toString).collect(Collectors.toList());
    }

}
