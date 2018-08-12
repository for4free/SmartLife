package com.example.smartlife;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetDevices_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Device_Pen extends Activity implements OnClickListener,OnTouchListener {

	private ImageView img_btn_device_back_pen,btn_qr;
	private Button btn_01, btn_02, btn_03, btn_04, btn_ok;
	private EditText id, name;
	private String type = "0",Uid,Did;
	private final static int SCANNIN_GREQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.right_device_pen);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.right_device_pen_titlebar);

		// 返回按钮
		img_btn_device_back_pen = (ImageView) findViewById(R.id.right_device_pen_btn);
		img_btn_device_back_pen.setOnClickListener(this);
		// BUTTON
		btn_01 = (Button) findViewById(R.id.right_device_pen_btn_01);
		btn_02 = (Button) findViewById(R.id.right_device_pen_btn_02);
		btn_03 = (Button) findViewById(R.id.right_device_pen_btn_03);
		btn_04 = (Button) findViewById(R.id.right_device_pen_btn_04);
		btn_ok = (Button) findViewById(R.id.right_device_pen_btn_ok);
		btn_qr = (ImageView) findViewById(R.id.right_header_right_btn_01);
		btn_qr.setOnClickListener(this);
		// 监听
		btn_01.setOnClickListener(this);
		btn_02.setOnClickListener(this);
		btn_03.setOnClickListener(this);
		btn_04.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		// 监听触摸
		btn_ok.setOnTouchListener(this);
		// EDITTEXT
		id = (EditText) findViewById(R.id.right_device_pen_edit_01);
		name = (EditText) findViewById(R.id.right_device_pen_edit_02);
		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		Uid = sharedPreferences.getString("id", "");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.right_device_pen_btn:
			Device_Pen.this.finish();
			break;
		case R.id.right_device_pen_btn_01:
			btn_01.setBackgroundResource(R.drawable.right_pen_min_clk);
			btn_02.setBackgroundResource(R.drawable.right_pen_min);
			btn_03.setBackgroundResource(R.drawable.right_pen_min);
			btn_04.setBackgroundResource(R.drawable.right_pen_min);
			name.setText(btn_01.getText().toString()+"-");
			name.setSelection(name.getText().length());
			break;
		case R.id.right_device_pen_btn_02:
			btn_02.setBackgroundResource(R.drawable.right_pen_min_clk);
			btn_01.setBackgroundResource(R.drawable.right_pen_min);
			btn_03.setBackgroundResource(R.drawable.right_pen_min);
			btn_04.setBackgroundResource(R.drawable.right_pen_min);
			name.setText(btn_02.getText().toString()+"-");
			name.setSelection(name.getText().length());
			break;
		case R.id.right_device_pen_btn_03:
			btn_03.setBackgroundResource(R.drawable.right_pen_min_clk);
			btn_02.setBackgroundResource(R.drawable.right_pen_min);
			btn_01.setBackgroundResource(R.drawable.right_pen_min);
			btn_04.setBackgroundResource(R.drawable.right_pen_min);
			name.setText(btn_03.getText().toString()+"-");
			name.setSelection(name.getText().length());
			break;
		case R.id.right_device_pen_btn_04:
			btn_04.setBackgroundResource(R.drawable.right_pen_min_clk);
			btn_02.setBackgroundResource(R.drawable.right_pen_min);
			btn_03.setBackgroundResource(R.drawable.right_pen_min);
			btn_01.setBackgroundResource(R.drawable.right_pen_min);
			name.setText(btn_04.getText().toString()+"-");
			name.setSelection(name.getText().length());
			break;
			// 扫描二维码
		case R.id.right_header_right_btn_01:
			/*Intent intent_02 = new Intent(Device_Pen.this, Qre.class);
			Device_Pen.this.startActivity(intent_02);*/
			Intent intent = new Intent();
			intent.setClass(Device_Pen.this, MipcaActivityCapture.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;
		case R.id.right_device_pen_btn_ok:
			if (id.getText().toString().equals("")) {
				Toast.makeText(getBaseContext(), "设备ID不能为空", Toast.LENGTH_SHORT).show();
			} else if (name.getText().toString().equals("")) {
				Toast.makeText(getBaseContext(), "显示名称不能为空", Toast.LENGTH_SHORT).show();
			} else {
				//String datatemp = id.getText().toString().substring(1, 2);
				String[] data = id.getText().toString().split("-");
				//Toast.makeText(getBaseContext(), "++"+data.length+data[0]+data[1], Toast.LENGTH_SHORT).show();
				if((id.getText().length()>=3)&&(data.length==2)){
					type = data[0];
					Did = data[1];
					// 处理
					btn_ok.setText("正在添加...");
					btn_ok.setEnabled(false);
					new Thread(addDevice).start();
				}else{
					Toast.makeText(getBaseContext(), "设备ID不正确", Toast.LENGTH_SHORT).show();
				}
				
				
			}

			break;
		default:
			break;
		}

	}
	 //添加设备
	Runnable addDevice = new Runnable() {	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			AddDevice(App.address+"AddDevice.php?uidORtid="+Uid+"&getId="+Did+"&getType="+type+"&getName="+name.getText());	
		}
	};
	   public void AddDevice(String path){
	   	
	   	HttpUtils http = new HttpUtils(10000);//9s超时
	   	http.configCurrentHttpCacheExpiry(500);
			http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

	   		@Override
				public void onFailure(HttpException arg0, String arg1) {
	   			//txt.setVisibility(View.VISIBLE);
	   				btn_ok.setText("添加");
	   				btn_ok.setEnabled(true);
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					//txt.setVisibility(View.INVISIBLE);
					//加载数据  转化为JSON格式
					GetDevices_Api content = (GetDevices_Api) JSONObject.parseObject(arg0.result, GetDevices_Api.class);
					//根据字段用get方法获取字段内容
					if(content.getStatus().equals("401")){
						Toast.makeText(getBaseContext(), "添加失败", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("402")){
						Toast.makeText(getBaseContext(), "设备已存在", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("403")){
						Toast.makeText(getBaseContext(), "添加成功", Toast.LENGTH_SHORT).show();
					}else if(content.getStatus().equals("400")){
						Toast.makeText(getBaseContext(), "未加入家庭", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getBaseContext(), "未知错误", Toast.LENGTH_SHORT).show();
					}
					//adapter.notifyDataSetChanged();
					btn_ok.setText("添加");
					btn_ok.setEnabled(true);
				}
				
			});
	   }
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.right_device_pen_btn_ok:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				btn_ok.setBackgroundResource(R.drawable.right_pen_max_clk);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				btn_ok.setBackgroundResource(R.drawable.right_pen_max);
			}

			break;

		default:
			break;
		}
		return false;
	}
	
	//扫描返回的结果处理
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				//显示扫描到的内容
				if(bundle.getString("flag").equals("1")){
					id.setText(bundle.getString("Type")+"-"+bundle.getString("Did"));
				}
				//mTextView.setText(bundle.getString("result"));
				
				//mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		}
    }	

}
