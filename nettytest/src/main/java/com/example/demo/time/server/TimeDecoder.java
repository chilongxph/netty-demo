package com.example.demo.time.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/4/20 14:50
 */
public class TimeDecoder extends ByteToMessageDecoder {
	/**
	 * 每当有新数据接收的时候，ByteToMessageDecoder 都会调用 decode() 方法来处理内部的那个累积缓冲
	 * @param ctx
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return; // (3)
		}

		out.add(in.readBytes(4)); // (4)
	}
}
