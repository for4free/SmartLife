package com.example.smartlife;

import android.app.Application;
import android.content.SharedPreferences;

public class App extends Application {

	// API��ַ����
	static String address = "http://2naive.cn/api/";
	//��Ƶ����ַ
	static String moniAddress = "rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
	private String id;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		id = sharedPreferences.getString("id", "");
		// address = "http://www.kd2h.com/smartlifeapi/";
	}
	public String getId() {
		return id;
	}
	

}
