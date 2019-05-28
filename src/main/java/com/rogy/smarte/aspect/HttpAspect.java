package com.rogy.smarte.aspect;

import com.rogy.smarte.resp.ServerResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by vostor on 2018/10/26.
 */
@Aspect
@Component
public class HttpAspect {
    private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);
    private boolean DEBUG = false;

    //表示DogController.dogList(..)所有[dogList]参数被拦截
    //表示DogController.*(..)所有[方法]参数被拦截
    @Pointcut("execution(public * com.rogy.smarte.controller.*.*.*(..))")
    private void cutPoint() {
        //切点
    }

    /**
     *
     */
    @Before("cutPoint()")
    private void doBefore(JoinPoint joinPoint) {
        if (DEBUG) {
            ServletRequestAttributes att = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest req = Objects.requireNonNull(att).getRequest();
//            String id = req.getSession().getId();
            logger.warn("remoteAddr=[{}] | requestURL=[{}] | Method=[{}] ", req.getRemoteAddr(), req.getRequestURL(), req.getMethod());
            //获取 类名.类方法
//            logger.warn("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            //获取 参数
//            logger.warn("args=[{}]", joinPoint.getArgs());
        }
    }

    /**
     *
     */
    @After("cutPoint()")
    private void doAfter() {
//        logger.warn("After...");
    }

    /**
     *
     */
    @AfterReturning(returning = "object", pointcut = "cutPoint()")
    private void doAfterReturning(Object object) {
        if (DEBUG) {
            if (object instanceof ServerResponse) {
                //获取请求后返回的内容
                ServerResponse respResult = ((ServerResponse) object);
                logger.warn("Response=[{}]", respResult);
            }
        }
    }
}
