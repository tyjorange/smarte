package com.rogy.smarte.aspect;

import com.rogy.smarte.aspect.annotation.CheckNull;
import com.rogy.smarte.aspect.annotation.NotNull;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;

@Component
@Aspect
public class CheckNullAspect {
    private static final ThreadLocal<ErrorInfo> LOCAL_INFO = ThreadLocal.withInitial(ErrorInfo::new);

    // 拦截带@CheckNull的方法
    @Pointcut("@annotation(com.rogy.smarte.aspect.annotation.CheckNull)")
    private void annotationPointCut() {
    }

    // 环绕切面
    @Around("annotationPointCut()")
    public Object process(ProceedingJoinPoint pjp) throws Throwable {
        // 1、获取目标方法
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        // 1.1、设置info的类名和方法名
        ErrorInfo errorInfo = LOCAL_INFO.get();
        errorInfo.setClassName(targetMethod.getDeclaringClass().getName());
        errorInfo.setMethodName(targetMethod.getName());
        // 2、获取方法参数和参数值
        Parameter[] parameters = targetMethod.getParameters();
        Object[] args = pjp.getArgs();
        // 3、校验每个参数
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            // 3.1、获取参数注解
            CheckNull annotation = parameter.getAnnotation(CheckNull.class);
            // 3.1、不存在@NotNull，忽略
            if (Objects.isNull(annotation)) {
                continue;
            }
            // 3.2、校验参数
            boolean verify = this.verifyParameter(annotation.group(), parameter.getName(), args[i]);
            if (!verify) {
                throw new NullPointerException(LOCAL_INFO.get().toString() + "为空！");
            }
        }
        // finish、执行目标方法
        return pjp.proceed();
    }

    private boolean verifyParameter(String groupName, String paramName, Object paramValue) throws Exception {
        // 1、设置info的参数名
        ErrorInfo errorInfo = LOCAL_INFO.get();
        errorInfo.setParamName(paramName);
        // 2、校验参数本身是否为null
        if (Objects.isNull(paramValue)) {
            return false;
        }
        // 3、如果参数注解的group属性为""，则无需校验参数属性
        if (Objects.equals(groupName, "")) {
            return true;
        }
        // 4、校验类的字段
        Class<?> clazz = paramValue.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            NotNull fieldAnnotation = field.getAnnotation(NotNull.class);
            // 3.1、没有注解或者注解不包含指定分组
            if (Objects.isNull(fieldAnnotation) || !Arrays.asList(fieldAnnotation.groups()).contains(groupName)) {
                // 不需要校验
                continue;
            }
            field.setAccessible(true);
            // 3.2、获取属性值
            Object value = field.get(paramValue);
            if (Objects.isNull(value)) {
                //获取属性名
                String name = field.getName();
                errorInfo.setFieldName(name);
                return false;
            }
        }
        // 5、校验通过
        return true;
    }

}
