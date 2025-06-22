package com.hsbc.homewk.trans.common;

import com.hsbc.homewk.trans.api.TransactionAPI;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class ResultAspect {
    private final static Logger log = LoggerFactory.getLogger(TransactionAPI.class);

    // pointcut连接点使用注解： @ResultHandler
    @Around("@annotation(ResultHandler)")
    public Object autoAppendException(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(">>调用方法：{}", joinPoint.getSignature().getName());
        log.info(">>目标对象：{}", joinPoint.getTarget());
       // log.info(">>首个参数：{}", joinPoint.getArgs().length == 0 ? "无" : joinPoint.getArgs()[0]);

        Object proceed = null;
        try {
            proceed = joinPoint.proceed();
        } catch (Exception e) {
            log.error(">>调用失败：", e);
            return Result.fail(e);
        }
        log.info(">>调用完毕。");
        return proceed;
    }
}