package com.atguigu.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty 服务端
 *
 * @author xujia
 */
public class NettyServer {
    public static void main(String[] args) throws Exception {


        /**
         * 创建BossGroup 和 WorkerGroup
         *
         *  1. 创建两个线程组 bossGroup 和 workerGroup
         *  2. bossGroup 只是处理连接请求 , 真正的和客户端业务处理，会交给 workerGroup完成
         *  3. 两个都是无限循环
         *  4. bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数， 默认实际 cpu核数 * 2
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //8

        try {

            /**
             * 创建客户端启动对象
             * Bootstrap 和 ServerBootstrap 分别是客户端和服务器端的引导类，
             * 一个Netty应用程序通常由一个引导类开始，主要是用来配置整个Netty程序、设置业务处理类（Handler）、绑定端口、发起连接等。
             */
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 使用链式编程来进行设置
            bootstrap
                    //设置两个线程组
                    .group(bossGroup, workerGroup)
                    //使用NioSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //设置等待连接的队列的容量（当客户端连接请求速率大于 NioServerSocketChannel 接收速率的时候，会使用该队列做缓冲）
                    //option()方法用于给服务端的ServerSocketChannel添加配置
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态，childOption()方法用于给服务端ServerSocketChannel接收到的SocketChannel添加配置
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 该 handler对应 bossGroup , childHandler 对应 workerGroup
//                    .handler(null)
                    // 给我们的workerGroup 的 EventLoop 对应的管道设置业务处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() { //创建一个通道初始化对象(匿名对象)
                        //给pipeline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //可以使用一个集合管理SocketChannel，在推送消息时，可以将业务加入到各个channel对应的NIOEventLoop的taskQueue或者scheduleTaskQueue
                            System.out.println("客户socketchannel hashcode=" + ch.hashCode());
                            ch.pipeline().addLast(new NettyServerHandler());

                            /*
                             * 可以继续调用 socketChannel.pipeline().addLast()添加更多 Handler
                             */
                        }
                    });

            System.out.println(".....服务器 is ready...");

            //绑定一个端口并且同步, 生成了一个ChannelFuture对象。相当于在这里启动服务器(并绑定端口)
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();

            //给cf注册监听器，监控我们关心的事件
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口 6668 成功");
                    } else {
                        System.out.println("监听端口 6668 失败");
                    }
                }
            });

            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
