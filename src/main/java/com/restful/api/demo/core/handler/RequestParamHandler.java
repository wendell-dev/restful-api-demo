package com.restful.api.demo.core.handler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.restful.api.demo.core.annotation.AccessToken;
import com.restful.api.demo.core.annotation.Sign;
import com.restful.api.demo.core.component.UserTokenComponent;
import com.restful.api.demo.core.config.Constant;
import com.restful.api.demo.core.enums.MsgEnum;
import com.restful.api.demo.core.exception.AccessException;
import com.restful.api.demo.core.resolver.UserPrincipalVO;
import com.restful.api.demo.core.util.IpUtils;
import com.restful.api.demo.core.util.RequestSignUtils;

/**
 * 请求参数处理, 不处理body传过来的参数
 * 
 * @see RequestBodyParamHandler
 * @author wendell
 */
@Component
public class RequestParamHandler extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(RequestParamHandler.class);

	private ThreadLocal<Long> startTime = new ThreadLocal<>();
	
	@Autowired
	private UserTokenComponent userToken;

	/**
	 * 在请求处理之前进行调用 - Controller方法调用之前
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		startTime.set(System.currentTimeMillis());
		logger.info("开始请求 ==> {} {} {}", request.getMethod(), request.getServletPath(), IpUtils.getIpAddr(request));
		Enumeration<String> headerNames = request.getHeaderNames();
		String paraName;
		while (headerNames.hasMoreElements()) {
			paraName = headerNames.nextElement();
			logger.info("请求header参数 ==> {}: {}", paraName, request.getHeader(paraName));
		}
		Enumeration<String> parameterNames = request.getParameterNames();
		Map<String, String> paraMap = new HashMap<>();
		while (parameterNames.hasMoreElements()) {
			paraName = parameterNames.nextElement();
			paraMap.put(paraName, request.getParameter(paraName));
			logger.info("请求参数 ==> {}: {}", paraName, request.getParameter(paraName));
		}
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
		response.setCharacterEncoding("UTF-8");
		return validate(request, response, handler, paraMap);
	}

	/**
	 * 请求验证
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @param paraMap
	 *            请求参数转为map后的数据
	 * @return
	 * @throws Exception
	 */
	private boolean validate(HttpServletRequest request, HttpServletResponse response, Object handler,
			Map<String, String> paraMap) throws Exception {
		// 不是映射到方法的不处理
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		Sign sign = method.getAnnotation(Sign.class);
		boolean existSign = (handlerMethod.getBeanType().getAnnotation(Sign.class) == null && sign == null)
				|| (sign != null && !sign.hasSign());
		AccessToken token = method.getAnnotation(AccessToken.class);
		boolean existToken = (handlerMethod.getBeanType().getAnnotation(AccessToken.class) == null && token == null)
				|| (token != null && !token.hasAccess());
		if (existSign && existToken) {
			// 不需要验证
			return true;
		}
		boolean signSuccess = false;
		boolean tokenSuccess = false;
		if (!existSign) {
			// 进行签名验证
			signSuccess = validateSign(request, paraMap, method);
		}
		if (!existToken) {
			// 进行token验证
			tokenSuccess = validateToken(request);
		}
		return signSuccess && tokenSuccess;
	}

	/**
	 * 开始签名验证
	 * 
	 * @param request
	 * @param paraMap
	 * @param method
	 * @return
	 * @throws IOException
	 */
	private boolean validateSign(HttpServletRequest request, Map<String, String> paraMap, Method method) {
		// 需要签名验证, 进行验证
		logger.info("开始进行签名验证...");
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		for (Annotation[] annotation1 : parameterAnnotations) {
			for (Annotation annotation2 : annotation1) {
				if (annotation2 instanceof RequestBody) {
					logger.info("转到RequestBodyParamHandler进行签名验证...");
					return true;
				}
			}
		}
		Boolean validate = RequestSignUtils.validate(request.getHeader(Constant.ACCESS_KEY_ID),
				request.getHeader(Constant.HEADER_SIGN), paraMap);
		if (!validate) {
			throw new AccessException(MsgEnum.CHECK_SECRET);
		}
		return true;
	}
	
	/**
	 * 开始token验证
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private boolean validateToken(HttpServletRequest request) {
		// 取出token参数,进行验证
		String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
		if (StringUtils.isBlank(accessToken)) {
			accessToken = request.getParameter(Constant.ACCESS_TOKEN);
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