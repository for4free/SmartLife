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

		// 启动画面
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

		File temp = new File("/sdcard/SmartLife/");// 创建文件夹
		if (!temp.exists()) {
			temp.mkdir();
		}

		final SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		String name = sharedPreferences.getString("name", "");
		String pass = sharedPreferences.getString("pass", "");
		final Editor editor = sharedPreferences.edit();
		mark = sharedPreferences.getString("mark", "");
		String path = App.address + "login.php?name=" + name + "&pass=" + pass;
		HttpUtils http = new HttpUtils(10000);//10s超时
		http.configCurrentHttpCacheExpiry(5000); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。
		
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
				// 加载数据 转化为JSON格式{"status":
				
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
								UpdateManger mUpdateManger = new UpdateManger(LoadActivity.this);// 注意此处不能传入getApplicationContext();会报错，因为只有是一个Activity才可以添加窗体
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
				Toast.makeText(getBaseContext(), "网络问题,请检查网络重新登陆", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};*/
	
	
//获取设置信息
	public void getInfo() {

		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		String id = sharedPreferences.getString("id", "");
		final Editor editor = sharedPreferences.edit();
		String path = App.address + "GetInfo.php?tidORuid=" + id;
		HttpUtils http = new HttpUtils(10000);//10s超时
		http.configCurrentHttpCacheExpiry(5000); // 设置缓存5秒,5秒内直接返回上次成功请求的结果。

		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// 加载数据 转化为JSON格式
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
	
	
	
	//版本名  
	public static String getVersionName(Context context) {  
	    return getPackageInfo(context).versionName;  
	}  
	//版本号  
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
		 // 应用程序Context
		 private Context mContext;
		 // 提示消息
		 private String updateMsg = "有最新的软件包，请下载！";
		 // 下载安装包的网络路径
		 private String apkUrl = "http://2naive.cn/update/SmartLife_"+version+".apk";
		 private Dialog noticeDialog;// 提示有软件更新的对话框
		 private Dialog downloadDialog;// 下载对话框
		 private static final String savePath = "/sdcard/updatedemo/";// 保存apk的文件夹
		 private static final String saveFileName = savePath + "UpdateDemoRelease.apk";
		 // 进度条与通知UI刷新的handler和msg常量
		 private ProgressBar mProgress;
		 private static final int DOWN_UPDATE = 1;
		 private static final int DOWN_OVER = 2;

		 private int progress;// 当前进度
		 private Thread downLoadThread; // 下载线程
		 private boolean interceptFlag = false;// 用户取消下载
		 // 通知处理刷新界面的handler
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
		 // 显示更新程序对话框，供主程序调用
		 public void checkUpdateInfo() {
		  showNoticeDialog();
		 }
		 private void showNoticeDialog() {
		  android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
		    mContext);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
		  builder.setTitle("软件版本更新");
		  builder.setMessage(updateMsg);
		  builder.setPositiveButton("下载", new OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
		    showDownloadDialog();
		   }
		  });
		  builder.setNegativeButton("以后再说", new OnClickListener() {
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
		  builder.setTitle("软件版本更新");
		  final LayoutInflater inflater = LayoutInflater.from(mContext);
		  View v = inflater.inflate(R.layout.progress, null);

		  mProgress = (ProgressBar) v.findViewById(R.id.progress);
		  builder.setView(v);// 设置对话框的内容为一个View
		  builder.setNegativeButton("取消", new OnClickListener() {
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
		  i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");// File.toString()会返回路径信息
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
		     // 下载进度
		     mHandler.sendEmptyMessage(DOWN_UPDATE);
		     if (numread <= 0) {
		      // 下载完成通知安装
		      mHandler.sendEmptyMessage(DOWN_OVER);
		      break;
		     }
		     outStream.write(buf, 0, numread);
		    } while (!interceptFlag);// 点击取消停止下载
		    outStream.close();
		    ins.close();
		   } catch (Exception e) {
		    e.printStackTrace();
		   }
		  }
		 };
		}
	
}
