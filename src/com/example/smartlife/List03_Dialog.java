package com.example.smartlife;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class List03_Dialog extends Dialog implements OnClickListener {

	private Context context;
	private Button btn_open,btn_0101,btn_0102,btn_0201,btn_0202;
	private List03_DialogListener  listener;
	private TextView txt;
	private String newdata;
	private String name;
	public List03_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface List03_DialogListener {
		public void onClick(View view);
	}

	public List03_Dialog(String name,String newdata,Context context, int theme,List03_DialogListener listener) {
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
		this.setContentView(R.layout.dialog_list03);
		initViews();
	}

	private void initViews() {
		btn_open = (Button) findViewById(R.id.dialog_list03_power);
		//btn_close = (Button) findViewById(R.id.dialog_list01_but_close);
		btn_0101 = (Button) findViewById(R.id.dialog_list03_channel01);
		btn_0102 = (Button) findViewById(R.id.dialog_list03_channel02);
		btn_0201 = (Button) findViewById(R.id.dialog_list03_idvolume01);
		btn_0202 = (Button) findViewById(R.id.dialog_list03_idvolume02);
		txt = (TextView) findViewById(R.id.dialog_list03_txt);
		String[] dataSplit = newdata.split("_");
		if(dataSplit[0].equals("0")){
			txt.setText(name+" 已经关闭");
			btn_open.setText("开机");
		}else{
			txt.setText(name+" 音量:"+dataSplit[1]+" - 频道:"+dataSplit[2]);
			btn_open.setText("关机");
		}
		btn_open.setOnClickListener(this);
		btn_0101.setOnClickListener(this);
		btn_0102.setOnClickListener(this);
		btn_0201.setOnClickListener(this);
		btn_0202.setOnClickListener(this);
		//btn_close.setOnClickListener(this);
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
						txt.setText(name+" 音量:"+data01+" - 频道:"+data02);
						btn_open.setText("关机");
					}
				}
			}
			
		};
	}
}
