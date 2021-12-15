package com.atguigu.netty.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 自定义服务端初始化类
 */
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //获取对应的pipeline
        ChannelPipeline pipeline = ch.pipeline();//一会下断点

        //添加对应的handler
        // 入站的handler进行解码 MyByteToLongDecoder
        //pipeline.addLast(new MyByteToLongDecoder());
        pipeline.addLast(new MyByteToLongDecoder2());
        //出站的handler进行编码
        pipeline.addLast(new MyLongToByteEncoder());
        //自定义的handler 处理业务逻辑
        pipeline.addLast(new MyServerHandler());

        System.out.println("xx");
    }
}
