package com.example.smartlife_out;

import android.os.Environment;

/**
 * 
 * Create at 2012-8-17 ����10:14:40
 */
public class Tools {
	/**
	 * ����Ƿ����SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
}
