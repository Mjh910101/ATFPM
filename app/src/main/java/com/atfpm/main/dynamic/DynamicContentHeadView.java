package com.atfpm.main.dynamic;

import com.atfpm.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class DynamicContentHeadView extends LinearLayout {

	private LayoutInflater inflater;
	private View acitvity;

	private DynamicContentActivity fatherActivity;

	public DynamicContentHeadView(Context context,
			DynamicContentActivity fatherActivity) {
		super(context);
		this.fatherActivity = fatherActivity;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		acitvity = inflater.inflate(R.layout.dynamic_content, null);

		addView(acitvity);
	}

}
