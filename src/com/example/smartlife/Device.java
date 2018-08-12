package com.example.smartlife;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.example.smartlife_out.Attributes;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Device extends Activity implements OnTouchListener {

	private ImageView img_btn_01, img_btn_03;
	private ListView list;
	private TextView txt;
	private Main_Device_Adapter adapter;
	private List<GetDevices_Api_Result> totalList = new ArrayList<GetDevices_Api_Result>();
	private String id;
	private boolean flag = true;
	private String net_flag = "0";
	private LoadingDialog dialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 自定义TITlebar
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.device);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.right_main_titlebar);

		//loading
		LoadingDialog.Builder dialogbulder = new LoadingDialog.Builder(this);
		dialog = dialogbulder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
		// 句柄
		img_btn_01 = (ImageView) findViewById(R.id.right_header_left_btn);
		img_btn_03 = (ImageView) findViewById(R.id.right_header_right_btn);
		txt = (TextView) findViewById(R.id.right_device_temp_text);
		// 监听
		img_btn_01.setOnTouchListener(this);
		img_btn_03.setOnTouchListener(this);
		
		//加载listview
		list = (ListView) findViewById(R.id.right_device_list);
        adapter = new Main_Device_Adapter(totalList, Device.this);
        list.setAdapter(adapter);
        new Thread(updateData).start();
        SharedPreferences sharedPreferences_login = getSharedPreferences("LOGIN", 0);
        id = sharedPreferences_login.getString("id", "");

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		// BACK返回
		case R.id.right_header_left_btn:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				flag = false;
				Device.this.finish();
			}
			break;
		// 手动添加设备
		case R.id.right_header_right_btn:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				Intent intent_03 = new Intent(Device.this, Device_Pen.class);
				Device.this.startActivity(intent_03);
			}
			break;

		default:
			break;
		}
		return false;
	}
	 //加载数据数据
	Runnable updateData = new Runnable() {
		
		@Override
		public void run() {
			while (flag) {
				loadData(App.address+"CGetData.php?uid="+id);//所有类型
				try {
					Thread.sleep(1*1000);  //1秒刷新一次
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Log.e("111", "111");
			}
			Thread.interrupted();
			//Log.e("111", "222");
		}
	};
	 //加载数据数据
   public void loadData(String path){
   	
   	HttpUtils http = new HttpUtils(10000);//9s超时
   	http.configCurrentHttpCacheExpiry(500);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

   		@Override
			public void onFailure(HttpException arg0, String arg1) {
	   			if(net_flag.equals("0")){
	   				txt.setVisibility(View.VISIBLE);
	   				dialog.dismiss();
	   			}
	   			
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//加载数据  转化为JSON格式
				GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
				//根据字段用get方法获取字段内容
				if(content.getStatus().equals("403")){
					net_flag = "1";
					txt.setVisibility(View.INVISIBLE);
					List<GetDevices_Api_Result> items = content.getResult();
					try {
						totalList.clear();
						totalList.addAll(items);
					} catch (Exception e) {
						// TODO: handle exception
					}
					adapter.notifyDataSetChanged();
				}else{
					txt.setText("请先添加设备");
					txt.setVisibility(View.VISIBLE);
				}
				dialog.dismiss();
			}
		});
   }
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		flag = false;
		this.finish();
		super.onBackPressed();
	}
}
