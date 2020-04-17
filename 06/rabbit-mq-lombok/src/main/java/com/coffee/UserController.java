package com.coffee;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coffee.User.Exercise;

import lombok.val;
import lombok.var;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
//替代logFactory生成的log
public class UserController {

	@RequestMapping("/user")
	public User user()
	{
		User user=new User("liuwei");
		user.setAge(18);
		user.setScore(30.1);
		
		//lombok泛型演示
	 Exercise exercise=Exercise.of("liuT", 40);
	 user.setTags(new String[]{"liu",exercise.toString()});
	 log.trace(user.toString());
		return user;
	}
	@RequestMapping("/val")
	public Object Val()
	{   //var修饰的对象不添加final修饰
		var example = new ArrayList<String>();
	    example.add("coffeeliu!");
		//val修饰的对象都自动添加为final类型
		val map=new HashMap<>();
		map.put(1, "liu");
		map.put(2, "wei");
		map.put(3, example);
		return map;
		
	}
}
