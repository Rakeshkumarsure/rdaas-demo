package com.exl.rdaas.util;

import java.util.UUID;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class RequestIdAspect {
    private static final ThreadLocal<String> requestIdHolder = new ThreadLocal<>();

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}

    @Before("restController()")
    public void setRequestId() {
        requestIdHolder.set(UUID.randomUUID().toString());
    }

    @After("restController()")
    public void clearRequestId() {
        requestIdHolder.remove();
    }

    public static String getRequestId() {
        return requestIdHolder.get();
    }
}
