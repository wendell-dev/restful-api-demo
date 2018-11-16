package com.restful.api.demo.core.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 密码生成工具类
 * 
 * @author wendell
 */
public class PwdGeneratorUtils {

	private PwdGeneratorUtils() {
		throw new IllegalStateException("Utility class");
	}

	private static String[] beforeShuffle = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B",
			"C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z" };

	/**
	 * 生成密码Code值
	 * 
	 * @return
	 */
	public static String buildPwdCode() {
		List<?> list = Arrays.asList(beforeShuffle);
		Collections.shuffle(list);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
		}
		String afterShuffle = sb.toString();
		String result = afterShuffle.substring(5, 13);
		return result;
	}

	/**
	 * 生成密码
	 * 
	 * @param password
	 *            明文密码
	 * @param pwdCode
	 *            秘密Code值
	 * @return
	 */
	public static String buildPwd(String password, String pwdCode) {
		return "###" + Md5.md5(Md5.md5("LF0glG22hgXrXJprnx" + password + pwdCode));
	}

}
