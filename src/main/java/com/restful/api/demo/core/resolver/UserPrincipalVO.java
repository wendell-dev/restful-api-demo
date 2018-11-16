package com.restful.api.demo.core.resolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息
 * 
 * @author wendell
 */
@Data
@JsonInclude(Include.NON_NULL)
@ApiModel(value = "UserPrincipal", description = "用户信息")
public class UserPrincipalVO {

	@ApiModelProperty(value = "用户id")
	private String id;

	@ApiModelProperty(value = "登录账号(手机号)")
	private String account;

	@ApiModelProperty(value = "用户昵称")
	private String nickName;

	@ApiModelProperty(value = "用户头像地址")
	private String headImageUrl;

	@ApiModelProperty(value = "TOKEN")
	private String token;

	public UserPrincipalVO() {}
	
	public UserPrincipalVO(String account) {
		this.account = account;
	}

	public UserPrincipalVO(String id, String account, String nickName, String headImageUrl, String token) {
		super();
		this.id = id;
		this.account = account;
		this.nickName = nickName;
		this.headImageUrl = headImageUrl;
		this.token = token;
	}

}
