package com.hhly;

import com.hhly.common.ChatServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

@SpringBootApplication
public class NettyServerApplication implements CommandLineRunner {

	@Autowired
	private ChatServer chatServer;

	public static void main(String[] args) {
		SpringApplication.run(NettyServerApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9090);
		ChannelFuture future = chatServer.start(address);

		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				chatServer.destroy();
			}
		});

		future.channel().closeFuture().syncUninterruptibly();
	}
}
