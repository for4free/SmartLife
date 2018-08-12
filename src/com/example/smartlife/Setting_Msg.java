package com.example.smartlife;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Setting_Msg extends FragmentActivity implements OnGestureListener,
		OnClickListener {

	public static Fragment[] fragments;
	public static RelativeLayout[] relativelayout;
	public static TextView[] textviews;
	public static ImageView[] imgs;
	private ImageView img_back;
	// 定义手势监测实例
	public static GestureDetector detector;

	// 标签 当前的Fragment
	public int MARK = 0;
	// 手势两点间的最小距离
	final int DISTANT = 50;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.left_msg);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.left_msg_fragment_titlebar);
		setfragment();
		setrelativelayout();
		settextviews();
		// 手势
		detector = new GestureDetector(this, this);

		// TitleBar
		img_back = (ImageView) findViewById(R.id.left_msg_btn_back);
		img_back.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main_mean, menu);

		return true;
	}

	public void setfragment() {

		fragments = new Fragment[2];
		fragments[0] = getSupportFragmentManager().findFragmentById(
				R.id.left_msg_fragment_warning);
		fragments[1] = getSupportFragmentManager().findFragmentById(
				R.id.left_msg_fragment_sys);
		getSupportFragmentManager().beginTransaction().hide(fragments[1])
				.show(fragments[0]).commit();

	}

	public void setrelativelayout() {
		relativelayout = new RelativeLayout[2];
		relativelayout[0] = (RelativeLayout) findViewById(R.id.left_msg_fragment_title_left);
		relativelayout[1] = (RelativeLayout) findViewById(R.id.left_msg_fragment_title_right);
		relativelayout[0].setOnClickListener(this);
		relativelayout[1].setOnClickListener(this);
	}

	public void settextviews() {
		textviews = new TextView[2];
		textviews[0] = (TextView) findViewById(R.id.left_msg_fragment_title_left_txt);
		textviews[1] = (TextView) findViewById(R.id.left_msg_fragment_title_right_txt);
		imgs = new ImageView[2];
		imgs[0] = (ImageView) findViewById(R.id.left_msg_fragment_title_left_line);
		imgs[1] = (ImageView) findViewById(R.id.left_msg_fragment_title_right_line);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODOAuto-generated method stub
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODOAuto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) { // TODO Auto-generated method stub
		if (MARK == 0) {
			if (arg0.getX() > arg1.getX() + DISTANT) {
				getSupportFragmentManager().beginTransaction()
						.hide(fragments[0]).show(fragments[1]).commit();
				textviews[1].setTextColor(Color.rgb(65, 175, 175));
				textviews[0].setTextColor(Color.rgb(0, 0, 0));
				imgs[0].setVisibility(View.INVISIBLE);
				imgs[1].setVisibility(View.VISIBLE);
				MARK = 1;
			}
		} else if (MARK == 1) {
			if (arg1.getX() > arg0.getX() + DISTANT) {
				getSupportFragmentManager().beginTransaction()
						.hide(fragments[1]).show(fragments[0]).commit();
				textviews[0].setTextColor(Color.rgb(65, 175, 175));
				textviews[1].setTextColor(Color.rgb(0, 0, 0));
				imgs[1].setVisibility(View.INVISIBLE);
				imgs[0].setVisibility(View.VISIBLE);
				MARK = 0;
			}
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODOAuto-generated method stub
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_msg_fragment_title_left:
			getSupportFragmentManager().beginTransaction().hide(fragments[1])
					.show(fragments[0]).commit();
			textviews[0].setTextColor(Color.rgb(65, 175, 175));
			textviews[1].setTextColor(Color.rgb(0, 0, 0));
			imgs[1].setVisibility(View.INVISIBLE);
			imgs[0].setVisibility(View.VISIBLE);
			MARK = 0;
			break;
		case R.id.left_msg_fragment_title_right:
			getSupportFragmentManager().beginTransaction().hide(fragments[0])
					.show(fragments[1]).commit();
			textviews[1].setTextColor(Color.rgb(65, 175, 175));
			textviews[0].setTextColor(Color.rgb(0, 0, 0));
			imgs[0].setVisibility(View.INVISIBLE);
			imgs[1].setVisibility(View.VISIBLE);
			MARK = 1;
			break;
		case R.id.left_msg_btn_back:
			this.finish();
			break;
		default:
			break;
		}

	}

}
