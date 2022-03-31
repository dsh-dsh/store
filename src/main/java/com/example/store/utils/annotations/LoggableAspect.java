package com.example.store.utils.annotations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggableAspect {

    public static final Logger logger = LogManager.getLogger("methodReturn");

    @Pointcut("@annotation(com.example.store.utils.annotations.Loggable)")
    public void onMethodLog() {
    }

    @Pointcut("@within(com.example.store.utils.annotations.Loggable)")
    public void onClassLog() {
    }

    @Pointcut("onMethodLog() || onClassLog()")
    public void anyTypeLog() {
    }

    @Around("anyTypeLog()")
    public Object loggingResultOfMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        String args = Arrays.toString(joinPoint.getArgs());
        logger.info("{}\narguments - {}\nreturning - {}\n", joinPoint.getSignature(), args, result);
        return result;
    }
}
