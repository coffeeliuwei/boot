package com.coffee.eureka.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.coffee.eureka.entity.Product;

@RestController
public class ProductController {
	@RequestMapping(value="/list",method=RequestMethod.GET)
			//,produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String,Object> listProduct(){
		Map<String,Object> map=new HashMap<String, Object>();
		List<Product> list=new ArrayList<Product>();
		list.add(new Product(1,"刘伟"));
		map.put("一", list);
		map.put("二", new Date());
		return  map;
	}
}
