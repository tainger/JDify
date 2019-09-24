package io.terminus.dalaran.console.exception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DalaranExceptionHandler {

    @Pointcut(value = "execution(* io.terminus.dalaran.console.rest.*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(exception)")
    public Object execute(ProceedingJoinPoint joinPoint, DalaranException exception) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceHandleException(exception.value(), e.getMessage());
        }
    }
}
