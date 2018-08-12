package com.example.smartlife;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.PullDownScrollView.RefreshListener;
import com.example.smartlife_api.GetData_Api;
import com.example.smartlife_api.GetData_Api_Result;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Main_Air extends Activity implements OnTouchListener,OnLongClickListener, RefreshListener {

	private ImageView back;
	private RelativeLayout change;
	private ProgressBar temp, hum, light;
	private TextView txt_temp, txt_hum, txt_light, lastUpdatedTextView;
	private PullDownScrollView mPullDownScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_air);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.main_air_titlebar);

		// TitleBar
		back = (ImageView) findViewById(R.id.main_air_btn_back);
		back.setOnTouchListener(this);

		change = (RelativeLayout) findViewById(R.id.main_air_change);
		change.setOnTouchListener(this);

		temp = (ProgressBar) findViewById(R.id.main_air_btm_temp);
		hum = (ProgressBar) findViewById(R.id.main_air_btm_hum);
		light = (ProgressBar) findViewById(R.id.main_air_btm_light);

		txt_temp = (TextView) findViewById(R.id.main_air_btm_temp_txt);
		txt_light = (TextView) findViewById(R.id.main_air_btm_light_txt);
		txt_hum = (TextView) findViewById(R.id.main_air_btm_hum_txt);

		// 下拉监听
		mPullDownScrollView = (PullDownScrollView) findViewById(R.id.refresh_root);
		mPullDownScrollView.setRefreshListener(this);
		mPullDownScrollView.setPullDownElastic(new PullDownElasticImp(this));
		lastUpdatedTextView = (TextView) findViewById(R.id.refresh_time);

		//初始化界面
		SharedPreferences sharedPreferences = getSharedPreferences("DATA", 0);
		String time_refresh = sharedPreferences.getString("time_refresh", "");
		lastUpdatedTextView.setText("上次刷新时间: " + time_refresh);
		try {
			int int_temp = Integer.valueOf(sharedPreferences.getString("temp", "")).intValue();
			int int_hum = Integer.valueOf(sharedPreferences.getString("hum", "")).intValue();
			int int_light = Integer.valueOf(sharedPreferences.getString("light", "")).intValue();

			temp.setProgress(int_temp);
			txt_temp.setText("" + int_temp + " ℃");

			hum.setProgress(int_hum);
			txt_hum.setText("" + int_hum + " %");

			light.setProgress(int_light);
			txt_light.setText("" + int_light + " LUX");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_air_btn_back:
			this.finish();
			break;
		case R.id.main_air_change:
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				Toast.makeText(getBaseContext(), "正在改善", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		/*
		 * case R.id.main_air_top_btn: //刷新数据 SharedPreferences
		 * sharedPreferences = getSharedPreferences("SOCKET", 0);
		 * 
		 * try { int int_temp =
		 * Integer.valueOf(sharedPreferences.getString("temp", "")).intValue();
		 * int int_hum = Integer.valueOf(sharedPreferences.getString("hum",
		 * "")).intValue(); int int_light =
		 * Integer.valueOf(sharedPreferences.getString("light", "")).intValue();
		 * 
		 * temp.setProgress(int_temp); txt_temp.setText(""+int_temp+" ℃");
		 * 
		 * hum.setProgress(int_hum); txt_hum.setText(""+int_hum+" %");
		 * 
		 * light.setProgress(int_light); txt_light.setText(""+int_light+" LUX");
		 * 
		 * Toast.makeText(getBaseContext(), "刷新成功", Toast.LENGTH_SHORT).show();
		 * 
		 * } catch (Exception e) { // TODO: handle exception
		 * e.printStackTrace(); Toast.makeText(getBaseContext(), "刷新失败",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * break;
		 */

		default:
			break;
		}

		return false;
	}

	// 下拉监听
	@Override
	public void onRefresh(PullDownScrollView view) {
		// TODO Auto-generated method stub
		new Thread(updateAir).start();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SharedPreferences sharedPreferences = getSharedPreferences("DATA", 0);
				Editor editor = sharedPreferences.edit();
				Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time
										// Zone资料
				t.setToNow(); // 取得系统时间。
				int date = t.hour;
				int hour = t.minute; // 0-23
				editor.putString("time_refresh", "" + date + ":" + hour);
				editor.commit();
				String time_refresh = sharedPreferences.getString("time_refresh", "");
				mPullDownScrollView.finishRefresh("上次刷新时间: " + time_refresh);
				String suc = sharedPreferences.getString("suc", "");
				if (suc.equals("1")) {
					try {
						int int_temp = Integer.valueOf(sharedPreferences.getString("temp", "")).intValue();
						int int_hum = Integer.valueOf(sharedPreferences.getString("hum", "")).intValue();
						int int_light = Integer.valueOf(sharedPreferences.getString("light", "")).intValue();

						temp.setProgress(int_temp);
						txt_temp.setText("" + int_temp + " ℃");

						hum.setProgress(int_hum);
						txt_hum.setText("" + int_hum + " %");

						light.setProgress(int_light);
						txt_light.setText("" + int_light + " LUX");

						Toast.makeText(getBaseContext(), "刷新成功",Toast.LENGTH_SHORT).show();

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						Toast.makeText(getBaseContext(), "刷新失败",Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getBaseContext(), "刷新失败",Toast.LENGTH_SHORT).show();
				}

			}
		}, 2000);

	}
	//更新温湿度传感器数据
	Runnable updateAir = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//获取用户id 
			SharedPreferences sharedPreferences_Login = getSharedPreferences("LOGIN", 0);
			final SharedPreferences sharedPreferences_data = getSharedPreferences("DATA", 0);
			String id = sharedPreferences_Login.getString("id", "");
			//指定温湿度传感器
			String path = App.address + "CGetData.php?uid="+id;
			HttpUtils http = new HttpUtils(10000);//10s超时
			http.configCurrentHttpCacheExpiry(5000); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。
			http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					Editor editor_data = sharedPreferences_data.edit();
					editor_data.putString("suc", "0");
					editor_data.commit();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					// 加载数据 转化为JSON格式
					GetData_Api content = (GetData_Api) JSONObject.parseObject(arg0.result, GetData_Api.class);
					if (content.getStatus().equals("402")|| content.getStatus().equals("401")) {
						Editor editor_data = sharedPreferences_data.edit();
						editor_data.putString("suc", "0");
						editor_data.commit();
					} else if (content.getStatus().equals("403")) {
						//解析数据
						List<GetData_Api_Result> json_temp = new ArrayList<GetData_Api_Result>() ;
						json_temp = content.getResult();
						Iterator<GetData_Api_Result> it = json_temp.iterator();
						GetData_Api_Result gar = new GetData_Api_Result();
						while(it.hasNext())
						{	
							//Log.e("111", "111-"+it.next().getGetId());
							gar = it.next();
							if((gar.getGetId().toString().equals("1"))&&(gar.getGetType().toString().equals("2"))){
								String data_temp= gar.getDdata().toString();
								String[] dataSplit = null;
								if(data_temp.length() == 1){
									dataSplit = new String[3];
									dataSplit[0] = "0";
									dataSplit[1] = "0";
									dataSplit[2] = "0";
								}else{
									dataSplit = data_temp.split("_");
								}
								Editor editor_data = sharedPreferences_data.edit();
								editor_data.putString("temp", dataSplit[0]);
								editor_data.putString("hum", dataSplit[1]);
								editor_data.putString("light", dataSplit[2]);
								editor_data.putString("suc", "1");
								editor_data.commit();
								//Log.e("111", "222"+it.next().getGetId());
							}
						}
					} else {
						Editor editor_data = sharedPreferences_data.edit();
						editor_data.putString("suc", "0");
						editor_data.commit();
					}
					
				}

			});
		}
			
	};

}
