package com.restful.api.demo.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Redis缓存配置类
 * 
 * @author wendell
 */
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public void configRedisCacheManger(RedisCacheManager rcm) {
		// 设置RedisCacheManager默认的失效时间为60*30秒
		rcm.setDefaultExpiration(60L * 30L);
	}

	/**
	 * redis模板操作类
	 * 
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		setSerializer(template);
		return template;
	}

	/**
	 * 设置序列化
	 * 
	 * @param template
	 */
	private void setSerializer(RedisTemplate<Object, Object> template) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		// 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
		template.setValueSerializer(jackson2JsonRedisSerializer);
		// 使用StringRedisSerializer来序列化和反序列化redis的key值,注意key类型为字符串
		template.setKeySerializer(new StringRedisSerializer());
		template.afterPropertiesSet();
	}

	@Bean
	@Override
	public KeyGenerator keyGenerator() {
		return (o, method, objects) -> {
			StringBuilder sb = new StringBuilder();
			sb.append(o.getClass().getName());
			sb.append(":");
			sb.append(method);
			for (Object object : objects) {
				sb.append(":");
				sb.append(object.toString());
			}
			return sb.toString();
		};
	}

	/**
	 * redis数据操作异常处理 这里的处理：在日志中打印出错误信息，但是放行
	 * 保证redis服务器出现连接等问题的时候不影响程序的正常运行，使得能够出问题时不用缓存
	 * 
	 * @return
	 */
	@Bean
	@Override
	public CacheErrorHandler errorHandler() {
		return new CacheErrorHandler() {
			@Override
			public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
				logger.error("redis异常-handleCacheGetError：key=[{}]", key, e.getMessage());
			}

			@Override
			public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
				logger.error("redis异常-handleCachePutError：key=[{}]", key, e.getMessage());
			}

			@Override
			public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
				logger.error("redis异常-handleCacheEvictError：key=[{}]", key, e.getMessage());
			}

			@Override
			public void handleCacheClearError(RuntimeException e, Cache cache) {
				logger.error("redis异常-handleCacheClearError：", e.getMessage());
			}
		};
	}

}