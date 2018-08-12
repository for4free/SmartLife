package com.example.smartlife;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.example.smartlife.Setting.QRCodeUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class Setting_Dialog extends Dialog {
	

	public Setting_Dialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Setting_Dialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private Bitmap image;

		public Builder(Context context) {
			this.context = context;
		}

		public Bitmap getImage() {
			return image;
		}

		public void setImage(Bitmap image) {
			this.image = image;
		}

		public Setting_Dialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final Setting_Dialog dialog = new Setting_Dialog(context,R.style.Dialog);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			View layout = inflater.inflate(R.layout.dialog_2code, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			dialog.setContentView(layout);
			ImageView img = (ImageView) layout.findViewById(R.id.dialog_2code_img);
			//ЖўЮЌТы
			String filePath = "/sdcard/SmartLife/img/qrcode.jpg";
			File imgfile = new File(filePath);
			if (imgfile.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(filePath);
				Drawable drawable = new BitmapDrawable(bitmap);
				img.setImageDrawable(drawable);
			}
			return dialog;
		}
	}
	 
}
