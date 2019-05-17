package com.rogy.smarte.aspect;

import java.util.Objects;

public class ErrorInfo {
    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数名
     */
    private String paramName;

    /**
     * 属性名
     */
    private String fieldName;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
//        if (Objects.nonNull(className)) {
//            builder.append("类").append(className);
//        }
//        if (Objects.nonNull(methodName)) {
//            builder.append("的方法").append(methodName);
//        }
        if (Objects.nonNull(paramName)) {
            builder.append("参数").append(paramName);
        }
        if (Objects.nonNull(fieldName)) {
            builder.append("属性").append(fieldName);
        }

        return builder.toString();
    }

}
