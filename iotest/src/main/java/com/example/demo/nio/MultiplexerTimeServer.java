
package com.example.demo.nio;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * TODO
 * @author xuph-1028
 * @date 2018/4/9 16:34
 */
public class MultiplexerTimeServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel socketChannel;
	private volatile boolean stop;

	/**
	 * 初始化多路复用器，绑定监听端口
	 * @param port
	 */
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();
			socketChannel = ServerSocketChannel.open();
			// 设置连接为非阻塞模式
			socketChannel.configureBlocking(false);
			socketChannel.bind(new InetSocketAddress(port), 1024);
			// 将ServerSocketChannel注册到线程的多路复用器Selector上，监听accept事件
			socketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("---time server started in port---" + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				//selector每隔1秒被唤醒一次，当有处于就绪状态的Channel时，返回该Channel的SelectionKey集合
				selector.select(1000L);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey key = null;
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			// 处理新接入的请求信息
			if (key.isAcceptable()) {
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
				SocketChannel socketChannel = serverSocketChannel.accept();
				socketChannel.configureBlocking(false);
				// 添加一个线程的连接给复用器
				socketChannel.register(selector, SelectionKey.OP_READ);
			}
			if (key.isReadable()) {
				// 读取数据
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(buffer);
				if (readBytes > 0) {
					buffer.flip();
					byte[] bytes = new byte[buffer.remaining()];
					buffer.get(bytes);
					String content = new String(bytes, "UTF-8");
					System.out.println("----accept order----" + content);
					String curremtTimes = "now".equalsIgnoreCase(content)
							? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
					doWrite(sc, curremtTimes);
				} else if (readBytes < 0) {
					key.cancel();
					sc.close();
				}
			}
		}
	}

	private void doWrite(SocketChannel channel, String resp) throws IOException {
		if (!StringUtils.isEmpty(resp)) {
			byte[] bytes = resp.getBytes("UTF-8");
			ByteBuffer src = ByteBuffer.allocate(bytes.length);
			src.put(bytes);
			src.flip();
			channel.write(src);
		}
	}
}
