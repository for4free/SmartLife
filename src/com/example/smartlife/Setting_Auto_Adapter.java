package com.example.smartlife;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Device_Dialog.Device_Dialog_DialogListener;
import com.example.smartlife.Main_Device_Adapter.ViewHolder;
import com.example.smartlife_api.DeleteDevice_Api;
import com.example.smartlife_api.GetDevices_Api_Result;
import com.example.smartlife_api.GetModelResultAPI;
import com.example.smartlife_api.GetModelResultDataAPI;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Setting_Auto_Adapter extends BaseAdapter {

	private List<GetModelResultAPI> items;
	private Context context;
	private ViewHolder holder;
	private String id;

	public Setting_Auto_Adapter(List<GetModelResultAPI> items, Context context) {
		this.items = items;
		this.context = context;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"LOGIN", 0);
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

		if (arg1 == null) {
			// 加载布局
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.left_auto_item, null);
			holder.text = (TextView) arg1.findViewById(R.id.left_auto_re01_txt);
			holder.text2 = (TextView) arg1.findViewById(R.id.left_auto_re01_txt_b);
			holder.re = (RelativeLayout) arg1.findViewById(R.id.left_auto_re01);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		// Log.e("111", ""+items.get(arg0).getDname().toString());
		holder.text.setText(items.get(arg0).getMname().toString());
		holder.text2.setText(items.get(arg0).getModelTotal().toString()+ "个响应设备");
		final String Mid = items.get(arg0).getMid();
		final int sort = arg0;
		holder.re.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View a0) {
				// TODO Auto-generated method stub
				// 新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
				Intent intent = new Intent(context,Setting_Auto_Setting.class);
				// 用Bundle携带数据
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				bundle.putString("Mid", Mid);
				bundle.putInt("sort", sort);
				intent.putExtras(bundle);
				context.startActivity(intent);
				
			}
		});
	

		holder.re.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View a0) {
				// TODO Auto-generated method stub
				holder.dialog = new Device_Dialog(context,R.style.Dialog, new Device_Dialog_DialogListener() {

							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub
								switch (view.getId()) {
								case R.id.dialog_device_but_cancle:
									new Thread(back).start();
									break;
								case R.id.dialog_device_but_ok:

									HttpUtils http = new HttpUtils(10000);// 9s超时
									http.configCurrentHttpCacheExpiry(500);
									final String PATH = App.address + "DeleteModel.php?Mid="+Mid;
									http.send(HttpMethod.GET, PATH,new RequestCallBack<String>() {

												@Override
												public void onFailure(HttpException arg,String s) {
													Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
												}

												@Override
												public void onSuccess(
														ResponseInfo<String> arg) {
													// 暂用一下这个API
													DeleteDevice_Api content_1 = (DeleteDevice_Api) JSONObject.parseObject(arg.result,DeleteDevice_Api.class);
													if (content_1.getStatus().toString().equals("401")) {
														Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
													} else if (content_1.getStatus().toString().equals("402")) {
														Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
													} else if (content_1.getStatus().toString().equals("403")) {
														Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
													} else {
														Toast.makeText(context,"未知错误"+ content_1.getStatus().toString(),Toast.LENGTH_SHORT).show();
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
				
				holder.dialog.show();
				return true;
			}
		});

		return arg1;
	}

	class ViewHolder {
		TextView text;
		TextView text2;
		RelativeLayout re;
		Device_Dialog dialog;
	}

	// 模拟返回键
	Runnable back = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stubnew Thread () {

			try {
				Instrumentation inst = new Instrumentation();
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
