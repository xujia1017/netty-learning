package com.atguigu.netty.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 自定义客户端初始化类
 *
 * 出站：客户端 --> 服务端
 * 入站：服务端 --> 客户端
 */
public class MyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //获取pipeline
        ChannelPipeline pipeline = ch.pipeline();

        //加入一个出站的编码器handler，对数据进行一个编码
        pipeline.addLast(new MyLongToByteEncoder());

        //这时一个入站的解码器(入站handler )
        //pipeline.addLast(new MyByteToLongDecoder());
        pipeline.addLast(new MyByteToLongDecoder2());
        //加入一个自定义的业务逻辑handler，处理业务
        pipeline.addLast(new MyClientHandler());

    }
}
