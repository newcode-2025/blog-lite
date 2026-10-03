package com.example.bloglite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志记录注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLogRecord {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 操作动作
     */
    String action() default "";

    /**
     * 操作描述
     */
    String description() default "";
}