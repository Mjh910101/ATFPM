package com.atfpm.main.user;

import com.atfpm.R;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.UrlHandle;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class VipLevelActivity extends Activity {

	private Context context;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.vipLevel_content)
	private WebView content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_level);
		context = this;
		ViewUtils.inject(this);

		initAcitvity();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		VipHandler.judgeVipTime(context, vipHint);
	}

	@OnClick({ R.id.titel_back, R.id.title_vip })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_vip:
			VipHandler.jumpOnlyVipActivity(context);
			break;
		}
	}

	private void initAcitvity() {
		titleName.setText(getResources().getString(R.string.vip_level));
		content.loadUrl(UrlHandle.getMemberIntroURL());
		VipHandler.judgeVipTime(context, vipHint);
	}

}
