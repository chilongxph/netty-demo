package com.example.demo.reactor2.multi;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * nio 线程组;简易的NIO线程组（子反应堆）
 *
 * @author xuph-1028
 * @date 2018/4/18 17:27
 */
public class NioReactorThreadGroup {
	//请求计数器
	private static final AtomicInteger requestCounter = new AtomicInteger();
	// 线程池IO线程的数量
	private final int nioThreadCount;
	private static final int DEFAULT_NIO_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
	private NioReactorThread[] nioThreads;
	public NioReactorThreadGroup() {
		this(DEFAULT_NIO_THREAD_COUNT);
	}

	public NioReactorThreadGroup(int threadCount) {
		if(threadCount < 1) {
			threadCount = DEFAULT_NIO_THREAD_COUNT;
		}
		this.nioThreadCount = threadCount;
		this.nioThreads = new NioReactorThread[threadCount];
		for(int i = 0; i < threadCount; i ++ ) {
			this.nioThreads[i] = new NioReactorThread();
			this.nioThreads[i].start(); //构造方法中启动线程，由于nioThreads不会对外暴露，故不会引起线程逃逸
		}
		System.out.println("Nio 线程数量：" + threadCount);
	}
	//分发请求
	public void dispatch(SocketChannel socketChannel) {
		if(socketChannel != null ) {
			next().register(socketChannel);
		}
	}

	protected NioReactorThread next() {
		return this.nioThreads[ requestCounter.getAndIncrement() %  nioThreadCount ];
	}
}
