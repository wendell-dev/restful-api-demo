package com.restful.api.demo.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.restful.api.demo.core.exception.SystemException;

/**
 * 请求签名工具类
 * 
 * @author wendell
 */
public class RequestSignUtils {

	private RequestSignUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 参数签名
	 * 
	 * @param paraMap
	 *            参数
	 * @return 签名串
	 */
	public static String sign(Map<String, String> paraMap) {
		if (null == paraMap || paraMap.isEmpty() || !paraMap.containsKey("accessKeyId")
				|| !paraMap.containsKey("accessKeySecret")) {
			throw new NullPointerException("参数签名错误, 至少应该包含accessKeyId和accessKeySecret");
		}
		return md5(md5(formatUrlMap(paraMap)));
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
	public static String md5(String str) {
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
