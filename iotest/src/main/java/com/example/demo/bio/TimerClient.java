package com.example.demo.bio;

import java.io.*;
import java.net.Socket;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/4/9 10:12
 */
public class TimerClient {

	public static void main(String[] args) {
		int port = 8080;
		if(args != null && args.length > 0){
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		Socket socket = null;
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			socket = new Socket("127.0.0.1",port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(),true);
			writer.println("now");
			System.out.println("send order to server succeed!");
			String resp = reader.readLine();
			System.out.println("-------now time-----"+resp);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(writer != null){
				writer.close();
				writer = null;
			}
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reader = null;
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
