
package com.example.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * TODO
 * @author xuph-1028
 * @date 2018/4/11 16:04
 */
public class TimerClientHandler implements Runnable {

	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;

	public TimerClientHandler(String host, int port) {
		this.host = host == null ? "127.0.0.1" : host;
		this.port = port;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			doConnet();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!stop){
			try {
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey key = null;
				while (it.hasNext()){
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
				System.exit(1);
			}
		}

		//选择器关闭后，所有注册在其上的Channel、Pipe等资源都会被自动注销并关闭，所以不需要重复释放
		if(selector != null){
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			//判断是否连接成功
			SocketChannel sc = (SocketChannel) key.channel();
			//处于连接状态，说明服务端已返回确认信息
			if(key.isConnectable()){
				//客户端连接成功
				if(sc.finishConnect()){
					//将其注册到选择器上，然后就可以开始通信了
					sc.register(selector,SelectionKey.OP_READ);
					doWrite(sc);
				}else {
					//连接失败，直接退出
					System.exit(1);
				}
			}

			if (key.isReadable()) {
				// 读取数据
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(buffer);
				if (readBytes > 0) {
					buffer.flip();
					byte[] bytes = new byte[buffer.remaining()];
					buffer.get(bytes);
					String content = new String(bytes, "UTF-8");
					System.out.println("----now is----" + content);
					this.stop = true;
				} else if (readBytes < 0) {
					key.cancel();
					sc.close();
				}
			}
		}
	}



	private void doConnet() throws IOException {
		// 如果直接连接成功，则注册到选择器上，发送请求消息，读应答
		if (socketChannel.connect(new InetSocketAddress(host, port))) {
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		} else {
			//未直接连接成功，说明服务端未返回TCP握手应答消息，将socketChannel注册到选择器上，
			// 服务端返回确认消息后，选择器就能轮询到这个socketChannel处于连接就绪状态
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	private void doWrite(SocketChannel channel) throws IOException {
		byte[] req = "now".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		channel.write(writeBuffer);
		if (!writeBuffer.hasRemaining()) {
			System.out.println("send order 2 server succeed");
		}
	}
}
