package com.atfpm.main.user;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.tool.ShowMessage;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ResetPasswordActivity extends Activity {

	private Context context;

	private boolean isCorrect = false;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.reset_oldPasswordInput)
	private EditText oldPasswordInput;
	@ViewInject(R.id.reset_passwordInput)
	private EditText passwordInput;
	@ViewInject(R.id.reset_passwordAgainInput)
	private EditText passwordAgainInput;
	@ViewInject(R.id.reset_passwordJudge)
	private ImageView passwordJudge;
	@ViewInject(R.id.reset_progress)
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		context = this;
		ViewUtils.inject(this);

		initAcitvity();
		setTextChangedListener();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		VipHandler.judgeVipTime(context, vipHint);
	}

	@OnClick({ R.id.titel_back, R.id.title_vip, R.id.reset_resetBtn })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_vip:
			VipHandler.jumpOnlyVipActivity(context);
			break;
		case R.id.reset_resetBtn:
			submit();
			break;
		}
	}

	private void submit() {
		if (isCommit()) {
			submitReset();
		}
	}

	private boolean isCommit() {
		if (oldPasswordInput.getText().toString().equals("")) {
			ShowMessage.showToast(context,
					getResources().getString(R.string.pass_not_null));
			return false;
		}
		if (passwordInput.getText().toString().equals("")) {
			ShowMessage.showToast(context,
					getResources().getString(R.string.pass_not_null));
			return false;
		}
		if (passwordInput.getText().toString().length() < 6
				|| passwordInput.getText().toString().length() > 20) {
			ShowMessage.showToast(context,
					getResources().getString(R.string.pass_length));
			return false;
		}
		return true;
	}

	private void setTextChangedListener() {
		passwordAgainInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				passwordJudge.setVisibility(View.VISIBLE);
				if (passwordInput.getText().toString()
						.equals(passwordAgainInput.getText().toString())) {
					passwordJudge.setImageResource(R.drawable.password_true);
					isCorrect = true;
				} else {
					passwordJudge.setImageResource(R.drawable.password_false);
					isCorrect = false;
				}
			}
		});
	}

	private void initAcitvity() {
		titleName.setText(getResources().getString(R.string.reset_password));
		VipHandler.judgeVipTime(context, vipHint);
	}

	private void submitReset() {
		progress.setVisibility(View.VISIBLE);

		String url = UrlHandle.getResetPassURL();

		RequestParams params = HttpUtilsBox.getRequestParams(context);
		params.addBodyParameter("accesstoken",
				UserObjHandler.getAccesstoken(context));
		params.addBodyParameter("pass", oldPasswordInput.getText().toString());
		params.addBodyParameter("new_pass", passwordInput.getText().toString());
		params.addBodyParameter("re_new_pass", passwordAgainInput.getText()
				.toString());

		HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						progress.setVisibility(View.GONE);
						ShowMessage.showFailure(context);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						progress.setVisibility(View.GONE);
						String result = responseInfo.result;
						Log.d("", result);

						JSONObject json = JsonHandle.getJSON(result);
						if (!ShowMessage.showException(context, json)) {
							int r = JsonHandle.getInt(json, "result");
							if (r == 1) {
								ShowMessage.showToast(context, getResources()
										.getString(R.string.send_succeed));
								finish();
							}
						}
					}

				});
	}

}
