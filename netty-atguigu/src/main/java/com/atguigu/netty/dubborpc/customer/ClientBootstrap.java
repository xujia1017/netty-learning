package com.atguigu.netty.dubborpc.customer;

import com.atguigu.netty.dubborpc.netty.NettyClient;
import com.atguigu.netty.dubborpc.publicinterface.HelloService;

/**
 * 客户端启动器
 *
 * @author Athletic
 * Created on 2022-01-04 20:58:09
 */
public class ClientBootstrap {

    //这里定义协议头
    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) throws  Exception{

        //创建一个消费者（Netty客户端）
        NettyClient customer = new NettyClient();

        //通过反射创建服务类的代理对象
        HelloService helloService = (HelloService) customer.getBean(HelloService.class, providerName);

        while (true) {
            Thread.sleep(2 * 1000);
            //通过代理对象调用服务提供者的方法(服务)
            String res = helloService.hello("你好 dubbo~");
            System.out.println("调用的结果 res= " + res);
        }
    }
}
