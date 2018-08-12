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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Device_Dialog.Device_Dialog_DialogListener;
import com.example.smartlife_api.DeleteDevice_Api;
import com.example.smartlife_api.GetModelResultDataAPI;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting_Auto_Adapter2 extends BaseAdapter {

	
	private List<GetModelResultDataAPI> items;
	private Context context;
	private ViewHolder holder;
	private boolean flag = false;
	private String id,Mid;
	
	public  Setting_Auto_Adapter2(List<GetModelResultDataAPI> items, Context context,String Mid){ 
		this.items = items;
		this.context = context;
		this.Mid = Mid;
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
			//���ز���
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.left_auto_item2, null);
			holder.text = (TextView) arg1.findViewById(R.id.left_auto_item2_Did);
			holder.text2 = (TextView) arg1.findViewById(R.id.left_auto_item2_doThing);
			holder.btn_change = (Button) arg1.findViewById(R.id.left_auto_item2_btn);
			arg1.setTag(holder);
		}
		else{
			holder = (ViewHolder) arg1.getTag();
		}
		//Log.e("111", ""+items.get(arg0).getDname().toString());
		holder.text.setText(""+items.get(arg0).getDname());
		holder.text2.setText(""+items.get(arg0).getDoThing());
		final String TYPE = items.get(arg0).getDtype();
		final String DID = items.get(arg0).getDid();
		
		arg1.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
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

							HttpUtils http = new HttpUtils(10000);// 9s��ʱ
							http.configCurrentHttpCacheExpiry(500);
							final String PATH = App.address + "DeleteModelThing.php?Mid="+Mid+"&Did="+TYPE+"_"+DID;
							http.send(HttpMethod.GET, PATH,new RequestCallBack<String>() {

										@Override
										public void onFailure(HttpException arg,String s) {
											Toast.makeText(context,"ɾ��ʧ��",Toast.LENGTH_SHORT).show();
										}

										@Override
										public void onSuccess(
												ResponseInfo<String> arg) {
											// ����һ�����API
											DeleteDevice_Api content_1 = (DeleteDevice_Api) JSONObject.parseObject(arg.result,DeleteDevice_Api.class);
											if (content_1.getStatus().toString().equals("401")) {
												Toast.makeText(context,"ɾ��ʧ��",Toast.LENGTH_SHORT).show();
											} else if (content_1.getStatus().toString().equals("402")) {
												Toast.makeText(context,"ɾ��ʧ��",Toast.LENGTH_SHORT).show();
											} else if (content_1.getStatus().toString().equals("403")) {
												Toast.makeText(context,"ɾ���ɹ�",Toast.LENGTH_SHORT).show();
											} else {
												Toast.makeText(context,"δ֪����"+ content_1.getStatus().toString(),Toast.LENGTH_SHORT).show();
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
				
				
				return false;
			}
		});
		
		final String Thing;
		if(items.get(arg0).getDoThing().equals("��")){
			Thing = "��";
		}else{
			Thing = "��";
		}
		
		holder.btn_change.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HttpUtils http = new HttpUtils(10000);// 9s��ʱ
				http.configCurrentHttpCacheExpiry(500);
				final String PATH = App.address + "ChangeModelThing.php?Mid="+Mid+"&Did="+TYPE+"_"+DID+"&Thing="+Thing;
				http.send(HttpMethod.GET, PATH,new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg,String s) {
								Toast.makeText(context,"����ʧ��",Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onSuccess(
									ResponseInfo<String> arg) {
								// ����һ�����API
								DeleteDevice_Api content_1 = (DeleteDevice_Api) JSONObject.parseObject(arg.result,DeleteDevice_Api.class);
								if (content_1.getStatus().toString().equals("401")) {
									Toast.makeText(context,"����ʧ��",Toast.LENGTH_SHORT).show();
								} else if (content_1.getStatus().toString().equals("402")) {
									Toast.makeText(context,"����ʧ��",Toast.LENGTH_SHORT).show();
								} else if (content_1.getStatus().toString().equals("403")) {
									Toast.makeText(context,"���ĳɹ�",Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context,"δ֪����"+ content_1.getStatus().toString(),Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
		});
		
		return arg1;
	}

	class ViewHolder{
		TextView text;
		TextView text2;
		Device_Dialog dialog;
		Button btn_change;
	}
	
	// ģ�ⷵ�ؼ�
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
