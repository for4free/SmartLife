package com.example.smartlife;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Setting_Auto_Dialog.Setting_Auto_DialogListener;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.example.smartlife_api.GetModelAPI;
import com.example.smartlife_api.GetModelResultAPI;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting_Auto extends Activity implements OnTouchListener {

	private ImageView back, add;
	//private RelativeLayout re01;
	private TextView txt;
	private ListView list;
	private Setting_Auto_Adapter adapter;
	private List<GetModelResultAPI> totalList = new ArrayList<GetModelResultAPI>();
	private boolean flag = true;
	private String net_flag = "0",Uid;
	private LoadingDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.left_auto);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.left_auto_titlebar);
		//loading
		LoadingDialog.Builder dialogbulder = new LoadingDialog.Builder(this);
		dialog = dialogbulder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		// 标题栏
		back = (ImageView) findViewById(R.id.left_auto_btn_back);
		add = (ImageView) findViewById(R.id.left_auto_btn_add);
		back.setOnTouchListener(this);
		add.setOnTouchListener(this);
		
		txt = (TextView) findViewById(R.id.left_auto_temp_text);
		
		SharedPreferences sharedPreferences = getSharedPreferences("MODEL", 0);
		String model = sharedPreferences.getString("model", "");
		
		//加载listview
		list = (ListView) findViewById(R.id.left_auto_list);
		adapter = new Setting_Auto_Adapter(totalList, Setting_Auto.this);
		list.setAdapter(adapter);
		      
		new Thread(updateData).start();
		SharedPreferences sharedPreferences_login = getSharedPreferences("LOGIN", 0);
        Uid = sharedPreferences_login.getString("id", "");
	
	}

	// 触摸事件处理
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		// 标题栏
		case R.id.left_auto_btn_add:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {

				Setting_Auto_Dialog dialog = new Setting_Auto_Dialog(this,
						R.style.Dialog, new Setting_Auto_DialogListener() {

							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub
								switch (view.getId()) {
								/*
								 * case R.id.dialog_auto_but_cancle: onBack();
								 * break;
								 */
								

								default:
									break;
								}

							}
						});
				dialog.show();
			}
			break;
		case R.id.left_auto_btn_back:
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
			while (flag) {
				loadData(App.address+"GetModel.php?uid=" + Uid);
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
	  //加载数据数据
    public void loadData(String path){
    	
    	HttpUtils http = new HttpUtils(1*1000);//1s超时
    	http.configCurrentHttpCacheExpiry(100);
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
				GetModelAPI content = (GetModelAPI) JSONObject.parseObject(arg0.result, GetModelAPI.class);
				//根据字段用get方法获取字段内容
				List<GetModelResultAPI> items = content.getData();
				if(content.getStatus().equals("403")){
					net_flag = "1";
					txt.setVisibility(View.INVISIBLE);
					try {
						totalList.clear();
						totalList.addAll(items);
					} catch (Exception e) {
						// TODO: handle exception
					}
					adapter.notifyDataSetChanged();
				}else{
					txt.setText("请先设定模式");
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
