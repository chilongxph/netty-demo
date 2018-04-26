
package com.example.demo.time.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 时间服务器
 * @author xuph-1028
 * @date 2018/4/8 10:57
 */
public class TimeServer {

	private int port;

	public TimeServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		// 配置服务端的NIO线程组（EventLoopGroup是个线程组）
		// 用于服务端接受客户端的连接
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		// 用于进行SocketChannel的网络读写
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			// 服务端辅助启动类，用于启动服务端
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 128)
					// 绑定事件处理类
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel socketChannel) {
							socketChannel.pipeline().addLast(new TimeServerHandler());
						}
					});
			// 绑定监听端口，sync(),同步阻塞，等待绑定操作完成
			// ChannelFuture 继承自Future，用于异步操作的通知回调，完成后返回ChannelFuture
			ChannelFuture f = b.bind(port).sync();
			// 等待服务器监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 8080;
		}
		new TimeServer(port).run();
	}
}
