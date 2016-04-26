package com.atfpm.main.user;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.atfpm.R;
import com.atfpm.box.UserObj;
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

public class ForgetActivity extends Activity {

	private Context context;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.forget_telInput)
	private EditText telInput;
	@ViewInject(R.id.forget_getCode)
	private TextView getCodeText;
	@ViewInject(R.id.forget_codeInput)
	private EditText codeInput;
	@ViewInject(R.id.forget_passwordInput)
	private EditText passwordInput;
	@ViewInject(R.id.forget_passwordAgainInput)
	private EditText passwordAgainInput;
	@ViewInject(R.id.forget_progress)
	private ProgressBar progress;
	@ViewInject(R.id.forget_passwordJudge)
	private ImageView passwordJudge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget);
		context = this;
		ViewUtils.inject(this);

		initAcitvity();
		setTextChangedListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterEventHandler(eh);
	}

	@OnClick({ R.id.titel_back, R.id.title_vip, R.id.forget_getCode,
			R.id.forget_forgetBtn })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_vip:
			VipHandler.jumpOnlyVipActivity(context);
			break;
		case R.id.forget_getCode:
			getCode();
			break;
		case R.id.forget_forgetBtn:
			submit();
			break;
		}
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
				} else {
					passwordJudge.setImageResource(R.drawable.password_false);
				}
			}
		});
	}

	private void initAcitvity() {
		titleName.setText(getResources().getString(R.string.forget_password));
		SMSSDK.registerEventHandler(eh);
	}

	private void getCode() {
		String phone = telInput.getText().toString();
		Log.e("phone ==>>", phone);
		SMSSDK.getVerificationCode("853", phone);
		// SMSSDK.getVerificationCode("86", phone);
		startClock();
	}

	private void startClock() {
		getCodeText.setClickable(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 90; i >= 0; i--) {
					Message.obtain(clockHandler, i).sendToTarget();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void submit() {
		if (isCommit()) {
			progress.setVisibility(View.VISIBLE);
			submitVerificationCode();
		}
	}

	private void submitVerificationCode() {
		String phone = telInput.getText().toString();
		String code = codeInput.getText().toString();
		SMSSDK.submitVerificationCode("86", phone, code);
	}

	private boolean isCommit() {
		if (telInput.getText().toString().equals("")) {
			ShowMessage.showToast(context,
					getResources().getString(R.string.tel_not_null));
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

	private void submitForget() {
		String url = UrlHandle.getForgetURL();

		RequestParams params = HttpUtilsBox.getRequestParams(context);
		params.addBodyParameter("mobile", telInput.getText().toString());
		params.addBodyParameter("newPass", passwordInput.getText().toString());
		params.addBodyParameter("verifyCode", codeInput.getText().toString());

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

	EventHandler eh = new EventHandler() {

		@Override
		public void afterEvent(int event, int result, Object data) {

			if (result == SMSSDK.RESULT_COMPLETE) { // 回调完成
				Log.e("", "回调完成");
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
					Log.e("", "提交验证码成功");
					submitForget();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {// 获取验证码成功
					Log.e("", "获取验证码成功");
				} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表

				}
			} else {
				Log.e("", "回调失败");
				((Throwable) data).printStackTrace();
				Message.obtain(msgHandler).sendToTarget();
			}
		}

	};

	private Handler msgHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			ShowMessage.showToast(context,
					getResources().getString(R.string.sms_exception));
		}
	};

	private Handler clockHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// super.handleMessage(msg);
			int time = msg.what;
			getCodeText.setText(time + "s");
			if (time == 0) {
				getCodeText
						.setText(getResources().getString(R.string.get_code));
				getCodeText.setClickable(true);
			}
		}

	};

}
