package com.xiangxue.annotation;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import  static java.lang.annotation.RetentionPolicy.CLASS;
import java.lang.annotation.Target;

@Target(TYPE) // 类
@Retention(CLASS) // 编译期  === APT
public @interface ARouter {

    String path(); // 路径详情

    String group() default "";  // order,app,personal

}
