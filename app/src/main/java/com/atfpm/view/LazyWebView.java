package com.atfpm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class LazyWebView extends VestrewWebView {

	public LazyWebView(Context context) {
		super(context);
	}

	public LazyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LazyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
