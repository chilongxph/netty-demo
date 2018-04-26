package com.example.demo.time.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/4/12 18:20
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

	private ByteBuf msg;

	public TimeClientHandler(){
		byte[] req = "now".getBytes();
		msg = Unpooled.buffer(req.length);
		msg.writeBytes(req);
	}


//	@Override
//	public void channelActive(ChannelHandlerContext ctx){
//		ctx.writeAndFlush(msg);
//	}




	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		msg = ctx.alloc().buffer(4); // (1)
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		msg.release(); // (1)
		msg = null;
	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
		ByteBuf buf = (ByteBuf) msg; // (1)
		try {
			long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
			Date currentTime = new Date(currentTimeMillis);
			System.out.println("Default Date Format:" + currentTime.toString());

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = formatter.format(currentTime);
			// 转换一下成中国人的时间格式
			System.out.println("------------Date Format:" + dateString);

			ctx.close();
		} finally {
			buf.release();
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
