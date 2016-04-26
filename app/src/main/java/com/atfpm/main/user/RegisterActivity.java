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

public class RegisterActivity extends Activity {

	private Context context;
	private boolean isCorrect = false;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.register_telInput)
	private EditText telInput;
	@ViewInject(R.id.register_pNameInput)
	private EditText pNameInput;
	@ViewInject(R.id.register_sNameInput)
	private EditText sNameInput;
	@ViewInject(R.id.register_passwordInput)
	private EditText passwordInput;
	@ViewInject(R.id.register_passwordAgainInput)
	private EditText passwordAgainInput;
	@ViewInject(R.id.register_registerBtn)
	private TextView registerBtn;
	@ViewInject(R.id.register_passwordJudge)
	private ImageView passwordJudge;
	@ViewInject(R.id.register_progress)
	private ProgressBar progress;
	@ViewInject(R.id.register_getCode)
	private TextView getCodeText;
	@ViewInject(R.id.register_codeInput)
	private EditText codeInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
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

	@Override
	protected void onRestart() {
		super.onRestart();
		VipHandler.judgeVipTime(context, vipHint);
	}

	@OnClick({ R.id.titel_back, R.id.title_vip, R.id.register_registerBtn,
			R.id.register_getCode })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_vip:
			VipHandler.jumpOnlyVipActivity(context);
			break;
		case R.id.register_registerBtn:
			submit();
			break;
		case R.id.register_getCode:
			getCode();
			break;
		}
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
			// submitRegister();
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
		if (pNameInput.getText().toString().equals("")) {
			ShowMessage.showToast(context,
					getResources().getString(R.string.pname_not_null));
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

	private void initAcitvity() {
		titleName.setText(getResources().getString(R.string.register));
		VipHandler.judgeVipTime(context, vipHint);
		SMSSDK.registerEventHandler(eh);
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

	private void submitRegister() {
		progress.setVisibility(View.VISIBLE);

		String url = UrlHandle.getRegisterURL();

		RequestParams params = HttpUtilsBox.getRequestParams(context);
		params.addBodyParameter("mobile", telInput.getText().toString());
		params.addBodyParameter("p_name", pNameInput.getText().toString());
		params.addBodyParameter("s_name", sNameInput.getText().toString());
		params.addBodyParameter("pass", passwordInput.getText().toString());
		params.addBodyParameter("re_pass", passwordAgainInput.getText()
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
							UserObj obj = UserObjHandler.getUserObj(JsonHandle
									.getJSON(
											JsonHandle.getJSON(json, "result"),
											"user"));
							if (!obj.getAccesstoken().equals("")) {
								ShowMessage.showToast(context, getResources()
										.getString(R.string.register_succeed));
								UserObjHandler.save(context, obj);
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
					submitRegister();
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
