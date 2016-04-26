package com.atfpm.http;

import android.content.Context;
import android.util.Log;

import com.atfpm.tool.Nationality;
import com.lidroid.xutils.http.RequestParams;

public class HttpUtilsBox {

	private static MyHttpUtils http;

	public static MyHttpUtils getHttpUtil() {
		if (http == null) {
			http = new MyHttpUtils();
			http.configTimeout((int) 1000 * 10);
			http.configRequestThreadPoolSize(1);
			http.configCurrentHttpCacheExpiry(0);
		}
		return http;
	}

	public static RequestParams getRequestParams(Context context) {
		RequestParams params = new RequestParams();
		Log.e("lang", Nationality.getNowNationality(context));
		params.addHeader("lang", Nationality.getNowNationality(context));
		return params;
	}

}
