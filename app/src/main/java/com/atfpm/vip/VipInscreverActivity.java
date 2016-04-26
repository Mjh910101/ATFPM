package com.atfpm.vip;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atfpm.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class VipInscreverActivity extends Activity {

	private Context context;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.title_vip)
	private ImageView vipIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_inscrever);
		context = this;
		ViewUtils.inject(this);

		initAcitvity();
	}

	@OnClick({ R.id.titel_back })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		}
	}

	private void initAcitvity() {
		titleName.setText(getResources().getString(R.string.detail));
		vipHint.setVisibility(View.GONE);
		vipIcon.setVisibility(View.GONE);
	}

}
