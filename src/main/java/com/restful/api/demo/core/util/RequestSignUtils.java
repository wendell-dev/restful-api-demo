package com.restful.api.demo.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.restful.api.demo.core.enums.MsgEnum;
import com.restful.api.demo.core.exception.AccessException;
import com.restful.api.demo.core.exception.SystemException;

/**
 * 请求签名工具类
 * 
 * <pre>
 * 1. 我们会分配accessKeyId和accessKeySecret给接入方
 * 2. 接入方每次请求我们的API时，需要把accessKeyId和accessKeySecret与其他参数进行签名得到签名串sign(得到签名串方式参考步骤7)
 * 3. 接入方需要在请求Header中加入我们提供accessKeyId
 * 4. 接入方需要在请求Header加入步骤2生成的sign
 * 5. 我们在header中拿到accessKeyId去数据库进行匹配验证取出其accessKeySecret，并按照第二步进行同样方式的加密得到sign
 * 6. 我们对比两个sign是否匹配，匹配则通过，反之不通过
 * 7. 加密方式: 由我们的提供的RequestSignUtils工具类进行操作
 *      Map<String, String> data = new HashMap<>();
 *      data.put("xxx参数名1", "xxx参数值1");
 *      data.put("xxx参数名2", "xxx参数值2");
 *      String sign = RequestSignUtils.sign(accessKeyId, accessKeySecret, data);
 *      
 *      String sign = RequestSignUtils.signBody(accessKeyId, accessKeySecret, JSON.toJSONString(data));
 * </pre>
 * 
 * @author wendell
 */
public class RequestSignUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestSignUtils.class);

	private RequestSignUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	private static final Map<String, String> PARTNERS = new HashMap<>();

	/**
	 * 暂时发放的商户accessKeyId和accessKeySecret就没有存入数据库了,放入内存中加载
	 */
	static {
		// key is accessKeyId, value is accessKeySecret
		PARTNERS.put("1", "1");
	}

	/**
	 * 模拟客户端操作
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String accessKeyId = "1";
		String accessKeySecret = "1";
		// 1. 组装请求参数
		Map<String, String> data = new HashMap<>();
		data.put("id", "1");
		data.put("name", "测试名");

		// 2. FORM表单格式参数签名
		String sign = sign(accessKeyId, accessKeySecret, data);
		logger.info("sign ==> {}", sign);
		
		// 2. POST body格式参数签名
		String body = JSON.toJSONString(data);
		logger.info("body json ==> {}", body);
		String signBody = signBody(accessKeyId, accessKeySecret, body);
		logger.info("signBody ==> {}", signBody);
		
	}

	/**
	 * 验证签名
	 * 
	 * @param accessKeyId
	 * @param sign
	 * @param paraMap
	 * @return
	 */
	public static Boolean validate(String accessKeyId, String sign, Map<String, String> paramMap) {
		String secret = validate(accessKeyId, sign);
		Map<String, String> data = new HashMap<>();
		if (null != paramMap) {
			data.putAll(paramMap);
		}
		// 进行sign验证
		if (!sign.equals(sign(accessKeyId, secret, data))) {
			logger.error("sign签名不匹配");
			throw new AccessException(MsgEnum.CHECK_SECRET);
		}
		return true;
	}

	/**
	 * 验证签名
	 * 
	 * @param accessKeyId
	 * @param sign
	 * @param body
	 * @return
	 */
	public static Boolean validateBody(String accessKeyId, String sign, String body) {
		String secret = validate(accessKeyId, sign);
		// 进行sign验证
		if (!sign.equals(signBody(accessKeyId, secret, body))) {
			logger.error("sign签名不匹配");
			throw new AccessException(MsgEnum.CHECK_SECRET);
		}
		return true;
	}

	/**
	 * 验证签名前准备 - 成功后返回secret
	 * 
	 * @param accessKeyId
	 * @param sign
	 * @return
	 */
	private static String validate(String accessKeyId, String sign) {
		if (StringUtils.isBlank(accessKeyId)) {
			throw new AccessException(MsgEnum.NO_SECRET_ID);
		}
		if (StringUtils.isBlank(sign)) {
			throw new AccessException(MsgEnum.NO_SECRET_SIGN);
		}
		// 根据accessKeyId查看商户是否存在
		String secret = PARTNERS.get(accessKeyId);
		if (StringUtils.isBlank(secret)) {
			logger.error("验证失败, accessKeyId不存在");
			throw new AccessException(MsgEnum.CHECK_SECRET);
		}
		return secret;
	}

	/**
	 * 参数签名
	 * 
	 * @param accessKeyId
	 * @param accessKeySecret
	 * @param data
	 * @return
	 */
	public static String sign(String accessKeyId, String accessKeySecret, Map<String, String> data) {
		signValidate(accessKeyId, accessKeySecret);
		return md5(md5(formatUrlMap(data)));
	}

	/**
	 * POST-body参数签名
	 * 
	 * @param accessKeyId
	 * @param accessKeySecret
	 * @param body
	 * @return
	 */
	public static String signBody(String accessKeyId, String accessKeySecret, String body) {
		signValidate(accessKeyId, accessKeySecret);
		return md5(md5(accessKeyId.concat(accessKeySecret).concat(body)));
	}

	/**
	 * 签名参数验证
	 * 
	 * @param accessKeyId
	 * @param accessKeySecret
	 */
	private static void signValidate(String accessKeyId, String accessKeySecret) {
		if (isBlank(accessKeyId) || isBlank(accessKeySecret)) {
			throw new NullPointerException("参数签名错误, 至少应该包含accessKeyId和accessKeySecret");
		}
	}

	/**
	 * 方法用途: 对所有传入参数按照字段名的ASCII码从小到大排序（字典序），并且生成url参数串
	 * 
	 * @param paraMap
	 *            要排序的Map对象
	 * @return
	 */
	private static String formatUrlMap(Map<String, String> paraMap) {
		String buff = "";
		Map<String, String> tmpMap = paraMap;
		List<Map.Entry<String, String>> infoIds = new ArrayList<>(tmpMap.entrySet());
		// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
		infoIds.sort(
				(Map.Entry<String, String> o1, Map.Entry<String, String> o2) -> o1.getKey().compareTo(o2.getKey()));
		// 构造URL 键值对的格式
		StringBuilder buf = new StringBuilder();
		for (Map.Entry<String, String> item : infoIds) {
			if (!isBlank(item.getKey())) {
				String val = item.getValue();
				try {
					val = URLEncoder.encode(val, "utf-8");
				} catch (UnsupportedEncodingException e) {
					throw new SystemException(e.getMessage());
				}
				// 将Key转换为全小写
				buf.append(item.getKey().toLowerCase() + "=" + val);
				buf.append("&");
			}
		}
		buff = buf.toString();
		if (!buff.isEmpty()) {
			buff = buff.substring(0, buff.length() - 1);
		}
		return buff;
	}

	/**
	 * 重载org.apache.commons.lang3的isBlank方法,避免导入包
	 * 
	 * <p>
	 * Checks if a CharSequence is empty (""), null or whitespace only.
	 * </p>
	 *
	 * <p>
	 * Whitespace is defined by {@link Character#isWhitespace(char)}.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isBlank(null)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("bob")     = false
	 * StringUtils.isBlank("  bob  ") = false
	 * </pre>
	 *
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is null, empty or whitespace only
	 * @since 2.0
	 * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
	 */
	private static boolean isBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * MD5加密
	 * 
	 * @param str
	 * @return
	 */
	private static String md5(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new SystemException(e.getMessage());
		}
		md.update(str.getBytes());
		byte[] b = md.digest();
		int i;
		StringBuilder buf = new StringBuilder("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0) {
				i += 256;
			}
			if (i < 16) {
				buf.append("0");
			}
			buf.append(Integer.toHexString(i));
		}
		return buf.toString();
	}

}
