package com.example.smartlife;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.BackMsg_Api;
import com.example.smartlife_api.Login_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Setting_BackMsg extends Activity implements OnTouchListener{
	
	private ImageView back;
	private Button sum;
	private EditText text1,text2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.left_setting_backmsg);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.left_backmsg_titlebar);
		
		back = (ImageView) findViewById(R.id.left_backmsg_btn_back);
		sum = (Button) findViewById(R.id.left_setting_backmsg_btn_ok);
		text1 = (EditText) findViewById(R.id.left_setting_backmsg_edit_01);
		text2 = (EditText) findViewById(R.id.left_setting_backmsg_edit_02);
		back.setOnTouchListener(this);
		sum.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_backmsg_btn_back:
			this.finish();
			break;
		case R.id.left_setting_backmsg_btn_ok:
			
			if(arg1.getAction() == MotionEvent.ACTION_DOWN){
				sum.setBackgroundResource(R.drawable.right_pen_max_clk);
			}
			if(arg1.getAction() == MotionEvent.ACTION_UP){sum.setBackgroundResource(R.drawable.right_pen_max);
				
				if(text1.getText().toString().equals("")){
					Toast.makeText(getBaseContext(), "����û�����뽨��", Toast.LENGTH_SHORT).show();
				}else{
					sum.setText("�ύ��...");
					uploadMsg();
					sum.setEnabled(false);
				}
				
			}
			
			break;
		default:
			break;
		}
		return false;
	}
	
	//�ϴ�����
	public void uploadMsg(){
		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		String id = sharedPreferences.getString("id", "");
		
		String path = App.address + "BackMsg.php";
		HttpUtils http = new HttpUtils(10000);//10s��ʱ
		http.configCurrentHttpCacheExpiry(5000); // ���û���5��,5����ֱ�ӷ����ϴγɹ�����Ľ����
		//String path = App.address + "temp.php";	
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", id);
		params.addBodyParameter("msg", text1.getText().toString());
		params.addBodyParameter("con", text2.getText().toString());
		
		http.send(HttpMethod.POST, path, params,new RequestCallBack<String>() {
		//http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "��������ʧ��", Toast.LENGTH_SHORT).show();
				sum.setText("�ύ");
				sum.setEnabled(true);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// �������� ת��ΪJSON��ʽ
				BackMsg_Api content = (BackMsg_Api) JSONObject.parseObject(arg0.result, BackMsg_Api.class);
				if (content.getStatus().equals("402")|| content.getStatus().equals("401")) {
					sum.setText("�ύ");
					Toast.makeText(getBaseContext(),"�ύ����", Toast.LENGTH_SHORT).show();
					sum.setEnabled(true);
				} else if (content.getStatus().equals("403")) {
					sum.setText("�ύ�ɹ�");
				//	Toast.makeText(getBaseContext(), "�ύ�ɹ�",Toast.LENGTH_SHORT).show();
					sum.setEnabled(false);
				} else {
					sum.setText("�ύ");
					Toast.makeText(getBaseContext(), "δ֪����", Toast.LENGTH_SHORT).show();
					sum.setEnabled(true);
				}
			}
		});
	}
	
}
