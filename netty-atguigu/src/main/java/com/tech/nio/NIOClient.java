package com.tech.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIO中的读和写:
 *      从server -> client 是读 [channel.read(bufer)], 将channel中的数据读到buffer中去
 *      从client -> server 是写 [channel.write(buffer)], 将buffer中的数据写入到channel中去
 */
public class NIOClient {

    public static void main(String[] args) throws Exception{

        // 得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        // 设置非阻塞
        socketChannel.configureBlocking(false);
        // 提供服务器端的ip 和 端口
        InetSocketAddress serverSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        // 连接服务器
        if (!socketChannel.connect(serverSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其它工作..");
            }
        }

        // ...如果连接成功，就发送数据
        String str = "hello, 尚硅谷~";
        // Wraps a byte array into a buffer(将字节数组包装到缓冲区中)
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        // 发送数据，将buffer数据写入channel
        socketChannel.write(buffer);

        System.in.read();

    }
}
