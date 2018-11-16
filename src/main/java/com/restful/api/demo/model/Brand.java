package com.restful.api.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(Include.NON_NULL)
@ApiModel(value = "Brand", description = "品牌对象")
public class Brand {

	@ApiModelProperty(value = "品牌编号")
	private Long id;

	@ApiModelProperty(value = "品牌名称")
	private String name;

	public Brand() {
	}

	public Brand(Long id) {
		super();
		this.id = id;
	}

	public Brand(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
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

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Brand)) {
			return false;
		}
		Brand brand = (Brand) obj;
		// 只要id相同则认为是同一个数据
		return this.id == brand.getId();
	}

	@Override
	public int hashCode() {
        return 31 * 17 + id.hashCode();
	}

}
