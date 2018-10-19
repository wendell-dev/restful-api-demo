package com.restful.api.demo.core;

/**
 * API访问受限异常
 * 
 * @author wendell
 */
public class AccessException  extends RuntimeException {

	private static final long serialVersionUID = 891168742033342843L;
	
	/**
	 * {"code":401,"msg":"无访问权限"}
	 */
	public AccessException() {
		super(BusinessMsgEnum.TOKEN_ERROR.toString());
	}
	
	/**
	 * {"code":401,"msg":"${message}"}
	 * @param message
	 */
	public AccessException(String message) {
		super(BusinessMsgEnum.TOKEN_ERROR.msg(message));
	}
    
	/**
	 * HTTP状态码为401的业务异常类，如果需要自定义返回的code和msg，那肯定是通过BusinessMsgEnum枚举来定义操作的
	 * 
	 * {"code":${ienum.code},"msg":"${ienum.msg}"}
	 * @param ienum
	 */
    public AccessException(BusinessMsgEnum ienum) {
        super(ienum.toString());
    }

}
