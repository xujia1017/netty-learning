package com.atguigu.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Netty群聊系统-服务端
 *
 * @author xujia
 */
public class GroupChatServer {

    //监听端口
    private int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    /**
     * 编写run方法，处理客户端的请求
     *
     * @throws Exception
     */
    public void run() throws Exception{

        /**
         * 创建BossGroup 和 WorkerGroup
         *
         *  1. 创建两个线程组 bossGroup 和 workerGroup
         *  2. bossGroup只是处理连接请求, 真正的和客户端业务处理会交给workerGroup完成
         *  3. 两个都是无限循环
         *  4. bossGroup和workerGroup含有的子线程(NioEventLoop)的个数， 默认实际 cpu核数 * 2
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);  //8个NioEventLoop

        try {

            /**
             * 创建服务端启动对象
             *
             * Bootstrap 和 ServerBootstrap 分别是客户端和服务器端的引导类，
             * 一个Netty应用程序通常由一个引导类开始，主要是用来配置整个Netty程序、设置业务处理类（Handler）、绑定端口、发起连接等。
             */
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    //设置两个线程组
                    .group(bossGroup, workerGroup)
                    //使用NioSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //设置等待连接的队列的容量（当客户端连接请求速率大于 NioServerSocketChannel 接收速率的时候，会使用该队列做缓冲）
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态，childOption()方法用于给服务端ServerSocketChannel接收到的SocketChannel添加配置
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 给我们的workerGroup 的 EventLoop 对应的管道设置业务处理器，该handler对应bossGroup, childHandler对应workerGroup
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //向pipeline加入解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            //向pipeline加入编码器
                            pipeline.addLast("encoder", new StringEncoder());
                            //加入自己的业务处理handler
                            pipeline.addLast(new GroupChatServerHandler());
                        }
                    });

            System.out.println("netty 服务器启动");
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            //监听关闭事件
            channelFuture.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {

        new GroupChatServer(8800).run();
    }
}
