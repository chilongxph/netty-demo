package com.example.demo.reactor2.multi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Nio 线程，专门负责nio read,write
 *
 * @author xuph-1028
 * @date 2018/4/18 17:28
 */
public class NioReactorThread extends Thread {

	private static final byte[] b = "hello,服务器收到了你的信息。".getBytes(); //服务端给客户端的响应

	private Selector selector;
	private List<SocketChannel> waitRegisterList = new ArrayList<>(512);
	private ReentrantLock registerLock = new ReentrantLock();

	public NioReactorThread() {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void register(SocketChannel socketChannel) {
		if(socketChannel != null ) {
			try {
				registerLock.lock();
				waitRegisterList.add(socketChannel);
			} finally {
				registerLock.unlock();
			}
		}
	}

	@Override
	public void run() {
		while(true) {
			Set<SelectionKey> ops = null;
			try {
				selector.select(1000);
				ops = selector.selectedKeys();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}

			//处理相关事件
			for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
				SelectionKey key =  it.next();
				it.remove();
				try {
					//向客户端发送请求
					if (key.isWritable()) {
						SocketChannel clientChannel = (SocketChannel)key.channel();
						ByteBuffer buf = (ByteBuffer)key.attachment();
						buf.flip();
						clientChannel.write(buf);
						System.out.println("服务端向客户端发送数据。。。");
						//重新注册读事件
						clientChannel.register(selector, SelectionKey.OP_READ);
					} else if(key.isReadable()) {  //接受客户端请求
						System.out.println("服务端接收客户端连接请求。。。");
						SocketChannel clientChannel = (SocketChannel)key.channel();
						ByteBuffer buf = ByteBuffer.allocate(1024);
						System.out.println(buf.capacity());
						clientChannel.read(buf);//
						buf.put(b);
						clientChannel.register(selector, SelectionKey.OP_WRITE, buf);//注册写事件
					}
				} catch(Throwable e) {
					e.printStackTrace();
					System.out.println("客户端主动断开连接。。。。。。。");
				}

			}

			//注册事件
			if(!waitRegisterList.isEmpty()) {
				try {
					registerLock.lock();
					for (Iterator<SocketChannel> it = waitRegisterList.iterator(); it.hasNext();) {
						SocketChannel sc = it.next();
						try {
							//向选择器注册读事件，客户端向服务端发送数据准备好后，再处理
							sc.register(selector, SelectionKey.OP_READ);
						} catch(Throwable e) {
							e.printStackTrace();//ignore
						}
						it.remove();
					}

				} finally {
					registerLock.unlock();
				}
			}


		}
	}
}
