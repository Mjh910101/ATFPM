package com.atfpm.main.speak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.tool.Passageway;

public class SpeakHeadView extends LinearLayout {

	private Context context;
	private LayoutInflater inflater;
	private View acitvity;

	private TextView shareBtn;

	public SpeakHeadView(Context context) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		acitvity = inflater.inflate(R.layout.speak_head, null);

		initView();
		initOnClickLietener();

		addView(acitvity);
	}

	private void initOnClickLietener() {
		shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Passageway.jumpActivity(context, SpeakShareActivity.class);
			}
		});
	}

	private void initView() {
		shareBtn = (TextView) acitvity.findViewById(R.id.speak_content_share);
	}

}
