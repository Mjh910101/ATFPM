package com.atfpm.dialog;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.interfaces.CallbackForString;
import com.atfpm.tool.WinTool;

public class InputDialog {

	private Context context;
	private AlertDialog ad;
	private Window window;

	private EditText mEditText;
	private TextView title, commit, cancel;
	private ImageView cancelInput;

	private InputMethodManager imm = null;

	public InputDialog(Context context) {
		this.context = context;
		ad = new android.app.AlertDialog.Builder(context).create();

		ad.setView(((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.inputdialog, null));
		ad.show();

		window = ad.getWindow();
		window.setContentView(R.layout.inputdialog);

		title = (TextView) window.findViewById(R.id.inputDialog_title);
		mEditText = (EditText) window.findViewById(R.id.inputDialog_ET);
		commit = (TextView) window.findViewById(R.id.inputDialog_commit);
		cancel = (TextView) window.findViewById(R.id.inputDialog_cancel);
		cancelInput = (ImageView) window
				.findViewById(R.id.inputDialog_cancelInput);

		imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		setLayout();
		setCancelListener();
		setCancelInputListener();

		showImm();
	}

	private void showImm() {

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				imm.showSoftInput(mEditText, 0);
			}
		}, 1000);
	}

	private void setLayout() {
		setLayout(0.8, 0.3);
	}

	public void setLayout(double Xnum, double Ynum) {
		window.setLayout((int) (WinTool.getWinWidth(context) * Xnum),
				(int) (WinTool.getWinHeight(context) * Ynum));
	}

	public void setTitle(String t) {
		if (t != null && !t.equals("")) {
			title.setVisibility(View.VISIBLE);
			title.setText(t);
		}
	}

	public void setHint(String h) {
		if (h != null && !h.equals("")) {
			mEditText.setHint(h);
		}
	}

	private void setCancelInputListener() {
		cancelInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditText.setText("");
			}
		});
	}

	public void setCancelListener() {
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public void setListener(final CallbackForString callback) {
		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = mEditText.getText().toString();
				if (!str.equals("")) {
					if (callback != null) {
						callback.callback(str);
					}
				}
				dismiss();
			}
		});
	}

	public void dismiss() {
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(mEditText.getApplicationWindowToken(),
					0);
		}
		ad.dismiss();
	}

}
