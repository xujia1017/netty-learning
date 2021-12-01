package com.atguigu.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty 客户端
 *
 * @author xujia
 */
public class NettyClient {

    public static void main(String[] args) throws Exception {

        //客户端只需要一个事件循环组，可以看做 BossGroup
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {

            /**
             * 创建客户端启动对象
             * Bootstrap 和 ServerBootstrap 分别是客户端和服务器端的引导类，
             * 一个Netty应用程序通常由一个引导类开始，主要是用来配置整个Netty程序、设置业务处理类（Handler）、绑定端口、发起连接等。
             */
            Bootstrap bootstrap = new Bootstrap();

            //设置相关参数
            bootstrap
                    //设置线程组
                    .group(eventLoopGroup)
                    //设置客户端通道的实现类(反射)
                    .channel(NioSocketChannel.class)
                    //handler()方法用于给BossGroup设置业务处理器
                    .handler(
                            //创建一个通道初始化对象
                            new ChannelInitializer<SocketChannel>() {
                                //向Pipeline添加业务处理器
                                @Override
                                protected void initChannel(SocketChannel socketChannel) {
                                    //加入自己的处理器
                                    socketChannel.pipeline().addLast(new NettyClientHandler());

                                    /*
                                     * 可以继续调用socketChannel.pipeline().addLast()添加更多Handler
                                     */
                                }
                            }
                    );

            System.out.println("客户端 ok..");

            //启动客户端去连接服务器端，ChannelFuture 涉及到 Netty 的异步模型，后面展开讲
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            //给关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
