package com.restful.api.demo.core;

/**
 * 自定义业务异常类
 * 
 * @author wendell
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = -5709149981778994972L;
	
	/**
	 * {"code":400,"msg":"操作失败"}
	 */
	public BusinessException() {
		super(BusinessMsgEnum.ERROR.toString());
	}

	/**
	 * {"code":400,"msg":"${message}"}
	 * @param message
	 */
	public BusinessException(String message) {
		super(BusinessMsgEnum.ERROR.msg(message));
	}
	
	/**
	 * {"code":400,"msg":"${message}","detailMsg":"${detailMsg}"}
	 * @param message
	 * @param detailMsg
	 */
	public BusinessException(String message, String detailMsg) {
		super(BusinessMsgEnum.ERROR.msg(message, detailMsg));
	}

	/**
	 * HTTP状态码为400的业务异常类，如果需要自定义返回的code和msg，那肯定是通过BusinessMsgEnum枚举来定义操作的
	 * 
	 * {"code":${ienum.code},"msg":"${ienum.msg}"}
	 * @param ienum
	 */
	public BusinessException(BusinessMsgEnum ienum) {
		super(ienum.toString());
	}

}
