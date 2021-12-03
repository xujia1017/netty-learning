package com.atguigu.netty.groupchat;

import java.text.SimpleDateFormat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Server端的业务处理handler
 *
 * @author xujia
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //public static List<Channel> channels = new ArrayList<Channel>();

    //使用一个hashmap 管理
    //public static Map<String, Channel> channels = new HashMap<String,Channel>();

    /**
     * 定义一个Channel线程组，管理所有的Channel, 参数执行器
     *  GlobalEventExecutor => 全局事件执行器
     *  INSTANCE => 表示是单例的
     */
    private static ChannelGroup  channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 将当前channel 加入到 channelGroup
     * handlerAdded表示连接建立之后，第一个被执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其它在线的客户端
        //该方法会将channelGroup中所有的channel遍历，并发送消息，我们不需要自己遍历
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天" + dateFormat.format(new java.util.Date()) + " \n");
        //将当前Channel加入ChannelGroup
        channelGroup.add(channel);

    }

    /**
     * 断开连接，将 XXX 退出群聊消息推送给当前在线的客户
     * 当某个Channel执行到这个方法，会自动从ChannelGroup中移除,不需要手动从ChannelGroup中剔除当前handler
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[" + dateFormat.format(new java.util.Date()) + "] " + "[客户端]" + channel.remoteAddress() + " 离开了\n");
        // 输出 ChannelGroup 的大小
        System.out.println("==== ChannelGroup-Size : " + channelGroup.size());

    }

    /**
     * 表示channel 处于活动状态, 提示xx上线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[" + dateFormat.format(new java.util.Date()) + "] " + ctx.channel().remoteAddress() + " 上线了~");
    }

    /**
     * 表示channel 处于不活动状态, 提示 xx离线了
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[" + dateFormat.format(new java.util.Date()) + "] " + ctx.channel().remoteAddress() + " 离线了~");
    }

    /**
     * 读取数据，并把读取到的数据转发给所有 客户
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        //获取到当前channel
        Channel currentChannel = ctx.channel();

        //这时我们遍历channelGroup, 根据不同的情况，回送不同的消息
        channelGroup.forEach(channel -> {
            if(currentChannel != channel) { //不是当前的channel,转发消息
                channel.writeAndFlush("[客户]" + currentChannel.remoteAddress() + " 发送了消息" + msg + "\n");
            }else {//回显自己发送的消息给自己
                channel.writeAndFlush("[自己]发送了消息" + msg + "\n");
            }
        });
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
        //关闭通道
        ctx.close();
    }
}
