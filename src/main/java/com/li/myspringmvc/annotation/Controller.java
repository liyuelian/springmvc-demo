package com.li.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author 李
 * @version 1.0
 *
 * 该注解用于标识一个控制器组件
 * 1.@Target(ElementType.TYPE) 指定自定义注解可修饰的类型
 * 2.@Retention(RetentionPolicy.RUNTIME) 作用范围，RUNTIME使得可以通过反射获取自定义注解
 * 3.@Documented 在生成文档时，可以看到自定义注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
