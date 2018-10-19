package com.restful.api.demo.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restful.api.demo.core.BusinessException;
import com.restful.api.demo.core.BusinessMsgEnum;
import com.restful.api.demo.model.Brand;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 测试接口 - controller类
 * 
 * <pre>
 * 为更好的演示,以对品牌场景的操作为列。
 * 为方便起见，这里直接演示的在controller层里进行所有的操作。
 * </pre>
 *
 * @author wendell
 */
@Api(tags = "测试接口 - 品牌为列")
@RestController
@RequestMapping(value = "/brands")
public class TestController {
	
	List<Brand> brands = new ArrayList<>();
	
	@ApiOperation(value = "创建品牌")
	@PostMapping
	public ResponseEntity<Brand> create(@RequestBody(required=true) Brand brand) {
		if (StringUtils.isEmpty(brand.getId())) {
			throw new BusinessException("品牌编号必传");
		}
		if (StringUtils.isEmpty(brand.getName())) {
			throw new BusinessException("品牌名称必传");
		}
		brands.add(brand);
		return ResponseEntity.created(URI.create("http://localhost/brands/" + brand.getId())).body(brand);
	}

	@ApiOperation(value = "根据品牌编号删除品牌信息")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteById(@PathVariable(name = "id", required = true) Integer id) {
		if (StringUtils.isEmpty(id)) {
			throw new BusinessException("品牌编号必传");
		}
		if(brands==null || brands.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		brands.remove(new Brand(id));
		return ResponseEntity.ok(BusinessMsgEnum.SUCCESS.msg("删除成功"));
	}
	
	@ApiOperation(value = "更新品牌信息")
	@PutMapping
	public ResponseEntity<Brand> update(@RequestBody(required=true) Brand brand) {
		if (StringUtils.isEmpty(brand.getId())) {
			throw new BusinessException("品牌编号必传");
		}
		if (StringUtils.isEmpty(brand.getName())) {
			throw new BusinessException("品牌名称必传");
		}
		for(Brand o: brands) {
			if(o.getId().equals(brand.getId())) {
				o.setName(brand.getName());
				return ResponseEntity.ok(brand);
			}
		}
		return ResponseEntity.noContent().build();
	}
	
	@ApiOperation(value = "获取品牌列表")
	@GetMapping
	public ResponseEntity<List<Brand>> getList() {
		return ResponseEntity.status((brands==null || brands.isEmpty()) ? HttpStatus.NO_CONTENT : HttpStatus.OK).body(brands);
	}
	
	@ApiOperation(value = "根据品牌编号获取品牌信息")
	@GetMapping("/{id}")
	public ResponseEntity<Brand> getById(@PathVariable(name = "id", required = true) Integer id) {
		if (StringUtils.isEmpty(id)) {
			throw new BusinessException("品牌编号必传");
		}
		if(brands==null || brands.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		for(Brand brand: brands) {
			if(id == brand.getId()) {
				return ResponseEntity.ok(brand);
			}
		}
		return ResponseEntity.noContent().build();
	}

}
