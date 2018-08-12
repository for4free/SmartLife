package com.example.smartlife;

import java.security.MessageDigest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.Login_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Login extends Activity implements OnTouchListener {

	private EditText name, pass;
	private Button btn_ok;
	private TextView reg;
	private String name_str, pass_str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.login_titlebar);

		name = (EditText) findViewById(R.id.login_edit_01);
		pass = (EditText) findViewById(R.id.login_edit_02);
		btn_ok = (Button) findViewById(R.id.login_btn_ok);
		reg = (TextView) findViewById(R.id.login_reg);
		btn_ok.setOnTouchListener(this);
		reg.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.login_btn_ok:
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				if (name.getText().toString().equals("")) {
					Toast.makeText(getBaseContext(), "账号不能为空",Toast.LENGTH_SHORT).show();
				} else if (pass.getText().toString().equals("")) {
					Toast.makeText(getBaseContext(), "密码不能为空",Toast.LENGTH_SHORT).show();
				} else if (pass.getText().length() < 6) {
					Toast.makeText(getBaseContext(), "密码最少六位数",Toast.LENGTH_SHORT).show();
				} else {
					btn_ok.setText("登陆中...");
					btn_ok.setEnabled(false);
					login();
				}
				btn_ok.setBackgroundResource(R.drawable.right_pen_max);
			}
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				btn_ok.setBackgroundResource(R.drawable.right_pen_max_clk);
			}

			break;
		case R.id.login_reg:
			Intent inten_reg = new Intent(Login.this, Reg.class);
			startActivityForResult(inten_reg, 1);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		name_str = data.getExtras().getString("name");
		pass_str = data.getExtras().getString("pass");
		name.setText(name_str);
		pass.setText(pass_str);
	}

	public void login() {
		String path = App.address + "login.php?name="+ name.getText().toString() + "&pass=" + MD5(pass.getText().toString());
		HttpUtils http = new HttpUtils(10000);//10s超时
		http.configCurrentHttpCacheExpiry(5000); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
				btn_ok.setText("确定");
				btn_ok.setEnabled(true);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// 加载数据 转化为JSON格式
				Login_Api content = (Login_Api) JSONObject.parseObject(arg0.result, Login_Api.class);
				if (content.getStatus().equals("102")|| content.getStatus().equals("101")) {
					Toast.makeText(getBaseContext(), "账号或密码错误",Toast.LENGTH_SHORT).show();
					btn_ok.setText("确定");
					btn_ok.setEnabled(true);
				} else if (content.getStatus().equals("103")) {
					SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
					Editor editor = sharedPreferences.edit();
					editor.putString("name", content.getName().toString());
					editor.putString("pass", MD5(pass.getText().toString()));
					editor.putString("id", content.getId().toString());
					editor.putString("easepass", MD5(content.getId().toString()));
					
					if(content.getFid().equals("a")&&content.getFid_t().equals("a")){
						editor.putString("fid", "0");
					}else{
						editor.putString("fid", content.getFid().toString());
					}
					editor.putString("mark", "1");
					editor.commit();
					
					Intent intent = new Intent(Login.this, SmartLife.class);
					Login.this.startActivity(intent);
					Login.this.finish();
				} else {
					Toast.makeText(getBaseContext(), "未知错误", Toast.LENGTH_SHORT).show();
					btn_ok.setText("确定");
					btn_ok.setEnabled(true);
				}
			}
		});
	}

	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = (md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
