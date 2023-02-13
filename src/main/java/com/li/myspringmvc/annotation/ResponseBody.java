package com.li.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author 李
 * @version 1.0
 * ResponseBody 注解用于指定目标方法是否要返回指定格式的数据
 * 如果value为默认值，或者value="json"，认为目标方法要返回的数据格式为json
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
    String value() default "";
}
