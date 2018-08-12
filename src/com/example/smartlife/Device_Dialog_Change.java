package com.example.smartlife;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.DeleteDevice_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Device_Dialog_Change extends Dialog implements OnClickListener {

	private Context context;
	private Button btn_cancle, btn_ok;
	private EditText text;
	private String uid,type,did;
	
	private Device_Dialog_ChangeListener listener;

	public Device_Dialog_Change(Context context) {
		super(context);
		this.context = context;
	}

	public interface Device_Dialog_ChangeListener {
		public void onClick(View view);
	}

	public Device_Dialog_Change(String uid,String type,String did,Context context, int theme,Device_Dialog_ChangeListener listener) {
		super(context, theme);
		this.context = context;
		this.uid = uid;
		this.type = type;
		this.did = did;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_device_change);
		initViews();
	}

	private void initViews() {
		btn_cancle = (Button) findViewById(R.id.dialog_device_change_but_cancle);
		btn_ok = (Button) findViewById(R.id.dialog_device_change_but_ok);
		text = (EditText) findViewById(R.id.dialog_device_change_edit);
		btn_cancle.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
		switch (view.getId()) {
		case R.id.dialog_device_change_but_cancle:
			this.dismiss();
			break;
		case R.id.dialog_device_change_but_ok:
			HttpUtils http = new HttpUtils(10000);//9s超时
	    	http.configCurrentHttpCacheExpiry(500);
	    	final String PATH = App.address+"ChangeDeviceName.php?uidORtid="+uid+"&getId="+did+"&getType="+type+"&newName="+text.getText().toString();
			http.send(HttpMethod.GET, PATH, new RequestCallBack<String>() {

	    		@Override
				public void onFailure(HttpException arg, String s) {
	    			//Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
	    			Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> arg) {
					//暂用一下这个API
					DeleteDevice_Api content_1 = (DeleteDevice_Api) JSONObject.parseObject(arg.result, DeleteDevice_Api.class);
					if(content_1.getStatus().toString().equals("401")){
						Toast.makeText(context, "更改失败", Toast.LENGTH_SHORT).show();
					}else if(content_1.getStatus().toString().equals("402")){
						Toast.makeText(context, "更改失败", Toast.LENGTH_SHORT).show();
					}else if(content_1.getStatus().toString().equals("403")){
						Toast.makeText(context, "更改成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "未知错误"+content_1.getStatus().toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});
			this.dismiss();
			break;
		default:
			break;
		}
	}

}
