package com.atguigu.netty.dubborpc.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Netty服务器端
 *
 * @author Athletic
 * Created on 2021/12/29 23:16
 */
public class NettyServer {

    /**
     * 对实际启动器的包装，以应对不同的启动方式
     */
    public static void startServer(String hostName, int port) {
        startServer0(hostName, port);
    }

    /**
     * 编写一个方法，完成对NettyServer的初始化和启动
     * @param hostname  地址
     * @param port  端口
     */
    private static void startServer0(String hostname, int port) {

        /*
         * 创建BossGroup 和 WorkerGroup
         *
         *  1. 创建两个线程组 bossGroup 和 workerGroup
         *  2. bossGroup只是处理连接请求, 真正的和客户端业务处理会交给workerGroup完成
         *  3. 两个都是无限循环
         *  4. bossGroup和workerGroup含有的子线程(NioEventLoop)的个数， 默认实际 cpu核数 * 2
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            /*
             * 创建服务端启动对象
             *
             * Bootstrap 和 ServerBootstrap 分别是客户端和服务器端的引导类，
             * 一个Netty应用程序通常由一个引导类开始，主要是用来配置整个Netty程序、设置业务处理类（Handler）、绑定端口、发起连接等。
             */
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            // 使用链式编程来进行设置
            serverBootstrap
                    //设置两个线程组
                    .group(bossGroup,workerGroup)
                    //使用NioSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //给我们的workerGroup 的 EventLoop 对应的管道设置业务处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      protected void initChannel(SocketChannel ch) throws Exception {
                                          ChannelPipeline pipeline = ch.pipeline();
                                          pipeline.addLast(new StringDecoder());
                                          pipeline.addLast(new StringEncoder());
                                          //业务处理器Handler
                                          pipeline.addLast(new NettyServerHandler());
                                      }
                                  }
                    );

            //绑定一个端口并且同步, 生成了一个ChannelFuture对象。相当于在这里启动服务器(并绑定端口)，等待异步操作执行完毕
            ChannelFuture channelFuture = serverBootstrap.bind(hostname, port).sync();
            System.out.println("服务提供方开始提供服务~~");

            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
