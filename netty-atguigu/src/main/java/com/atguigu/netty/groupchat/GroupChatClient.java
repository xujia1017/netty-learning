package com.atguigu.netty.groupchat;

import java.util.Scanner;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * Netty群聊系统-客户端
 *
 * @author xujia
 */
public class GroupChatClient {

    //属性
    private final String host;
    private final int port;

    public GroupChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 编写run方法，实现一些客户端的处理
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
        EventLoopGroup group = new NioEventLoopGroup();

        try {

            /**
             * 创建服务端启动对象
             *
             * Bootstrap 和 ServerBootstrap 分别是客户端和服务器端的引导类，
             * 一个Netty应用程序通常由一个引导类开始，主要是用来配置整个Netty程序、设置业务处理类（Handler）、绑定端口、发起连接等。
             */
            Bootstrap bootstrap = new Bootstrap()
                //设置线程组
                .group(group)
                //使用NioSocketChannel作为服务器的通道实现
                .channel(NioSocketChannel.class)
                // 给我们的workerGroup 的 EventLoop 对应的管道设置业务处理器
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        //得到pipeline
                        ChannelPipeline pipeline = channel.pipeline();
                        //加入相关handler
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        //加入自定义的业务处理handler
                        pipeline.addLast(new GroupChatClientHandler());
                    }
                });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //得到channel
            Channel channel = channelFuture.channel();
            System.out.println("-------" + channel.localAddress()+ "--------");

            //客户端需要输入信息，创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                //通过channel 发送到服务器端
                channel.writeAndFlush(msg + "\r\n");
            }

        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new GroupChatClient("127.0.0.1", 8800).run();
    }

}
