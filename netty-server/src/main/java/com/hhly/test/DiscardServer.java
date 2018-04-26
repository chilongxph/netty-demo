
package com.hhly.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TODO
 * @author xuph-1028
 * @date 2018/3/30 9:59
 */
public class DiscardServer {

	private int port;

	public DiscardServer(int port) {
		this.port = port;
	}

	public void exec() {
        //用来处理不同的传输
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
            //ServerBootstrap:启动 NIO 服务的辅助启动类
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
					.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
//ChannelInitializer : 帮助使用者配置一个新的 Channel
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new DiscardServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128) // (5)
					.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
			// 绑定端口，开始接收进来的连接
			ChannelFuture f = b.bind(port).sync(); // (7)
			// 等待服务器 socket 关闭 。
			// 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		int port;
		if (args.length > 0) {
			port = Integer.valueOf(args[0]);
		} else {
			port = 8080;
		}
		new DiscardServer(port).exec();
	}
}
