/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.atguigu.netty.source.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Echoes back any received data from a client.
 */
public final class EchoServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        /*
         * Configure the server.
         *
         * 说明:
         * 1) 先看启动类：main 方法中，首先创建了关于SSL 的配置类。
         *
         * 2) 重点分析下 创建了两个EventLoopGroup 对象：
         *         EventLoopGroup bossGroup = new NioEventLoopGroup(1);
         *         EventLoopGroup workerGroup = new NioEventLoopGroup();
         *  (1)这两个对象是整个 Netty 的核心对象，可以说，整个 Netty 的运作都依赖于他们。
         *          bossGroup 用于接受 Tcp 请求，他会将请求交给 workerGroup ，
         *          workerGroup 会获取到真正的连接，然后和连接进行通信，比如读写解码编码等操作。
         *  (2)EventLoopGroup 是 事件循环组（线程组）,含有多个 EventLoop，可以注册channel,用于在事件循环中去进行选择（和选择器相关）
         *  (3)new NioEventLoopGroup(1);
         *          这个1 表示 bossGroup 事件组有1个线程你可以指定，如果 new NioEventLoopGroup() 会含有默认个线程 cpu核数*2,
         *          即可以充分的利用多核的优势，
         *          DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
         *              "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
         *
         *          会创建EventExecutor 数组 children = new EventExecutor[nThreads];
         *          每个元素的类型就是 NIOEventLoop,  NIOEventLoop 实现了 EventLoop 接口 和 Executor 接口.
         *
         *      try 块中创建了一个 ServerBootstrap 对象，他是一个引导类，用于启动服务器和引导整个程序的初始化。它和 ServerChannel 关联，
         *      而 ServerChannel 继承了 Channel，有一些方法 remoteAddress等. 随后，变量 b 调用了 group 方法将两个 group 放入了自己的字段中，
         *      用于后期引导使用【debug 下group方法
         *      /**
         *      * Set the {@link EventLoopGroup} for the parent (acceptor) and the child (client). These
         *      * {@link EventLoopGroup}'s are used to handle all the events and IO for {@link ServerChannel} and
         *      * {@link Channel}'s.
         *
         *  (4) 然后添加了一个 channel，其中参数一个Class对象，引导类将通过这个 Class 对象反射创建 ChannelFactory。然后添加了一些TCP的参数。
         *    [说明：Channel 的创建在bind 方法，可以Debug下bind, 会找到channel = channelFactory.newChannel(); ]
         *
         *  (5) 再添加了一个服务器专属的日志处理器 handler。
         *
         *  (6) 再添加一个 SocketChannel（不是 ServerSocketChannel）的 handler。
         *
         *  (7) 然后绑定端口并阻塞至连接成功。
         *
         *  (8) 最后main线程阻塞等待关闭。
         *
         *  (9) finally 块中的代码将在服务器关闭时优雅关闭所有资源
         *
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /*
             * 说明:
             *  1) 链式调用：group方法，将boss和worker传入，boss赋值给parentGroup属性,worker赋值给 childGroup 属性
             *  2) channel 方法传入 NioServerSocketChannel class 对象。会根据这个 class 创建 channel 对象。
             *  3) option 方法传入 TCP 参数，放在一个LinkedHashMap 中。
             *  4) handler 方法传入一个 handler 中，这个hanlder 只专属于 ServerSocketChannel 而不是 SocketChannel
             *     childHandler 传入一个 hanlder ，这个handler 将会在每个客户端连接的时候调用。供 SocketChannel 使用
             */
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 100)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {

                 @Override
                 public void initChannel(SocketChannel ch) {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc()));
                     }
                     p.addLast(new LoggingHandler(LogLevel.INFO));
                     //p.addLast(new EchoServerHandler());
                 }
             });

            /*
             * Start the server.
             *  1 bind 方法代码, 追踪到 创建了一个端口对象，并做了一些空判断， 核心代码doBind,
             *  2 doBind 源码剖析, 核心是两个方法 initAndRegister 和  doBind0
             *      (1) 分析说明 initAndRegister
             */
            ChannelFuture f = bootstrap.bind(PORT).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();

        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
