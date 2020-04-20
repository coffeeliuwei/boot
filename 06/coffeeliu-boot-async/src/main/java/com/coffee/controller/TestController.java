package com.coffee.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coffee.service.AsyncService;

@RestController
@Slf4j
public class TestController {
    @Autowired
    private AsyncService Service;
    @RequestMapping("/async")
    public  String  createUser() {
        log.info("-----主线程1-------");
        this.Service.addThread();;
        return "OK";
    }

    @RequestMapping("/async2")
    public  String  createUser2() {
        log.info("-----主线程2-----");
        this.Service.addThread2();
        return "OK";
    }
}
