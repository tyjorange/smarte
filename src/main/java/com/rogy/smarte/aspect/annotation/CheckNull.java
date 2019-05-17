package com.rogy.smarte.aspect.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 设置在不同目标上面有着不同的作用<br>
 * 方法：说明该方法需要校验带该注解参数的非空<br>
 * 参数：说明该参数需要校验非空（自身非空、属性非空）<br>
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, PARAMETER})
public @interface CheckNull {
    /**
     * 作用于方法和参数上，表面当前校验属于哪一组
     * 不设置的话，无需校验参数的属性
     */
    String group() default "";

}
