package com.example.smartlife;

//import android.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
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
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Main_Moni extends Activity implements OnTouchListener {

	private ImageView add, back;
	private ImageView btn_open;
	private ListView list;
	private Main_Moni_Adapter adapter;
	private TextView txt;
	private List<GetDevices_Api_Result> totalList = new ArrayList<GetDevices_Api_Result>();
	private String net_flag = "0";
	private LoadingDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_moni_noitem);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.main_moni_titlebar);
		//loading
		LoadingDialog.Builder dialogbulder = new LoadingDialog.Builder(this);
		dialog = dialogbulder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
				
		txt = (TextView) findViewById(R.id.main_moni_temp_text);
		
		back = (ImageView) findViewById(R.id.main_moni_btn_back);
		add = (ImageView) findViewById(R.id.main_moni_btn_add);
		add.setOnTouchListener(this);
		back.setOnTouchListener(this);
		
		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		String uid = sharedPreferences.getString("id", "");
		
		list = (ListView) findViewById(R.id.main_moni_list);
        adapter = new Main_Moni_Adapter(totalList, Main_Moni.this);
        list.setAdapter(adapter);
        //totalList.addAll(listItem);
        loadData(App.address + "CGetData.php?uid=" + uid + "&getType=b2");
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_moni_btn_add:
			Intent intent = new Intent(Main_Moni.this, Device_Pen.class);
			Main_Moni.this.startActivity(intent);
			break;
		case R.id.main_moni_btn_back:
			this.finish();
			break;
		default:
			break;
		}
		return false;
	}
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
				//txt.setVisibility(View.INVISIBLE);
				//Toast.makeText(getBaseContext(), "网络连接成功", Toast.LENGTH_LONG).show();
				//加载数据  转化为JSON格式
				GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
				//根据字段用get方法获取字段内容
				List<GetDevices_Api_Result> items = content.getResult();
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
					txt.setText("没有相应的设备");
					txt.setVisibility(View.VISIBLE);
				}
				dialog.dismiss();
			}
		});
    }
}
