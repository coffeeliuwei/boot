package com.coffee.eureka.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class WebConfig  implements WebMvcConfigurer {
	
    @Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		//WebMvcConfigurer.super.configureContentNegotiation(configurer);
    	configurer.ignoreAcceptHeader(true).defaultContentType(
              MediaType.APPLICATION_JSON,MediaType.ALL);
    	
	}

	@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    	
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        		//格式化日期
        fastJsonConfig.setDateFormat("yyyy-MM-dd");
        fastJsonConfig.setSerializerFeatures(
                // 防止循环引用
                SerializerFeature.DisableCircularReferenceDetect,
                //序列化时写入类型信息,为以后反射用
                SerializerFeature.WriteClassName,
                // 空集合返回[],不返回null
                SerializerFeature.WriteNullListAsEmpty, 
                // 空字符串返回"",不返回null
                SerializerFeature.WriteNullStringAsEmpty,
                //是否输出值为null的字段
                SerializerFeature.WriteMapNullValue
        );
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);

        converters.add(0,fastJsonHttpMessageConverter);
      //  WebMvcConfigurer.super.configureMessageConverters(converters);
        for (HttpMessageConverter<?> messageConverter : converters) {
            System.out.println(messageConverter);
        }
    }
}
