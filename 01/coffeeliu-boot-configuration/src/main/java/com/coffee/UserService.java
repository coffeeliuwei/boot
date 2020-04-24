package com.coffee;

public class UserService {

    public UserBean findUser(){
        UserBean userBean= new UserBean();
        userBean.setUsername("liuwei");
        userBean.setPassword("111111");
        return userBean;
    }

}
