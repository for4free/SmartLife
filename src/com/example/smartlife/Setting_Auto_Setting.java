package com.example.smartlife;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Setting_Dialog_Add.Setting_Dialog_AddListener;
import com.example.smartlife_api.GetModelAPI;
import com.example.smartlife_api.GetModelResultAPI;
import com.example.smartlife_api.GetModelResultDataAPI;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting_Auto_Setting extends Activity implements OnClickListener {

	private ImageView back,add;
	
	private TextView txt;
	private ListView list;
	private Setting_Auto_Adapter2 adapter;
	private List<GetModelResultDataAPI> totalList = new ArrayList<GetModelResultDataAPI>();
	private boolean flag = true;
	private String net_flag = "0",Uid,Mid;
	private int sort;
	private LoadingDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.left_auto_setting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.left_auto_setting_titlebar);
		
		//新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        Mid = bundle.getString("Mid");
        sort = bundle.getInt("sort");
		//loading
		LoadingDialog.Builder dialogbulder = new LoadingDialog.Builder(this);
		dialog = dialogbulder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
		txt = (TextView) findViewById(R.id.left_auto_text);
		
		//加载listview
		list = (ListView) findViewById(R.id.left_auto_list2);
		adapter = new Setting_Auto_Adapter2(totalList, Setting_Auto_Setting.this,Mid);
		list.setAdapter(adapter);
				      
		new Thread(updateData).start();
		SharedPreferences sharedPreferences_login = getSharedPreferences("LOGIN", 0);
		Uid = sharedPreferences_login.getString("id", "");

		back = (ImageView) findViewById(R.id.left_auto_setting_back);
		add = (ImageView) findViewById(R.id.left_auto_setting_btn_add);
		back.setOnClickListener(this);
		add.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_auto_setting_back:
			this.finish();
			break;
		case R.id.left_auto_setting_btn_add:
			
			Setting_Dialog_Add dialog_ADD = new Setting_Dialog_Add(Uid,Mid,this, R.style.Dialog, new Setting_Dialog_AddListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					
				}
			});
			dialog_ADD.show();
			
			
			break;
		default:
			break;
		}
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
				//txt.setVisibility(View.INVISIBLE);
				//Toast.makeText(getBaseContext(), "网络连接成功", Toast.LENGTH_LONG).show();
				//加载数据  转化为JSON格式
				GetModelAPI content = (GetModelAPI) JSONObject.parseObject(arg0.result, GetModelAPI.class);
				//根据字段用get方法获取字段内容
				
				
				//GetModelResultAPI items_t = (GetModelResultAPI) content.getData();
				List<GetModelResultAPI> items_t = content.getData();
				List<GetModelResultDataAPI> items = items_t.get(sort).getMdata();
			//	Log.e("TAG", sort+"+"+Mid+"+"+items_t.get(sort).getMid()+"+"+items_t.get(sort).getModelTotal());
				if((content.getStatus().equals("403")&&(items_t.get(sort).getMid().equals(Mid))&&(!items_t.get(sort).getModelTotal().equals("0")))){

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
    
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		flag = false;
		this.finish();
		super.onBackPressed();
	}

}
