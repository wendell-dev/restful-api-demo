package com.restful.api.demo.core.exception;

import com.restful.api.demo.core.enums.MsgEnum;

/**
 * 用户信息解析异常
 * 
 * @author wendell
 */
public class UserPrincipalResolverException extends RuntimeException {

	private static final long serialVersionUID = -3476892562211755163L;

	public UserPrincipalResolverException() {
		super(MsgEnum.USERPRINCIPAL_RESOLVER_ERROR.toString());
	}

	public UserPrincipalResolverException(String message) {
		super(MsgEnum.USERPRINCIPAL_RESOLVER_ERROR.msg(message));
	}
	
}
