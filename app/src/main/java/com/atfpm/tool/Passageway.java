package com.atfpm.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class Passageway {
	/**
	 * 
	 * @param context
	 * @param cls
	 */
	public static void jumpActivity(Context context, Class<?> cls) {
		jumpActivity(context, cls, -1, null, -1);
	}

	/**
	 * 
	 * @param context
	 * @param cls
	 * @param requestCode
	 */
	public static void jumpActivity(Context context, Class<?> cls,
			int requestCode) {
		jumpActivity(context, cls, requestCode, null, -1);
	}

	/**
	 * 
	 * @param context
	 * @param cls
	 * @param bundle
	 */
	public static void jumpActivity(Context context, Class<?> cls, Bundle bundle) {
		jumpActivity(context, cls, -1, bundle, -1);
	}

	/**
	 * 
	 * @param context
	 * @param cls
	 * @param requestCode
	 * @param bundle
	 */
	public static void jumpActivity(Context context, Class<?> cls,
			int requestCode, Bundle bundle) {
		jumpActivity(context, cls, requestCode, bundle, -1);
	}

	public static void jumpDialing(Context context, String tel) {
		Uri uri = Uri.parse("tel:" + tel);
		Intent intent = new Intent(Intent.ACTION_DIAL, uri);
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param flagActivityClearTop
	 * @param cls
	 */
	public static void jumpActivity(Context context, int flagActivityClearTop,
			Class<?> cls) {
		jumpActivity(context, cls, -1, null, flagActivityClearTop);
	}

	/**
	 * 
	 * @param context
	 * @param cls
	 * @param bundle
	 * @param flagActivityClearTop
	 */
	public static void jumpActivity(Context context, Class<?> cls,
			Bundle bundle, int flagActivityClearTop) {
		jumpActivity(context, cls, -1, bundle, flagActivityClearTop);
	}

	/**
	 * 
	 * @param context
	 * @param cls
	 * @param requestCode
	 * @param bundle
	 * @param flagActivityClearTop
	 */
	public static void jumpActivity(Context context, Class<?> cls,
			int requestCode, Bundle bundle, int flagActivityClearTop) {
		Intent intent = new Intent();
		intent.setClass(context, cls);
		if (flagActivityClearTop > 0) {
			intent.addFlags(flagActivityClearTop);
		}
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		((Activity) (context)).startActivityForResult(intent, requestCode);
	}
}
