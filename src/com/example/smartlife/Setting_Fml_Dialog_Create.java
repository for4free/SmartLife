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
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setting_Fml_Dialog_Create extends Dialog implements
		OnClickListener {

	private Context context;
	private Button btn_cancle, btn_ok;
	private EditText text;
	private String uid,text_t;
	private Setting_Fml_Dialog_Create_DialogListener listener;

	public Setting_Fml_Dialog_Create(Context context) {
		super(context);
		this.context = context;
	}

	public interface Setting_Fml_Dialog_Create_DialogListener {
		public void onClick(View view);
	}

	public Setting_Fml_Dialog_Create(Context context, int theme,
			Setting_Fml_Dialog_Create_DialogListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_fml_create);
		initViews();
	}

	private void initViews() {
		btn_cancle = (Button) findViewById(R.id.dialog_fml_create_but_cancle);
		btn_ok = (Button) findViewById(R.id.dialog_fml_create_but_ok);
		text = (EditText) findViewById(R.id.dialog_fml_create_edit);// 暂时没有用到
		btn_cancle.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
		switch (view.getId()) {
		case R.id.dialog_fml_create_but_cancle:
			this.dismiss();
			break;
		case R.id.dialog_fml_create_but_ok:
			text_t = text.getText().toString();
			new Thread(createFamily).start();
			this.dismiss();
			break;
		default:
			break;
		}
	}
	
	 //创建家庭
	Runnable createFamily = new Runnable() {	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN", 0);
			uid = sharedPreferences.getString("id", "");
			 createFamily(App.address+"CreateFamily.php?uid="+uid+"&fmlName="+text_t);	
		}
	};
	   public void  createFamily(String path){
	   	
	   	HttpUtils http = new HttpUtils(10000);//9s超时
	   	http.configCurrentHttpCacheExpiry(500);
			http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

	   		@Override
				public void onFailure(HttpException arg0, String arg1) {
	   			//txt.setVisibility(View.VISIBLE);
	   			//	btn_ok.setText("添加");
	   			//	btn_ok.setEnabled(true);
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					//txt.setVisibility(View.INVISIBLE);
					//加载数据  转化为JSON格式
					GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
					//根据字段用get方法获取字段内容
					if(content.getStatus().equals("401")){
						Toast.makeText(context, "创建失败", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("402")){
						Toast.makeText(context, "家庭已经存在", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("403")){
						Toast.makeText(context, "创建成功", Toast.LENGTH_SHORT).show();
						SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN", 0);
						Editor editor = sharedPreferences.edit();
						editor.putString("fid", text_t);
						editor.commit();			
					}else{
						Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
					}
					//adapter.notifyDataSetChanged();
				//	btn_ok.setText("添加");
				//	btn_ok.setEnabled(true);
				}
				
			});
	   }

}
