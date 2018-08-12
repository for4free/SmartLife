package com.example.smartlife;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;

public class Main_Light_Adapter extends BaseAdapter{
	
	private List<GetDevices_Api_Result> items;
	private Context context;
	private ViewHolder holder;
	private boolean flag = false;
	private String id;
	
	public  Main_Light_Adapter(List<GetDevices_Api_Result> items, Context context){ 
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
			arg1 = LayoutInflater.from(context).inflate(R.layout.main_light, null);
			holder.text = (TextView) arg1.findViewById(R.id.main_light_txt_t);
			holder.img = (ImageView) arg1.findViewById(R.id.main_light_img_t);
			holder.touch = (RelativeLayout) arg1.findViewById(R.id.main_light_touch);
			holder.light = (TextView) arg1.findViewById(R.id.main_light_light_t);
			arg1.setTag(holder);
		}
		else{
			holder = (ViewHolder) arg1.getTag();
		}
		//Log.e("111", ""+items.get(arg0).getDname().toString());
		holder.text.setText(items.get(arg0).getDname().toString());
		
		if((items.get(arg0).getDdata().equals("0"))||(items.get(arg0).getDdata().equals("1"))){
			holder.img.setImageResource(R.drawable.zhaoming);
			holder.light.setText("关");
		}else{
			holder.img.setImageResource(R.drawable.ic_light);
			holder.light.setText(""+(Integer.valueOf(items.get(arg0).getDdata()).intValue()-1));
		}
		
		//触摸监听
		holder.touch.setOnTouchListener(new OnTouchListener() {
		
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if((arg1.getAction() == MotionEvent.ACTION_UP)){
					v.setBackgroundResource(R.drawable.main_background);
					
					if(Integer.valueOf(items.get(arg0).getDdata()).intValue()<9){
						updateData(Integer.valueOf(items.get(arg0).getDdata()).intValue()+1, items.get(arg0).getGetId().toString());
					}else if(Integer.valueOf(items.get(arg0).getDdata()).intValue()==9){
						updateData(1, items.get(arg0).getGetId().toString());
					}
					
				}else if(arg1.getAction() == MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.main_background_touch);
				}else if(arg1.getAction() == MotionEvent.ACTION_CANCEL){
					v.setBackgroundResource(R.drawable.main_background);
				}
				return false;
			}
		});
		
		//长按监听
		holder.touch.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				//v.setBackgroundResource(R.drawable.main_background);
				// TODO Auto-generated method stub
				if((items.get(arg0).getDdata().equals("0"))||(items.get(arg0).getDdata().equals("1"))){
					updateData(6, items.get(arg0).getGetId().toString());
				}else{
					updateData(1, items.get(arg0).getGetId().toString());
				}
				//Toast.makeText(context, "长按", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	
		return arg1;
	}
	
	class ViewHolder{
		TextView text;
		ImageView img;
		TextView light;
		RelativeLayout touch;
	}
	
	public void updateData(int value,String getId){
    	HttpUtils http = new HttpUtils(10000);//9s超时
    	http.configCurrentHttpCacheExpiry(0);
		http.send(HttpMethod.GET, App.address+"CSetData.php?uid="+id+"&getType=9&getId="+getId+"&newData="+value+"&flag=1", new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    			Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
				
			}
		});
	}
	

}
