package com.atguigu.netty.dubborpc.provider;

import com.atguigu.netty.dubborpc.netty.NettyServer;

/**
 * ServerBootstrap 会启动一个服务提供者，就是 NettyServer
 *
 * @author Athletic
 * Created on 2022-01-04 20:56:17
 */
public class ServerBootstrap {
    public static void main(String[] args) {

        //代码代填..
        NettyServer.startServer("127.0.0.1", 9000);
    }
}
