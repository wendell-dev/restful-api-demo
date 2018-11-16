package com.restful.api.demo.core.component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.restful.api.demo.core.resolver.UserPrincipalVO;
import com.restful.api.demo.core.util.Des;

/**
 * 登录用户信息token操作组件
 * 
 * @author wendell
 */
@Component
public class UserTokenComponent {

	/**
	 * 超时时间90天
	 */
	private static final Long REDISINTERVAL = 3600 * 24 * 90L;

	/**
	 * 前缀
	 */
	private static final String PRE = "USER:_CACHE_";

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	/**
	 * 创建token
	 * 
	 * @param userPrincipalVO
	 * @throws IOException
	 */
	public UserPrincipalVO createToken(UserPrincipalVO userPrincipalVO) {
		if (null == userPrincipalVO || StringUtils.isBlank(userPrincipalVO.getAccount())) {
			throw new NullPointerException("创建token失败,对象信息不能为空");
		}
		String key = PRE.concat(userPrincipalVO.getAccount());
		redisTemplate.delete(key);
		ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
		userPrincipalVO.setToken(Des.encode(System.currentTimeMillis() + "|" + userPrincipalVO.getAccount()));
		operations.set(key, userPrincipalVO, REDISINTERVAL, TimeUnit.SECONDS);
		return userPrincipalVO;
	}

	/**
	 * 根据token值获取UserPrincipalVO
	 * 
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public UserPrincipalVO getUserPrincipalVO(String token) {
		if (null == token) {
			return null;
		}
		ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
		String key = PRE.concat(this.getAccount(token));
		// 验证token是否匹配正确
		UserPrincipalVO userPrincipalVO = (UserPrincipalVO) operations.get(key);
		if (null == userPrincipalVO || !token.equals(userPrincipalVO.getToken())) {
			return null;
		}
		return userPrincipalVO;
	}

	/**
	 * 根据token获取登录账号(手机号)
	 * 
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public String getAccount(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		String decrypt = Des.decode(token);
		if (null == decrypt) {
			return null;
		}
		return decrypt.split("\\|")[1];
	}

	/**
	 * 续时
	 * 
	 * @param account
	 *            登录账号(手机号)
	 */
	public void expire(String account) {
		redisTemplate.expire(PRE.concat(account), REDISINTERVAL, TimeUnit.SECONDS);
	}

}
