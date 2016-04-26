package com.atfpm.main.intro;

import com.atfpm.tool.Passageway;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

	public final static String KEY = "URL";
	public final static String COLOR = "COLOR";

	private Context context;
	private int color;

	public MyWebViewClient(Context context, int color) {
		this.context = context;
		this.color = color;
	}

	// 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// view.loadUrl(url);// 如果不需要其他对点击链接事件的处理返回true，否则返回false

		Bundle b = new Bundle();
		b.putString(KEY, url);
		b.putInt(COLOR, color);
		Passageway.jumpActivity(context, IntroContentActivity.class, b);

		return true;

	}
}
