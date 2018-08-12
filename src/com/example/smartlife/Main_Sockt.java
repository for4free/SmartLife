package com.example.smartlife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetData_Api;
import com.example.smartlife_api.GetData_Api_Result;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;

public class Main_Sockt extends Activity implements OnTouchListener {
	private PinnedHeaderExpandableListView_Sockt explistview;
//	private String[][] childrenData = new String[5][5];
//	private String[] groupData = new String[5];
	
	private List<String> groupList;
	private  Map<String,List<Map<String,String>>> childMap;
	private List<Map<String,String>> list01 = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> list02 = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> list03 = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> list04 = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> list05 = new ArrayList<Map<String,String>>();
	
	private int expandFlag = -1;// 控制列表的展开
	private PinnedHeaderExpandableAdapter_Sockt adapter;
	private ImageView back, add;
	private boolean flag = true;
	private String id;
	private TextView tv;
	private String net_flag = "0";
	private LoadingDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_sockt);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.main_sockt_titlebar);
		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		id = sharedPreferences.getString("id", "");
		
		//loading
		LoadingDialog.Builder dialogbulder = new LoadingDialog.Builder(this);
		dialog = dialogbulder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
		initView();
		initData();

		back = (ImageView) findViewById(R.id.main_sockt_btn_back);
		back.setOnTouchListener(this);
		add = (ImageView) findViewById(R.id.main_sockt_btn_add);
		add.setOnTouchListener(this);
		
		tv = (TextView) findViewById(R.id.main_sockt_temp_text);
		
	}

	/**
	 * 初始化VIEW
	 */
	private void initView() {
		explistview = (PinnedHeaderExpandableListView_Sockt) findViewById(R.id.explistview_sockt);
		explistview.setVisibility(View.INVISIBLE);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		/*for (int i = 0; i < 5; i++) {
			groupData[i] = "插座设备分组" + i;
		}

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				childrenData[i][j] = "插座设备" + i + "-" + j;
			}
		}*/
		//加载数据
		childMap = new HashMap<String, List<Map<String,String>>>();
		groupList = new ArrayList<String>();
		
		groupList = new ArrayList<String>();
		groupList.add("窗帘");
		groupList.add("插座");
		groupList.add("电视");
		groupList.add("空调");
		groupList.add("其他");
		
		new Thread(updateData).start();
		// 设置悬浮头部VIEW
		explistview.setHeaderView(getLayoutInflater().inflate(R.layout.main_sockt_grouphead, explistview, false));
		//adapter = new PinnedHeaderExpandableAdapter_Sockt(childrenData,groupData, getApplicationContext(), explistview);
		adapter = new PinnedHeaderExpandableAdapter_Sockt(childMap,groupList, this, explistview);
		explistview.setAdapter(adapter);

		// 设置单个分组展开
		explistview.setOnGroupClickListener(new GroupClickListener());
	}

	class GroupClickListener implements OnGroupClickListener {
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			if (expandFlag == -1) {
				// 展开被选的group
				explistview.expandGroup(groupPosition);
				// 设置被选中的group置于顶端
			    explistview.setSelectedGroup(groupPosition);
				expandFlag = groupPosition;
			} else if (expandFlag == groupPosition) {
				explistview.collapseGroup(expandFlag);
				expandFlag = -1;
			} else {
				explistview.collapseGroup(expandFlag);
				// 展开被选的group
				explistview.expandGroup(groupPosition);
				// 设置被选中的group置于顶端
				explistview.setSelectedGroup(groupPosition);
				expandFlag = groupPosition;
			}
			return true;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_sockt_btn_back:
			flag = false;
			this.finish();
			break;
		case R.id.main_sockt_btn_add:
			Intent intent = new Intent(Main_Sockt.this, Device_Pen.class);
			Main_Sockt.this.startActivity(intent);
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
				loadData(App.address+"CGetData.php?uid="+id);//获取全部设备
				try {
					Thread.sleep(1*1000);  //1秒刷新一次
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Thread.interrupted();
		}
	};
    public void loadData(final String path){
    	
    	HttpUtils http = new HttpUtils(1000);//9s超时
    	http.configCurrentHttpCacheExpiry(0);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    			if(net_flag.equals("0")){
    				explistview.setVisibility(View.INVISIBLE);
        			tv.setVisibility(View.VISIBLE);
        			dialog.dismiss();
    			}
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//Toast.makeText(getBaseContext(), "网络连接成功", Toast.LENGTH_LONG).show();
				//解析数据
				//加载数据  转化为JSON格式
				GetData_Api content = (GetData_Api) JSONObject.parseObject(arg0.result, GetData_Api.class);
				//计数
			    SharedPreferences sharedPreferences = getSharedPreferences("DATA", 0);
				Editor editor = sharedPreferences.edit();
			    int i = 0;
				if(content.getStatus().equals("403")){
					net_flag = "1";
					explistview.setVisibility(View.VISIBLE);
					//根据字段用get方法获取字段内容
					//Log.e("Main_Sockt",String.valueOf(content.getStatus()));
					List<GetData_Api_Result> json_temp = new ArrayList<GetData_Api_Result>() ;
					json_temp = content.getResult();
					//Log.e("Main_Sockt",String.valueOf(json_temp));
					Iterator<GetData_Api_Result> it = json_temp.iterator();
					GetData_Api_Result gar = new GetData_Api_Result();
					//Log.e("111", "333---"+res.toString());
			    	childMap.clear();
				    list01.clear();
				    list02.clear();
				    list03.clear();
				    list04.clear();
				    list05.clear();
				    explistview.setVisibility(View.VISIBLE);
				    tv.setVisibility(View.INVISIBLE);
				    
				    while(it.hasNext())
					{	
						gar = it.next();
			    		//Log.e("111", "0000--"+gar.getDname()+id);
			    		Map<String,String> map = new HashMap<String,String>();
			    		//Log.e("111", "3330000--"+gar.getDname()+id);
			    		map.put("name", gar.getDname());
			    		map.put("id", gar.getGetId());
			    		map.put("newdata", gar.getDdata());
			    		map.put("type", gar.getGetType());
			    		String type = gar.getGetType();
				    	switch(type) {
				    	/*"电器",//继电器--》a
						"窗帘",//电机--》6
						"热释电",//--》5
						"照明"//pwm--》9
				    	 */	    	
				    	case "6":  //窗帘
							list01.add(map);
							i++;//计数
				    		break;
				    	case "a1": //插座
				    		list02.add(map);
				    		i++;//计数
				    		break;
				    	case "a3": //电视
				    		list03.add(map);
				    		i++;//计数
				    		break;
				    	case "a2": //空调
				    		list04.add(map);
				    		i++;//计数
				    		break;
				    	case "a4": //其他
				    		list05.add(map);
				    		i++;//计数
				    		break;
				    	case "a5"://其他
				    		list05.add(map);
				    		i++;//计数
				    		break;
						default:
							break;
				    	}
			    	}
				   
					try {
				    	childMap.put("窗帘",list01);
				    	childMap.put("插座",list02);
				    	childMap.put("电视",list03);
				    	childMap.put("空调",list04);
				    	childMap.put("其他",list05);
					} catch (Exception e) {
						// TODO: handle exception
					}
					adapter.notifyDataSetChanged();
				} else{
					explistview.setVisibility(View.INVISIBLE);
					tv.setText("没有相应的设备");
					tv.setVisibility(View.VISIBLE);
				}
				dialog.dismiss();
				//计数
			    editor.putString("sockt_count", ""+i);
				editor.commit();

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