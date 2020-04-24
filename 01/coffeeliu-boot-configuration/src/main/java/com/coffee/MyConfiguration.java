package com.coffee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {
    public MyConfiguration() {
        System.out.println("MyConfiguration初始化。。。");
    }

    @Bean
    public UserBean userBean(){
        UserBean userBean= new UserBean();
        userBean.setUsername("liuwei");
        userBean.setPassword("111111");
        return userBean;
    }
}
