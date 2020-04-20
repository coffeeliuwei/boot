package com.coffee.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coffee.controller.User.Exercise;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.val;
import lombok.var;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(value = "用户操作接口", tags = "用户接口操作")
//替代logFactory生成的log
public class UserController {
	@ApiOperation(value = "/返回用户信息", tags = { "用户", "标签" }, 
			notes = "接口发布说明",response = User.class,
			httpMethod = "POST")
	@ApiImplicitParam(paramType ="query",name = "name",
			value = "用户名",required = true,dataType = "String" )
	@PostMapping("/user/{name}")
	public User user(String name) {
		User user = new User(name);
		user.setAge(18);
		user.setScore(30.1);
		// lombok泛型演示
		Exercise exercise = Exercise.of("liuT", 40);
		user.setTags(new String[] { "liu", exercise.toString() });
		log.trace(user.toString());
		return user;
	}

	@ApiOperation(value = "/返回复杂信息", tags = { "复杂", "标签" }, 
			response = HashMap.class)
	@GetMapping("/val")
	public Object Val() { // var修饰的对象不添加final修饰
		var example = new ArrayList<String>();
		example.add("coffeeliu!");
		// val修饰的对象都自动添加为final类型
		val map = new HashMap<>();
		map.put(1, "liu");
		map.put(2, "wei");
		map.put(3, example);
		return map;
	}
}
