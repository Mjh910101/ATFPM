package com.atfpm.handler;

import com.atfpm.system.SystemHandle;

import android.content.Context;
import cn.jpush.android.api.JPushInterface;

public class PushHandler {

	private final static String KEY = "push";

	public static boolean isPush(Context context) {
		// return JPushInterface.isPushStopped(context);
		return !SystemHandle.getBoolean(context, KEY);
	}

	public static void revamPush(Context context) {
		boolean b = isPush(context);
		if (!b) {
			JPushInterface.resumePush(context);
		} else {
			JPushInterface.stopPush(context);
		}
		SystemHandle.saveBooleanMessage(context, KEY, b);
	}

	public static void init(Context context) {
		JPushInterface.setDebugMode(true);
		JPushInterface.init(context);
	}

}
