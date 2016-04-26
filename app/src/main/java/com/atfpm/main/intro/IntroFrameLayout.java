package com.atfpm.main.intro;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.atfpm.R;
import com.atfpm.http.UrlHandle;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Nationality;

public class IntroFrameLayout extends Fragment {

	private Context context;
	private WebView content;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contactsLayout = inflater.inflate(R.layout.intro_web_layout,
				container, false);
		// View contactsLayout = inflater.inflate(R.layout.intro_layout,
		// container, false);
		context = getActivity();
		initView(contactsLayout);
		return contactsLayout;
	}

	private void initView(View v) {
		content = (WebView) v.findViewById(R.id.intro_content);
		content.setWebViewClient(new MyWebViewClient(context, ColorBox
				.getColorForID(context, R.color.title_bg)));
		content.loadUrl(UrlHandle.getIntroURL() + "?lang="
				+ Nationality.getNowNationality(context));
	}
}
