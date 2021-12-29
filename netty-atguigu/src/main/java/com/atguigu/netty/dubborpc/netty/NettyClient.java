package com.atguigu.netty.dubborpc.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Netty客户端
 *
 * @author Athletic
 * Created on 2021/12/29 23:32
 */
public class NettyClient {

    //创建线程池
    private static final ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler clientHandler;
    private int count = 0;

    /**
     * 编写方法使用代理模式，获取一个代理对象
     * @param serivceClass 服务类
     * @param providerName 服务器端的协议头
     * @return 代理对象
     */
    public Object getBean(final Class<?> serivceClass, final String providerName) {

        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serivceClass}, (proxy, method, args) -> {

                    System.out.println("(proxy, method, args) 进入...." + (++count) + " 次");
                    //{}  部分的代码，客户端每调用一次 hello, 就会进入到该代码
                    if (clientHandler == null) {
                        initClient();
                    }

                    //设置要发给服务器端的信息
                    //providerName 协议头 args[0] 就是客户端调用api hello(???), 参数
                    clientHandler.setParam(providerName + args[0]);

                    //
                    return executor.submit(clientHandler).get();

                });
    }

    /**
     * 初始化客户端
     */
    private static void initClient() {
        clientHandler = new NettyClientHandler();
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();

        /*
         * 创建客户端启动对象
         * Bootstrap 和 ServerBootstrap 分别是客户端和服务器端的引导类，
         * 一个Netty应用程序通常由一个引导类开始，主要是用来配置整个Netty程序、设置业务处理类（Handler）、绑定端口、发起连接等。
         */
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                //设置TCP不延迟）
                .option(ChannelOption.TCP_NODELAY, true)
                //handler()方法用于给BossGroup设置业务处理器
                .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new StringDecoder());
                                pipeline.addLast(new StringEncoder());
                                //设置业务处理Handler
                                pipeline.addLast(clientHandler);
                            }
                        }
                );


        try {
            bootstrap.connect("127.0.0.1", 9000).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
