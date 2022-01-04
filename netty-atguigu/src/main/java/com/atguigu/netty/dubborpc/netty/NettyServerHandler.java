package com.atguigu.netty.dubborpc.netty;


import com.atguigu.netty.dubborpc.customer.ClientBootstrap;
import com.atguigu.netty.dubborpc.provider.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务器端的业务处理handler
 *
 * 说明
 *  1. 我们自定义一个Handler 需要继续netty 规定好的某个HandlerAdapter(规范)
 *  2. 这时我们自定义一个Handler, 才能称为一个handler
 *
 *  InboundHandler 用于处理数据流入本端（服务端）的 IO 事件
 *
 * @author Athletic
 * Created on 2022-01-04 20:57:32
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道有数据可读时执行，会触发此函数(即再这里我们可以读取客户端发送的消息)
     *
     * @param ctx 上下文对象，可以从中取得相关联的 管道Pipeline、通道Channel、客户端地址等
     * @param msg 客户端发送的数据
     * @throws Exception 抛出异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取客户端发送的消息，并调用服务
        System.out.println("msg=" + msg);
        //客户端在调用服务器的api 时，我们需要定义一个协议
        //比如我们要求 每次发消息是都必须以某个字符串开头 "HelloService#hello#你好"
        if(msg.toString().startsWith(ClientBootstrap.providerName)) {
            String result = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
