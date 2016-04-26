package com.atfpm.http;

import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MyHttpUtils extends HttpUtils {

	@Override
	public <T> HttpHandler<T> send(HttpMethod method, String url,
			RequestParams params, RequestCallBack<T> callBack) {
		url = url.replace(" ", "");
		Log.e("url", "URL:    " + url);
		return super.send(method, url, params, callBack);
	}

}
