package com.restful.api.demo.core.config;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.restful.api.demo.core.handler.RequestParamHandler;
import com.restful.api.demo.core.resolver.UserPrincipalMethodArgumentResolver;

/**
 * 自定义API-MVC配置, 继承自适配器
 * 
 * @author wendell
 */
@Configuration
public class ApiMvcConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private RequestParamHandler requestParamHandler;

	@Autowired
	private UserPrincipalMethodArgumentResolver userPrincipalMethodArgumentResolver;

	@Bean
	public HttpMessageConverter<String> responseBodyConverter() {
		return new StringHttpMessageConverter(Charset.forName("UTF-8"));
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
		converters.add(responseBodyConverter());
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowedHeaders("*");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 自定义API-拦截器
		registry.addInterceptor(requestParamHandler).addPathPatterns("/**");
		super.addInterceptors(registry);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		// 自定义用户信息解析器
		argumentResolvers.add(userPrincipalMethodArgumentResolver);
		super.addArgumentResolvers(argumentResolvers);
	}

}
