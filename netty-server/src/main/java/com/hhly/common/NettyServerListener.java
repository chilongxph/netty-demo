package com.hhly.common;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/3/28 15:26
 */
public class NettyServerListener {

    /**
     * 创建bootstrap
     */
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    /**
     * BOSS
     */
    EventLoopGroup boss = new NioEventLoopGroup();
    /**
     * Worker
     */
    EventLoopGroup work = new NioEventLoopGroup();
    /**
     * 通道适配器
     */
//    @Resource
//    private ServerChannelHandlerAdapter channelHandlerAdapter;

}
