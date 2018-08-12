package com.example.smartlife;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

public class ClientThread implements Runnable {
	private Handler handler;
	// ���߳��������Socket����Ӧ��������
	private BufferedReader br = null;

	public ClientThread(Socket socket, Handler handler) throws IOException {
		this.handler = handler;
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	@Override
	public void run() {
		try {
			String content = null;
			// ���϶�ȡSocket������������
			while ((content = br.readLine()) != null) {
				// ÿ���������Է�����������֮�󣬷�����Ϣ֪ͨ���������ʾ������
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