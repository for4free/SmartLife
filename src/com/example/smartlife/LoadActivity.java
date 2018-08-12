package com.example.smartlife;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;
import com.example.smartlife_api.GetInfo;
import com.example.smartlife_api.Login_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class LoadActivity extends Activity {

	private String mark;
	private String version;

	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// ��������
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		setContentView(R.layout.load);

		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo("com.example.smartlife", 0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getInfo();
				login();
			}
		}, 1000);

	}

	public void login() {

		File temp = new File("/sdcard/SmartLife/");// �����ļ���
		if (!temp.exists()) {
			temp.mkdir();
		}

		final SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		String name = sharedPreferences.getString("name", "");
		String pass = sharedPreferences.getString("pass", "");
		final Editor editor = sharedPreferences.edit();
		mark = sharedPreferences.getString("mark", "");
		String path = App.address + "login.php?name=" + name + "&pass=" + pass;
		HttpUtils http = new HttpUtils(10000);//10s��ʱ
		http.configCurrentHttpCacheExpiry(5000); // ���û���5��,5����ֱ�ӷ����ϴγɹ�����Ľ����
		
		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {
			
			Intent mainIntent = new Intent(LoadActivity.this, SmartLife.class);
			Intent loginIntent = new Intent(LoadActivity.this, Login.class);
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				editor.putString("conn", "0");
				editor.commit();
				if (mark.equals("1")) {
					LoadActivity.this.startActivity(mainIntent);
				} else {
					LoadActivity.this.startActivity(loginIntent);
				}
				LoadActivity.this.finish();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// �������� ת��ΪJSON��ʽ{"status":
				
				String result_t = arg0.result.toString().substring(0, 10);
				
				if(result_t.equals("{\"status\":")){
					Login_Api content = (Login_Api) JSONObject.parseObject(arg0.result, Login_Api.class);
					String versionCode = getVersionName(getBaseContext())+"";
					version = content.getVs().toString();
					editor.putString("conn", "1");
					editor.commit();
					if (mark.equals("1")) {
						if (content.getStatus().equals("102")|| content.getStatus().equals("101")) {
							LoadActivity.this.startActivity(loginIntent);
							LoadActivity.this.finish();
						} else if (content.getStatus().equals("103")) {
							editor.putString("easepass", MD5(content.getId().toString()));
							if(content.getFid().equals("a")&&content.getFid_t().equals("a")){
								editor.putString("fid", "0");
							}else{
								editor.putString("fid", content.getFid().toString());
							}
							editor.commit();
							
							if(!versionCode.equals(version)){
								UpdateManger mUpdateManger = new UpdateManger(LoadActivity.this);// ע��˴����ܴ���getApplicationContext();�ᱨ����Ϊֻ����һ��Activity�ſ�����Ӵ���
								mUpdateManger.checkUpdateInfo();
							}else{
								LoadActivity.this.startActivity(mainIntent);
								LoadActivity.this.finish();
							}
							
						}
					} else {
						LoadActivity.this.startActivity(loginIntent);
						LoadActivity.this.finish();
					}
				}else{
					/*Message message = new Message();
					message.what = 1;
					updateUI.sendMessage(message);
					//LoadActivity.this.finish();
*/				}
			}

		});

	}
	
/*	Handler updateUI = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getBaseContext(), "��������,�����������µ�½", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};*/
	
	
//��ȡ������Ϣ
	public void getInfo() {

		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		String id = sharedPreferences.getString("id", "");
		final Editor editor = sharedPreferences.edit();
		String path = App.address + "GetInfo.php?tidORuid=" + id;
		HttpUtils http = new HttpUtils(10000);//10s��ʱ
		http.configCurrentHttpCacheExpiry(5000); // ���û���5��,5����ֱ�ӷ����ϴγɹ�����Ľ����

		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// �������� ת��ΪJSON��ʽ
				GetInfo content = (GetInfo) JSONObject.parseObject(arg0.result, GetInfo.class);
				if(content.getStatus().equals("403")){
					editor.putString("lon", content.getLon());
					editor.putString("lat", content.getLat());
					editor.commit();
				}

			}

		});

	}
	

	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = (md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	
	
	
	//�汾��  
	public static String getVersionName(Context context) {  
	    return getPackageInfo(context).versionName;  
	}  
	//�汾��  
	public static int getVersionCode(Context context) {  
	    return getPackageInfo(context).versionCode;  
	}  

	private static PackageInfo getPackageInfo(Context context) {  
	    PackageInfo pi = null;  

	    try {  
	        PackageManager pm = context.getPackageManager();  
	        pi = pm.getPackageInfo(context.getPackageName(),  
	                PackageManager.GET_CONFIGURATIONS);  

	        return pi;  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  

	    return pi;  
	}

	
	
	public class UpdateManger {
		 // Ӧ�ó���Context
		 private Context mContext;
		 // ��ʾ��Ϣ
		 private String updateMsg = "�����µ�������������أ�";
		 // ���ذ�װ��������·��
		 private String apkUrl = "http://2naive.cn/update/SmartLife_"+version+".apk";
		 private Dialog noticeDialog;// ��ʾ��������µĶԻ���
		 private Dialog downloadDialog;// ���ضԻ���
		 private static final String savePath = "/sdcard/updatedemo/";// ����apk���ļ���
		 private static final String saveFileName = savePath + "UpdateDemoRelease.apk";
		 // ��������֪ͨUIˢ�µ�handler��msg����
		 private ProgressBar mProgress;
		 private static final int DOWN_UPDATE = 1;
		 private static final int DOWN_OVER = 2;

		 private int progress;// ��ǰ����
		 private Thread downLoadThread; // �����߳�
		 private boolean interceptFlag = false;// �û�ȡ������
		 // ֪ͨ����ˢ�½����handler
		 private Handler mHandler = new Handler() {
		  @SuppressLint("HandlerLeak")
		  @Override
		  public void handleMessage(Message msg) {
		   switch (msg.what) {
		   case DOWN_UPDATE:
		    mProgress.setProgress(progress);
		    break;
		   case DOWN_OVER:
		    installApk();
		    break;
		   }
		   super.handleMessage(msg);
		  }
		 };
		 public UpdateManger(Context context) {
		  this.mContext = context;
		 }
		 // ��ʾ���³���Ի��򣬹����������
		 public void checkUpdateInfo() {
		  showNoticeDialog();
		 }
		 private void showNoticeDialog() {
		  android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
		    mContext);// Builder������ͨ����builder���øı�AleartDialog��Ĭ�ϵ�������ʽ�����������Ϣ
		  builder.setTitle("����汾����");
		  builder.setMessage(updateMsg);
		  builder.setPositiveButton("����", new OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();// ��ȡ���Ի������в���һ���Ĵ��룿ȡ���Ի���
		    showDownloadDialog();
		   }
		  });
		  builder.setNegativeButton("�Ժ���˵", new OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    Intent mainIntent = new Intent(LoadActivity.this, SmartLife.class);
		    LoadActivity.this.startActivity(mainIntent);
			LoadActivity.this.finish();
		   }
		  });
		  noticeDialog = builder.create();
		  noticeDialog.show();
		  noticeDialog.setCanceledOnTouchOutside(false);
		 }
		 protected void showDownloadDialog() {
		  android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
		  builder.setTitle("����汾����");
		  final LayoutInflater inflater = LayoutInflater.from(mContext);
		  View v = inflater.inflate(R.layout.progress, null);

		  mProgress = (ProgressBar) v.findViewById(R.id.progress);
		  builder.setView(v);// ���öԻ��������Ϊһ��View
		  builder.setNegativeButton("ȡ��", new OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    interceptFlag = true;
		    Intent mainIntent = new Intent(LoadActivity.this, SmartLife.class);
		    LoadActivity.this.startActivity(mainIntent);
			LoadActivity.this.finish();
		   }
		  });
		  downloadDialog = builder.create();
		  downloadDialog.show();
		  downloadDialog.setCanceledOnTouchOutside(false);
		  downloadApk();
		 }
		 private void downloadApk() {
		  downLoadThread = new Thread(mdownApkRunnable);
		  downLoadThread.start();
		 }
		 protected void installApk() {
		  File apkfile = new File(saveFileName);
		  if (!apkfile.exists()) {
		   return;
		  }
		  Intent i = new Intent(Intent.ACTION_VIEW);
		  i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");// File.toString()�᷵��·����Ϣ
		  mContext.startActivity(i);
		 }
		 private Runnable mdownApkRunnable = new Runnable() {
		  @Override
		  public void run() {
		   URL url;
		   try {
		    url = new URL(apkUrl);
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.connect();
		    int length = conn.getContentLength();
		    InputStream ins = conn.getInputStream();

		    File file = new File(savePath);
		    if (!file.exists()) {
		     file.mkdir();
		    }
		    String apkFile = saveFileName;
		    File ApkFile = new File(apkFile);
		    FileOutputStream outStream = new FileOutputStream(ApkFile);
		    int count = 0;
		    byte buf[] = new byte[1024];
		    do {
		     int numread = ins.read(buf);
		     count += numread;
		     progress = (int) (((float) count / length) * 100);
		     // ���ؽ���
		     mHandler.sendEmptyMessage(DOWN_UPDATE);
		     if (numread <= 0) {
		      // �������֪ͨ��װ
		      mHandler.sendEmptyMessage(DOWN_OVER);
		      break;
		     }
		     outStream.write(buf, 0, numread);
		    } while (!interceptFlag);// ���ȡ��ֹͣ����
		    outStream.close();
		    ins.close();
		   } catch (Exception e) {
		    e.printStackTrace();
		   }
		  }
		 };
		}
	
}
