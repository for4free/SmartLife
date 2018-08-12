package com.example.smartlife;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.DeleteDevice_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.example.smartlife_api.GetModelAPI;
import com.example.smartlife_api.GetModelResultAPI;
import com.example.smartlife_api.GetModelResultDataAPI;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting_Auto_Adapter3 extends BaseAdapter{
	
	private List<GetDevices_Api_Result> items;
	private Context context;
	private ViewHolder holder;
	private boolean flag = false;
	private String id,Mid;
	
	public  Setting_Auto_Adapter3(String Mid,List<GetDevices_Api_Result> items, Context context){ 
		this.items = items;
		this.context = context;
		this.Mid = Mid;
		SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN", 0);
		id = sharedPreferences.getString("id", "");
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		
		
		if(arg1 == null){
			//加载布局
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.device_lsit_item, null);
			holder.text = (TextView) arg1.findViewById(R.id.right_device_txt_t);
			holder.img = (ImageView) arg1.findViewById(R.id.right_device_img_t);
			holder.touch = (LinearLayout) arg1.findViewById(R.id.main_device_touch);
					
			arg1.setTag(holder);
		}
		else{
			holder = (ViewHolder) arg1.getTag();
		}
		//Log.e("111", ""+items.get(arg0).getDname().toString());
		holder.text.setText(items.get(arg0).getDname().toString());
		final String DID = items.get(arg0).getGetId();
		final String TYPE = items.get(arg0).getGetType();
		
		holder.touch.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(final View v) {
				// TODO Auto-generated method stub
			 	String path = App.address+"AddModelThing.php?Mid="+Mid+"&Did="+TYPE+"_"+DID+"&Thing=关";
			 	
		    	HttpUtils http = new HttpUtils(1*1000);//1s超时
		    	http.configCurrentHttpCacheExpiry(100);
				http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

		    		@Override
					public void onFailure(HttpException ag0, String arg1) {
		    			Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
		   		
					}
					
					@Override
					public void onSuccess(ResponseInfo<String> a0) {
						//holder.text.setTextColor(Color.rgb(65, 175, 175));
						DeleteDevice_Api content_1 = (DeleteDevice_Api) JSONObject.parseObject(a0.result, DeleteDevice_Api.class);
						if(content_1.getStatus().toString().equals("401")){
							Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
						}else if(content_1.getStatus().toString().equals("402")){
							Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
						}else if(content_1.getStatus().toString().equals("403")){
							Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
							
						}else{
							Toast.makeText(context, "未知错误"+content_1.getStatus().toString(), Toast.LENGTH_SHORT).show();
						}
					}
				});
					
				return false;
			}
		});
		
		return arg1;
	}
	
	class ViewHolder{
		TextView text;
		ImageView img;
		LinearLayout touch;
	}
	
}
