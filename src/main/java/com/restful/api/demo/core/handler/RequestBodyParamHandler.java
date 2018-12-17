package com.restful.api.demo.core.handler;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.restful.api.demo.core.annotation.Sign;
import com.restful.api.demo.core.config.Constant;
import com.restful.api.demo.core.enums.MsgEnum;
import com.restful.api.demo.core.exception.AccessException;
import com.restful.api.demo.core.util.RequestSignUtils;

/**
 * 针对使用@RequestBody注解的请求参数处理
 * 
 * @see RequestParamHandler
 * @author wendell
 */
@ControllerAdvice
public class RequestBodyParamHandler extends RequestBodyAdviceAdapter {

	private static final Logger logger = LoggerFactory.getLogger(RequestBodyParamHandler.class);

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		logger.info("RequestBodyParamHandler==>supports(), MethodParameter==> {}  Type==> {}", methodParameter,
				targetType);
		Sign annotation = methodParameter.getMethodAnnotation(Sign.class);
		return !((methodParameter.getClass().getAnnotation(Sign.class) == null && annotation == null)
				|| (annotation != null && !annotation.hasSign()));
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		logger.info("RequestBodyParamHandler==>handleEmptyBody(), body==> {}", body);
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		logger.info("RequestBodyParamHandler==>beforeBodyRead()");
		return new MyHttpInputMessage(inputMessage);
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		logger.info("RequestBodyParamHandler==>afterBodyRead()");
		return body;
	}

	private class MyHttpInputMessage implements HttpInputMessage {
		private HttpHeaders headers;
		private InputStream body;

		public MyHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
			// 直接取出body内容,按照原生字符串形式进行签名验证,body其实就是字符串
			String sBody = IOUtils.toString(inputMessage.getBody(), "UTF-8");
			logger.info("MyHttpInputMessage, body ==> {}", sBody);
			this.headers = inputMessage.getHeaders();
			this.body = IOUtils.toInputStream(sBody, "UTF-8");
			Boolean validate = RequestSignUtils.validateBody(inputMessage.getHeaders().getFirst(Constant.ACCESS_KEY_ID),
					inputMessage.getHeaders().getFirst(Constant.HEADER_SIGN), sBody);
			if (!validate) {
				throw new AccessException(MsgEnum.CHECK_SECRET);
			}
		}

		@Override
		public InputStream getBody() throws IOException {
			return body;
		}

		@Override
		public HttpHeaders getHeaders() {
			return headers;
		}
	}

}
