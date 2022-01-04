package com.atguigu.netty.dubborpc.provider;

import com.atguigu.netty.dubborpc.publicinterface.HelloService;

/**
 * 服务提供方提供的服务(方法)的具体实现类
 *
 * @author Athletic
 * Created on 2022-01-04 20:56:30
 */
public class HelloServiceImpl implements HelloService{

    private static int count = 0;

    //当有消费方调用该方法时， 就返回一个结果
    @Override
    public String hello(String msg) {
        System.out.println("收到客户端消息=" + msg);
        //根据mes 返回不同的结果
        if(msg != null) {
            return "你好客户端, 我已经收到你的消息 [" + msg + "] 第" + (++count) + " 次";
        } else {
            return "你好客户端, 我已经收到你的消息 ";
        }
    }

}
