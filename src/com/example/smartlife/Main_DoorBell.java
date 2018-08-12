package com.example.smartlife;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.example.smartlife.MainDoorBell_Dialog.MainDoorBell_DialogListener;

public class Main_DoorBell extends Activity implements OnClickListener, OnTouchListener {

	private static Context context = null;
/*	private SurfaceView surfaceview;
	private SurfaceHolder surfaceholder;
	private Camera camera = null;*/
	private ImageView back, img_01, img_02, img_top, img_a_01, img_a_02,img_a_03;;
	private RelativeLayout img_ab_01, img_ab_02, img_ab_03;
/*
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		// 获取对象
		camera = Camera.open();
		try {
			// 设置预览监听
			camera.setPreviewDisplay(arg0);
			Camera.Parameters parameters = camera.getParameters();
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "lanscape");
				camera.setDisplayOrientation(90);
				parameters.setRotation(90);
			} else {
				parameters.set("orientation", "lanscape");
				camera.setDisplayOrientation(0);
				parameters.setRotation(0);
			}
			camera.setParameters(parameters);
			// 启动摄像头预览
			camera.startPreview();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			camera.release();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}

	}

*/	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	//	setContentView(R.layout.main_doorbell);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.main_doorbell_titlebar);

		context = this;
		/*surfaceview = (SurfaceView) findViewById(R.id.main_doorbell_surfaceview);
		surfaceholder = surfaceview.getHolder();
		surfaceholder.setType(surfaceholder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceholder.addCallback(this);
*/
				
		Uri uri = Uri.parse(App.moniAddress);  
     //   VideoView videoView = (VideoView)this.findViewById(R.id.main_doorbell_surfaceview);  
        //videoView.setMediaController(new MediaController(this));//控制条
       // videoView.setOnErrorListener(videoErrorListener); //报错处理
        //videoView.setVideoURI(uri);  
        //videoView.start();//自动播放
        //videoView.requestFocus();  
        Log.e("111", ""+App.moniAddress);
		// titlebar
		back = (ImageView) findViewById(R.id.main_doorbell_btn_back);
		back.setOnClickListener(this);

		// main
		img_01 = (ImageView) findViewById(R.id.main_doorbell_img_01);
		img_01.setOnTouchListener(this);
		img_02 = (ImageView) findViewById(R.id.main_doorbell_img_02);
		img_02.setOnTouchListener(this);

		img_ab_01 = (RelativeLayout) findViewById(R.id.main_doorbell_above_img_re01);
		img_ab_01.setOnTouchListener(this);
		img_ab_02 = (RelativeLayout) findViewById(R.id.main_doorbell_above_img_re02);
		img_ab_02.setOnTouchListener(this);
		img_ab_03 = (RelativeLayout) findViewById(R.id.main_doorbell_above_img_re03);
		img_ab_03.setOnTouchListener(this);
		img_a_01 = (ImageView) findViewById(R.id.main_doorbell_above_img_01);
		img_a_02 = (ImageView) findViewById(R.id.main_doorbell_above_img_02);
		img_a_03 = (ImageView) findViewById(R.id.main_doorbell_above_img_03);
		img_top = (ImageView) findViewById(R.id.main_doorbell_top_img);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_doorbell_btn_back:
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
		// 底部
		case R.id.main_doorbell_img_01:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_01.setAlpha(160);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_01.setAlpha(255);
			}
			break;
		case R.id.main_doorbell_img_02:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_02.setAlpha(160);
				img_top.setImageResource(R.drawable.ic_unprotect_state);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_02.setAlpha(255);
				img_top.setImageResource(R.drawable.ic_protect_state);
			}
			break;
		// 中部
		case R.id.main_doorbell_above_img_re01:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_a_01.setImageResource(R.drawable.ic_video_take_photo_pressed);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_a_01.setImageResource(R.drawable.ic_video_take_photo_normal);
			}

			break;
		case R.id.main_doorbell_above_img_re02:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_a_02.setImageResource(R.drawable.ic_video_talk_selected);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_a_02.setImageResource(R.drawable.ic_video_talk_normal);
			}
			break;
		case R.id.main_doorbell_above_img_re03:
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
