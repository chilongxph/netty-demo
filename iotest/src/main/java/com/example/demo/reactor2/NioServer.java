
package com.example.demo.reactor2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * TODO
 * @author xuph-1028
 * @date 2018/4/17 16:50
 */
public class NioServer {

	public static void main(String[] args) {
		(new Thread(new Reactor())).start();
	}

	/**
	 * 反应堆
	 */
	private static final class Reactor implements Runnable {

		private static final byte[] b = "hello,服务器收到了你的信息。".getBytes();

		@Override
		public void run() {
			System.out.println("服务端启动成功，等待客户端接入");
			ServerSocketChannel ssc = null;
			Selector selector = null;
			try {
				ssc = ServerSocketChannel.open();
				selector = Selector.open();
				ssc.configureBlocking(false);
				//服务端监听9080端口
				ssc.bind(new InetSocketAddress("127.0.0.1", 9080));
				SelectionKey selectionKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
				Set<SelectionKey> keys;
				while (true) {
					selector.select();
					keys = selector.selectedKeys();
					// 处理相关事件
					for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
						SelectionKey key = it.next();
						it.remove();
						// a connection was accepted by a ServerSocketChannel.
						if (key.isAcceptable()) {
							// 客户端建立连接
							ServerSocketChannel serverSc = (ServerSocketChannel) key.channel();
							//第二次握手，新接入的客户端连接，设置为非阻塞模式
							SocketChannel clientChannel = serverSc.accept();
							clientChannel.configureBlocking(false);
							//向选择器注册读事件，客户端向服务端发送数据准备好后，再处理
							clientChannel.register(selector, SelectionKey.OP_READ);
							System.out.println("-----收到客户端的连接请求。。。-----"+System.currentTimeMillis());
						} else if (key.isWritable()) {
							// 服务端写
							SocketChannel clientChannel = (SocketChannel) key.channel();
							ByteBuffer buf = (ByteBuffer) key.attachment();
							buf.flip();
							clientChannel.write(buf);
							System.out.println("服务端向客户端发送数据。。。"+System.currentTimeMillis());
							// 重新注册读事件
							clientChannel.register(selector, SelectionKey.OP_READ);
						} else if (key.isReadable()) {
							// 处理客户端发送的数据
							System.out.println("服务端接收客户端连接请求。。。"+System.currentTimeMillis());
							SocketChannel clientChannel = (SocketChannel) key.channel();
							ByteBuffer buf = ByteBuffer.allocate(1024);
							System.out.println(buf.capacity());
							clientChannel.read(buf);
							buf.put(b);
							// 注册写事件
							clientChannel.register(selector, SelectionKey.OP_WRITE, buf);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
