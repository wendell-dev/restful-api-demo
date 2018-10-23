package com.restful.api.demo.core.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 对Swagger2的配置信息
 * 
 * @author wendell
 */
@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "swagger")
public class Swagger2Config {

	private String title;

	private String desc;

	private String version;

	private String termsOfServiceUrl;

	private String license;

	private String licenseUrl;

	private String basePackage;

	private String groupName;

	private String contactName;

	private String contactUrl;

	private String contactEmail;

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(title).description(desc).version(version).termsOfServiceUrl(termsOfServiceUrl)
				.licenseUrl(licenseUrl).license(license).contact(new Contact(contactName, contactUrl, contactEmail))
				.build();
	}

	@Bean
	public Docket swaggerApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).groupName(groupName)
				.directModelSubstitute(LocalDate.class, String.class).genericModelSubstitutes(ResponseEntity.class)
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.POST, customerResponseMessage())
				.globalResponseMessage(RequestMethod.GET, customerResponseMessage())
				.forCodeGeneration(true).select()
				.apis(RequestHandlerSelectors.basePackage(basePackage))
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).paths(PathSelectors.any())
				.build();
	}

	private List<ResponseMessage> customerResponseMessage() {
		List<ResponseMessage> list = new ArrayList<ResponseMessage>();
		list.add(new ResponseMessageBuilder().code(200).message("请求成功").build());
		list.add(new ResponseMessageBuilder().code(201).message("资源创建成功").build());
		list.add(new ResponseMessageBuilder().code(204).message("服务器成功处理了请求，但不需要返回任何实体内容").build());
		list.add(new ResponseMessageBuilder().code(400).message("请求失败,具体查看返回业务状态码与对应消息").build());
		list.add(new ResponseMessageBuilder().code(401).message("请求失败,未经过身份认证").build());
		list.add(new ResponseMessageBuilder().code(405).message("请求方法不支持").build());
		list.add(new ResponseMessageBuilder().code(415).message("请求媒体类型不支持").build());
		list.add(new ResponseMessageBuilder().code(500).message("服务器遇到了一个未曾预料的状况,导致了它无法完成对请求的处理").build());
		list.add(new ResponseMessageBuilder().code(503).message("服务器当前无法处理请求,这个状况是临时的，并且将在一段时间以后恢复").build());
		return list;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the termsOfServiceUrl
	 */
	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	/**
	 * @param termsOfServiceUrl
	 *            the termsOfServiceUrl to set
	 */
	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}

	/**
	 * @return the license
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * @param license
	 *            the license to set
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * @return the licenseUrl
	 */
	public String getLicenseUrl() {
		return licenseUrl;
	}

	/**
	 * @param licenseUrl
	 *            the licenseUrl to set
	 */
	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	/**
	 * @return the basePackage
	 */
	public String getBasePackage() {
		return basePackage;
	}

	/**
	 * @param basePackage
	 *            the basePackage to set
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName
	 *            the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName
	 *            the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the contactUrl
	 */
	public String getContactUrl() {
		return contactUrl;
	}

	/**
	 * @param contactUrl
	 *            the contactUrl to set
	 */
	public void setContactUrl(String contactUrl) {
		this.contactUrl = contactUrl;
	}

	/**
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}

	/**
	 * @param contactEmail
	 *            the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

}
