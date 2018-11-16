package com.restful.api.demo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UserPrincipal Annotation 在登录后需要获取用户信息的API方法参数内加上此注解
 * 
 * @author wendell
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserPrincipal {
}