package com.example.smartlife;

import java.util.List;

import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Setting_Fml_Num_Dialog.Setting_Fml_Num_DialogListener;
import com.example.smartlife_api.GetDevices_Api;
import com.example.smartlife_api.GetFamilyNum_Res;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting_Fml_num_Adapter extends BaseAdapter{
	
	private List<GetFamilyNum_Res> items;
	private Context context;
	private ViewHolder holder;
	private boolean flag = false;
	private String id,cId,Fid;
	
	public  Setting_Fml_num_Adapter(List<GetFamilyNum_Res> items, Context context){ 
		this.items = items;
		this.context = context;
		SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN", 0);
		id = sharedPreferences.getString("id", "");
		cId = sharedPreferences.getString("fmlCr", "");
		Fid = sharedPreferences.getString("Fid", "");
		}
	
	public int getCount() {
		return items.size();
	}

	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}
	
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		
		if(arg1 == null){
			//加载布局
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.left_fml_join_item, null);
			holder.text = (TextView) arg1.findViewById(R.id.left_fml_num_item_txt_t);
			holder.img = (ImageView) arg1.findViewById(R.id.left_fml_num_item_img_t);
			holder.touch = (LinearLayout) arg1.findViewById(R.id.left_fml_num_item_touch);
			//holder.light = (TextView) arg1.findViewById(R.id.main_light_light_t);
			holder.btn_delete = (Button) arg1.findViewById(R.id.left_fml_num_item_btn_delete);
			holder.btn_agree = (Button) arg1.findViewById(R.id.left_fml_num_item_btn_agree);
			
			arg1.setTag(holder);
		}
		else{
			holder = (ViewHolder) arg1.getTag();
		}
		
		//布局设置
		final String id_t = items.get(arg0).getId();
		if(id.equals(cId)){
			if((items.get(arg0).getPassFid().equals(Fid))){
				holder.btn_agree.setVisibility(arg1.INVISIBLE);
				holder.btn_delete.setVisibility(arg1.VISIBLE);
				holder.btn_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(id_t.equals(cId)){
							Toast.makeText(context, "不能删除自己", Toast.LENGTH_SHORT).show();
						}else{
							doFamily(App.address+"BKFml.php?tidORuid="+id_t);
						}
						
					}
				});
							
			}else{
				holder.btn_delete.setVisibility(arg1.INVISIBLE);
				holder.btn_agree.setVisibility(arg1.VISIBLE);
				
				holder.btn_agree.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View a0) {
						// TODO Auto-generated method stub
						
						Setting_Fml_Num_Dialog dialog = new Setting_Fml_Num_Dialog(context, R.style.Dialog, new Setting_Fml_Num_DialogListener() {
							
							@Override
							public void onClick(View vw) {
								// TODO Auto-generated method stub
								switch (vw.getId()) {
								case R.id.dialog_left_fml_num_but_agree:
									
									doFamily(App.address+"AgreeFml.php?tidORuid="+id_t+"&Fid="+Fid+"&agree=2");
									
									break;
								case R.id.dialog_left_fml_num_but_disagree:
									
									doFamily(App.address+"AgreeFml.php?tidORuid="+id_t+"&Fid="+Fid+"&agree=1");
									
									break;
								default:
									break;
								}
								new Thread(back).start();
								
							}
						});
						dialog.show();
					}
				});
			}
			
		}
		
		holder.text.setText(items.get(arg0).getName().toString());
		BitmapUtils utils = new BitmapUtils(context); 
		utils.configDefaultLoadFailedImage(R.drawable.people);
		String path = App.address+"upload/img/id_"+items.get(arg0).getId()+"_headimg.jpg";
		utils.display(holder.img, path);

		return arg1;
	}
	
	class ViewHolder{
		TextView text;
		ImageView img;
		TextView light;
		LinearLayout touch;
		Button btn_delete;
		Button btn_agree;
	}
	
    
    //处理事件
	public void doFamily(String path){
	   	
	   	HttpUtils http = new HttpUtils(10000);//9s超时
	   	http.configCurrentHttpCacheExpiry(500);
			http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

	   		@Override
				public void onFailure(HttpException arg0, String arg1) {
	   				Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					
					GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
					
					if(content.getStatus().equals("401")){
						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("402")){
						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("403")){
						Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
						
					}else{
						Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
					}
				
				}
				
			});
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
