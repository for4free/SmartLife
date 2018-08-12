package com.example.smartlife;

import android.util.Log;

public class Logger {
	private static final boolean DEBUG = true;
	private static final String TAG = "XYZQ_ANDROIDPAD";

	public static void d(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void d(String tag, int msg) {
		if (DEBUG) {
			Log.d(tag, String.valueOf(msg));
		}
	}

	public static void e(String msg) {
		if (DEBUG) {
			Log.e(TAG, msg);
		}
	}

	public static void e(String tag, String msg) {

		if (DEBUG) {
			Log.e(tag, msg);
		}

	}

	public static void i(String msg) {
		if (DEBUG) {
			Log.i(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void v(String msg) {
		if (DEBUG) {
			Log.v(TAG, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}
}