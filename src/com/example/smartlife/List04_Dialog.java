package com.example.smartlife;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class List04_Dialog extends Dialog implements OnClickListener {

	private Context context;
	private Button btn_open,btn01,btn02,btn03;
	private List04_DialogListener  listener;
	private TextView txt;
	private String newdata;
	private String name;
	public List04_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface List04_DialogListener {
		public void onClick(View view);
	}

	public List04_Dialog(String name,String newdata,Context context, int theme,List04_DialogListener listener) {
		super(context, theme);
		this.name = name;
		this.newdata = newdata;
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_list04);
		initViews();
	}

	private void initViews() {
		btn_open = (Button) findViewById(R.id.dialog_list04_power);
		//btn_close = (Button) findViewById(R.id.dialog_list01_but_close);
		btn01 = (Button) findViewById(R.id.dialog_list04_temp01);
		btn02 = (Button) findViewById(R.id.dialog_list04_temp02);
		btn03 = (Button) findViewById(R.id.dialog_list04_pattern);
		
		txt = (TextView) findViewById(R.id.dialog_list04_txt);
		String[] dataSplit = newdata.split("_");
		if(dataSplit[0].equals("0")){
			txt.setText(name+" 已经关闭");
			btn_open.setText("开机");
		}else{
			txt.setText(name+" 温度:"+dataSplit[1]+" - 模式:"+dataSplit[2]);
			btn_open.setText("关机");
		}
		btn_open.setOnClickListener(this);
		//btn_close.setOnClickListener(this);
		btn01.setOnClickListener(this);
		btn02.setOnClickListener(this);
		//btn03.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
		PinnedHeaderExpandableAdapter_Sockt.handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				SharedPreferences sharedPreferences = context.getSharedPreferences("NEWDATA", 0);
				String data01 = sharedPreferences.getString("data01", "");
				String data02 = sharedPreferences.getString("data02", "");
				String power = sharedPreferences.getString("power", "");
				super.handleMessage(msg);
				if(msg.what==1){
					if(power.equals("1")){
						txt.setText(name+" 温度:"+data01+" - 模式:"+data02);
						btn_open.setText("关机");
					}
				}
			}
			
		};
	}
	

}
