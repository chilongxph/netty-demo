
package com.hhly.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * TODO
 * @author xuph-1028
 * @date 2018/3/29 18:31
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
		ByteBuf in = (ByteBuf) msg;
		// 直接抛弃
		// in.release();
		// 将接受到的小打印出来
		try {
			System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            in.release();
//			ReferenceCountUtil.release(msg); // (2)
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		// 当出现异常就关闭连接
		cause.printStackTrace();
		ctx.close();
	}
}
