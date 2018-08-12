package com.example.smartlife;

import java.util.List;
import java.util.Map;

import com.example.smartlife_api.GetDevices_Api_Result;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main_Moni_Adapter extends BaseAdapter{
	
	private List<GetDevices_Api_Result> items;
	private Context context;
	private ViewHolder holder;
	private boolean flag = false;
	private Map<String, Object> map;
	
	public  Main_Moni_Adapter(List<GetDevices_Api_Result> totalList, Context context){ 
		this.items = totalList;
		this.context = context;
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
			arg1 = LayoutInflater.from(context).inflate(R.layout.main_moni, null);
			holder.title = (TextView) arg1.findViewById(R.id.main_moni_txt);
			holder.img = (ImageView) arg1.findViewById(R.id.main_moni_img);
			holder.touch = (LinearLayout) arg1.findViewById(R.id.main_moni_open);
			holder.img_live = (ImageView) arg1.findViewById(R.id.main_moni_center_img);
			holder.info_location = (TextView) arg1.findViewById(R.id.main_moni_txt_btm);
			holder.img_top = (ImageView) arg1.findViewById(R.id.main_moni_t_top_img);
			arg1.setTag(holder);
		}
		else{
			holder = (ViewHolder) arg1.getTag();
		}
		//Log.e("111", ""+items.get(arg0).getDname().toString());
		String status = items.get(arg0).getDdata().toString();
		//Map<String, Object> i = items.get(arg0);
		String title = items.get(arg0).getDname().toString();
		String title_img = items.get(arg0).getDdata().toString();
		holder.title.setText(title);
		if(title_img.equals("0")){
			holder.img_top.setImageResource(R.drawable.ic_dev_offline);
			holder.img_live.setVisibility(arg1.INVISIBLE);
		}else{
			holder.img_top.setImageResource(R.drawable.ic_dev_normal);
		}
		//holder.img.setImageResource();
		//长按监听
		holder.touch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,Main_Moni_Moni.class);
				context.startActivity(intent);
			}
		});
	
		return arg1;
	}
	
	class ViewHolder{
		TextView title;
		ImageView img,img_top;
		TextView info_location;
		ImageView img_live;
		LinearLayout touch;
	}
}
