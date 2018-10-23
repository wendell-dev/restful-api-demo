package com.restful.api.demo.core.exception;

import com.restful.api.demo.core.enums.MsgEnum;

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
		super(MsgEnum.ERROR.toString());
	}

	/**
	 * {"code":400,"msg":"${message}"}
	 * @param message
	 */
	public BusinessException(String message) {
		super(MsgEnum.ERROR.msg(message));
	}
	
	/**
	 * {"code":400,"msg":"${message}","detailMsg":"${detailMsg}"}
	 * @param message
	 * @param detailMsg
	 */
	public BusinessException(String message, String detailMsg) {
		super(MsgEnum.ERROR.msg(message, detailMsg));
	}

	/**
	 * HTTP状态码为400的业务异常类，如果需要自定义返回的code和msg，那肯定是通过MsgEnum枚举来定义操作的
	 * 
	 * {"code":${ienum.code},"msg":"${ienum.msg}"}
	 * @param ienum
	 */
	public BusinessException(MsgEnum ienum) {
		super(ienum.toString());
	}

}
