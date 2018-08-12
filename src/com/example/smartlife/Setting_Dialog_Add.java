package com.example.smartlife;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.DeleteDevice_Api;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Setting_Dialog_Add extends Dialog {

	private Context context;
	private ListView list;
	private String uid;
	private String Mid;
	private Setting_Auto_Adapter3 adapter;
	private List<GetDevices_Api_Result> totalList = new ArrayList<GetDevices_Api_Result>();
	private boolean flag = true;
	private Setting_Dialog_AddListener listener;

	public Setting_Dialog_Add(Context context) {
		super(context);
		this.context = context;
	}

	public interface Setting_Dialog_AddListener {
		public void onClick(View view);
	}

	public Setting_Dialog_Add(String uid,String Mid,Context context, int theme,Setting_Dialog_AddListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
		this.uid = uid;
		this.Mid = Mid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_auto_setting);
		initViews();
	}

	private void initViews() {

		//加载listview
		list = (ListView) findViewById(R.id.dialog_auto_setting_edit);
		adapter = new Setting_Auto_Adapter3(Mid,totalList, context);
		list.setAdapter(adapter);
		new Thread(update).start();
		
	}

	
	 //加载数据数据
	Runnable update = new Runnable() {
		
		@Override
		
		public void run() {
			while(flag){
				Log.e("TAG","17");
				loadData(App.address+"GetCtrlDevice.php?uid="+uid+"&Mid="+Mid);
				try {
					Thread.sleep(1*1000);  //1.0秒刷新一次
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Thread.interrupted();
		}
	};
	
	   public void loadData(String path){
	   	
	   	HttpUtils http = new HttpUtils(10000);//9s超时
	   	http.configCurrentHttpCacheExpiry(500);
			http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

	   		@Override
				public void onFailure(HttpException arg0, String arg1) {
		   			
		   			
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					//加载数据  转化为JSON格式
					GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
					//根据字段用get方法获取字段内容
					if(content.getStatus().equals("403")){
		
						List<GetDevices_Api_Result> items = content.getResult();
						try {
							totalList.clear();
							totalList.addAll(items);
						} catch (Exception e) {
							// TODO: handle exception
						}
						adapter.notifyDataSetChanged();
						
					}else{

					}
					//new Thread(update).start();
				}
			});
	   }

	
	  @Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		flag = false;
	}
}
