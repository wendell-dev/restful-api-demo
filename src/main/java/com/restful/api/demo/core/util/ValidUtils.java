package com.restful.api.demo.core.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.restful.api.demo.core.exception.SystemException;

/**
 * 常用验证工具类
 * 
 * @author wendell
 */
public class ValidUtils {

	private ValidUtils() {
		throw new IllegalStateException("Utility class");
	}

	/** 手机号 */
	private static final String MOBILE = "^(13|14|15|16|17|18|19)[0-9]{9}$";
	/** 手机号(包括国际代码) */
	private static final String CODE_MOBILE= "^\\+[0-9]{2}\\-(13|14|15|16|17|18|19)[0-9]{9}$";
	/** 电话号码的函数(包括验证国内区号;国际区号;分机号) */
	private static final String TEL = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{1,}))?$";

	/** 手机网络运营商 */
	public enum MobileOperator {
		/**
		 * 中国移动
		 */
		CM("chinaMobile", "中国移动"),
		/**
		 * 中国联通
		 */
		CU("chinaUnicom", "中国联通"),
		/**
		 * 中国电信
		 */
		CT("chinaTelecom", "中国电信"),
		/**
		 * 未知
		 */
		UNKNOW("UNKNOW", "未知");
		private String code;
		private String name;

		private MobileOperator(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}
	}

	/** 中国移动运营商 */
	private static final int[] CHINA_MOBILE= { 134, 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 172,
			182, 183, 184, 187, 188 };
	/** 中国联通运营商 */
	private static final int[] CHINA_UNICOM= { 130, 131, 132, 145, 155, 156, 171, 175, 176, 185, 186 };
	/** 中国电信运营商 */
	private static final int[] CHINA_TELECOM= { 133, 149, 153, 173, 177, 180, 181, 189 };

	/** 身份证 */
	private static final String IDCARD= "^[1-9]([0-9]{14}|[0-9]{17})$";

	/** 车辆VIN码 */
	private static final String CAR_VIN= "^[1234567890WERTYUPASDFGHJKLZXCVBNM]{13}[0-9]{4}$";
	/** 车牌号码 */
	private static final String CAR_NUM= "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}[A-Z0-9]{4,5}[A-Z0-9挂学警港澳]{1}$";

	/** 金额(保留两位小数) */
	private static final String MONEY_TWO_DECIMAL= "(^[1-9](\\d+)?(\\.\\d{1,4})?$)|(^(0){1}$)|(^\\d\\.\\d{1,4}?$)";
	/** 金额(不限保留小数) */
	private static final String MONEY_ANY_DECIMAL= "(^[1-9](\\d+)?(\\.\\d+.*)?$)|(^(0){1}$)|(^\\d\\.\\d+.?$)";

	/**
	 * 是否是手机号(包括国际代码)
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isMobile(String input) {
		return isJustMobile(input) || isJustCodeAndMobile(input);
	}

	/**
	 * 是否只是手机号
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isJustMobile(String input) {
		return matches(MOBILE, input);
	}

	/**
	 * 是否只是包括国际代码的手机号
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isJustCodeAndMobile(String input) {
		return matches(CODE_MOBILE, input);
	}

	/**
	 * 是否是电话号码(包括验证国内区号;国际区号;分机号)
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isTel(String input) {
		return matches(TEL, input);
	}

	/**
	 * 获取手机号所属运营商
	 * 
	 * @param input
	 * @return
	 */
	public static MobileOperator getMobileOperator(String input) {
		if (!isMobile(input)) {
			throw new IllegalArgumentException("input varargs must be phone number");
		}
		String pre = null;
		if (isJustMobile(input)) {
			pre = MOBILE.trim().substring(0, 3);
		} else if (isJustCodeAndMobile(input)) {
			pre = MOBILE.trim().substring(4, 7);
		}
		if (pre == null) {
			throw new SystemException("this error never happen");
		}
		for (int mb : CHINA_MOBILE) {
			if (pre.equals(Integer.toString(mb))) {
				return MobileOperator.CM;
			}
		}
		for (int mb : CHINA_UNICOM) {
			if (pre.equals(Integer.toString(mb))) {
				return MobileOperator.CU;
			}
		}
		for (int mb : CHINA_TELECOM) {
			if (pre.equals(Integer.toString(mb))) {
				return MobileOperator.CT;
			}
		}
		return MobileOperator.UNKNOW;
	}

	/**
	 * 是否是合法身份证
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIdCard(String input) {
		return matches(IDCARD, input) || isAccurateIdCard(input);
	}

	/**
	 * 精准验证是否是合法身份证
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isAccurateIdCard(String input) {
		boolean validate = (input == null || (input.length() != 15 && input.length() != 18));
		if (validate) {
			return false;
		}
		int[] powerList= { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		final char[] cs = input.toUpperCase().toCharArray();
		// 校验位数
		int power = 0;
		for (int i = 0; i < cs.length; i++) {
			if (i == cs.length - 1 && cs[i] == 'X') {
				break;// 最后一位可以 是X或x
			}
			if (cs[i] < '0' || cs[i] > '9') {
				return false;
			}
			if (i < cs.length - 1) {
				power += (cs[i] - '0') * powerList[i];
			}
		}
		// 校验年份
		String year = input.length() == 15 ? "19" + input.substring(6, 8) : input.substring(6, 10);
		final int iyear = Integer.parseInt(year);
		if (iyear < 1900 || iyear > Calendar.getInstance().get(Calendar.YEAR)) {
			// 1900年的PASS，超过今年的PASS
			return false;
		}
		// 校验月份
		String month = input.length() == 15 ? input.substring(8, 10) : input.substring(10, 12);
		final int imonth = Integer.parseInt(month);
		if (imonth < 1 || imonth > 12) {
			return false;
		}
		// 校验天数
		String day = input.length() == 15 ? input.substring(10, 12) : input.substring(12, 14);
		final int iday = Integer.parseInt(day);
		if (iday < 1 || iday > 31) {
			return false;
		}
		// 校验"校验码"
		if (input.length() == 15) {
			return true;
		}
		// 验证身份证的区位码
		Map<Integer, String> zoneNum = new HashMap<>(36);
		zoneNum.put(11, "北京");
		zoneNum.put(12, "天津");
		zoneNum.put(13, "河北");
		zoneNum.put(14, "山西");
		zoneNum.put(15, "内蒙古");
		zoneNum.put(21, "辽宁");
		zoneNum.put(22, "吉林");
		zoneNum.put(23, "黑龙江");
		zoneNum.put(31, "上海");
		zoneNum.put(32, "江苏");
		zoneNum.put(33, "浙江");
		zoneNum.put(34, "安徽");
		zoneNum.put(35, "福建");
		zoneNum.put(36, "江西");
		zoneNum.put(37, "山东");
		zoneNum.put(41, "河南");
		zoneNum.put(42, "湖北");
		zoneNum.put(43, "湖南");
		zoneNum.put(44, "广东");
		zoneNum.put(45, "广西");
		zoneNum.put(46, "海南");
		zoneNum.put(50, "重庆");
		zoneNum.put(51, "四川");
		zoneNum.put(52, "贵州");
		zoneNum.put(53, "云南");
		zoneNum.put(54, "西藏");
		zoneNum.put(61, "陕西");
		zoneNum.put(62, "甘肃");
		zoneNum.put(63, "青海");
		zoneNum.put(64, "宁夏");
		zoneNum.put(65, "新疆");
		zoneNum.put(71, "台湾");
		zoneNum.put(81, "香港");
		zoneNum.put(82, "澳门");
		zoneNum.put(91, "国外");
		int[] parityBit= { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
		// 校验区位码
		if (!zoneNum.containsKey(Integer.valueOf(input.substring(0, 2)))) {
			return false;
		}
		return cs[cs.length - 1] == parityBit[power % 11];
	}

	/**
	 * 判断车辆VIN码是否合法
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isCarVIN(String input) {
		return matches(CAR_VIN, input) || isAccurateCarVIN(input);
	}

	/**
	 * 精准验证车辆VIN码是否合法
	 * 
	 * <pre>
	 * 第1～3位：世界制造厂识别代码(WMI)：制造厂、品牌和类型
	 * 第2位：汽车制造商代码
	 * 第3位：汽车类型代码
	 * 第4～8位：车辆特征码(VDS)：有欧规和美规两种，美规是第4～8位;欧规是第4～9。中国使用的是美规
	 * 第9位：美规是校验位，按标准通过加权计算得到，欧规是属于VDS的最后一位
	 * 第10位：车型年份(一般标识为车辆的出厂年份，是识别车辆的重要标识)
	 * 第11位：装配厂
	 * 第12～17位：出厂顺序号(后6位)，这个是我们经常会用到的数字
	 * </pre>
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isAccurateCarVIN(String input) {
		// 非空及长度验证
		if (StringUtils.isBlank(input) || input.length() != 17) {
			return false;
		}
		String uppervin = input.toUpperCase();
		// 排除字母O、I
		if (uppervin.indexOf('O') >= 0 || uppervin.indexOf('I') >= 0) {
			return false;
		}
		boolean flag = false;
		Map<Integer, Integer> vinMapWeighting = new HashMap<>(17);
		Map<Character, Integer> vinMapValue = new HashMap<>(32);
		vinMapWeighting.put(1, 8);
		vinMapWeighting.put(2, 7);
		vinMapWeighting.put(3, 6);
		vinMapWeighting.put(4, 5);
		vinMapWeighting.put(5, 4);
		vinMapWeighting.put(6, 3);
		vinMapWeighting.put(7, 2);
		vinMapWeighting.put(8, 10);
		vinMapWeighting.put(9, 0);
		vinMapWeighting.put(10, 9);
		vinMapWeighting.put(11, 8);
		vinMapWeighting.put(12, 7);
		vinMapWeighting.put(13, 6);
		vinMapWeighting.put(14, 5);
		vinMapWeighting.put(15, 4);
		vinMapWeighting.put(16, 3);
		vinMapWeighting.put(17, 2);
		vinMapValue.put('0', 0);
		vinMapValue.put('1', 1);
		vinMapValue.put('2', 2);
		vinMapValue.put('3', 3);
		vinMapValue.put('4', 4);
		vinMapValue.put('5', 5);
		vinMapValue.put('6', 6);
		vinMapValue.put('7', 7);
		vinMapValue.put('8', 8);
		vinMapValue.put('9', 9);
		vinMapValue.put('A', 1);
		vinMapValue.put('B', 2);
		vinMapValue.put('C', 3);
		vinMapValue.put('D', 4);
		vinMapValue.put('E', 5);
		vinMapValue.put('F', 6);
		vinMapValue.put('G', 7);
		vinMapValue.put('H', 8);
		vinMapValue.put('J', 1);
		vinMapValue.put('K', 2);
		vinMapValue.put('M', 4);
		vinMapValue.put('L', 3);
		vinMapValue.put('N', 5);
		vinMapValue.put('P', 7);
		vinMapValue.put('R', 9);
		vinMapValue.put('S', 2);
		vinMapValue.put('T', 3);
		vinMapValue.put('U', 4);
		vinMapValue.put('V', 5);
		vinMapValue.put('W', 6);
		vinMapValue.put('X', 7);
		vinMapValue.put('Y', 8);
		vinMapValue.put('Z', 9);
		char[] vinArr = uppervin.toCharArray();
		int amount = 0;
		for (int i = 0; i < 17; i++) {
			// VIN码从从第一位开始，码数字的对应值×该位的加权值，计算全部17位的乘积值相加
			amount += vinMapValue.get(vinArr[i]) * vinMapWeighting.get(i + 1);
		}
		// 乘积值相加除以11、若余数为10，即为字母Ｘ
		if (amount % 11 == 10) {
			if (vinArr[8] == 'X') {
				flag = true;
			} else {
				flag = false;
			}
		} else {
			// VIN码从从第一位开始，码数字的对应值×该位的加权值，
			// 计算全部17位的乘积值相加除以11，所得的余数，即为第九位校验值
			if (amount % 11 != vinMapValue.get(vinArr[8])) {
				flag = false;
			} else {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 车牌号码
	 * 
	 * @param input
	 * @return
	 */
	public static boolean iscarNum(String input) {
		return matches(CAR_NUM, input);
	}

	/**
	 * 验证金额是否正确
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isMoney(String input) {
		return matches(MONEY_ANY_DECIMAL, input);
	}

	/**
	 * 金额(保留两位小数)
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isMoneyTwoDecimal(String input) {
		return matches(MONEY_TWO_DECIMAL, input);
	}

	/**
	 * 正则验证
	 * 
	 * @param regex
	 * @param input
	 * @return
	 */
	public static boolean matches(String regex, String input) {
		if (StringUtils.isBlank(input)) {
			return false;
		}
		return input.matches(regex);
	}

}
