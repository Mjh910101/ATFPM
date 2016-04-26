package com.atfpm.handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHandle {

	// public final static String DATESTYP_1 = "yyyy.MM.dd HH:mm:ss";
	// public final static String DATESTYP_2 = "MM月dd日";
	// public final static String DATESTYP_3 = "K:mm a";
	public final static String DATESTYP_4 = "yyyy-MM-dd";
	// public final static String DATESTYP_5 = "K:mm";
	// public final static String DATESTYP_6 = "yyyy年M月d日 a h:mm";
	// public final static String DATESTYP_7 = "HH:mm:ss";
	public final static String DATESTYP_8 = "HH:mm";
	public final static String DATESTYP_9 = "MM-dd HH:mm";
	public final static String DATESTYP_10 = "yyyy-MM-dd KK:mm";

	public static String getTimeString(Date date, String type) {
		SimpleDateFormat dateformat = new SimpleDateFormat(type);
		return dateformat.format(date);
	}

	// public static String formatting(long time) {
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(new Date(time));
	// Calendar today = getToday();
	// if (getYear(calendar) == getYear(today)
	// && getMonth(calendar) == getMonth(today)
	// && getDay(calendar) == getDay(today)) {
	// if (getHour(calendar) <= 12) {
	// return "上午  " + getTimeString(calendar.getTime(), DATESTYP_5);
	// } else {
	// return "下午  " + getTimeString(calendar.getTime(), DATESTYP_5);
	// }
	// } else {
	// return getTimeString(calendar.getTime(), DATESTYP_6);
	// }
	// }

	public static String format(long time, String type) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		return getTimeString(calendar.getTime(), type);
	}

	// public static String getTime(long time) {
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(new Date(time));
	// return getTimeString(calendar.getTime(), DATESTYP_3);
	// }

	// public static String getDayForGone(long time) {
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(new Date(time));
	// if (isToday(calendar)) {
	// return getTimeString(calendar.getTime(), DATESTYP_7);
	// } else {
	// return getTimeString(calendar.getTime(), DATESTYP_1);
	// }
	// }

	public static boolean isToday(Calendar calendar) {
		Calendar today = getToday();
		return getYear(calendar) == getYear(today)
				&& getMonth(calendar) == getMonth(today)
				&& getDay(calendar) == getDay(today);
	}

	public static String getIsTodayFormat(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		if (isToday(calendar)) {
			return format(time, DATESTYP_8);
		} else {
			return format(time, DATESTYP_9);
		}
	}

	// public static String getDay(long time, int dayCount) {
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(new Date(time));
	// if (dayCount > 0) {
	// calendar.add(Calendar.DATE, dayCount);
	// }
	// if (isToday(calendar)) {
	// return "今日";
	// } else {
	// return getTimeString(calendar.getTime(), DATESTYP_2);
	// }
	// }

	// public static String getDay(long time) {
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(new Date(time));
	// if (isToday(calendar)) {
	// return "今日";
	// } else {
	// return getTimeString(calendar.getTime(), DATESTYP_2);
	// }
	// }

	public static Calendar getToday() {
		return Calendar.getInstance();
	}

	public static int getYear(Calendar calendar) {
		return calendar.get(Calendar.YEAR);
	}

	public static int getMonth(Calendar calendar) {
		return calendar.get(Calendar.MONTH);
	}

	public static int getDay(Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static int getHour(Calendar calendar) {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

}
