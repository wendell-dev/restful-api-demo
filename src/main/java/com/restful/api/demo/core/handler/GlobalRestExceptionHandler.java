package com.restful.api.demo.core.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import com.restful.api.demo.core.enums.MsgEnum;
import com.restful.api.demo.core.exception.AccessException;
import com.restful.api.demo.core.exception.BusinessException;
import com.restful.api.demo.core.exception.NotFoundException;
import com.restful.api.demo.core.exception.SystemException;
import com.restful.api.demo.core.exception.UserPrincipalResolverException;

/**
 * 全局异常Handler
 * 
 * @author wendell
 */
@RestControllerAdvice
public class GlobalRestExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

	/**
	 * 自定义普通业务异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = BusinessException.class)
	public ResponseEntity<String> businessExceptionHandler(BusinessException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	/**
	 * 自定义访问受限异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = AccessException.class)
	public ResponseEntity<String> accessExceptionHandler(AccessException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}

	/**
	 * 自定义资源不存在异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = NotFoundException.class)
	public ResponseEntity<Void> notFoundExceptionHandler(NotFoundException e) {
		return ResponseEntity.notFound().build();
	}

	/**
	 * 自定义系统异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = SystemException.class)
	public ResponseEntity<String> systemExceptionHandler(SystemException e) {
		logger.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MsgEnum.SYSTEM_ERROR.toString());
	}

	/**
	 * 自定义用户信息解析异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = UserPrincipalResolverException.class)
	public ResponseEntity<String> userPrincipalResolverException(UserPrincipalResolverException e) {
		logger.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MsgEnum.SYSTEM_ERROR.toString());
	}

	/**
	 * 非法参数异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = IllegalArgumentException.class)
	public ResponseEntity<String> illegalArgumentExceptionHandler(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(MsgEnum.ERROR.msg(e.getMessage()));
	}

	@ExceptionHandler(value = IllegalStateException.class)
	public ResponseEntity<String> illegalStateExceptionHandler(IllegalStateException e) {
		return ResponseEntity.badRequest().body(MsgEnum.ERROR.msg(e.getMessage()));
	}

	/**
	 * 参数缺失异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	public ResponseEntity<String> missingServletRequestParameterExceptionHandler(
			MissingServletRequestParameterException e) {
		return ResponseEntity.badRequest().body(MsgEnum.ERROR.msg(e.getParameterName() + "参数缺失"));
	}

	/**
	 * 参数类型不匹配异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = TypeMismatchException.class)
	public ResponseEntity<String> typeMismatchExceptionHandler(TypeMismatchException e) {
		return ResponseEntity.badRequest()
				.body(MsgEnum.ERROR.msg("参数类型不匹配,参数" + e.getPropertyName() + "类型应该为" + e.getRequiredType()));
	}

	/**
	 * 请求方法不支持异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<String> methodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(MsgEnum.ERROR.msg(e.getMethod() + "请求方法不支持"));
	}

	/**
	 * 媒体类型不支持异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<String> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException e) {
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
				.body(MsgEnum.ERROR.msg(e.getContentType() + "媒体类型不支持"));
	}

	/**
	 * 使用@Valid/@Validated注解进行验证时未通过的异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		List<ObjectError> allErrors = bindingResult.getAllErrors();
		StringBuilder sb = new StringBuilder(10);
		allErrors.stream().forEach(oe -> sb.append(oe.getDefaultMessage()).append(";"));
		String body = sb.toString();
		if (body.endsWith(";")) {
			body = body.substring(0, body.length() - 1);
		}
		logger.error(body);
		return ResponseEntity.badRequest().body(MsgEnum.ERROR.msg(body));
	}

	/**
	 * 远程服务调用异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = RestClientException.class)
	public ResponseEntity<String> restClientExceptionHandler(RestClientException e) {
		logger.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(MsgEnum.RPC_ERROR.toString());
	}

	/**
	 * 系统异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MsgEnum.SYSTEM_ERROR.toString());
	}
}
