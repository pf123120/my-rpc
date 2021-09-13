package cn.nju.peter.impl;

import cn.nju.peter.HelloObject;
import cn.nju.peter.HelloService;
import cn.nju.peter.annotation.Service;
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
