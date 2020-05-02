package com.coffee.eureka.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.coffee.eureka.entity.Product;

@Service
public class ProductService {
	@Autowired
	private LoadBalancerClient loadBalancerClient;//ribbon 负载均衡客户端
	
	public  Map<String,Object> listProduct(){
		ServiceInstance serviceInstance=loadBalancerClient.choose("eureka-provider");
		String url="http://" + serviceInstance.getHost() + ":"
				+serviceInstance.getPort()+"/list";
		System.out.println(url);
		RestTemplate rt=new RestTemplate();
		Map<String,Object> plist=rt.getForObject(url, Map.class);
		return plist;
	}
}
