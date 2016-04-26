package com.atfpm.nationality;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.atfpm.R;
import com.atfpm.main.speak.SpeakShareActivity;
import com.atfpm.tool.Nationality;
import com.atfpm.tool.ShowMessage;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class NationalityActivity extends Activity {

	public final static int resultCode = 9394;
	public final static String KEY = "date";

	private final static long EXITTIME = 2000;
	private long EXIT = 0;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nationality);
		context = this;
		ViewUtils.inject(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (System.currentTimeMillis() - EXIT < EXITTIME) {
				close();
			} else {
				ShowMessage.showToast(context,
						getResources().getString(R.string.exit));
			}
			EXIT = System.currentTimeMillis();
		}
		return false;
	}

	@OnClick({ R.id.nationlity_PT, R.id.nationlity_ZH })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nationlity_PT:
			Nationality.setNationality(context, Nationality.getPT(context));
			break;
		case R.id.nationlity_ZH:
			Nationality.setNationality(context, Nationality.getZH(context));
			break;
		}
	}

	private void close() {
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putBoolean(KEY, true);
		i.putExtras(b);
		setResult(NationalityActivity.resultCode, i);
		finish();
	}

}
