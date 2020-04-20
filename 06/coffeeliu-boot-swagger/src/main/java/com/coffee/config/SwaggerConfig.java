package com.coffee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value(value = "${swagger.enabled}")
    private Boolean swaggerEnabled;
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(swaggerEnabled)
                .select()
                	//RequestHandlerSelectors.any() 默认值，任意
                	.apis(RequestHandlerSelectors.basePackage("com.coffee"))
                	.paths(PathSelectors.any())//url过滤
                	.build();
    }
    final ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring文档接口")
                .description("这是一个接口文档")
                .contact(contact)
                .version("API V1.0")
                .license("Apache")
                .licenseUrl("http://www.apache.org/")
                .build();
    }
    Contact contact=new Contact("刘伟", 
    		"https://github.com/coffeeliuwei/boot", "coffee.liu@gmail.com");
}
