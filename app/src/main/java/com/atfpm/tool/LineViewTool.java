package com.atfpm.tool;

import com.atfpm.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LineViewTool {

	private static int P = 15;

	public static View getGrayLine(Context context) {
		View v = new View(context);
		v.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 2));
		v.setBackgroundColor(ColorBox.getColorForID(context,
				R.color.line_gray_01));
		v.setPadding(P, 0, P, 0);
		return v;
	}

	public static TextView getFirst(Context context, int id) {
		TextView v = new TextView(context);
		v.setTextSize(16);
		v.setText(context.getResources().getString(id));
		v.setTextColor(ColorBox.getColorForID(context, R.color.text_black_01));
		v.setPadding(0, 100, 0, 180);
		v.setGravity(Gravity.CENTER);
		v.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return v;
	}
}
