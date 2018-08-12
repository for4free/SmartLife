package com.example.smartlife;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Setting_Fml_Dialog_Create.Setting_Fml_Dialog_Create_DialogListener;
import com.example.smartlife.Setting_Fml_Dialog_Join.Setting_Fml_Dialog_Join_DialogListener;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.example.smartlife_api.GetFamilyNum;
import com.example.smartlife_api.GetFamilyNum_Res;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Setting_Fml_num extends Activity implements OnTouchListener{
	
	private ImageView back;
	private TextView txt_fml,txt;
	private Button btn,btn_add;
	
	private ListView list;
	private Setting_Fml_num_Adapter adapter;
	private List<GetFamilyNum_Res> totalList = new ArrayList<GetFamilyNum_Res>();
	private String id,Uid,Fid;
	private boolean flag = true;
	private LinearLayout title;
	private String net_flag = "0";
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private LoadingDialog dialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.left_setting_fml_num);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.left_fml_titlebar);
		
		//loading
		LoadingDialog.Builder dialogbulder = new LoadingDialog.Builder(this);
		dialog = dialogbulder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
		back = (ImageView) findViewById(R.id.left_fml_btn_back);
		txt_fml = (TextView) findViewById(R.id.left_fml_num_text_ttt);
		back.setOnTouchListener(this);
		
		title = (LinearLayout) findViewById(R.id.left_fml_num_text_lin);
		txt = (TextView) findViewById(R.id.left_fml_num_text);
		//加载listview
		list = (ListView) findViewById(R.id.left_fml_num_list);
        adapter = new Setting_Fml_num_Adapter(totalList, Setting_Fml_num.this);
        list.setAdapter(adapter);
        
        
        new Thread(updateData).start();
        SharedPreferences sharedPreferences_login = getSharedPreferences("LOGIN", 0);
        id = sharedPreferences_login.getString("id", "");
        Fid = sharedPreferences_login.getString("Fid", "");
        
        btn = (Button) findViewById(R.id.left_fml_num_btn);
        btn_add = (Button) findViewById(R.id.left_fml_num_btn_add);
        btn.setOnTouchListener(this);
        btn_add.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_fml_btn_back:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				flag = false;
				this.finish();
			}
			break;
		case R.id.left_fml_num_btn:
			if(arg1.getAction() == MotionEvent.ACTION_UP){
				flag = false;
				bKFml(App.address+"BKFml.php?tidORuid="+id);//退出家庭
				this.finish();
			}
			break;
		case R.id.left_fml_num_btn_add:
			if(arg1.getAction() == MotionEvent.ACTION_UP){
				//Toast.makeText(getBaseContext(), "添加成员",Toast.LENGTH_SHORT).show();
				//扫码添加成员
				Intent intent = new Intent();
				intent.setClass(Setting_Fml_num.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
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
				loadData(App.address+"CGetFamilyNum.php?uid="+id);
				try {
					Thread.sleep(1*5000);  //1.0秒刷新一次
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
    				txt.setText("网络连接失败");
        			txt.setVisibility(View.VISIBLE);
        			dialog.dismiss();
    			}
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				
				//Toast.makeText(getBaseContext(), "网络连接成功", Toast.LENGTH_LONG).show();
				//加载数据  转化为JSON格式
				GetFamilyNum content = (GetFamilyNum) JSONObject.parseObject(arg0.result, GetFamilyNum.class);
				//根据字段用get方法获取字段内容
				List<GetFamilyNum_Res> items = content.getResult();
				SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
				Editor editor = sharedPreferences.edit();
				if(content.getStatus().equals("403")){
					net_flag = "1";
					txt.setVisibility(View.INVISIBLE);
					List<GetFamilyNum_Res> json_temp = new ArrayList<GetFamilyNum_Res>() ;
					json_temp = content.getResult();
					Iterator<GetFamilyNum_Res> it = json_temp.iterator();
					GetFamilyNum_Res gar = new GetFamilyNum_Res();
					while (it.hasNext()) {
						gar=it.next();
					}		
					editor.putString("fmlCr", content.getCreatorId());
					editor.putString("Fid", content.getFid());
					editor.commit();
					
					title.setVisibility(View.VISIBLE);
					if(!content.getCreatorId().equals(id)){
						btn.setVisibility(View.VISIBLE);
						btn_add.setVisibility(View.INVISIBLE);
					}else{
						btn.setVisibility(View.INVISIBLE);
						btn_add.setVisibility(View.VISIBLE);
					}
					txt_fml.setVisibility(View.VISIBLE);
					txt_fml.setText("家庭ID:"+content.getFid()+" 家庭名称:"+content.getFname());
					try {
						totalList.clear();
						totalList.addAll(items);
					} catch (Exception e) {
						// TODO: handle exception
					}
					adapter.notifyDataSetChanged();
				}else{
					txt.setText("管理员审核还未通过");
	    			txt.setVisibility(View.VISIBLE);
				}
				dialog.dismiss();
				//editor.putString("light_count", ""+i);
				//editor.commit();
			}
		});
    }
    
    public void bKFml(String path){
    	
    	HttpUtils http = new HttpUtils(1*1000);//1s超时
    	http.configCurrentHttpCacheExpiry(100);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    			//txt.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//txt.setVisibility(View.INVISIBLE);
				//Toast.makeText(getBaseContext(), "网络连接成功", Toast.LENGTH_LONG).show();
				//加载数据  转化为JSON格式
				GetFamilyNum content = (GetFamilyNum) JSONObject.parseObject(arg0.result, GetFamilyNum.class);
				//根据字段用get方法获取字段内容
				if(content.getStatus().equals("403")){
					SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
					Editor editor = sharedPreferences.edit();
					editor.putString("fid", "0");
					editor.commit();
				}
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
	
	//扫描返回的结果处理
		@Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        switch (requestCode) {
			case SCANNIN_GREQUEST_CODE:
				if(resultCode == RESULT_OK){
					Bundle bundle = data.getExtras();
					if(bundle.getString("flag").equals("2")){
						//显示扫描到的内容
						//id.setText(bundle.getString("Type")+"-"+bundle.getString("Did"));
						//Toast.makeText(getBaseContext(), ""+bundle.getString("Uid"), Toast.LENGTH_SHORT).show();
						Uid = bundle.getString("Uid").toString();
						new Thread(addFmlNum).start();
					}
				}
				break;
			}
	    }	
		//添加成员
		Runnable addFmlNum = new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				addFmlNum(App.address+"AddFmlNum.php?fid="+Fid+"&uid="+Uid);	
			}
		};
		//添加成员
		 public void addFmlNum(String path){
		    	
		    	HttpUtils http = new HttpUtils(1*1000);//1s超时
		    	http.configCurrentHttpCacheExpiry(100);
				http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

		    		@Override
					public void onFailure(HttpException arg0, String arg1) {
		    			//txt.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						//txt.setVisibility(View.INVISIBLE);
						//Toast.makeText(getBaseContext(), "网络连接成功", Toast.LENGTH_LONG).show();
						//加载数据  转化为JSON格式
						GetFamilyNum content = (GetFamilyNum) JSONObject.parseObject(arg0.result, GetFamilyNum.class);
						//根据字段用get方法获取字段内容
						Message message = new Message();
						if(content.getStatus().equals("403")){
							message.what = 1;
							 	
						}else{
							message.what = 0;
						}
						 updateUI.sendMessage(message);
					}
				});
		    }
		 Handler updateUI = new Handler(){
			 public void handleMessage(Message msg) {
					switch (msg.what) {
					case 1:
						Toast.makeText(getBaseContext(), "添加成功", Toast.LENGTH_SHORT).show();
						break;
					case 0:
						Toast.makeText(getBaseContext(), "添加失败", Toast.LENGTH_SHORT).show();
						break;
					}
					}
		 };
}
