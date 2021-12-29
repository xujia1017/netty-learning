package com.atguigu.netty.dubborpc.publicinterface;

/**
 * 这个是公共接口，是服务提供方和服务消费方都需要
 *
 * 服务提供方实现这个接口
 * 服务消费方调用这个接口
 *
 */
public interface HelloService {

    String hello(String mes);

}
