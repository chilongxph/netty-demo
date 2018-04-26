
package com.example.demo.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TODO
 * @author xuph-1028
 * @date 2018/4/8 17:05
 */
public class TimeServer {

	public static void main(String[] args) {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("-------serverSocket----start----port----" + port);
			Socket socket;
			while (true) {
				socket = serverSocket.accept();
				new Thread(new TimeServerHandler(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
