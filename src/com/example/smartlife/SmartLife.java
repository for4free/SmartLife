package com.example.smartlife;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.example.smartlife_api.CaiYun_Api_Items;
import com.example.smartlife_api.Caiyun_Api;
import com.example.smartlife_api.GetData_Api;
import com.example.smartlife_api.GetData_Api_Result;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.example.smartlife_api.GetModelAPI;
import com.example.smartlife_api.GetModelResultAPI;
import com.example.smartlife_ease.VideoCallActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class SmartLife extends Activity implements OnClickListener,OnTouchListener {

	static Activity smartlife;
	private ImageView img_title_btn_left, img_title_btn_right;
	private TextView text_ok, txt_temp,txt_pm25,txt_pm25_2,text_0102,text_0201;
	private ImageView img_air;
	private int mBackPressedTimes = 0;
	private LinearLayout linear_11, linear_12, linear_21, linear_22;
	private PopupWindow popupwindow;
	private RelativeLayout air;
	private boolean flag = true;
	private String id;
	private TextView war_txt,txt;
	private String toUser;//环信对方id
	private ListView list;
	private MainMenuAdapter adapter;
	private List<GetModelResultAPI> totalList = new ArrayList<GetModelResultAPI>();
	private String net_flag = "0";
	private String doorbell_flag = "0";
	private String PIR_flag = "0";
	//获取位置
	protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    private Double long_t=102.0,lat_t=22.0;  //默认值
    private String loc_flag = ""+1;
    private LoadingDialog dialog = null;
    private String Load_flag = "0";
	private ProgressBar temp, hum, light,pm25;
	private TextView txt_templ, txt_hum, txt_light;
	private String PIRID;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 自定义TitleBar
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_smart_life);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.main_titlebar);
		
		smartlife = this;
		//网络是否连接/是否加入家庭的提醒文字
		war_txt = (TextView) findViewById(R.id.main_status);
		war_txt.setOnClickListener(this);
		//传感器
		temp = (ProgressBar) findViewById(R.id.main_air_btm_temp);
		hum = (ProgressBar) findViewById(R.id.main_air_btm_hum);
		light = (ProgressBar) findViewById(R.id.main_air_btm_light);
		pm25 = (ProgressBar) findViewById(R.id.main_air_btm_pm25);
		
		txt_templ = (TextView) findViewById(R.id.main_air_btm_temp_txt);
		txt_light = (TextView) findViewById(R.id.main_air_btm_light_txt);
		txt_hum = (TextView) findViewById(R.id.main_air_btm_hum_txt);
		//
		// titlebar上的两个按钮
		img_title_btn_left = (ImageView) findViewById(R.id.header_left_btn);
		img_title_btn_right = (ImageView) findViewById(R.id.header_right_btn);
		text_ok = (TextView) findViewById(R.id.text_btn_main_01);
		text_ok.setOnClickListener(this);
		// 四个选项块
		linear_11 = (LinearLayout) findViewById(R.id.linear_main_switch_0101);
		linear_12 = (LinearLayout) findViewById(R.id.linear_main_switch_0102);
		linear_21 = (LinearLayout) findViewById(R.id.linear_main_switch_0201);
		linear_22 = (LinearLayout) findViewById(R.id.linear_main_switch_0202);
		text_0102 = (TextView) findViewById(R.id.txt_main_0102);
		text_0201 = (TextView) findViewById(R.id.txt_main_0201);
		linear_11.setOnTouchListener(this);
		linear_12.setOnTouchListener(this);
		linear_21.setOnTouchListener(this);
		linear_22.setOnTouchListener(this);
		//更新计数 
		SharedPreferences sharedPreferences_newdata = getSharedPreferences("DATA", 0);
		text_0102.setText(sharedPreferences_newdata.getString("light_count", "0")+" 控制端");
		text_0201.setText(sharedPreferences_newdata.getString("sockt_count", "0")+" 控制端");
		// 按钮监听
		img_title_btn_left.setOnClickListener(this);
		img_title_btn_right.setOnClickListener(this);
		// 环境监测
		air = (RelativeLayout) findViewById(R.id.main_air);
		air.setOnClickListener(this);
		// 实时天气
		txt_pm25_2 = (TextView) findViewById(R.id.main_air_btm_pm25_txt);
		txt_pm25 = (TextView) findViewById(R.id.text_main_pm25);
		txt_temp = (TextView) findViewById(R.id.text_main_air);
		img_air = (ImageView) findViewById(R.id.img_main_air);
		//初始化天气
		SharedPreferences sharedPreferences = getSharedPreferences("CLOUD", 0);
		String flag = sharedPreferences.getString("flag", "");
		  if(flag.equals("1")){
			  float temp = sharedPreferences.getFloat("temp",0);
			  float pm25 = sharedPreferences.getFloat("pm25",0);
			  String skycon = sharedPreferences.getString("skycon", "");
			  String t="";
			  if(skycon.equals("CLEAR_DAY")){
				  t="晴";
				  img_air.setImageResource(R.drawable.qingtian);
			  }
			  if(skycon.equals("CLEAR_NIGHT")){
				  t="晴";
				  img_air.setImageResource(R.drawable.qingye);
			  }
			  if(skycon.equals("PARTLY_CLOUDY_DAY")){
				  t="多云";
				  img_air.setImageResource(R.drawable.duoyunbaitian);
			  }
			  if(skycon.equals("PARTLY_CLOUDY_NIGHT")){
				  t="多云";
				  img_air.setImageResource(R.drawable.duoyunye);
			  }
			  if(skycon.equals("CLOUDY")){
				  t="阴";
				  img_air.setImageResource(R.drawable.yin);
			  }
			  if(skycon.equals("RAIN")){
				  t="雨";
				  img_air.setImageResource(R.drawable.yu);
			  }
			  if(skycon.equals("SLEET")){
				  t="冻雨";
				  img_air.setImageResource(R.drawable.dongyu);
			  }
			  if(skycon.equals("SNOW")){
				  t="雪";
				  img_air.setImageResource(R.drawable.xue);
			  }
			  if(skycon.equals("WIND")){
				  t="大风";
				  img_air.setImageResource(R.drawable.dafeng);
			  }
			  if(skycon.equals("FOG")){
				  t="雾";
				  img_air.setImageResource(R.drawable.wu);
			  }
			  if(skycon.equals("HAZE")){
				  t="霾";
				  img_air.setImageResource(R.drawable.mai);
			  }
			  txt_temp.setText(t+"  "+(int)temp+"℃");
			  this.pm25.setProgress((int)pm25);
			  txt_pm25.setText(""+(int)pm25+"μg/m³");
			  txt_pm25_2.setText(""+(int)pm25+"μg/m³");
		  }

		login();
	 //
	    LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
       
            LocationListener locationListener = new LocationListener() {  
                  
                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数  
                @Override  
                public void onStatusChanged(String provider, int status, Bundle extras) {  
                	Log.e("Map", "onStatusChanged");
                }  
                  
                // Provider被enable时触发此函数，比如GPS被打开  
                @Override  
                public void onProviderEnabled(String provider) {  
                	Log.e("Map", "onProviderEnabled"); 
                }  
                  
                // Provider被disable时触发此函数，比如GPS被关闭   
                
                @Override  
                public void onProviderDisabled(String provider) {  
                	 Log.e("Map", "onProviderDisabled"); 
                	 loc_flag = ""+1;
                }  
                  
                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发   
                @Override  
                public void onLocationChanged(Location location) {  
                    if (location != null) {     
                        Log.e("Map", "Location changed : Lat: "+ location.getLatitude() + " Lng: " + location.getLongitude());     
                    }  
                }  
            };  
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);     
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);     
            if(location != null){   
            	loc_flag = ""+2;
                lat_t = location.getLatitude(); //经度     
                long_t = location.getLongitude(); //纬度  
            }   
            new Thread(initMode).start();//循环加载模式
    		new Thread(initView).start();//循环检查网络3s
    		new Thread(initAir_60s).start(); //更新天气情况60s
    		new Thread(initData_31s).start(); //更新传感器数据31s
    		new Thread(getDoorbell).start();//监测门铃事件
	}

	// 重写BACK键
	@Override
	public void onBackPressed() {
		
		if (mBackPressedTimes == 0) {
			
			if(popupwindow != null && popupwindow.isShowing()){
				popupwindow.dismiss();
			}else{
				Toast.makeText(getBaseContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mBackPressedTimes = 1;
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						} finally {
							mBackPressedTimes = 0;
						}
					};
				}.start();
			}
			return;
		} else {
			flag = false;
			this.finish();
		}
		super.onBackPressed();
	}

	// 监听点击事件处理
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = getSharedPreferences("MODEL", 0);
		Editor editor = sharedPreferences.edit();
		switch (arg0.getId()) {
		//提醒文字
		case R.id.main_status:
			SharedPreferences sharedPreferences_login_t = getSharedPreferences("LOGIN", 0);
			String conn_t = sharedPreferences_login_t.getString("conn", "");
			String fid_t = sharedPreferences_login_t.getString("fid", "");
			Intent intent_t = null;
			if(conn_t.equals("0")){
				if(android.os.Build.VERSION.SDK_INT > 10 ){
				    //3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
					intent_t =  new Intent(android.provider.Settings.ACTION_SETTINGS);
				} else {
					intent_t = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				}
				//intent_t = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			}else if(fid_t.equals("0")){
				intent_t = new Intent(SmartLife.this, Setting_Fml.class);
			}
			this.startActivity(intent_t);
			break;
		// titlebar
		case R.id.header_left_btn:
			Intent intent_left = new Intent(SmartLife.this, Setting.class);
			SmartLife.this.startActivity(intent_left);
			break;
		case R.id.header_right_btn:
			Intent intent_right = new Intent(SmartLife.this, Device.class);
			SmartLife.this.startActivity(intent_right);
			break;
		// 模式
		case R.id.text_btn_main_01:
			if (popupwindow != null && popupwindow.isShowing()) {
				popupwindow.dismiss();
				return;
			} else {
				int[] location = new int[2];
				initmPopupWindowView();
				arg0.getLocationOnScreen(location);
				popupwindow.showAtLocation(arg0, Gravity.BOTTOM,0, 0);
			}
			break;
		// 环境监测
		/*case R.id.main_air:
			Intent intent_air = new Intent(SmartLife.this, Main_Air.class);
			SmartLife.this.startActivity(intent_air);
			break;*/
		default:
			break;
		}
		editor.commit();
	}

	// 触摸监听
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.linear_main_switch_0101:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				linear_11.setBackgroundResource(R.drawable.main_background_touch);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				linear_11.setBackgroundResource(R.drawable.main_background);
				//Intent intent_doorbell = new Intent(SmartLife.this,Main_DoorBell.class);
				//SmartLife.this.startActivity(intent_doorbell);
				doorBellVideo();
			}
			break;
		case R.id.linear_main_switch_0102:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				linear_12.setBackgroundResource(R.drawable.main_background_touch);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				linear_12.setBackgroundResource(R.drawable.main_background);
				Intent intent_light = new Intent(SmartLife.this,Main_Light.class);
				SmartLife.this.startActivity(intent_light);
			}
			break;
		case R.id.linear_main_switch_0201:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				linear_21.setBackgroundResource(R.drawable.main_background_touch);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				linear_21.setBackgroundResource(R.drawable.main_background);
				Intent intent_sockt = new Intent(SmartLife.this,Main_Sockt.class);
				SmartLife.this.startActivity(intent_sockt);
			}
			break;
		case R.id.linear_main_switch_0202:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				linear_22.setBackgroundResource(R.drawable.main_background_touch);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				linear_22.setBackgroundResource(R.drawable.main_background);
				Intent intent_moni = new Intent(SmartLife.this, Main_Moni.class);
				SmartLife.this.startActivity(intent_moni);
			}
			break;
		default:
			break;
		}
		return false;
	}

	public void initmPopupWindowView() {
		// 获取自定义文件视图
		View customView = getLayoutInflater().inflate(R.layout.main_menu, null,false);
		// 创建实例
		popupwindow = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		popupwindow.setAnimationStyle(R.style.AnimationFade);
		popupwindow.setOutsideTouchable(true);
		txt = (TextView) customView.findViewById(R.id.main_menu_text_temp);
		//加载listview
		list = (ListView)customView.findViewById(R.id.main_menu_list);
		adapter = new MainMenuAdapter(totalList, this);
		list.setAdapter(adapter);
		loadDataNew(App.address+"GetModel.php?uid=" + id);//加载模式
		
		//loading
		if(Load_flag.equals("0")){
			LoadingDialog.Builder dialogbulder = new LoadingDialog.Builder(this);
			dialog = dialogbulder.create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
		
		
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					popupwindow = null;
				}
				return false;
			}
		});
		list.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					popupwindow = null;
				}
				return false;
			}
		});
	}

	// 循环请求天气数据
	Runnable initAir_60s = new Runnable() {
		
		@Override
		public void run() {
			SharedPreferences sharedPreferences_air = getSharedPreferences("LOGIN", 0);
			final String lon = sharedPreferences_air.getString("lon", "120.1325");
			final String lat = sharedPreferences_air.getString("lat", "36.0103");
			// 更新天气数据
			String path ="http://api.caiyunapp.com/v2/kQq6LBc-ueXw2M0d/"+lon+","+lat+"/realtime.json";
			//山科大的经纬度 36.0103,120.1325   彩云天气token：kQq6LBc-ueXw2M0d 
			 HttpUtils http = new HttpUtils(10000);//10s超时
			 http.configCurrentHttpCacheExpiry(5000); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。
			while (flag) {
				//Toast.makeText(getBaseContext(), ""+lon+"---"+lat, Toast.LENGTH_LONG).show();
				 http.send(HttpMethod.GET, path,new RequestCallBack<String>() {
					 SharedPreferences sharedPreferences = getSharedPreferences("CLOUD", 0);
				  @Override 
				  public void onFailure(HttpException arg0, String arg1) { 
				  //联网失败
					 // Toast.makeText(getBaseContext(), ""+lon+"---"+lat, Toast.LENGTH_LONG).show();
				 }	  
				  @Override 
				  public void onSuccess(ResponseInfo<String> arg0) { 
					  //联网成功
					  Caiyun_Api content = (Caiyun_Api)JSONObject.parseObject(arg0.result, Caiyun_Api.class);
					  content = JSONObject.parseObject(arg0.result, Caiyun_Api.class);
					  CaiYun_Api_Items items = content.getResult();
					  if(content.getStatus().equals("ok")){
						  //Toast.makeText(getBaseContext(), "xxx"+items.getPm25(), Toast.LENGTH_SHORT).show();
						  String t="";
						  if(items.getSkycon().equals("CLEAR_DAY")){
							  t="晴";
							  img_air.setImageResource(R.drawable.qingtian);
						  }
						  if(items.getSkycon().equals("CLEAR_NIGHT")){
							  t="晴";
							  img_air.setImageResource(R.drawable.qingye);
						  }
						  if(items.getSkycon().equals("PARTLY_CLOUDY_DAY")){
							  t="多云";
							  img_air.setImageResource(R.drawable.duoyunbaitian);
						  }
						  if(items.getSkycon().equals("PARTLY_CLOUDY_NIGHT")){
							  t="多云";
							  img_air.setImageResource(R.drawable.duoyunye);
						  }
						  if(items.getSkycon().equals("CLOUDY")){
							  t="阴";
							  img_air.setImageResource(R.drawable.yin);
						  }
						  if(items.getSkycon().equals("RAIN")){
							  t="雨";
							  img_air.setImageResource(R.drawable.yu);
						  }
						  if(items.getSkycon().equals("SLEET")){
							  t="冻雨";
							  img_air.setImageResource(R.drawable.dongyu);
						  }
						  if(items.getSkycon().equals("SNOW")){
							  t="雪";
							  img_air.setImageResource(R.drawable.xue);
						  }
						  if(items.getSkycon().equals("WIND")){
							  t="大风";
							  img_air.setImageResource(R.drawable.dafeng);
						  }
						  if(items.getSkycon().equals("FOG")){
							  t="雾";
							  img_air.setImageResource(R.drawable.wu);
						  }
						  if(items.getSkycon().equals("HAZE")){
							  t="霾";
							  img_air.setImageResource(R.drawable.mai);
						  }
						 // txt_temp.setText(t+"  "+(int)items.getTemperature()+"℃");
						 // txt_pm25.setText(""+(int)items.getPm25()+"μg/m³");
						  Editor editor = sharedPreferences.edit();
						  editor.putFloat("temp", items.getTemperature());
						  editor.putFloat("pm25", items.getPm25());
						  editor.putString("flag", "1");
						  editor.putString("skycon", items.getSkycon());
						  editor.commit();
						  Message message = new Message();
						  message.what = 1;
						  updateUI.sendMessage(message);
						
					  }
				  }
				});
				 try {
					Thread.sleep(60*1000);
					Log.e("cloud_30s", "status:ok");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Thread.interrupted();
		}
	};
	
	Runnable initMode = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (flag) {
				loadData(App.address+"GetModel.php?uid=" + id);//循环加载模式
				try {
					Thread.sleep(1*1000); //暂定延时1s
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Thread.interrupted();
		}
	};
	
	//循环检查网络、更新UI5s
	Runnable initView = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
			id = sharedPreferences.getString("id", "");
			String path = null;
			if(loc_flag.equals("1")){
				 path = "http://2naive.cn/";
			}
			if(loc_flag.equals("2")){
				path = App.address + "CUpdateLoc.php?id="+id+"&setLong="+long_t+"&setLat="+lat_t;
			}
			final Editor editor = sharedPreferences.edit();
			HttpUtils http = new HttpUtils(3000);//10s超时
			http.configCurrentHttpCacheExpiry(300); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。
			while (flag) {
				 http.send(HttpMethod.GET, path,new RequestCallBack<String>() {
				  @Override 
				  public void onFailure(HttpException arg0, String arg1) { 
				  //联网失败
					 // Toast.makeText(getBaseContext(), ""+lon+"---"+lat, Toast.LENGTH_LONG).show();
					 editor.putString("conn", "0");
				 }	  
				  @Override 
				  public void onSuccess(ResponseInfo<String> arg0) { 
					  //联网成功
					  editor.putString("conn", "1");
				  }
				});
				editor.commit();
				Message message = new Message();
				message.what = 2;
				updateUI.sendMessage(message);
				
				try {
					Thread.sleep(3*1000); //暂定延时3s
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Thread.interrupted();
		}
	};
	
	//天气
	Runnable initData_31s = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//获取用户id 
			SharedPreferences sharedPreferences_Login = getSharedPreferences("LOGIN", 0);
			final SharedPreferences sharedPreferences_data = getSharedPreferences("DATA", 0);
			id = sharedPreferences_Login.getString("id", "");
			//暂且设定为温湿度传感器
			String path = App.address + "CGetData.php?uid="+id+"&getType=2&getId=1";//getId已经过时了
			HttpUtils http = new HttpUtils(10000);//10s超时
			http.configCurrentHttpCacheExpiry(5000); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。
			while (flag) {
				http.send(HttpMethod.GET, path, new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						// 加载数据 转化为JSON格式
						GetData_Api content = (GetData_Api) JSONObject.parseObject(arg0.result, GetData_Api.class);
						if (content.getStatus().equals("402")|| content.getStatus().equals("401")) {
							//出错
						} else if (content.getStatus().equals("403")) {
							//解析数据
							List<GetData_Api_Result> json_temp = new ArrayList<GetData_Api_Result>() ;
							json_temp = content.getResult();
							Iterator<GetData_Api_Result> it = json_temp.iterator();
							GetData_Api_Result gar = new GetData_Api_Result();
							while(it.hasNext())
							{	
								gar = it.next();
								if((gar.getGetId().toString().equals("1"))&&(gar.getGetType().toString().equals("2"))){
									String data_temp= gar.getDdata().toString();		
									String[] dataSplit = data_temp.split("_");
									Editor editor_data = sharedPreferences_data.edit();
									editor_data.putString("temp", dataSplit[0]);
									editor_data.putString("hum", dataSplit[1]);
									editor_data.putString("light", dataSplit[2]);
									editor_data.commit();
								}
							}
						} else {
							//其他错误
						}	
					}

				});
				try {
					Thread.sleep(31*1000); //暂定延时31s
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Thread.interrupted();
		}
	};

	// 主线程更新UI
	Handler updateUI = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				//更新天气
				SharedPreferences sharedPreferences = getSharedPreferences("CLOUD", 0);
				String flag = sharedPreferences.getString("flag", "");
				  if(flag.equals("1")){
					  float temp = sharedPreferences.getFloat("temp",0);
					  float pm25 = sharedPreferences.getFloat("pm25",0);
					  String skycon = sharedPreferences.getString("skycon", "");
					  String t="";
					  if(skycon.equals("CLEAR_DAY")){
						  t="晴";
						  img_air.setImageResource(R.drawable.qingtian);
					  }
					  if(skycon.equals("CLEAR_NIGHT")){
						  t="晴";
						  img_air.setImageResource(R.drawable.qingye);
					  }
					  if(skycon.equals("PARTLY_CLOUDY_DAY")){
						  t="多云";
						  img_air.setImageResource(R.drawable.duoyunbaitian);
					  }
					  if(skycon.equals("PARTLY_CLOUDY_NIGHT")){
						  t="多云";
						  img_air.setImageResource(R.drawable.duoyunye);
					  }
					  if(skycon.equals("CLOUDY")){
						  t="阴";
						  img_air.setImageResource(R.drawable.yin);
					  }
					  if(skycon.equals("RAIN")){
						  t="雨";
						  img_air.setImageResource(R.drawable.yu);
					  }
					  if(skycon.equals("SLEET")){
						  t="冻雨";
						  img_air.setImageResource(R.drawable.dongyu);
					  }
					  if(skycon.equals("SNOW")){
						  t="雪";
						  img_air.setImageResource(R.drawable.xue);
					  }
					  if(skycon.equals("WIND")){
						  t="大风";
						  img_air.setImageResource(R.drawable.dafeng);
					  }
					  if(skycon.equals("FOG")){
						  t="雾";
						  img_air.setImageResource(R.drawable.wu);
					  }
					  if(skycon.equals("HAZE")){
						  t="霾";
						  img_air.setImageResource(R.drawable.mai);
					  }
					  txt_temp.setText(t+"  "+(int)temp+"℃");
					  txt_pm25.setText(""+(int)pm25+"μg/m³");
					  txt_pm25_2.setText(""+(int)pm25+"μg/m³");
				  }
				break;
			case 2:
				//更新计数 //非该传感器
				SharedPreferences sharedPreferences_newdata = getSharedPreferences("DATA", 0);
				text_0102.setText(sharedPreferences_newdata.getString("light_count", "0")+" 控制端");
				text_0201.setText(sharedPreferences_newdata.getString("sockt_count", "0")+" 控制端");
				SharedPreferences sharedPreferences_login = getSharedPreferences("LOGIN", 0);
				SharedPreferences sharedPreferences_cloud = getSharedPreferences("CLOUD", 0);
				String conn = sharedPreferences_login.getString("conn", "");
				String fid = sharedPreferences_login.getString("fid", "");
				Editor editor = sharedPreferences_login.edit();
				if(conn.equals("0")){
					war_txt.setText("没有连接网络");
					war_txt.setVisibility(View.VISIBLE);
					editor.putString("easeconn", "0");
					editor.commit();
					//登陆环信服务器
				}else if(fid.equals("0")){
					war_txt.setText("没有加入家庭");
					war_txt.setVisibility(View.VISIBLE);
				}else{
					war_txt.setVisibility(View.INVISIBLE);
					//重新登陆环信
					if(sharedPreferences_login.getString("easeconn", "").equals("0")){
						login();	
					}			
					//传感器数据
					//初始化界面			
					try {
		
						int int_temp = Integer.valueOf(sharedPreferences_newdata.getString("temp", "")).intValue();
						int int_hum = Integer.valueOf(sharedPreferences_newdata.getString("hum", "")).intValue();
						int int_light = Integer.valueOf(sharedPreferences_newdata.getString("light", "")).intValue();
						//float int_pm25 = sharedPreferences_login.getFloat("pm25", 0);
						temp.setProgress(int_temp);
						txt_templ.setText("" + int_temp + " ℃");

						hum.setProgress(int_hum);
						txt_hum.setText("" + int_hum + " %");

						light.setProgress(int_light);
						txt_light.setText("" + int_light + " LUX");
						
						

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				
				break;
			case 3:			
				//模式初始化
				SharedPreferences sharedPreferences_login2 = getSharedPreferences("LOGIN", 0);
				String fid2 = sharedPreferences_login2.getString("fid", "");
				if(!fid2.equals("0")){
					SharedPreferences sharedPreferences_model = getSharedPreferences("MODEL", 0);
					String model = sharedPreferences_model.getString("model", "");
					text_ok.setText(model);
				}
				break;
			case 4://门铃事件
				if(doorbell_flag.equals("0")){
					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
			        Notification notification = new Notification(R.drawable.ic_launcher,"通知:门前有人",System.currentTimeMillis());  	          
			        notification.flags = Notification.FLAG_AUTO_CANCEL;           
			       // Intent notificationIntent = new Intent();  //doorBellVideo();
			        notification.defaults = Notification.DEFAULT_SOUND;//使用系统默认声音
			        Intent notificationIntent = new Intent(getBaseContext(), VideoCallActivity.class);
			        notificationIntent.putExtra("username", toUser);
			        notificationIntent.putExtra("isComingCall", false);
		           //startActivity(intent);        
			        PendingIntent pedingIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);  
			        notification.setLatestEventInfo(getBaseContext(), "通知:门前有人", "点击进行视频对讲", pedingIntent);           
			        notificationManager.notify(R.drawable.ic_launcher,notification);  
			        doorbell_flag = "1";
			        new Thread(updateDataDoorbell).start();
				}
				break;
			case 5://门铃事件
				if(PIR_flag.equals("0")){
					PIR_flag = "1";
					Bundle b = msg.getData();
					String PIRID = b.getString("PIRID");
					String PIRNAME = b.getString("PIRNAME");
				
					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
					Notification notification = new Notification(R.drawable.ic_launcher,"通知:安防警告",System.currentTimeMillis());  	          
					notification.flags = Notification.FLAG_AUTO_CANCEL;           
					notification.defaults = Notification.DEFAULT_SOUND;//使用系统默认声音
					Intent notificationIntent = new Intent(getBaseContext(), Main_Moni.class);
					//startActivity(notificationIntent);        
					PendingIntent pedingIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);  
					notification.setLatestEventInfo(getBaseContext(), "通知:"+PIRNAME, "点击查看视频", pedingIntent);           
					notificationManager.notify(R.drawable.ic_launcher,notification);  
					new Thread(updateDataDoorbell).start();
					updateData(App.address+"CSetData.php?uid="+id+"&getType=5&getId="+PIRID+"&newData=1_0&flag=0");		
				}
			    break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	  //登陆环信服务器
    private void login() {
    	SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
        final String u=sharedPreferences.getString("id", "");
        final String p=sharedPreferences.getString("easepass", "");
        if (TextUtils.isEmpty(u)||TextUtils.isEmpty(p)){
            //Toast.makeText(getBaseContext(),"账号或密码不能为空！",Toast.LENGTH_LONG).show();
        	Log.e("111", "环信-账号或密码不能为空！");
            return ;
        }
        //这里先进行自己服务器的登录操作
        //自己服务器登录成功后再执行环信服务器的登录操作
        EMChatManager.getInstance().login(u, p, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                       // Toast.makeText(getBaseContext(), "登陆监控服务器成功", Toast.LENGTH_SHORT).show();
                        Log.e("111", "登陆监控服务器成功！");
                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("easeconn", "1");
                        editor.commit();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.e("111", "登陆聊天服务器中 " + "progress:" + progress + " status:" + status);
            }

            @Override
            public void onError(int code, String message) {
                Log.e("111", "登陆聊天服务器失败！"+code+"=="+message+"用户名"+u+"密码："+p);
                //login();
                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
                Editor editor = sharedPreferences.edit();
                editor.putString("easeconn", "0");
                editor.commit();
            }
        });
    }

    //门前监控
    private void doorBellVideo() {
    	new Thread(updateDataDoorbell).start();
        if (!EMChatManager.getInstance().isConnected()) {
            Toast.makeText(getBaseContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
        }
        else {
        	//链接
			getEaseId("b1");
        }
    }
    //只适用于type=b1
    public void getEaseId(String type){
    	
    	HttpUtils http = new HttpUtils(1000);//9s超时
    	http.configCurrentHttpCacheExpiry(0);
    	String path = App.address+"CGetData.php?uid="+id+"&getType="+type;
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    			
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//解析数据
				//加载数据  转化为JSON格式
				GetData_Api content = (GetData_Api) JSONObject.parseObject(arg0.result, GetData_Api.class);
				if(content.getStatus().equals("403")){
					List<GetData_Api_Result> json_temp = new ArrayList<GetData_Api_Result>() ;
					json_temp = content.getResult();
					Iterator<GetData_Api_Result> it = json_temp.iterator();
					GetData_Api_Result gar = new GetData_Api_Result();
					while (it.hasNext()) {
						gar=it.next();
						toUser=gar.getGetId().toString();
						//Log.e("111", ""+toUser);
					}
					//打开监控
					if (TextUtils.isEmpty(toUser)){
		                Toast.makeText(getBaseContext(), "未发现监控设备", Toast.LENGTH_SHORT).show();
		                return ;
		            }
		            Intent intent = new Intent(getBaseContext(), VideoCallActivity.class);
		            intent.putExtra("username", toUser);
		            intent.putExtra("isComingCall", false);
		            startActivity(intent);
		            //更新门前监控数据
				}else{
					Toast.makeText(getBaseContext(), "未发现监控设备", Toast.LENGTH_SHORT).show();
					return ;
				}			
			}
		});
    }
	  //加载模式数据
    public void loadData(String path){
    	
    	HttpUtils http = new HttpUtils(1*1000);//1s超时
    	http.configCurrentHttpCacheExpiry(100);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    					
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//加载数据  转化为JSON格式
				GetModelAPI content = (GetModelAPI) JSONObject.parseObject(arg0.result, GetModelAPI.class);
				//根据字段用get方法获取字段内容
				if(content.getStatus().equals("403")){
					
					SharedPreferences sharedPreferences = getSharedPreferences("MODEL", 0);
					Editor editor = sharedPreferences.edit();
					editor.putString("model", content.getMODEL());
					editor.commit();
					//更新界面
					Message message = new Message();
					message.what = 3;
					updateUI.sendMessage(message);
					
				}
			}
		});
    }
    
	  //更新数据  //循环检查门铃事件 
	Runnable getDoorbell = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (flag) {
				
				getDoorbellData(App.address+"CGetData.php?uid="+id+"&getType=b1");
				getPIRData(App.address+"CGetData.php?uid="+id+"&getType=5");
				try {
					Thread.sleep(2*1000); //暂定延时2s
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Thread.interrupted();
		}
	};
    //
	Runnable updateDataDoorbell = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//b1为门前监控的类型
			//Log.e("TAG", "1");
			updateData(App.address+"CSetData.php?uid="+id+"&getType=b1&getId="+toUser+"&newData=0&flag=0");		
		}
	};
    public void updateData(String path){
    	//Log.e("TAG", "2");
    	HttpUtils http = new HttpUtils(1*1000);//1s超时
    	http.configCurrentHttpCacheExpiry(100);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    			//Log.e("TAG", "3");
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//加载数据  转化为JSON格式
				GetData_Api content = (GetData_Api) JSONObject.parseObject(arg0.result, GetData_Api.class);
				//根据字段用get方法获取字段内容
				if(content.getStatus().equals("403")){
					//更新界面
					//Message message = new Message();
					//message.what = 5;
					//updateUI.sendMessage(message);
					doorbell_flag = "0";
					PIR_flag = "0";
					//Log.e("TAG", "4");
				}
			}
		});
    }
    public void getDoorbellData(String path){
    	
    	HttpUtils http = new HttpUtils(1*1000);//1s超时
    	http.configCurrentHttpCacheExpiry(100);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    					
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//加载数据  转化为JSON格式
				GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
				//根据字段用get方法获取字段内容
				List<GetDevices_Api_Result> items = content.getResult();
				if(content.getStatus().equals("403")){
					
					if(items.get(0).getDdata().equals("1")){
						toUser=items.get(0).getGetId().toString();
						//更新界面
						Message message = new Message();
						message.what = 4;
						updateUI.sendMessage(message);
					}
					
				}
			}
		});
    }
    //获取红外热释电的数据
    public void getPIRData(String path){
    	
    	HttpUtils http = new HttpUtils(1*1000);//1s超时
    	http.configCurrentHttpCacheExpiry(100);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    					
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//加载数据  转化为JSON格式
				GetData_Api content = (GetData_Api) JSONObject.parseObject(arg0.result, GetData_Api.class);
				//根据字段用get方法获取字段内容
				if(content.getStatus().equals("403")){
					
					
					List<GetData_Api_Result> json_temp = new ArrayList<GetData_Api_Result>() ;
					json_temp = content.getResult();
					Iterator<GetData_Api_Result> it = json_temp.iterator();
					GetData_Api_Result gar = new GetData_Api_Result();
					while(it.hasNext())
					{	
						gar = it.next();
						if(gar.getDdata().equals("1_1")){
							Message message = new Message();
							Bundle b = new Bundle();
							b.putString("PIRID", gar.getGetId());
							b.putString("PIRNAME", gar.getDname());
							message.setData(b);
							message.what = 5;
							updateUI.sendMessage(message);		
						}
					}
				}
			}
		});
    }
    
    //加载模式数据
    public void loadDataNew(String path){
    	
    	HttpUtils http = new HttpUtils(1*1000);//1s超时
    	http.configCurrentHttpCacheExpiry(100);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    			if(net_flag.equals("0")){
    				txt.setVisibility(View.VISIBLE);
    				if(dialog.isShowing()){
    					dialog.dismiss();
    				}
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
					Load_flag = "1";
				}else{
					txt.setText("请先设定模式");
					txt.setVisibility(View.VISIBLE);
				}
				if(dialog.isShowing()){
					dialog.dismiss();
				}
			}
		});
    }
}
