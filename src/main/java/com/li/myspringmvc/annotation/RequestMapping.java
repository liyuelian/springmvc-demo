package com.li.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author 李
 * @version 1.0
 * RequestMapping 注解用于指定控制器-方法的映射路径
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
