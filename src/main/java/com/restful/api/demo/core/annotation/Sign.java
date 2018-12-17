package com.restful.api.demo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为私有API接口提供签名验证
 * 
 * <pre>
 * 1. 在需要签名的API方法上加上此注解 
 * 2. 直接在API类上使用,代表该API类的所有方法均需要签名
 * 3. 直接在API类上使用,如果类中有方法不需要签名则hasSign参数置为False
 * </pre>
 * 
 * @author wendell
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Sign {

	/**
	 * 是否需要签名认证
	 * 
	 * @return
	 */
	boolean hasSign() default true;

}