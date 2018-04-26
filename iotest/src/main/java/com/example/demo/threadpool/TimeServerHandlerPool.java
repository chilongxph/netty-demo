package com.example.demo.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/4/9 11:51
 */
public class TimeServerHandlerPool {

	private ExecutorService executor;

	public TimeServerHandlerPool(int maxPoolSize, int queueSize){
		executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),maxPoolSize,120L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueSize));
	}

	public void execute(Runnable task){
		executor.execute(task);
	}



}
