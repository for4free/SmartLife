package com.example.smartlife;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

public class ClientThread implements Runnable {
	private Handler handler;
	// 该线程所处理的Socket所对应的输入流
	private BufferedReader br = null;

	public ClientThread(Socket socket, Handler handler) throws IOException {
		this.handler = handler;
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	@Override
	public void run() {
		try {
			String content = null;
			// 不断读取Socket输入流的内容
			while ((content = br.readLine()) != null) {
				// 每当读到来自服务器的数据之后，发送消息通知程序界面显示该数据
				Message msg = new Message();
				msg.what = 0x234;
				msg.obj = content;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}