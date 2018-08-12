package com.example.smartlife;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Setting_Fml_Dialog_Create.Setting_Fml_Dialog_Create_DialogListener;
import com.example.smartlife.Setting_Fml_Dialog_Join.Setting_Fml_Dialog_Join_DialogListener;
import com.example.smartlife_api.GetDevices_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting_Fml extends Activity implements OnTouchListener {

	private Button create, join;
	private ImageView back;
	private boolean flag = true;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.left_fml);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.left_fml_titlebar);

		// 按钮
		create = (Button) findViewById(R.id.left_fml_btn_create);
		join = (Button) findViewById(R.id.left_fml_btn_join);
		back = (ImageView) findViewById(R.id.left_fml_btn_back);
		create.setOnTouchListener(this);
		join.setOnTouchListener(this);
		back.setOnTouchListener(this);
	
		//监听加入家庭事件
		new Thread(updateData).start();
	}

	// 触摸事件

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_fml_btn_create:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				create.setBackgroundResource(R.drawable.right_pen_max_clk);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				create.setBackgroundResource(R.drawable.right_pen_max);

				Setting_Fml_Dialog_Create dialog_create = new Setting_Fml_Dialog_Create(this, R.style.Dialog,new Setting_Fml_Dialog_Create_DialogListener() {

							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub
							

							}
						});
				dialog_create.show();
			}

			break;
		case R.id.left_fml_btn_join:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				join.setBackgroundResource(R.drawable.auto_dialog_btn);
				join.setTextColor(Color.rgb(65, 175, 175));
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				join.setBackgroundResource(R.drawable.auto_dialog_btn_clk);
				join.setTextColor(Color.rgb(255, 255, 255));
				Setting_Fml_Dialog_Join dialog = new Setting_Fml_Dialog_Join(
						this, R.style.Dialog,
						new Setting_Fml_Dialog_Join_DialogListener() {

							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub
								switch (view.getId()) {
								/*
								 * case R.id.dialog_fml_join_but_cancle:
								 * onBack(); break;
								 */
								case R.id.dialog_fml_join_but_ok:
									
									//Toast.makeText(getBaseContext(), "开发中...",Toast.LENGTH_SHORT).show();						
									break;
								default:
									break;
								}

							}
						});
				dialog.show();
			}
			break;
		case R.id.left_fml_btn_back:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				this.finish();
			}
			break;

		default:
			break;
		}

		return false;
	}

	Runnable updateData = new Runnable() {
		@Override
		public void run() {
			SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
			Intent intent_fml;
			while (flag) {
				String fid = sharedPreferences.getString("fid", "");
				
				if(fid.equals("0")){
					//intent_fml = new Intent(Setting_Fml.this, Setting_Fml.class);
					//Setting_Fml.this.startActivity(intent_fml);
				}else{
					flag = false;	
					intent_fml = new Intent(Setting_Fml.this, Setting_Fml_num.class);
					Setting_Fml.this.finish();
					Setting_Fml.this.startActivity(intent_fml);
				}
			}
			Thread.interrupted();
			}
	};
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		flag = false;
		this.finish();
		super.onBackPressed();
	}
}
