package com.restful.api.demo.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@ApiModel(value = "Demo", description = "Demo对象")
@Data
public class Demo {

	@NotNull(message = "编号不能为空")
	@ApiModelProperty(value = "编号")
	private Long id;

	@NotBlank(message = "名称不能为空")
	@ApiModelProperty(value = "名称")
	private String name;
	
	@ApiModelProperty(value = "昵称")
	private String nickName;
}
