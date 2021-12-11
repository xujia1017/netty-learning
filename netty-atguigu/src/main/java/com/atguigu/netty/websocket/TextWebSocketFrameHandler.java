package com.atguigu.netty.websocket;

import java.time.LocalDateTime;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 处理浏览器请求的Handler
 *
 * 这里 TextWebSocketFrame 类型，表示一个文本帧(frame)
 *
 * @author xujia
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{


    /**
     * channelRead0 读取客户端数据
     *
     * @param ctx   上线文环境
     * @param msg   消息
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        System.out.println("服务器收到消息 " + msg.text());

        //回复客户端(浏览器)消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间" + LocalDateTime.now() + " " + msg.text()));
    }

    /**
     * handlerAdded表示连接建立之后，第一个被执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id 表示唯一的值，LongText是唯一的，ShortText不是唯一
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asShortText());
    }


    /**
     * 断开连接
     * 当某个Channel执行到这个方法，会自动从ChannelGroup中移除,不需要手动从ChannelGroup中剔除当前handler
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // id 表示标识，asLongText 输出的是唯一的，asShortText 不一定是唯一的
        System.out.println("handlerRemoved 被调用-- "+ctx.channel().id().asLongText()+" (LongText)");
        System.out.println("handlerRemoved 被调用-- "+ctx.channel().id().asShortText()+" (ShortText)");
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生 " + cause.getMessage());
        ctx.close(); //关闭连接
    }
}
