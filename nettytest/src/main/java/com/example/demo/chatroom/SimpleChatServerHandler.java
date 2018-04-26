package com.example.demo.chatroom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/4/23 12:24
 */
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> {

	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	/**
	 * 当从服务端收到新的客户端连接时，客户端的 Channel 存入 ChannelGroup 列表中，
	 * 并通知列表中的其他客户端 Channel
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)throws Exception{
		Channel incoming = ctx.channel();
		// Broadcast a message to multiple Channels
		channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 加入\n");

		channels.add(ctx.channel());

	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
		Channel incoming = ctx.channel();

		// Broadcast a message to multiple Channels
		channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 离开\n");

		// A closed Channel is automatically removed from ChannelGroup,
		// so there is no need to do "channels.remove(ctx.channel());"
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
			if (channel != incoming){
				channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + msg + "\n");
			} else {
				channel.writeAndFlush("[you]" + msg + "\n");
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
		Channel incoming = ctx.channel();
		System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"在线");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
		Channel incoming = ctx.channel();
		System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"掉线");
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (7)
		Channel incoming = ctx.channel();
		System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"异常");
		// 当出现异常就关闭连接
		cause.printStackTrace();
		ctx.close();
	}
}
