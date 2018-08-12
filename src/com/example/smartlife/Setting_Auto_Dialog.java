package com.example.smartlife;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetDevices_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setting_Auto_Dialog extends Dialog implements OnClickListener {

	private Context context;
	private Button btn_cancle, btn_ok;
	private EditText text;
	private Setting_Auto_DialogListener listener;

	public Setting_Auto_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface Setting_Auto_DialogListener {
		public void onClick(View view);
	}

	public Setting_Auto_Dialog(Context context, int theme,
			Setting_Auto_DialogListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_auto);
		initViews();
	}

	private void initViews() {
		btn_cancle = (Button) findViewById(R.id.dialog_auto_but_cancle);
		btn_ok = (Button) findViewById(R.id.dialog_auto_but_ok);
		text = (EditText) findViewById(R.id.dialog_auto_edit);// 暂时没有用到
		btn_cancle.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
		switch (view.getId()) {
		case R.id.dialog_auto_but_cancle:
			this.dismiss();
			break;
		case R.id.dialog_auto_but_ok:
			//Toast.makeText(getBaseContext(), "开发中...",Toast.LENGTH_SHORT).show();
			//AddModel.php?uid=64&name=场景名称
			String txt = text.getText().toString();
			if(txt.length()>4){
				Toast.makeText(context, "场景名称不得多于四字", Toast.LENGTH_SHORT).show();
			}else{
				SharedPreferences sharedPreferences_login = context.getSharedPreferences("LOGIN", 0);
		        String Uid = sharedPreferences_login.getString("id", "");
				doModel(App.address+"AddModel.php?uid="+Uid+"&name="+txt);
				this.dismiss();
			}
			break;
		default:
			break;
		}
	}
    //处理事件
	public void doModel(String path){
	   	
	   	HttpUtils http = new HttpUtils(10000);//9s超时
	   	http.configCurrentHttpCacheExpiry(500);
			http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

	   		@Override
				public void onFailure(HttpException arg0, String arg1) {
	   				Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
					if(content.getStatus().equals("401")){
						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("402")){
						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("403")){
						Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
					}
				}
			});
	   }
}
