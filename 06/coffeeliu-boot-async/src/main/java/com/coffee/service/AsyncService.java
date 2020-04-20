package com.coffee.service;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AsyncService {
    @Async
    public void addThread(){
        //TODO模拟
        try {
            Thread.sleep(2000);
            log.info("------普通thread------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Async("MyPoolTaskExecutor")
    public void addThread2(){
        //TODO 模拟
        try {
            Thread.sleep(2000);
            log.info("-------自定义thread---------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
