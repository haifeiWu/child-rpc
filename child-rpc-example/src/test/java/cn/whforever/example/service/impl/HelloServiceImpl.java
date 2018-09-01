package cn.whforever.example.service.impl;

import cn.whforever.example.service.HelloService;

/**
 * @author wuhf
 * @Date 2018/9/1 18:41
 **/
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHi() {
        System.out.println("hello data from client");
        return "this is server";
    }
}
