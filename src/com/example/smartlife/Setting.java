package com.example.smartlife;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartlife_out.SlidingMenu;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class Setting extends Activity implements OnClickListener,OnTouchListener {

	static Activity setting;
	private SlidingMenu mMenu;
	private ImageView qrcode, img_back_left_btn;
	private RelativeLayout btn_note;
	private int mBackPressedTimes = 0;
	private RelativeLayout auto, msg, file, fml, cloud, set, bMsg;
	private TextView nic, id;
	private CircularImage cover_user_photo;
	private String img_path, img_url,Uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.left_main_titlebar);
		setting = this;
		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		Uid = sharedPreferences.getString("id", "0");
		mMenu = (SlidingMenu) findViewById(R.id.id_menu);
		// ��ť����
		img_back_left_btn = (ImageView) findViewById(R.id.left_header_left_btn);
		img_back_left_btn.setOnClickListener(this);
		// ������¼
		btn_note = (RelativeLayout) findViewById(R.id.left_header_left_btn_act);
		btn_note.setOnClickListener(this);
		// Dialog
		File temp = new File("/sdcard/SmartLife/img/");
		if (!temp.exists()) {
			temp.mkdir();
		}
		qrcode = (ImageView) findViewById(R.id.left_setting_img_2code);
		final String filePath = "/sdcard/SmartLife/img/qrcode.jpg";
		qrcode.setOnClickListener(this);
		// ͷ��
		cover_user_photo = (CircularImage) findViewById(R.id.cover_user_photo);
		img_path = "/sdcard/SmartLife/img/id_"+ Uid + "_headimg.jpg";
		img_url = App.address + "upload/img/id_"+ Uid + "_headimg.jpg";
		File imgfile = new File(img_path);
		boolean success = false;
		if (imgfile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(img_path);
			Drawable drawable = new BitmapDrawable(bitmap);
			cover_user_photo.setImageDrawable(drawable);
			success = QRCodeUtil.createQRImage("SmartLife_Uid_"+Uid, 800, 800,bitmap,filePath);  
		} else {
			cover_user_photo.setImageResource(R.drawable.people);
			success = QRCodeUtil.createQRImage("SmartLife_Uid_"+Uid, 800, 800,null,filePath);  
			new Thread(downloadImg).start();
		}
		 if (success) {  
	            runOnUiThread(new Runnable() {  
	                @Override  
	                public void run() {  
	                	qrcode.setImageBitmap(BitmapFactory.decodeFile(filePath));  
	                }  
	            });  
	        } 
		cover_user_photo.setOnClickListener(this);
		// Below
		auto = (RelativeLayout) findViewById(R.id.left_main_auto);
		msg = (RelativeLayout) findViewById(R.id.left_main_msg);
		file = (RelativeLayout) findViewById(R.id.left_main_file);
		fml = (RelativeLayout) findViewById(R.id.left_main_fml);
		cloud = (RelativeLayout) findViewById(R.id.left_main_cloud);
		set = (RelativeLayout) findViewById(R.id.left_main_set);
		bMsg = (RelativeLayout) findViewById(R.id.left_main_bMsg);

		auto.setOnTouchListener(this);
		msg.setOnTouchListener(this);
		file.setOnTouchListener(this);
		fml.setOnTouchListener(this);
		cloud.setOnTouchListener(this);
		set.setOnTouchListener(this);
		bMsg.setOnTouchListener(this);

		// ��ʼ����������
		String nic_str = sharedPreferences.getString("name", "");
		String id_str = Uid;
		nic = (TextView) findViewById(R.id.left_setting_txt_nic);
		id = (TextView) findViewById(R.id.left_setting_txt_res);
		if (!nic_str.equals("")) {
			nic.setText(nic_str);
			id.setText("ID:" + id_str);
		}
		
		new Thread(downloadImg).start();
		
	}

	public void toggleMenu(View view) {
		mMenu.toggle();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		// BACK
		case R.id.left_header_left_btn:
			Setting.this.finish();
			break;
		// ��ά��
		case R.id.left_setting_img_2code:
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.erweima);
			Setting_Dialog.Builder dialogBuild = new Setting_Dialog.Builder(this);
			dialogBuild.setImage(bitmap);
			Setting_Dialog dialog = dialogBuild.create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();

			break;
		// �໬��
		case R.id.left_header_left_btn_act:
			toggleMenu(arg0);
			break;
		// ͷ��
		case R.id.cover_user_photo:
			Intent intent_01 = new Intent(Setting.this, Setting_Mod.class);
			// Setting.this.startActivity(intent_01);
			startActivityForResult(intent_01, 1);
			break;
		default:
			break;
		}

	}

	// ��дBACK��
	@Override
	public void onBackPressed() {
		if (mBackPressedTimes == 0 && mMenu.getState() == false) {
			mMenu.openMenu();
			mBackPressedTimes = 1;
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(1500);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					} finally {
						mBackPressedTimes = 0;
					}
				};
			}.start();
			return;
		} else {
			this.finish();
		}
		super.onBackPressed();
	}

	// ���������¼�
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		// ���ܳ���
		case R.id.left_main_auto:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				auto.setBackgroundResource(R.color.backgroundColor);

			} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
				auto.setBackgroundResource(R.color.backgroundColor_low);
				
				SharedPreferences sharedPreferences_login_t = getSharedPreferences("LOGIN", 0);
				String fid_t = sharedPreferences_login_t.getString("fid", "");
				if(!fid_t.equals("0")){
					Intent intent_auto = new Intent(Setting.this,Setting_Auto.class);
					Setting.this.startActivity(intent_auto);
				}else{
					Toast.makeText(getBaseContext(), "δ�����ͥ", Toast.LENGTH_SHORT).show();
				}
				
			} else {
				auto.setBackgroundResource(R.color.backgroundColor_low);
			}
			break;
		// ��Ϣ
		case R.id.left_main_msg:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				msg.setBackgroundResource(R.color.backgroundColor);
			} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
				msg.setBackgroundResource(R.color.backgroundColor_low);
				Intent intent_msg = new Intent(Setting.this, Setting_Msg.class);
				Setting.this.startActivity(intent_msg);
			} else {
				msg.setBackgroundResource(R.color.backgroundColor_low);
			}
			break;
		// ������Ϣ
		case R.id.left_main_bMsg:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				bMsg.setBackgroundResource(R.color.backgroundColor);
			} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
				bMsg.setBackgroundResource(R.color.backgroundColor_low);
				Intent intent_backMsg =new Intent(Setting.this,Setting_BackMsg.class);
				Setting.this.startActivity(intent_backMsg);
			} else {
				bMsg.setBackgroundResource(R.color.backgroundColor_low);
			}
			break;
		// ����
		case R.id.left_main_set:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				set.setBackgroundResource(R.color.backgroundColor);
			} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
				set.setBackgroundResource(R.color.backgroundColor_low);
				Intent intent_set = new Intent(Setting.this,
						Setting_Setting.class);
				Setting.this.startActivity(intent_set);
			} else{
				set.setBackgroundResource(R.color.backgroundColor_low);
			}
			break;
		// �Ʒ���
		case R.id.left_main_cloud:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				cloud.setBackgroundResource(R.color.backgroundColor);
			} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
				cloud.setBackgroundResource(R.color.backgroundColor_low);
				Toast.makeText(getBaseContext(), "δ�洢���ļ�", Toast.LENGTH_SHORT)
						.show();
			} else {
				cloud.setBackgroundResource(R.color.backgroundColor_low);
			}
			break;
		// �����ļ�
		case R.id.left_main_file:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				file.setBackgroundResource(R.color.backgroundColor);
			} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
				file.setBackgroundResource(R.color.backgroundColor_low);
				//Toast.makeText(getBaseContext(), "δ�洢�����ļ�", Toast.LENGTH_SHORT).show();
				Intent intentfile = new Intent(Setting.this, Setting_File_Main.class);
				Setting.this.startActivity(intentfile);
			} else {
				file.setBackgroundResource(R.color.backgroundColor_low);
			}
			break;
		// ��ͥ��Ա
		case R.id.left_main_fml:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				fml.setBackgroundResource(R.color.backgroundColor);
			} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
				fml.setBackgroundResource(R.color.backgroundColor_low);
				//msg.setBackgroundResource(R.color.backgroundColor_low);
				SharedPreferences sharedPreferences_lo = getSharedPreferences("LOGIN", 0);
				String fid = sharedPreferences_lo.getString("fid", "");
				Intent intent_fml;
				if(fid.equals("0")){
					intent_fml = new Intent(Setting.this, Setting_Fml.class);
				}else{
					intent_fml = new Intent(Setting.this, Setting_Fml_num.class);
				}
				Setting.this.startActivity(intent_fml);
			} else {
				fml.setBackgroundResource(R.color.backgroundColor_low);
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		String mod_temp = data.getExtras().getString("mod_temp");
		if (mod_temp.equals("1")) {
			SharedPreferences sharedPreferences = getSharedPreferences("LOGIN",
					0);
			img_path = "/sdcard/SmartLife/img/id_"+ Uid + "_headimg.jpg";
			File imgfile = new File(img_path);
			if (imgfile.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(img_path);
				Drawable drawable = new BitmapDrawable(bitmap);
				cover_user_photo.setImageDrawable(drawable);

			} else {
				cover_user_photo.setImageResource(R.drawable.people);
			}
			// ��ʼ����������
			String nic_str = sharedPreferences.getString("name", "");
			String id_str = Uid;
			nic = (TextView) findViewById(R.id.left_setting_txt_nic);
			id = (TextView) findViewById(R.id.left_setting_txt_res);
			if (!nic_str.equals("")) {
				nic.setText(nic_str);
				id.setText("ID:" + id_str);
			}
		}
	}

	Runnable downloadImg = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// ������������
			try {
				//Log.e("111", "ͬ��ͷ��");		
				File f = new File(img_path);
				if (!f.exists()) {
				//	Log.e("111", "ͬ��ͷ��-����");
					URL url = new URL(img_url);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream inputStream = conn.getInputStream();
					try {
						FileOutputStream out = new FileOutputStream(f);
						BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 90, out);
						out.flush();
						out.close();
						Message msg = new Message();
						msg.what = 3;
						updateUI.sendMessage(msg);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	// ���̸߳���UI
	Handler updateUI = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getBaseContext(), "�ϴ��ɹ�", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(getBaseContext(), "�ϴ�ʧ��", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				try {
					Bitmap bitmap = BitmapFactory.decodeFile(img_path);
					Drawable drawable = new BitmapDrawable(bitmap);
					cover_user_photo.setImageDrawable(drawable);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	 /** 
     * ��ά�����ɹ����� 
     */  
    public static class QRCodeUtil {  
        /** 
         * ���ɶ�ά��Bitmap 
         * 
         * @param content   ���� 
         * @param widthPix  ͼƬ��� 
         * @param heightPix ͼƬ�߶� 
         * @param logoBm    ��ά�����ĵ�Logoͼ�꣨����Ϊnull�� 
         * @param filePath  ���ڴ洢��ά��ͼƬ���ļ�·�� 
         * @return ���ɶ�ά�뼰�����ļ��Ƿ�ɹ� 
         */  
        public static boolean createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm, String filePath) {  
            //content = "SmartLife_Uid_"+Uid;
        	try {  
                if (content == null || "".equals(content)) {  
                    return false;  
                }  
      
                //���ò���  
                Map<EncodeHintType, Object> hints = new HashMap<>();  
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
                //�ݴ���  
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);  
                //���ÿհױ߾�Ŀ��  
                hints.put(EncodeHintType.MARGIN, 1); //default is 4  
      
                // ͼ������ת����ʹ���˾���ת��  
                BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);  
                int[] pixels = new int[widthPix * heightPix];  
                // �������ﰴ�ն�ά����㷨��������ɶ�ά���ͼƬ��  
                // ����forѭ����ͼƬ����ɨ��Ľ��  
                for (int y = 0; y < heightPix; y++) {  
                    for (int x = 0; x < widthPix; x++) {  
                        if (bitMatrix.get(x, y)) {  
                            pixels[y * widthPix + x] = 0xff000000;  
                        } else {  
                            pixels[y * widthPix + x] = 0xffffffff;  
                        }  
                    }  
                }  
      
                // ���ɶ�ά��ͼƬ�ĸ�ʽ��ʹ��ARGB_8888  
                Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);  
                bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);  
      
                if (logoBm != null) {  
                    bitmap = addLogo(bitmap, logoBm);  
                }  
      
                //����ʹ��compress������bitmap���浽�ļ����ٽ��ж�ȡ��ֱ�ӷ��ص�bitmap��û���κ�ѹ���ģ��ڴ����ľ޴�  
                return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));  
            } catch (WriterException | IOException e) {  
                e.printStackTrace();  
            }  
      
            return false;  
        }  
      
        /** 
         * �ڶ�ά���м����Logoͼ�� 
         */  
        private static Bitmap addLogo(Bitmap src, Bitmap logo) {  
            if (src == null) {  
                return null;  
            }  
      
            if (logo == null) {  
                return src;  
            }  
      
            //��ȡͼƬ�Ŀ��  
            int srcWidth = src.getWidth();  
            int srcHeight = src.getHeight();  
            int logoWidth = logo.getWidth();  
            int logoHeight = logo.getHeight();  
      
            if (srcWidth == 0 || srcHeight == 0) {  
                return null;  
            }  
      
            if (logoWidth == 0 || logoHeight == 0) {  
                return src;  
            }  
      
            //logo��СΪ��ά�������С��1/5  
            float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;  
            Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);  
            try {  
                Canvas canvas = new Canvas(bitmap);  
                canvas.drawBitmap(src, 0, 0, null);  
                canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);  
                canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);  
      
                canvas.save(Canvas.ALL_SAVE_FLAG);  
                canvas.restore();  
            } catch (Exception e) {  
                bitmap = null;  
                e.getStackTrace();  
            }  
      
            return bitmap;  
        }  
      
    }  
}
