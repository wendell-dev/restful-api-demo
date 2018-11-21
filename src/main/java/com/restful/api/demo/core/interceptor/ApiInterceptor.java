package com.restful.api.demo.core.interceptor;

import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.restful.api.demo.core.annotation.AccessToken;
import com.restful.api.demo.core.component.UserTokenComponent;
import com.restful.api.demo.core.enums.MsgEnum;
import com.restful.api.demo.core.exception.AccessException;
import com.restful.api.demo.core.resolver.UserPrincipalVO;
import com.restful.api.demo.core.util.IpUtils;

/**
 * 自定义API-拦截配置, 继承自适配器
 * 
 * @author wendell
 */
@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ApiInterceptor.class);

	/**
	 * 请求中 ACCESS_TOKEN 标志
	 */
	public static final String ACCESS_TOKEN = "token";

	@Autowired
	private UserTokenComponent userToken;

	private ThreadLocal<Long> startTime = new ThreadLocal<>();

	/**
	 * 在请求处理之前进行调用 - Controller方法调用之前
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		startTime.set(System.currentTimeMillis());
		logger.info("开始请求 ==> {} {}", request.getServletPath(), IpUtils.getIpAddr(request));
		Enumeration<String> headerNames = request.getHeaderNames();
		String paraName;
		while (headerNames.hasMoreElements()) {
			paraName = headerNames.nextElement();
			logger.info("请求header参数 ==> {}: {}", paraName, request.getHeader(paraName));
		}
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			paraName = parameterNames.nextElement();
			logger.info("请求参数 ==>  {}: {}", paraName, request.getParameter(paraName));
		}
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "*");
		response.setCharacterEncoding("UTF-8");

		// =========↓↓↓↓========= 进行token验证 =========↓↓↓=========
		// 不是映射到方法的不处理
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		AccessToken annotation = method.getAnnotation(AccessToken.class);
		// 如果没有注解控制,说明不用登录即可访问 (主要对类头部与方法头部的判断)
		// 当类没有注解并且方法也没有注解，说明可直接访问；如果类有注解，除非方法声明了access为false，其它方法都为需要token访问
		boolean existAccessToken = (handlerMethod.getBeanType().getAnnotation(AccessToken.class) == null && annotation == null)
				|| (annotation != null && !annotation.access());
		if (existAccessToken) {
			return true;
		}
		// 取出token参数,进行验证
		String accessToken = request.getHeader(ACCESS_TOKEN);
		if (StringUtils.isBlank(accessToken)) {
			accessToken = request.getParameter(ACCESS_TOKEN);
		}
		if (StringUtils.isBlank(accessToken)) {
			throw new AccessException(MsgEnum.NO_TOKEN);
		}
		// token验证
		UserPrincipalVO userPrincipalVO = null;
		try {
			userPrincipalVO = userToken.getUserPrincipalVO(accessToken);
		} catch (Exception e) {
			throw new AccessException(MsgEnum.CHECK_TOKEN);
		}
		if (null == userPrincipalVO) {
			throw new AccessException(MsgEnum.CHECK_TOKEN);
		}
		// 当前登录用户信息续时
		userToken.expire(userToken.getAccount(accessToken));
		request.setAttribute("userInfo", userPrincipalVO);
		return true;
	}

	/**
	 * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行(主要是用于进行资源清理工作)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e)
			throws Exception {
		long times = System.currentTimeMillis() - startTime.get();
		logger.info("结束请求 ==> {} {}s {}", request.getServletPath(), times / 1000, response.getStatus());
		startTime.remove();
	}

}