package com.restful.api.demo.core.exception;

import com.restful.api.demo.core.enums.MsgEnum;

/**
 * API访问受限异常
 * 
 * @author wendell
 */
public class AccessException  extends RuntimeException {

	private static final long serialVersionUID = 891168742033342843L;
	
	/**
	 * {"code":401,"msg":"未经过身份认证"}
	 */
	public AccessException() {
		super(MsgEnum.TOKEN_ERROR.toString());
	}
	
	/**
	 * {"code":401,"msg":"${message}"}
	 * @param message
	 */
	public AccessException(String message) {
		super(MsgEnum.TOKEN_ERROR.msg(message));
	}
    
	/**
	 * HTTP状态码为401的业务异常类，如果需要自定义返回的code和msg，那肯定是通过MsgEnum枚举来定义操作的
	 * 
	 * {"code":${ienum.code},"msg":"${ienum.msg}"}
	 * @param ienum
	 */
    public AccessException(MsgEnum ienum) {
        super(ienum.toString());
    }

}
