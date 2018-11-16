package com.restful.api.demo.core.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.restful.api.demo.core.annotation.UserPrincipal;
import com.restful.api.demo.core.exception.UserPrincipalResolverException;

/**
 * 用户信息解析器, 对使用@UserPrincipal注解的方法参数注入用户信息
 * 
 * @author wendell
 */
@Component
public class UserPrincipalMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().isAssignableFrom(UserPrincipalVO.class)
				&& parameter.hasParameterAnnotation(UserPrincipal.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		// 如果接口不需要token访问,方法却又加入了@UserPrincipal,则需要抛出UserPrincipalResolverException
		UserPrincipalVO user = null;
		try {
			user = (UserPrincipalVO) webRequest.getAttribute("userInfo", RequestAttributes.SCOPE_REQUEST);
		} catch (Exception e) {
			throw new UserPrincipalResolverException("用户信息解析异常");
		}
		if (user != null) {
			return user;
		}
		throw new UserPrincipalResolverException("用户信息解析异常");
	}

}
