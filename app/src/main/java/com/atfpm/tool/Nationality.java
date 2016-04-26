package com.atfpm.tool;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.atfpm.main.MainActivity;
import com.atfpm.system.SystemHandle;

public class Nationality {

	public final static int CH = 0;
	public final static int PT = 1;

	public static void initNationality(Context context) {
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		config.locale = getNowNationalityLocale(context);
		resources.updateConfiguration(config, dm);
	}

	public static void setNationality(Context context) {
		setNationality(context, getNationality(context));
	}

	public static void setNationality(Context context, Locale l) {
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		config.locale = l;
		resources.updateConfiguration(config, dm);
		restart(context);
	}

	private static void restart(Context context) {
		SystemHandle.setFirst(context);
		Intent intent = new Intent();
		intent.setClass(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static Locale getZH(Context context) {
		SystemHandle.saveNationality(context, CH);
		return new Locale("zh", "CN");
	}

	public static Locale getPT(Context context) {
		SystemHandle.saveNationality(context, PT);
		return new Locale("pt", "PT");
	}

	public static Locale getNationality(Context context) {
		int now = SystemHandle.getNationality(context);
		if (now == PT) {
			return getZH(context);
		} else {
			return getPT(context);
		}
	}

	public static Locale getNowNationalityLocale(Context context) {
		int now = SystemHandle.getNationality(context);
		if (now == PT) {
			return getPT(context);
		} else {
			return getZH(context);
		}
	}

	public static String getNowNationality(Context context) {
		int now = SystemHandle.getNationality(context);
		if (now == PT) {
			return "pt";
		} else {
			return "zh";
		}
	}

}
