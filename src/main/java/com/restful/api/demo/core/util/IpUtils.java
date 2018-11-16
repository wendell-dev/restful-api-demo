package com.restful.api.demo.core.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * IP辅助工具类
 * 
 * @author wendell
 */
public class IpUtils {

	private IpUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * 获取请求IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		if (ip.contains(",")) {
			ip = ip.split(",")[0];
		}
		return ip;
	}

	/**
	 * 获取MAC地址的方法
	 * 
	 * @param ia
	 * @return
	 * @throws SocketException 
	 * @throws Exception
	 */
	public static String getMACAddress(InetAddress ia) throws SocketException {
		// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		// 下面代码是把mac地址拼装成String
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// mac[i] & 0xFF 是为了把byte转化为正整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}
		// 把字符串所有小写字母改为大写成为正规的mac地址并返回
		return sb.toString().toUpperCase();
	}

}
