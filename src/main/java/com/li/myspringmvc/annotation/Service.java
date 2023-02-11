package com.li.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author 李
 * @version 1.0
 * @Service 注解用于标识一个Service对象，并注入到spring容器中
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
