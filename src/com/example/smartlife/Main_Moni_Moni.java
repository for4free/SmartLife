package com.example.smartlife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.smartlife.MainDoorBell_Dialog.MainDoorBell_DialogListener;

public class Main_Moni_Moni extends Activity implements OnClickListener, OnTouchListener {

	private static Context context = null;
	private ImageView back, img_01, img_02, img_top, img_a_01, img_a_02,img_a_03;;
	private RelativeLayout img_ab_01, img_ab_02, img_ab_03;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_moni_moni);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.main_moni_moni_titlebar);

		context = this;
		Uri uri = Uri.parse(App.moniAddress);  
        VideoView videoView = (VideoView)this.findViewById(R.id.main_moni_t_surfaceview);  
        //videoView.setMediaController(new MediaController(this));//控制条
        videoView.setOnErrorListener(videoErrorListener); //报错处理
        videoView.setVideoURI(uri);  
        videoView.start();//自动播放
        videoView.requestFocus();  
        
        
		// titlebar
		back = (ImageView) findViewById(R.id.main_moni_t_btn_back);
		back.setOnClickListener(this);

		// main
	

		img_ab_01 = (RelativeLayout) findViewById(R.id.main_moni_t_above_img_re01);
		img_ab_01.setOnTouchListener(this);
		//img_ab_02 = (RelativeLayout) findViewById(R.id.main_moni_t_above_img_re02);
		//img_ab_02.setOnTouchListener(this);
		img_ab_03 = (RelativeLayout) findViewById(R.id.main_moni_t_above_img_re03);
		img_ab_03.setOnTouchListener(this);
		img_a_01 = (ImageView) findViewById(R.id.main_moni_t_above_img_01);
		//img_a_02 = (ImageView) findViewById(R.id.main_moni_t_above_img_02);
		img_a_03 = (ImageView) findViewById(R.id.main_moni_t_above_img_03);
		img_top = (ImageView) findViewById(R.id.main_moni_t_top_img);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_moni_t_btn_back:
			this.finish();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		// 中部
		case R.id.main_moni_t_above_img_re01:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_a_01.setImageResource(R.drawable.ic_video_take_photo_pressed);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_a_01.setImageResource(R.drawable.ic_video_take_photo_normal);
			}

			break;
		/*case R.id.main_moni_t_above_img_re02:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_a_02.setImageResource(R.drawable.ic_video_talk_selected);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_a_02.setImageResource(R.drawable.ic_video_talk_normal);
			}
			break;*/
		case R.id.main_moni_t_above_img_re03:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_a_03.setImageResource(R.drawable.ic_video_record_selected);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_a_03.setImageResource(R.drawable.ic_video_record_normal);
			}
			break;

		default:
			break;
		}

		return false;
	}
	
	// 播放出错处理  
    public OnErrorListener videoErrorListener = new OnErrorListener() {  
        @Override  
        public boolean onError(MediaPlayer mp, int what, int extra) {  
            
           // …… 
        MainDoorBell_Dialog dialog = new MainDoorBell_Dialog(context, R.style.Dialog, new MainDoorBell_DialogListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.dialog_maindoorbell_but_open:
					onBackPressed();
					break;
				default:
					break;
				}
			}
		});
		dialog.show(); 
         return true;  
        }  
    };  
}
