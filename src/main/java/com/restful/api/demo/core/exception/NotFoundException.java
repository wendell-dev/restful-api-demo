package com.restful.api.demo.core.exception;

import com.restful.api.demo.core.enums.MsgEnum;

/**
 * 自定义资源不存在异常类
 * 
 * @author wendell
 */
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -5709149981778994972L;
	
	/**
	 * {"code":404,"msg":"资源不存在"}
	 */
	public NotFoundException() {
		super(MsgEnum.NOT_FOUND.toString());
	}

	/**
	 * {"code":404,"msg":"${message}"}
	 * @param message
	 */
	public NotFoundException(String message) {
		super(MsgEnum.NOT_FOUND.msg(message));
	}
	
	/**
	 * {"code":404,"msg":"${message}","detailMsg":"${detailMsg}"}
	 * @param message
	 * @param detailMsg
	 */
	public NotFoundException(String message, String detailMsg) {
		super(MsgEnum.NOT_FOUND.msg(message, detailMsg));
	}

	/**
	 * HTTP状态码为404的业务异常类，如果需要自定义返回的code和msg，那肯定是通过MsgEnum枚举来定义操作的
	 * 
	 * {"code":${ienum.code},"msg":"${ienum.msg}"}
	 * @param ienum
	 */
	public NotFoundException(MsgEnum ienum) {
		super(ienum.toString());
	}

}
