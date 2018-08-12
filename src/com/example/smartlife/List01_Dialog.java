package com.example.smartlife;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class List01_Dialog extends Dialog implements OnClickListener {

	private Context context;
	private Button btn_open, btn_close,suspend;
	private List01_DialogListener  listener;
	private TextView txt;
	private String newdata;
	private String name;
	public List01_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface List01_DialogListener {
		public void onClick(View view);
	}

	public List01_Dialog(String name,String newdata,Context context, int theme,List01_DialogListener listener) {
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
		this.setContentView(R.layout.dialog_list01);
		initViews();
	}

	private void initViews() {
		btn_open = (Button) findViewById(R.id.dialog_list01_but_open);
		//btn_close = (Button) findViewById(R.id.dialog_list01_but_close);
		txt = (TextView) findViewById(R.id.dialog_list01_txt);
		
		if(newdata.equals("0")){
			txt.setText(name+" 已经关闭");
			btn_open.setText("打开");
		}else{
			txt.setText(name+" 已经打开");
			btn_open.setText("关闭");
		}
		btn_open.setOnClickListener(this);
		//btn_close.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
	}
}
