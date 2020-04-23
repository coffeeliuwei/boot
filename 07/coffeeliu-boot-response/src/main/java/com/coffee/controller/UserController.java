package com.coffee.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.coffee.codes.ResultCode;
import com.coffee.exceptions.BusinessException;
import com.coffee.response.Result;



@Api("用户接口")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @PostMapping(value="/user/create")
    public void  createUser( @RequestBody @Validated User user ){
        log.info("---------------crreteUser------------------");
    }

    @GetMapping(value="/getStr")
    public String  getStr(  ){
        return  "test";
    }

    @GetMapping(value="/getObject")
    //@ResponseBody
    public User  getObject(  ){
        User user=new User();
        user.setUsername("liuwei");
        return  user;
    }

    @GetMapping(value="/empty")
    public void  empty(  ){

    }

    @GetMapping(value="/error")
    public void  error(  ){
        int i=9/0;
    }


    @PostMapping(value="/error1")
    public void  error1(  ){
        throw new BusinessException(ResultCode.USER_HAS_EXISTED);
    }

    @PostMapping(value="/error2")
    public void  error2(  ){
        throw new RuntimeException("用户已存在！！");
    }
    

    @PostMapping(value = "/user/update")
    public void updateUser(@RequestBody @Validated User use) {
        User user = null;
        //user = userDao.selectById(userId);
        Assert.notNull(user, "用户不存在！");
    }
}
