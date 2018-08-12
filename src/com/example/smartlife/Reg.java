package com.example.smartlife;

import java.io.IOException;
import java.security.MessageDigest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.Login_Api;
import com.example.smartlife_ease.RegisterModel;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
//����
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.example.smartlife.R;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


public class Reg extends Activity implements OnTouchListener {

	private Button btn_ok, btn_c;
	private EditText name, pass01, pass02;
	private static final OkHttpClient mOkHttpClient=new OkHttpClient();
	private static final Gson gson=new Gson();
	private static final int REGISTER=0x01;
    private Handler mHandler=new Handler(){
    
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REGISTER:
                    RegisterModel bean= (RegisterModel) msg.obj;
                    if (bean.getStatus()==200){
                      //  Toast.makeText(getApplicationContext(),"ע��ɹ���",Toast.LENGTH_LONG).show();
    					Toast.makeText(getBaseContext(), "ע��ɹ�", Toast.LENGTH_SHORT).show();
    					SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
    					Editor editor = sharedPreferences.edit();
    					editor.putString("easepass", MD5(pass01.getText().toString()));
    					
    					editor.commit();
    					// ��������
    					Intent intent = new Intent();
    					intent.putExtra("name", name.getText().toString());
    					intent.putExtra("pass", pass01.getText().toString());
    					Reg.this.setResult(RESULT_OK, intent);
    					Reg.this.finish();
                    }else{
                    	deleteuser();
                        Toast.makeText(getApplicationContext(),"ע��ʧ�ܣ�"+bean.getMessage(),Toast.LENGTH_SHORT).show();
                        btn_ok.setText("ע��");
        				btn_ok.setEnabled(true);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
	
    private String uid;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.reg);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.login_titlebar);

		btn_ok = (Button) findViewById(R.id.reg_btn_ok);
		btn_c = (Button) findViewById(R.id.reg_btn_c);
		name = (EditText) findViewById(R.id.reg_edit_01);
		pass01 = (EditText) findViewById(R.id.reg_edit_02);
		pass02 = (EditText) findViewById(R.id.reg_edit_03);
		btn_c.setOnTouchListener(this);
		btn_ok.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.reg_btn_c:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				btn_c.setBackgroundResource(R.drawable.right_pen_max_clk);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				btn_c.setBackgroundResource(R.drawable.right_pen_max);
				// ��������
				Intent intent = new Intent();
				intent.putExtra("name", name.getText().toString());
				intent.putExtra("pass", pass01.getText().toString());
				this.setResult(RESULT_OK, intent);
				this.finish();
			}
			break;
		case R.id.reg_btn_ok:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				btn_ok.setBackgroundResource(R.drawable.right_pen_max_clk);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				if (name.getText().toString().equals("")) {
					Toast.makeText(getBaseContext(), "�˺Ų���Ϊ��",Toast.LENGTH_SHORT).show();
				}else if (TextUtils.isDigitsOnly(name.getText().toString())) {
					Toast.makeText(getBaseContext(), "�˺Ų���Ϊ������",Toast.LENGTH_SHORT).show();
				}else if (pass01.getText().toString().equals("")|| pass02.getText().toString().equals("")) {
					Toast.makeText(getBaseContext(), "���벻��Ϊ��",Toast.LENGTH_SHORT).show();
				} else if (pass01.getText().length() < 6) {
					Toast.makeText(getBaseContext(), "����������λ��",Toast.LENGTH_SHORT).show();
				} else if (!pass01.getText().toString().equals(pass02.getText().toString())) {
					Toast.makeText(getBaseContext(), "�������벻һ��",Toast.LENGTH_SHORT).show();
				} else {
					btn_ok.setText("ע����...");
					btn_ok.setEnabled(false);
					regst();
				}
				btn_ok.setBackgroundResource(R.drawable.right_pen_max);
			}
			break;
		default:
			break;
		}
		return false;
	}

	// ��д���ؼ� ��������
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		Intent intent = new Intent();
		intent.putExtra("name", "");
		intent.putExtra("pass", "");
		this.setResult(RESULT_OK, intent);
		this.finish();

		super.onBackPressed();
	}
//�û�ע��
	public void regst() {
		String path = App.address + "reg.php?name=" + name.getText().toString()+ "&pass=" + MD5(pass01.getText().toString());
		HttpUtils http = new HttpUtils(10000);//10s��ʱ
		http.configCurrentHttpCacheExpiry(5000); // ���û���5��,5����ֱ�ӷ����ϴγɹ�����Ľ����

		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "��������ʧ��", Toast.LENGTH_SHORT).show();
				btn_ok.setText("ע��");
				btn_ok.setEnabled(true);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// �������� ת��ΪJSON��ʽ
				Login_Api content = (Login_Api) JSONObject.parseObject(arg0.result, Login_Api.class);
				if (content.getStatus().equals("202")) {
					Toast.makeText(getBaseContext(), "�˺��ѱ�ע��",Toast.LENGTH_SHORT).show();
					btn_ok.setText("ע��");
					btn_ok.setEnabled(true);
				} else if (content.getStatus().equals("203")) {
					//����ע��
					uid = content.getId().toString();
					register();	
				}
			}
		});
	}
//����ע��ʧ�ܺ�ɾ���û�
	public void deleteuser() {
		String path = App.address + "DeleteUser.php?name=" + name.getText().toString() + "&pass=" + MD5(pass01.getText().toString());
		HttpUtils http = new HttpUtils(10000);//10s��ʱ
		http.configCurrentHttpCacheExpiry(5000); // ���û���5��,5����ֱ�ӷ����ϴγɹ�����Ľ����

		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				//Toast.makeText(getBaseContext(), "��������ʧ��", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
	// MD5����
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
	//����ע��
    private void register() {
        String u = uid;
        String p=MD5(uid);
        if (TextUtils.isEmpty(u)||TextUtils.isEmpty(p)){
          //  Toast.makeText(getApplicationContext(),"�˺Ż����벻��Ϊ�գ�",Toast.LENGTH_LONG).show();
            return ;
        }

        RequestBody requestBody= new FormEncodingBuilder().add("username",u).add("password",p).build();
        String url=App.address+"videoApi/index.php";
        Request request=new Request.Builder().url(url).post(requestBody).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("TAG","Error,register failure.");
            }
            @Override
            public void onResponse(Response response) throws IOException {
                String result=response.body().string();
                RegisterModel bean=gson.fromJson(result,RegisterModel.class);
                Message message=Message.obtain();
                message.obj=bean;
                message.what=REGISTER;
                mHandler.sendMessage(message);
            }
        });
    }
}
