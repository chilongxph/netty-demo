package com.example.demo.reactor;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/4/16 18:25
 */
public class Acceptor implements Runnable {
	private Reactor reactor;
	public Acceptor(Reactor reactor){
		this.reactor=reactor;
	}
	@Override
	public void run() {
		try {
			SocketChannel socketChannel=reactor.serverSocketChannel.accept();
			//调用Handler来处理channel
			if(socketChannel!=null){
				new SocketReadHandler(reactor.selector, socketChannel);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
