package com.restful.api.demo.core.exception;

import com.restful.api.demo.core.enums.MsgEnum;

/**
 * 自定义系统异常类
 * 
 * @author wendell
 */
public class SystemException extends RuntimeException {

	private static final long serialVersionUID = -5709149981778994972L;
	
	/**
	 * {"code":500,"msg":"操作失败"}
	 */
	public SystemException() {
		super(MsgEnum.SYSTEM_ERROR.toString());
	}

	/**
	 * {"code":500,"msg":"${message}"}
	 * @param message
	 */
	public SystemException(String message) {
		super(MsgEnum.SYSTEM_ERROR.msg(message));
	}
	
	/**
	 * {"code":500,"msg":"${message}","detailMsg":"${detailMsg}"}
	 * @param message
	 * @param detailMsg
	 */
	public SystemException(String message, String detailMsg) {
		super(MsgEnum.SYSTEM_ERROR.msg(message, detailMsg));
	}

	/**
	 * HTTP状态码为500的业务异常类，如果需要自定义返回的code和msg，那肯定是通过MsgEnum枚举来定义操作的
	 * 
	 * {"code":${ienum.code},"msg":"${ienum.msg}"}
	 * @param ienum
	 */
	public SystemException(MsgEnum ienum) {
		super(ienum.toString());
	}

}
