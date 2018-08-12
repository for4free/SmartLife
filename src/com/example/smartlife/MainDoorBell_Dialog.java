package com.example.smartlife;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MainDoorBell_Dialog extends Dialog implements OnClickListener {

	private Context context;
	private Button btn_open;
	private MainDoorBell_DialogListener  listener;
	public MainDoorBell_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface MainDoorBell_DialogListener {
		public void onClick(View view);
	}

	public MainDoorBell_Dialog(Context context, int theme,MainDoorBell_DialogListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_maindoorbell);
		initViews();
	}

	private void initViews() {
		btn_open = (Button) findViewById(R.id.dialog_maindoorbell_but_open);
		btn_open.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
	}
}
