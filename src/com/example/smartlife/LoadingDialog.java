package com.example.smartlife;

import java.io.File;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class LoadingDialog extends Dialog {
	

	public LoadingDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LoadingDialog(Context context, int theme) {
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

		public LoadingDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final LoadingDialog dialog = new LoadingDialog(context,R.style.Dialog);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			View layout = inflater.inflate(R.layout.dialogloading, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			dialog.setContentView(layout);
				
			return dialog;
		}
	}
	 
}
