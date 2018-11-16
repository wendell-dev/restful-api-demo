package com.restful.api.demo.core.enums;

/**
 * 业务消息枚举类,也是web层API响应固定格式内容,如果有自定义业务CODE,则更新此枚举
 * 
 * @author wendell
 */
public enum MsgEnum {
	/**
     * 操作成功
     */
	SUCCESS(200, "操作成功"),
	/**
     * 操作失败
     */
	ERROR(400, "操作失败"),
	/**
     * 未经过身份认证
     */
	TOKEN_ERROR(401, "未经过身份认证"),
	/**
     * 服务器异常,请稍后再试
     */
	SYSTEM_ERROR(500, "服务器异常,请稍后再试"),
	/**
     * RPC或其他项目通信调用异常，外部服务异常
     */
	RPC_ERROR(503, "外部服务异常"),
	/**
     * token参数不存在,无访问权限
     */
	NO_TOKEN(40101, "token参数不存在,无访问权限"),
	/**
     * token验证失败,无访问权限
     */
	CHECK_TOKEN(40102, "token验证失败,无访问权限"),
	;

	private final int code;
	private String msg;

	private MsgEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	/**
	 * 简要字符串消息
	 * 
	 * @param msg 自定义消息(一般是可直接供前端提示的信息)
	 * @return JSON字符串
	 */
	public String msg(String msg) {
		return this.toJson(msg);
	}
	
	/**
	 * 带简要和详细消息的字符串消息
	 * 
	 * @param msg 自定义消息(一般是可直接供前端提示的信息)
	 * @param detailMsg 详细消息(供详细描述查看的消息)
	 * @return JSON字符串
	 */
	public String msg(String msg, String detailMsg) {
		return this.toJson(msg, detailMsg);
	}

	private static final String JSON_CODE = "{\"code\":";
	private static final String JSON_MSG = ",\"msg\":\"";
	private static final String JSON_MSG_END = "\"}";
	private static final String JSON_DETAIL_MSG = "\",\"detailMsg\":";
	private static final String JSON_END = "}";

	/**
	 * 组装成JSON字符串返回
	 */
	@Override
	public String toString() {
		return JSON_CODE + this.getCode() + JSON_MSG + this.getMsg() + JSON_MSG_END;
	}

	/**
	 * 组装成JSON字符串返回
	 */
	public String toJson() {
		return JSON_CODE + this.getCode() + JSON_MSG + this.getMsg() + JSON_MSG_END;
	}

	private String toJson(String msg) {
		return JSON_CODE + this.getCode() + JSON_MSG + msg + JSON_MSG_END;
	}

	private String toJson(String msg, String detailMsg) {
		return JSON_CODE + this.getCode() + JSON_MSG + msg + JSON_DETAIL_MSG + detailMsg + JSON_END;
	}

}
