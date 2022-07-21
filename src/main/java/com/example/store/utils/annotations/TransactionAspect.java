package com.example.store.utils.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.example.store.exceptions.TransactionException;

@Aspect
@Component
public class TransactionAspect {

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @Pointcut("@annotation(com.example.store.utils.annotations.Transaction)")
    private void onMethod() {}

    @Pointcut("@annotation(com.example.store.utils.annotations.Transaction)")
    private void onClass() {}

    @Pointcut("onMethod() || onClass()")
    private void anyType() {}

    @Around("anyType()")
    public Object transactionOnMethod(ProceedingJoinPoint joinPoint) {

        transactionTemplate = new TransactionTemplate(transactionManager);
        try {
            transactionTemplate.execute(status -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new TransactionException();
                }
            });
            return null;
        } catch (Throwable throwable) {
            throw new TransactionException();
        }
    }

}
