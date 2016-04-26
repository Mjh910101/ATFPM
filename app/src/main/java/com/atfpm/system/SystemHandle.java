package com.atfpm.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.atfpm.tool.Nationality;

public class SystemHandle {

	public final static String NAME = "ATFPM";
	public final static int MODE = 2;

	public final static String NATIONALITY = "Nationality";
	public final static String IS_FRIST = "IS_FRIST";
	public final static String VIP_TIME = "VIP_TIME";
	public final static String TALK_TIME = "TALK_TIME";
	public final static String TOPIC_TIME = "TOPIC_TIME";
	public final static String MESSAGE_TIME = "MESSAGE_TIME";

	public static void saveIntMessage(Context context, String ksy, int i) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		Editor editor = pref.edit();
		editor.putInt(ksy, i);
		editor.commit();
	}

	public static int getInt(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		return pref.getInt(key, 0);
	}

	// *************************************************************************

	public static void saveStringMessage(Context context, String ksy, String v) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		Editor editor = pref.edit();
		editor.putString(ksy, v);
		editor.commit();
	}

	public static String getString(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		return pref.getString(key, "");
	}

	// *************************************************************************

	public static void saveBooleanMessage(Context context, String ksy, boolean b) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		Editor editor = pref.edit();
		editor.putBoolean(ksy, b);
		editor.commit();
	}

	public static boolean getBoolean(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		return pref.getBoolean(key, false);
	}

	// *************************************************************************

	public static void saveLongMessage(Context context, String ksy, long l) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		Editor editor = pref.edit();
		editor.putLong(ksy, l);
		editor.commit();
	}

	public static long getLong(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		return pref.getLong(key, 0);
	}

	// *************************************************************************
	public static boolean isFlist(Context context) {
		return !getBoolean(context, IS_FRIST);
	}

	public static void setFirst(Context context) {
		saveBooleanMessage(context, IS_FRIST, true);
	}

	// *************************************************************************
	public static void saveNationality(Context context, int n) {
		saveIntMessage(context, NATIONALITY, n);
	}

	public static int getNationality(Context context) {
		SharedPreferences pref = context.getSharedPreferences(NAME, MODE);
		return pref.getInt(NATIONALITY, Nationality.CH);
	}

	// *************************************************************************
	public static long getVipTime(Context context) {
		return getLong(context, VIP_TIME);
	}

	public static void setVipTime(Context context, long l) {
		saveLongMessage(context, VIP_TIME, l);
	}

	// *************************************************************************
	public static long getTopicTime(Context context) {
		return getLong(context, TOPIC_TIME);
	}

	public static void setTopicTime(Context context, long l) {
		saveLongMessage(context, TOPIC_TIME, l);
	}

	// *************************************************************************
	public static long getMessageTime(Context context) {
		return getLong(context, MESSAGE_TIME);
	}

	public static void setMessageTime(Context context, long l) {
		saveLongMessage(context, MESSAGE_TIME, l);
	}

	// *************************************************************************
	public static long getTalkTime(Context context) {
		return getLong(context, TALK_TIME);
	}

	public static void setTalkTime(Context context, long l) {
		saveLongMessage(context, TALK_TIME, l);
	}

}
