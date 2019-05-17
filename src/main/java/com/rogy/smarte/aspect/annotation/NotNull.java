package com.rogy.smarte.aspect.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 设置在不同目标上面有着不同的作用<br>
 * 字段：说明该字段需要校验非空<br>
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD})
public @interface NotNull {
    /**
     * 作用于字段上，表面当前注解对哪一些组有效
     */
    String[] groups();

}
