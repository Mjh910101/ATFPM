package com.atfpm.vip;

import com.atfpm.R;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.UrlHandle;
import com.atfpm.interfaces.CallbackForString;
import com.atfpm.main.intro.MyWebChromeClient;
import com.atfpm.main.intro.MyWebViewClient;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Nationality;
import com.atfpm.tool.ShowMessage;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VipActicity extends Activity {

	private Context context;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.vip_content)
	private WebView content;
	@ViewInject(R.id.title_bg)
	private RelativeLayout titleBg;
	@ViewInject(R.id.titel_back)
	private ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip);
		context = this;
		ViewUtils.inject(this);

		initAcitvity();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		VipHandler.judgeVipTime(context, vipHint);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			close();
		}
		return false;
	}

	@OnClick({ R.id.title_vip })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_vip:
			close();
			break;
		}
	}

	private void close() {
		finish();
		overridePendingTransition(R.anim.circulation_in, R.anim.circulation_out);
	}

	private void initAcitvity() {
		int color = ColorBox.getColorForID(context, R.color.vip_title_bg);
		titleBg.setBackgroundColor(color);
		titleName.setText(getResources().getString(R.string.vip_title));
		titleName.setTextColor(ColorBox.getColorForID(context, R.color.white));
		back.setVisibility(View.INVISIBLE);
		VipHandler.judgeVipTime(context, vipHint);

		content.getSettings().setJavaScriptEnabled(true);
		content.setWebViewClient(new MyWebViewClient(context, color));
		content.setWebChromeClient(new MyWebChromeClient(
				new CallbackForString() {

					@Override
					public void callback(String result) {
						titleName.setText(result);
					}
				}));
		content.loadUrl(UrlHandle.getVipURL() + "?accesstoken="
				+ UserObjHandler.getAccesstoken(context) + "&lang="
				+ Nationality.getNowNationality(context));

	}
}
