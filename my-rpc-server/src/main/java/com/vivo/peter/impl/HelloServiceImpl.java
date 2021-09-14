package com.vivo.peter.impl;

import com.vivo.peter.HelloObject;
import com.vivo.peter.HelloService;
import com.vivo.peter.annotation.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service()
public class HelloServiceImpl implements HelloService {
    @Override
    public String Hello(HelloObject object) {
        log.info("接收到：{}", object.getMessage());
        return "这是调用的返回值，id=" + object.getId();
    }
}
