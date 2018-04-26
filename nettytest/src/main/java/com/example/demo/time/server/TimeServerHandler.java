package com.example.demo.time.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/4/8 9:36
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter{

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req,"UTF-8");
//        System.out.println("---now time---"+body);
//        String nowTime = "now".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString()
//                : "Error order";
//        ByteBuf resp = Unpooled.copiedBuffer(nowTime.getBytes());
//        ctx.write(resp);
//    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        //将消息发送队列中的消息写入到SocketChannel中发送给对方
        //为防止频繁地唤醒Selector进行消息发送，Netty的write方法并不直接把消息写入到SocketChannel中
        //而是先发送到缓存数组中，然后再调用flush方法将缓存区（ByteBuf）中的数据全部写入到SocketChannel
        ctx.flush();
    }


    /**
     * 在连接被建立并且准备进行通信时被调用
     * @param ctx
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        final ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
