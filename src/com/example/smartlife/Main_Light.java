package com.example.smartlife;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Text;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetData_Api_Result;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Main_Light extends Activity implements OnTouchListener {
	
	private ImageView back, add;
	private ListView list;
	private TextView txt;
	private Main_Light_Adapter adapter;
	private List<GetDevices_Api_Result> totalList = new ArrayList<GetDevices_Api_Result>();
	private String id;
	private boolean flag = true;
	private String net_flag = "0";
	private LoadingDialog dialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.layout_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.main_light_titlebar);
		
		back = (ImageView) findViewById(R.id.main_light_btn_back);
		back.setOnTouchListener(this);
		add = (ImageView) findViewById(R.id.main_light_btn_add);
		add.setOnTouchListener(this);
		txt = (TextView) findViewById(R.id.main_light_temp_text);
		//loading
		LoadingDialog.Builder dialogbulder = new LoadingDialog.Builder(this);
		dialog = dialogbulder.create();
		dialog.setCanceledOnTouchOutside(false);		
		dialog.show();
		//加载listview
		list = (ListView) findViewById(R.id.main_light_list);
        adapter = new Main_Light_Adapter(totalList, Main_Light.this);
        list.setAdapter(adapter);
        
        new Thread(updateData).start();
        SharedPreferences sharedPreferences_login = getSharedPreferences("LOGIN", 0);
        id = sharedPreferences_login.getString("id", "");
        
	}
	
	Runnable updateData = new Runnable() {
		
		@Override
		public void run() {
			while (flag) {
				loadData(App.address+"CGetData.php?uid="+id+"&getType=9");//9是pwm的类型  灯光控制
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
				//txt.setVisibility(View.INVISIBLE);
				//Toast.makeText(getBaseContext(), "网络连接成功", Toast.LENGTH_LONG).show();
				//加载数据  转化为JSON格式
				GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
				//根据字段用get方法获取字段内容
				List<GetDevices_Api_Result> items = content.getResult();
				//计数
				int i = 0;
				SharedPreferences sharedPreferences = getSharedPreferences("DATA", 0);
				Editor editor = sharedPreferences.edit();
				if(content.getStatus().equals("403")){
					net_flag = "1";
					txt.setVisibility(View.INVISIBLE);
					List<GetDevices_Api_Result> json_temp = new ArrayList<GetDevices_Api_Result>() ;
					json_temp = content.getResult();
					Iterator<GetDevices_Api_Result> it = json_temp.iterator();
					GetDevices_Api_Result gar = new GetDevices_Api_Result();
					while (it.hasNext()) {
						gar=it.next();
						i++;
						//计数
					}
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
				editor.putString("light_count", ""+i);
				editor.commit();
			}
		});
    }


	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_light_btn_back:
			flag = false;
			//Thread.interrupted();
			this.finish();
			break;
		case R.id.main_light_btn_add:
			Intent intent = new Intent(Main_Light.this, Device_Pen.class);
			Main_Light.this.startActivity(intent);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		flag = false;
		this.finish();
		super.onBackPressed();
	}
}