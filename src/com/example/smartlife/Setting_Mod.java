package com.example.smartlife;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.Setting_Mod_Dialog.Setting_Mod_DialogListener;
import com.example.smartlife_api.Login_Api;
import com.example.smartlife_out.Tools;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Setting_Mod extends Activity implements OnClickListener,OnTouchListener {

	static Activity setting_mod;
	private ImageView back;
	private EditText edittext;
	private Button mod_btn;
	private RelativeLayout mod_pass;
	private TextView nic, id;
	private String nic_str, img_path, imgUrl;
	private CircularImage user_photo;
	private Setting_Mod_Dialog dialog;
	private int mod_img = 0, mod_name = 0;
	/* ͷ������ */
	private static final String IMAGE_FILE_NAME = "_tempImage.jpg";
	/* ������ */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.left_mod);
		setting_mod = this;
		// ���ذ�ť
		back = (ImageView) findViewById(R.id.left_mod_title_btn);
		back.setOnClickListener(this);
		// �޸�ͷ��
		user_photo = (CircularImage) findViewById(R.id.left_cover_user_photo);
		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		img_path = "/sdcard/SmartLife/img/id_" + sharedPreferences.getString("id", "") + "_headimg.jpg";
		File imgfile = new File(img_path);
		if (imgfile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(img_path);
			Drawable drawable = new BitmapDrawable(bitmap);
			user_photo.setImageDrawable(drawable);
		} else {
			user_photo.setImageResource(R.drawable.people);
		}

		user_photo.setOnClickListener(this);
		// �޸��ǳ�
		edittext = (EditText) findViewById(R.id.left_mod_editTxt);
		nic = (TextView) findViewById(R.id.left_mod_txt_nic);
		// ��ʼ����������
		nic_str = sharedPreferences.getString("name", "");
		String id_str = sharedPreferences.getString("id", "");
		id = (TextView) findViewById(R.id.left_mod_txt_res);
		if (!nic_str.equals("")) {
			nic.setText(nic_str);
			edittext.setHint(nic_str);
			id.setText("ID:" + id_str);
		}

		mod_btn = (Button) findViewById(R.id.left_mod_btn);
		mod_btn.setOnTouchListener(this);
		// �޸�����
		mod_pass = (RelativeLayout) findViewById(R.id.left_mod_pass);
		mod_pass.setOnTouchListener(this);

	}

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
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	// �����¼�����
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_mod_title_btn:
			// ��������
			Intent intent = new Intent();
			if (mod_img == 1 || mod_name == 1) {
				intent.putExtra("mod_temp", "1");
			} else {
				intent.putExtra("mod_temp", "0");
			}
			this.setResult(RESULT_OK, intent);
			this.finish();
			break;
		case R.id.left_cover_user_photo:
			/*
			 * Setting_Mod_Dialog.Builder builder = new
			 * Setting_Mod_Dialog.Builder(this); Setting_Mod_Dialog dialog =
			 * builder.create(); dialog.setCanceledOnTouchOutside(true);
			 * dialog.show();
			 */

			dialog = new Setting_Mod_Dialog(this, R.style.Dialog,
					new Setting_Mod_DialogListener() {

						@Override
						public void onClick(View view) {
							// TODO Auto-generated method stub
							switch (view.getId()) {
							case R.id.dialog_mod_img_photo:
								Intent intentFromGallery = new Intent();
								intentFromGallery.setType("image/*"); // �����ļ�����
								intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intentFromGallery,
										IMAGE_REQUEST_CODE);
								break;
							case R.id.dialog_mod_img_camera:
								Intent intentFromCapture = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								// �жϴ洢���Ƿ�����ã����ý��д洢
								if (Tools.hasSdcard()) {

									File tempPath = new File("/sdcard/SmartLife/temp/"+ IMAGE_FILE_NAME);
									File temp = new File("/sdcard/SmartLife/temp/");
									if (!temp.exists()) {
										temp.mkdir();
									}
									intentFromCapture.putExtra(
											MediaStore.EXTRA_OUTPUT,
											Uri.fromFile(tempPath));
								}
								startActivityForResult(intentFromCapture,
										CAMERA_REQUEST_CODE);
								break;
							default:
								break;
							}
						}
					});
			dialog.show();

			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_mod_btn:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				mod_btn.setBackgroundResource(R.drawable.right_pen_max_clk);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				mod_btn.setBackgroundResource(R.drawable.right_pen_max);
				//
				if (edittext.getText().toString().equals("")) {
					Toast.makeText(getBaseContext(), "���������ǳ�",Toast.LENGTH_SHORT).show();
				}else if (TextUtils.isDigitsOnly(edittext.getText().toString())) {
					Toast.makeText(getBaseContext(), "���ǳƲ���Ϊ������",Toast.LENGTH_SHORT).show();
				}else if (!edittext.getText().toString().equals("")) {
					mod_btn.setText("�޸���...");
					mod_btn.setEnabled(false);
					modNic();
					// Toast.makeText(getBaseContext(), "����ɹ�",
					// Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.left_mod_pass:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				mod_pass.setBackgroundResource(R.color.backgroundColor);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				mod_pass.setBackgroundResource(R.color.backgroundColor_low);
				Intent intent = new Intent(Setting_Mod.this,
						Setting_Mod_Pass.class);
				Setting_Mod.this.startActivity(intent);
			}
		default:
			break;
		}

		return false;
	}

	// �޸��ǳ�
	public void modNic() {
		String path = App.address + "modnic.php?name="
				+ edittext.getText().toString() + "&oldname=" + nic_str;
		HttpUtils http = new HttpUtils();

		http.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "��������ʧ��", Toast.LENGTH_SHORT)
						.show();
				mod_btn.setText("����");
				mod_btn.setEnabled(true);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				// �������� ת��ΪJSON��ʽ
				Login_Api content = (Login_Api) JSONObject.parseObject(
						arg0.result, Login_Api.class);
				if (content.getStatus().equals("302")
						|| content.getStatus().equals("301")) {
					Toast.makeText(getBaseContext(), "�ǳƱ�ռ��",
							Toast.LENGTH_SHORT).show();
					mod_btn.setText("����");
					mod_btn.setEnabled(true);
				} else if (content.getStatus().equals("303")) {
					SharedPreferences sharedPreferences = getSharedPreferences(
							"LOGIN", 0);
					Editor editor = sharedPreferences.edit();
					editor.putString("name", edittext.getText().toString());
					editor.commit();
					Toast.makeText(getBaseContext(), "����ɹ�", Toast.LENGTH_SHORT)
							.show();
					mod_btn.setText("����");
					nic.setText(edittext.getText().toString());
					mod_btn.setEnabled(true);
					mod_name = 1;

				} else {
					Toast.makeText(getBaseContext(), "δ֪����", Toast.LENGTH_SHORT)
							.show();
					mod_btn.setText("����");
					mod_btn.setEnabled(true);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ����벻����ȡ��ʱ��
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File("/sdcard/SmartLife/temp/"
							+ IMAGE_FILE_NAME);
					File temp = new File("/sdcard/SmartLife/temp/");
					if (!temp.exists()) {
						temp.mkdir();
					}
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(Setting_Mod.this, "δ�ҵ��洢�����޷��洢��Ƭ��",
							Toast.LENGTH_SHORT).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * �ü�ͼƬ����ʵ��
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// ���òü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);

		SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
		img_path = "/sdcard/SmartLife/img/id_"
				+ sharedPreferences.getString("id", "") + "_headimg.jpg";
		// �洢�ü�����ļ�
		File tempFile = new File(img_path); // �ļ���
		File temp = new File("/sdcard/SmartLife/img/");// �ļ���
		if (!temp.exists()) {
			temp.mkdir();
		}
		intent.putExtra("output", Uri.fromFile(tempFile)); // д��Ŀ���ļ�
		intent.putExtra("outputFormat", "JPEG");

		startActivityForResult(intent, 2);
	}

	/**
	 * ����ü�֮���ͼƬ����
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			user_photo.setImageDrawable(drawable);
			Log.e("photo", drawable.toString());
			mod_img = 1;
			dialog.dismiss();

			FileUploadTask fileuploadtask = new FileUploadTask();
			fileuploadtask.execute();

		}
	}

	// ��д���ؼ�
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// ��������
		Intent intent = new Intent();
		if (mod_img == 1 || mod_name == 1) {
			intent.putExtra("mod_temp", "1");
		} else {
			intent.putExtra("mod_temp", "0");
		}
		this.setResult(RESULT_OK, intent);
		this.finish();
		super.onBackPressed();
	}

	class FileUploadTask extends AsyncTask<Object, Integer, Void> {

		private ProgressDialog dialog = null;
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;
		// the file path to upload
		String pathToOurFile = img_path;
		// the server address to process uploaded file
		String urlServer = App.address + "receive_file.php";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		File uploadFile = new File(pathToOurFile);
		long totalSize = uploadFile.length(); // Get size of file, bytes

		@Override
		protected Void doInBackground(Object... arg0) {

			long length = 0;
			int progress;
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 256 * 1024;// 256KB
			Message message = new Message();

			try {
				FileInputStream fileInputStream = new FileInputStream(new File(
						pathToOurFile));

				URL url = new URL(urlServer);
				connection = (HttpURLConnection) url.openConnection();

				// Set size of every block for post
				connection.setChunkedStreamingMode(256 * 1024);// 256KB

				// Allow Inputs & Outputs
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);

				// Enable POST method
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("Charset", "UTF-8");
				connection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				outputStream = new DataOutputStream(
						connection.getOutputStream());
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
								+ pathToOurFile + "\"" + lineEnd);
				outputStream.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// Read file
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {
					outputStream.write(buffer, 0, bufferSize);
					length += bufferSize;
					progress = (int) ((length * 100) / totalSize);
					publishProgress(progress);

					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(twoHyphens + boundary + twoHyphens
						+ lineEnd);
				publishProgress(100);
				fileInputStream.close();
				outputStream.flush();
				outputStream.close();
				message.what = 1;

			} catch (Exception ex) {
				message.what = 0;
			}
			updateUI.sendMessage(message);
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected void onPostExecute(Void result) {

		}

	}

}
