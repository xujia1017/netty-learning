package com.atguigu.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 服务初始化器 ：HttpServerInitializer
 *
 * @author xujia
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        //向管道加入处理器

        //得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        //1. 加入一个netty提供的netty提供的处理http的编-解码器-httpServerCodec codec =>[coder - decoder]
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());

        //2. 增加一个自定义的handler
        pipeline.addLast("MyHttpServerHandler", new HttpServerHandler());

        System.out.println("ok~~~~");

    }
}
