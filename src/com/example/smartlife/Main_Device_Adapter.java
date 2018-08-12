package com.example.smartlife;

import java.util.List;

import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Device_Dialog.Device_Dialog_DialogListener;
import com.example.smartlife.Device_Dialog_Change.Device_Dialog_ChangeListener;
import com.example.smartlife_api.DeleteDevice_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Main_Device_Adapter extends BaseAdapter{
	
	private List<GetDevices_Api_Result> items;
	private Context context;
	private ViewHolder holder;
	private boolean flag = false;
	private String id;
	
	public  Main_Device_Adapter(List<GetDevices_Api_Result> items, Context context){ 
		this.items = items;
		this.context = context;
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
		//短按监听
		holder.touch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "触摸", Toast.LENGTH_SHORT).show();	
				Device_Dialog_Change dialog = new Device_Dialog_Change(id,items.get(arg0).getGetType().toString(),
						items.get(arg0).getGetId().toString(),context, R.style.Dialog, new Device_Dialog_ChangeListener() {
					
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						
					}
				});
				dialog.show();
			}
		});
		
		//长按监听
		holder.touch.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				//v.setBackgroundResource(R.drawable.main_background);
				// TODO Auto-generated method stub
				//Toast.makeText(context, "长按"+arg0, Toast.LENGTH_SHORT).show();	
				Device_Dialog dialog = new Device_Dialog(context, R.style.Dialog, new Device_Dialog_DialogListener() {
					
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						switch (view.getId()) {
						case R.id.dialog_device_but_cancle:
							new Thread(back).start();
							break;
						case R.id.dialog_device_but_ok:
							
						 	HttpUtils http = new HttpUtils(10000);//9s超时
					    	http.configCurrentHttpCacheExpiry(500);
					    	final String PATH = App.address+"DeleteDevice.php?uidORtid="+id+"&getId="+items.get(arg0).getGetId()+"&getType="+items.get(arg0).getGetType();
							http.send(HttpMethod.GET, PATH, new RequestCallBack<String>() {

					    		@Override
								public void onFailure(HttpException arg, String s) {
					    			//Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
					    			Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
								}
								
								@Override
								public void onSuccess(ResponseInfo<String> arg) {
									//暂用一下这个API
									DeleteDevice_Api content_1 = (DeleteDevice_Api) JSONObject.parseObject(arg.result, DeleteDevice_Api.class);
									if(content_1.getStatus().toString().equals("401")){
										Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
									}else if(content_1.getStatus().toString().equals("402")){
										Toast.makeText(context, "非家庭创建者不能删除", Toast.LENGTH_SHORT).show();
									}else if(content_1.getStatus().toString().equals("403")){
										Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
									}else{
										Toast.makeText(context, "未知错误"+content_1.getStatus().toString(), Toast.LENGTH_SHORT).show();
									}
								}
							});
							
							new Thread(back).start();
							break;
						default:
							break;
						}
					}
				});
				dialog.show();
				
				return true;
			}
		});
	
		return arg1;
	}
	
	class ViewHolder{
		TextView text;
		ImageView img;
		LinearLayout touch;
	}
	

	//模拟返回键
 Runnable back = new Runnable() {
	
	@Override
	public void run() {
		// TODO Auto-generated method stubnew Thread () {
		
		try {
		Instrumentation inst= new Instrumentation();
		inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
		} catch(Exception e) {
		e.printStackTrace();
		}
	}
};
}
