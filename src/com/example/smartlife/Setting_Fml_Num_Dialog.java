package com.example.smartlife;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class Setting_Fml_Num_Dialog extends Dialog implements OnClickListener {

	private Context context;
	private Button btn_agree, btn_disagree;
	private Setting_Fml_Num_DialogListener listener;
	public Setting_Fml_Num_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface Setting_Fml_Num_DialogListener {
		public void onClick(View view);
	}

	public Setting_Fml_Num_Dialog(Context context, int theme,Setting_Fml_Num_DialogListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.left_fml_dialog);
		initViews();
	}

	private void initViews() {
		btn_agree = (Button) findViewById(R.id.dialog_left_fml_num_but_agree);
		btn_disagree = (Button) findViewById(R.id.dialog_left_fml_num_but_disagree);
		btn_agree.setOnClickListener(this);
		btn_disagree.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
	}
}
