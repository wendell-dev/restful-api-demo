package com.restful.api.demo.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restful.api.demo.core.annotation.AccessToken;
import com.restful.api.demo.core.annotation.Sign;
import com.restful.api.demo.core.annotation.UserPrincipal;
import com.restful.api.demo.core.enums.MsgEnum;
import com.restful.api.demo.core.exception.BusinessException;
import com.restful.api.demo.core.exception.NotFoundException;
import com.restful.api.demo.core.resolver.UserPrincipalVO;
import com.restful.api.demo.model.Demo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 测试接口 - controller类
 *
 * @author wendell
 */
@Api(tags = "测试接口")
@RestController
@RequestMapping(value = "/tests")
public class TestController {

	List<Demo> demos = new ArrayList<>();

	@ApiOperation(value = "新增一个Demo类", notes = "新增资源成功返回201, 并给出新增资源地址")
	@PostMapping
	public ResponseEntity<Demo> create(@Validated @RequestBody(required = true) Demo demo) {
		demos.add(demo);
		return ResponseEntity.created(URI.create("http://localhost/demos/" + demo.getId())).body(demo);
	}

	@ApiOperation(value = "更新Demo信息", notes = "更新资源成功返回204,资源与客户端请求参数体未发生改变,资源若不存在则响应404")
	@PutMapping
	public ResponseEntity<Void> update(@Validated @RequestBody(required = true) Demo demo) {
		for (Demo o : demos) {
			if (o.getId().equals(demo.getId())) {
				o.setName(demo.getName());
				return ResponseEntity.noContent().build();
			}
		}
		throw new NotFoundException();
	}

	@ApiOperation(value = "根据Demo编号删除Demo信息", notes = "删除资源成功返回204,资源若不存在则响应404")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable(name = "id", required = true) Long id) {
		validateId(id);
		validateNull();
		boolean removed = demos.removeIf(o -> o.getId().equals(id));
		if (removed) {
			return ResponseEntity.noContent().build();
		}
		throw new NotFoundException();
	}

	@ApiOperation(value = "获取Demo列表", notes = "列表资源有数据响应200,无数据则响应204")
	@GetMapping
	public ResponseEntity<List<Demo>> getList() {
		return ResponseEntity.status((demos == null || demos.isEmpty()) ? HttpStatus.NO_CONTENT : HttpStatus.OK)
				.body(demos);
	}

	@ApiOperation(value = "根据Demo编号获取Demo信息", notes = "获取资源成功响应200,资源若不存在则响应404")
	@GetMapping("/{id}")
	public ResponseEntity<Demo> getById(@PathVariable(name = "id", required = true) Long id) {
		validateId(id);
		validateNull();
		for (Demo demo : demos) {
			if (id.equals(demo.getId())) {
				return ResponseEntity.ok(demo);
			}
		}
		throw new NotFoundException();
	}

	@ApiOperation(value = "token测试")
	@GetMapping("/token")
	@AccessToken
	public ResponseEntity<UserPrincipalVO> testToken(@ApiIgnore @UserPrincipal UserPrincipalVO user) {
		return ResponseEntity.ok(user);
	}

	@ApiOperation(value = "签名测试 - GET请求参数")
	@GetMapping("/sign")
	@Sign
	public ResponseEntity<String> signTest(@RequestParam Long id, @RequestParam String name) {
		return ResponseEntity.ok(MsgEnum.SUCCESS.msg("签名验证成功"));
	}

	@ApiOperation(value = "签名测试 - POST请求body参数")
	@PostMapping("/sign")
	@Sign
	public ResponseEntity<String> signTest(@RequestBody(required = true) Demo demo) {
		return ResponseEntity.ok(MsgEnum.SUCCESS.msg("签名验证成功"));
	}

	private void validateId(Long id) {
		if (StringUtils.isEmpty(id)) {
			throw new BusinessException("编号不能为空");
		}
	}

	private void validateNull() {
		if (demos == null || demos.isEmpty()) {
			throw new BusinessException("数据异常,列表为空");
		}
	}
}
