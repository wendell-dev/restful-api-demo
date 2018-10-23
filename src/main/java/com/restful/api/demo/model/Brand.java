package com.restful.api.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(Include.NON_NULL)
@ApiModel(value = "Brand", description = "品牌对象")
public class Brand {

	@ApiModelProperty(value = "品牌编号")
	private Integer id;

	@ApiModelProperty(value = "品牌名称")
	private String name;

	public Brand() {
	}

	public Brand(Integer id) {
		super();
		this.id = id;
	}

	public Brand(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 重写equals方法，只要id相同则认为是同一个数据
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Brand) {
			Brand brand = (Brand) obj;
			return this.id == brand.getId();
		}
		return false;
	}

}
