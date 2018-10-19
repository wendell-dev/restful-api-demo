package com.restful.api.demo.core;

/**
 * 业务消息枚举类
 * 
 * @author wendell
 */
public enum BusinessMsgEnum {

	SUCCESS(200, "操作成功"),
	ERROR(400, "操作失败"),
	TOKEN_ERROR(401, "未经过身份认证"),
	SYSTEM_ERROR(500, "服务器异常,请稍后再试"),
	RPC_ERROR(503, "外部服务异常"),
	NO_TOKEN(40101, "token参数不存在,无访问权限"),
	CHECK_TOKEN(40102, "token验证失败,无访问权限"),
	;

	private Integer code;
	private String msg;

	private BusinessMsgEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	/**
	 * 简要字符串消息
	 * 
	 * @param msg 自定义消息(一般是可直接供前端提示的信息)
	 * @return
	 */
	public String msg(String msg) {
		return this.toString(msg);
	}
	
	/**
	 * 带简要和详细消息的字符串消息
	 * 
	 * @param msg 自定义消息(一般是可直接供前端提示的信息)
	 * @param detailMsg 详细消息(供详细描述查看的消息)
	 * @return
	 */
	public String msg(String msg, String detailMsg) {
		return this.toString(msg, detailMsg);
	}

	/**
	 * 组装成JSON字符串返回
	 */
	@Override
	public String toString() {
		return "{\"code\":" + this.getCode() + ",\"msg\":\"" + this.getMsg() + "\"}";
	}

	private String toString(String msg) {
		return "{\"code\":" + this.getCode() + ",\"msg\":\"" + msg + "\"}";
	}

	private String toString(String msg, String detailMsg) {
		return "{\"code\":" + this.getCode() + ",\"msg\":\"" + msg + "\"" + ",\"detailMsg\":" + detailMsg + "}";
	}

}
