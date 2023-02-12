package com.li.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author 李
 * @version 1.0
 * RequestParam 注解标注在目标方法的参数上，表示映射http请求的参数
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
