package com.atguigu.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;


/**
 * 自定义一个 Handler，需要继承 Netty 规定好的某个 HandlerAdapter（规范）
 *
 *  InboundHandler 用于处理数据流入本端（服务端）的 IO 事件
 *  OutboundHandler 用于处理数据流出本端（服务端）的 IO 事件
 *
 * @author xujia
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道就绪就会触发该方法, 这个方法是与服务器的连接创建后第一个被调用
     *
     * @param ctx 上下文对象
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client " + ctx);
        // 向服务器发送数据
        ctx.writeAndFlush(
                // Unpooled类是Netty提供的专门操作缓冲区的工具类，copiedBuffer方法返回的ByteBuf 对象类似于NIO中的ByteBuffer，但性能更高
                Unpooled.copiedBuffer("hello, server: (>^ω^<)喵", CharsetUtil.UTF_8)
        );
    }

    /**
     * 当通道有读取事件时，会触发
     *
     * @param ctx 上下文对象
     * @param msg 服务器端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ByteBuf 是 Netty 提供的类，比 NIO 的 ByteBuffer 性能更高
        ByteBuf buf = (ByteBuf) msg;
        // 接收服务器端发来的数据
        System.out.println("服务器回复的消息:" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址： "+ ctx.channel().remoteAddress());
    }

    /**
     * 发生异常时执行
     *
     * @param ctx   上下文对象
     * @param cause 异常对象
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 关闭与服务器端的 Socket 连接
        ctx.close();
    }
}
