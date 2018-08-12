package com.example.smartlife;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class Device_Dialog extends Dialog implements
		OnClickListener {

	private Context context;
	private Button btn_cancle, btn_ok;
	private Device_Dialog_DialogListener listener;

	public Device_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface Device_Dialog_DialogListener {
		public void onClick(View view);
	}

	public Device_Dialog(Context context, int theme,Device_Dialog_DialogListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_device);
		initViews();
	}

	private void initViews() {
		btn_cancle = (Button) findViewById(R.id.dialog_device_but_cancle);
		btn_ok = (Button) findViewById(R.id.dialog_device_but_ok);
		btn_cancle.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
	}
}
