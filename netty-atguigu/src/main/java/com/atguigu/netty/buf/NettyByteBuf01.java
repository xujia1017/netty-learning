package com.atguigu.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {


        /**
         * 创建一个ByteBuf (类似于NIO的重ByteBuffer, 但有区别)
         *  说明
         *         1. 创建对象，该对象包含一个数组arr, 是一个byte[10]
         *         2. 在netty的buffer中, 不需要使用flip进行反转, 底层维护了readerindex 和writerIndex
         *         3. 通过 readerindex 和writerIndex 和capacity， 将buffer分成三个区域
         *          readerindex                 下一个可以读取的位置
         *          writerIndex                 下一个可以写入的位置
         *          0 -- readerindex            已经读取的区域
         *          readerindex -- writerIndex  可读的区域
         *          writerIndex -- capacity     可写的区域
         */
        ByteBuf buffer = Unpooled.buffer(10);

        for(int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        System.out.println("capacity=" + buffer.capacity());//10

        //输出
//        for(int i = 0; i<buffer.capacity(); i++) {
//            System.out.println(buffer.getByte(i));
//        }
        for(int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.readByte());
        }

        System.out.println("执行完毕");
    }
}
