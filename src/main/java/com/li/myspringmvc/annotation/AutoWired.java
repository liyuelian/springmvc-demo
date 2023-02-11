package com.li.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author 李
 * @version 1.0
 * AutoWired 注解完成对象属性的装配
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoWired {
    String value() default "";
}
