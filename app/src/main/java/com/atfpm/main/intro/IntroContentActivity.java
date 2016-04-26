package com.atfpm.main.intro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.handler.VipHandler;
import com.atfpm.interfaces.CallbackForString;
import com.atfpm.main.MainActivity;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Nationality;
import com.atfpm.tool.Passageway;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class IntroContentActivity extends Activity {

	private Context context;

	private boolean isVIP = false;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.intro_content)
	private WebView introContent;
	@ViewInject(R.id.title_bg)
	private RelativeLayout titleBg;
	@ViewInject(R.id.titel_back)
	private ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro_content);
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
			if (isVIP) {
				close();
			} else {
				VipHandler.jumpOnlyVipActivity(context);
			}
			break;
		}
	}

	private void close() {
		Passageway.jumpActivity(context, Intent.FLAG_ACTIVITY_CLEAR_TOP,
				MainActivity.class);
		overridePendingTransition(R.anim.circulation_in, R.anim.circulation_out);
	}

	private void initAcitvity() {
		// titleName.setText(getResources().getString(R.string.intro_name));
		VipHandler.judgeVipTime(context, vipHint);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			int color = b.getInt(MyWebViewClient.COLOR);
			titleBg.setBackgroundColor(color);
			if (color == ColorBox.getColorForID(context, R.color.vip_title_bg)) {
				isVIP = true;
				titleName.setText(getResources().getString(R.string.vip_title));
				titleName.setTextColor(ColorBox.getColorForID(context,
						R.color.white));
				back.setImageResource(R.drawable.back_white);
			} else {
				isVIP = false;
				titleName.setTextColor(ColorBox.getColorForID(context,
						R.color.text_green_01));
				back.setImageResource(R.drawable.back);
			}
			introContent.setWebViewClient(new MyWebViewClient(context, color));
			introContent.setWebChromeClient(new MyWebChromeClient(
					new CallbackForString() {

						@Override
						public void callback(String result) {
							titleName.setText(result);
						}
					}));
			introContent.getSettings().setJavaScriptEnabled(true);
			introContent.loadUrl(b.getString(MyWebViewClient.KEY));
		} else {
			finish();
		}

	}
}
