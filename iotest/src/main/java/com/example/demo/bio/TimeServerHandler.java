
package com.example.demo.bio;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * TODO
 * @author xuph-1028
 * @date 2018/4/8 17:30
 */
public class TimeServerHandler implements Runnable {

	private Socket socket;

	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			out = new PrintWriter(this.socket.getOutputStream(), true);
			String currentTime = null;
			String body = null;
			while (true) {

				body = in.readLine();
				if (null == body) {
					break;
				}
				currentTime = "now".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "error";
				out.println(currentTime);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null){
				out.close();
				out = null;
			}
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				in = null;
			}
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				socket = null;
			}

		}
	}
}
