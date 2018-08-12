package com.example.smartlife;

import java.util.List;

import android.app.Instrumentation;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetModelAPI;
import com.example.smartlife_api.GetModelResultAPI;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MainMenuAdapter extends BaseAdapter {

	
	private List<GetModelResultAPI> items;
	private Context context;
	private ViewHolder holder;
	private boolean flag = false;
	private String id;
	
	public  MainMenuAdapter(List<GetModelResultAPI> items, Context context){ 
		this.items = items;
		this.context = context;
		SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN", 0);
		id = sharedPreferences.getString("id", "");
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		if(arg1 == null){
			//加载布局
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.main_menu_item, null);
			holder.text = (TextView) arg1.findViewById(R.id.main_menu_text_btn_02);
			
			arg1.setTag(holder);
		}
		else{
			holder = (ViewHolder) arg1.getTag();
		}
		//Log.e("111", ""+items.get(arg0).getDname().toString());
		holder.text.setText(items.get(arg0).getMname().toString());
		final String Mid = items.get(arg0).getMid();

		holder.text.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View a0, MotionEvent a1) {
				// TODO Auto-generated method stub
				if(a1.getAction()==MotionEvent.ACTION_DOWN){
					loadData(App.address+"ChangeModel.php?uid=" + id + "&Mid=" + Mid);
					//Log.e("TAG", "模式更改"+"id"+id+"mid"+Mid);
				}
				return false;
			}
		});
		
		return arg1;
	}

	class ViewHolder{
		TextView text;

	}

	
    public void loadData(String path){
    	
    	HttpUtils http = new HttpUtils(5*1000);//1s超时
    	http.configCurrentHttpCacheExpiry(1000);
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    		//	Log.e("TAG", "模式更改失败"+arg1);	
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
			//	Log.e("TAG", "模式更改成功");
			}
		});
    }
	
}
