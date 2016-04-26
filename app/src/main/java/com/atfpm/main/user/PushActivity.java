package com.atfpm.main.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.atfpm.R;
import com.lidroid.xutils.ViewUtils;

public class PushActivity extends Activity {

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push);
		context = this;
		ViewUtils.inject(this);

		// initAcitvity();
	}

}
