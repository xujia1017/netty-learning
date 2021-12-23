package com.atguigu.netty.protocoltcp;

import lombok.Data;

/**
 * 协议包
 */
@Data
public class MessageProtocol {

    private int length; //关键
    private byte[] content;

}
