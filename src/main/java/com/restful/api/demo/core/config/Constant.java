package com.restful.api.demo.core.config;

/**
 * 常量属性
 * 
 * @author wendell
 */
public class Constant {

	private Constant() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 商户申请的访问KEY
	 */
	public static final String ACCESS_KEY_ID = "accessKeyId";

	/**
	 * 商户访问签名参数
	 */
	public static final String HEADER_SIGN = "sign";

	/**
	 * 请求中 ACCESS_TOKEN 标志
	 */
	public static final String ACCESS_TOKEN = "token";

}
