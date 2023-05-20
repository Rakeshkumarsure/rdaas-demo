
package com.exl.rdaas.util;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Order(2)
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
        " || within(@org.springframework.stereotype.Service *)" +
        " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.exl.rdaas.controller..*)" +
        " || within(com.exl.rdaas.service..*)" +
        " || within(com.exl.rdaas.provider..*)" +
        " || within(com.exl.rdaas.util..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL" , RequestIdAspect.getRequestId());
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    	final StopWatch stopWatch = new StopWatch();

        if (log.isDebugEnabled()) {
            log.info("Enter: {}.{} RequestID: {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()), RequestIdAspect.getRequestId());
            
        }
        if (log.isInfoEnabled()) {
            log.info("Enter: {}.{} RequestID: {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), RequestIdAspect.getRequestId());
            
        }
        try {
        	stopWatch.start();
            Object result = joinPoint.proceed();
            stopWatch.stop();
            if (log.isInfoEnabled()) {
                log.info("Exit: {}.{} Execution Time: {} RequestID: {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),stopWatch.getTotalTimeMillis() + " ms" , RequestIdAspect.getRequestId());
            }
            if(log.isDebugEnabled()) {
            	log.info("Exit: {}.{}() with result = {} RequestID: {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result, "Execution Time: ",stopWatch.getTotalTimeMillis() + " ms", RequestIdAspect.getRequestId());
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}() RequestID: {}", Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), RequestIdAspect.getRequestId());
            throw e;
        }
    }
}