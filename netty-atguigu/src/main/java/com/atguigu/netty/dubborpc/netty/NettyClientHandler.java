package com.atguigu.netty.dubborpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * 客户端的业务处理Handle
 *
 * @author Athletic
 * Created on 2021/12/29 23:13
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;  //上下文
    private String result; //返回的结果
    private String param; //客户端调用方法时，传入的参数


    /**
     * 当通道就绪就会触发该方法, 这个方法是与服务器的连接创建后第一个被调用 (1)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" channelActive 被调用  ");
        //因为我们在其它方法会使用到 ctx
        context = ctx;
    }

    /**
     * 当通道有读取事件时，即收到服务器的数据后，就会触发调用该方法 (4)
     */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(" channelRead 被调用  ");
        result = msg.toString();
        notify(); //唤醒等待的线程
    }

    /**
     * 发生异常时执行
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 被代理对象调用, 发送数据给服务器，-> wait -> 等待被唤醒(channelRead) -> 返回结果 (3)->(5)
     */
    @Override
    public synchronized Object call() throws Exception {
        System.out.println(" call1 被调用  ");
        context.writeAndFlush(param);
        //进行wait，等待channelRead 方法获取到服务器的结果后，唤醒该线程
        wait();
        System.out.println(" call2 被调用  ");
        return  result; //服务方返回的结果
    }

    /**
     * 客户端参数的传递 (2)
     */
    public void setParam(String param) {
        System.out.println(" setPara  ");
        this.param = param;
    }
}
