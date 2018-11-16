package com.restful.api.demo.core.util;

import java.security.MessageDigest;

/**
 * md5工具类
 * 
 * @author wendell
 */
public class Md5 {

	private Md5() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * MD5加密
	 * 
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
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
			str = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

}
