package com.atguigu.netty.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 自定义客户端初始化类
 *
 * 出站：客户端 --> 服务端
 * 入站：服务端 --> 客户端
 *
 * @author xujia
 */
public class MyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //获取pipeline
        ChannelPipeline pipeline = ch.pipeline();

        //加入一个出站的Decoder编码器handler，对出站的数据进行一个编码
        pipeline.addLast(new MyLongToByteEncoder());

        //这是一个入站的Decoder解码器handle，对入站的数据进行一个解码
        //pipeline.addLast(new MyByteToLongDecoder());
        pipeline.addLast(new MyByteToLongDecoder2());

        //加入一个自定义的业务逻辑handler，处理业务
        pipeline.addLast(new MyClientHandler());

    }
}
