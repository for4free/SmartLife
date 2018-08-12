package com.example.smartlife;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

public class Setting_Mod_Dialog extends Dialog implements OnClickListener {

	private Context context;
	private RelativeLayout photo, camera;
	private Setting_Mod_DialogListener listener;

	public Setting_Mod_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface Setting_Mod_DialogListener {
		public void onClick(View view);
	}

	public Setting_Mod_Dialog(Context context, int theme,
			Setting_Mod_DialogListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_mod_nic);
		initViews();
	}

	private void initViews() {
		photo = (RelativeLayout) findViewById(R.id.dialog_mod_img_photo);
		camera = (RelativeLayout) findViewById(R.id.dialog_mod_img_camera);
		photo.setOnClickListener(this);
		camera.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
	}

}
