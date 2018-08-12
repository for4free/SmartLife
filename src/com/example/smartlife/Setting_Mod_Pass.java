package com.example.smartlife;

import java.security.MessageDigest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.Login_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting_Mod_Pass extends Activity implements OnTouchListener {

	private Button ok;
	private ImageView back;
	private EditText edtTxt1, edtTxt2;
	private CheckBox chB1, chB2;
	private String oldpass, name, newpass, uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.left_mod_pass);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.left_mod_pass_titlebar);

		ok = (Button) findViewById(R.id.left_mod_pass_btn);
		ok.setOnTouchListener(this);

		back = (ImageView) findViewById(R.id.left_mod_pass_title_btn);
		back.setOnTouchListener(this);

		edtTxt1 = (EditText) findViewById(R.id.left_mod_pass_start);
		edtTxt2 = (EditText) findViewById(R.id.left_mod_pass_end);
		chB1 = (CheckBox) findViewById(R.id.left_mod_pass_switch_start);
		chB2 = (CheckBox) findViewById(R.id.left_mod_pass_switch_end);
		chB1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					edtTxt1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					edtTxt1.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
		chB2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					edtTxt2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					edtTxt2.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_mod_pass_btn:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				ok.setBackgroundResource(R.drawable.right_pen_max_clk);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				ok.setBackgroundResource(R.drawable.right_pen_max);
				if (edtTxt1.getText().toString().equals("")) {
					Toast.makeText(getBaseContext(), "请输入原密码",Toast.LENGTH_SHORT).show();
				} else if (edtTxt2.getText().toString().equals("")) {
					Toast.makeText(getBaseContext(), "请输入新密码",Toast.LENGTH_SHORT).show();
				} else {
					if (edtTxt1.getText().toString().equals(edtTxt2.getText().toString())) {
						Toast.makeText(getBaseContext(), "两次密码不能相同",Toast.LENGTH_SHORT).show();
					} else {
						SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
						oldpass = sharedPreferences.getString("pass", "");
						name = sharedPreferences.getString("name", "");
						uid = sharedPreferences.getString("id", "");
						
						if (!oldpass.equals(MD5(edtTxt1.getText().toString()))) {
							Toast.makeText(getBaseContext(), "原密码不正确",Toast.LENGTH_SHORT).show();
						} else {
							modPass();
							ok.setText("修改中...");
							ok.setEnabled(false);
						}
					}
				}
			}
			break;
		case R.id.left_mod_pass_title_btn:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				this.finish();
			}
			break;

		default:
			break;
		}
		return false;
	}

	// 密码
	public void modPass() {
		newpass = MD5(edtTxt2.getText().toString());
		String path = App.address + "modpass.php?name="+name+"&pass="+newpass;
		//+"&id="+uid+"&oldpass="+oldpass
		HttpUtils http = new HttpUtils();
		
		/*post方式
		 * RequestParams params = new RequestParams();
		params.addBodyParameter("id", uid);
		params.addBodyParameter("name", name);
		params.addBodyParameter("pass", newpass);
		params.addBodyParameter("oldpass", oldpass);*/

		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
				ok.setText("确定");
				ok.setEnabled(true);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// 加载数据 转化为JSON格式
				Login_Api content = (Login_Api) JSONObject.parseObject(
						arg0.result, Login_Api.class);
				if (content.getStatus().equals("302")|| content.getStatus().equals("301")) {
					Toast.makeText(getBaseContext(),"修改出错", Toast.LENGTH_SHORT).show();
					ok.setText("确定");
					ok.setEnabled(true);
				} else if (content.getStatus().equals("303")) {
					SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
					Editor editor = sharedPreferences.edit();
					editor.putString("pass", newpass);
					editor.putString("mark", "0");
					editor.commit();
					Toast.makeText(getBaseContext(), "修改成功,请重新登陆",Toast.LENGTH_SHORT).show();

					// 先结束前几个activity
					Setting setting = new Setting();
					Setting.setting.finish();
					SmartLife smartlife = new SmartLife();
					SmartLife.smartlife.finish();
					Setting_Mod settingmod = new Setting_Mod();
					Setting_Mod.setting_mod.finish();

					Intent intent = new Intent(Setting_Mod_Pass.this,Login.class);
					Setting_Mod_Pass.this.startActivity(intent);
					Setting_Mod_Pass.this.finish();

				} else {
					Toast.makeText(getBaseContext(), "未知错误", Toast.LENGTH_SHORT).show();
					ok.setText("确定");
					ok.setEnabled(true);
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
