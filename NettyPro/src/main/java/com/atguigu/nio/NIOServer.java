package com.atguigu.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO中的读和写:
 *      从server -> client 是读 [channel.read(bufer)], 将channel中的数据读到buffer中去
 *      从client -> server 是写 [channel.write(buffer)], 将buffer中的数据写入到channel中去
 */
@SuppressWarnings({"checkstyle:RegexpSingleline", "checkstyle:MagicNumber", "checkstyle:WhitespaceAfter"})
public class NIOServer {

    public static void main(String[] args) throws Exception {

        // 创建ServerSocketChannel -> ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 得到一个Selecor对象
        Selector selector = Selector.open();

        // 绑定一个端口6666, 在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        // 将ServerSocketChannel设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 把serverSocketChannel注册到selector, 关心事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("注册后的Selectionkey 数量=" + selector.keys().size()); // 1



        // 服務端循环等待客户端连接
        while (true) {

            //  这里我们阻塞等待1秒，如果没有事件发生, 则返回
            if (selector.select(1000) == 0) { //没有事件发生
                System.out.println("服务器等待了1秒，无连接");
                continue;
            }

            // 如果返回的>0, 就获取到相关的 selectionKey集合
            // 1. 如果返回的>0， 表示已经获取到关注的事件
            // 2. selector.selectedKeys() 返回关注事件的集合。通过 selectionKeys 反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selectionKeys 数量 = " + selectionKeys.size());

            // 遍历 Set<SelectionKey>, 使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            // 根据key对应的通道发生的事件做相应处理
            while (keyIterator.hasNext()) {
                // 获取到SelectionKey
                SelectionKey key = keyIterator.next();

                // 如果是 OP_ACCEPT, 有新的客户端连接
                if (key.isAcceptable()) {
                    // 该该客户端生成一个SocketChannel，此时已经知道有链接请求，因此accept方法不会阻塞
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());
                    // 将SocketChannel 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    // 将socketChannel注册到selector, 关注事件为 OP_READ, 同时给socketChannel关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("客户端连接后，注册的Selectionkey数量=" + selector.keys().size()); //2,3,4..
                }

                // 如果是 OP_READ，有读取的实践
                if (key.isReadable()) {
                    // 通过key 反向获取到对应channel
                    SocketChannel channel = (SocketChannel)key.channel();
                    // 获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    // 将当前channel中的数据读到buffer中去
                    channel.read(buffer);
                    System.out.println("form 客户端 " + new String(buffer.array()));
                }

                // 手动从集合中移动当前的selectionKey, 防止重复操作
                keyIterator.remove();

            }
        }
    }
}
