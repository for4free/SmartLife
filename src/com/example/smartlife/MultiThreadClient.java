package com.example.smartlife;

import java.io.OutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MultiThreadClient extends Activity {

	private EditText input, show;
	private Button sendBtn;
	private OutputStream os;
	private Handler handler;
	private ImageView back;
	private String ipadress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_sockt);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.activity_socket_titlebar);

		back = (ImageView) findViewById(R.id.socket_btn_back);
		input = (EditText) findViewById(R.id.main_et_input);
		show = (EditText) findViewById(R.id.main_et_show);
		sendBtn = (Button) findViewById(R.id.main_btn_send);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// �����Ϣ�������߳�
				if (msg.what == 0x234) {
					// ����ȡ������׷����ʾ���ı�����
					show.append("\n" + msg.obj.toString());
				}
			}
		};

		SharedPreferences sharedPreferences = getSharedPreferences("SOCKET", 0);
		String host = sharedPreferences.getString("ipadress", "");
		if (!host.toString().equals("")) {
			ipadress = host;
			new Thread(socket_runnable_2).start();
		} else {
			Toast.makeText(getBaseContext(), "������IP��ַ", Toast.LENGTH_SHORT)
					.show();
			this.finish();
		}

		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// ���û����ı��������������д������
					os.write((input.getText().toString() + "\r\n").getBytes());
					// ���input�ı�������
					input.setText("");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MultiThreadClient.this.finish();

			}
		});
	}

	Runnable socket_runnable_2 = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Socket socket;
			try {
				socket = new Socket(ipadress, 18088);
				// �ͻ�������ClientThread�̲߳��϶�ȡ���Է�����������
				show.append("�ɹ����ӵ������\n");
				new Thread(new ClientThread(socket, handler)).start();
				os = socket.getOutputStream();
			} catch (Exception e) {
				e.printStackTrace();
				show.append("���ӷ����ʧ��\n");
			}
		}
	};

}