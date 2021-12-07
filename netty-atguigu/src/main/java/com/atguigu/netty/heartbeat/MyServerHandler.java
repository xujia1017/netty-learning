package com.atguigu.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 自定义一个Handler对空闲检测进一步进行处理
 *
 * 自定义一个Handler，需要继承 Netty 规定好的某个 HandlerAdapter（规范）
 *  InboundHandler 用于处理数据流入本端（服务端）的 IO 事件
 *  OutboundHandler 用于处理数据流出本端（服务端）的 IO 事件
 *
 * @author xujia
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {






    /**
     * 对 空闲事件 的处理
     *
     * @param ctx 上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent) {

            //将 evt 向下转型 IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                  eventType = "读空闲";
                  break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "--超时时间--" + eventType);
            System.out.println("服务器做相应处理..");

            //如果发生空闲，我们关闭通道
           // ctx.channel().close();
        }
    }
}
