package com.atfpm.main.user;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.UserObj;
import com.atfpm.dialog.InputDialog;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.interfaces.CallbackForBoolean;
import com.atfpm.interfaces.CallbackForString;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.ShowMessage;
import com.atfpm.tool.WinTool;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class UserCompileActivity extends Activity {

	private Context context;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.user_compile_tel)
	private TextView tel;
	@ViewInject(R.id.user_compile_pName)
	private TextView pName;
	@ViewInject(R.id.user_compile_sName)
	private TextView sName;
	@ViewInject(R.id.user_compile_pic)
	private ImageView pic;
	@ViewInject(R.id.user_compile_vipLevel)
	private ImageView vipLevel;
	@ViewInject(R.id.user_compile_progress)
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_compile);
		context = this;
		ViewUtils.inject(this);

		initAcitvity();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setUserPic();
		VipHandler.judgeVipTime(context, vipHint);
	}

	@OnClick({ R.id.titel_back, R.id.title_vip,
			R.id.user_compile_resetPassword, R.id.user_compile_pNameBox,
			R.id.user_compile_sNameBox, R.id.user_compile_picBox })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_vip:
			VipHandler.jumpOnlyVipActivity(context);
			break;
		case R.id.user_compile_resetPassword:
			Passageway.jumpActivity(context, ResetPasswordActivity.class);
			break;
		case R.id.user_compile_pNameBox:
			setPName();
			break;
		case R.id.user_compile_sNameBox:
			setSName();
			break;
		case R.id.user_compile_picBox:
			Passageway.jumpActivity(context, PicCompileActivity.class);
			break;
		}
	}

	private void initAcitvity() {
		titleName.setText(getResources().getString(R.string.compile_myself));
		VipHandler.judgeVipTime(context, vipHint);
		setUserTel();
		setUserSName();
		setUserPName();
		setUserPic();
		setUserlevel();
	}

	private void setUserlevel() {
		UserObjHandler.setUsetType(context, vipLevel);
	}

	private void setUserPic() {
		DownloadImageLoader.loadImage(pic,
				UserObjHandler.getUserAvatar(context),
				WinTool.dipToPx(context, 40) / 2);
	}

	private void setUserSName() {
		String s = UserObjHandler.getUserSName(context);
		sName.setText(s);
	}

	private void setUserPName() {
		String p = UserObjHandler.getUserPName(context);
		pName.setText(p);
	}

	private void setUserTel() {
		tel.setText(UserObjHandler.getUserTel(context));
	}

	private void setSName() {
		InputDialog dialog = new InputDialog(context);
		dialog.setTitle(getResources().getString(R.string.s_name));
		dialog.setHint(getResources().getString(R.string.input_s_name));
		dialog.setListener(new CallbackForString() {

			@Override
			public void callback(final String result) {
				submitReset(result, UserObjHandler.getUserPName(context),
						new CallbackForBoolean() {

							@Override
							public void callback(boolean b) {
								if (b) {
									setUserSName();
								}
							}
						});
			}
		});
	}

	private void setPName() {
		InputDialog dialog = new InputDialog(context);
		dialog.setTitle(getResources().getString(R.string.p_name));
		dialog.setHint(getResources().getString(R.string.input_p_name));
		dialog.setListener(new CallbackForString() {

			@Override
			public void callback(final String result) {
				submitReset(UserObjHandler.getUserSName(context), result,
						new CallbackForBoolean() {

							@Override
							public void callback(boolean b) {
								if (b) {
									setUserPName();
								}
							}
						});
			}
		});
	}

	private void submitReset(String s_name, String p_name,
			final CallbackForBoolean callback) {
		progress.setVisibility(View.VISIBLE);

		String url = UrlHandle.getUserModURL();

		RequestParams params = HttpUtilsBox.getRequestParams(context);
		params.addBodyParameter("accesstoken",
				UserObjHandler.getAccesstoken(context));
		params.addBodyParameter("s_name", s_name);
		params.addBodyParameter("p_name", p_name);

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
							// int r = JsonHandle.getInt(json, "result");
							UserObj obj = UserObjHandler.getUserObj(JsonHandle
									.getJSON(
											JsonHandle.getJSON(json, "result"),
											"user"));
							boolean b = !obj.getAccesstoken().equals("");
							if (b) {
								ShowMessage.showToast(context, getResources()
										.getString(R.string.send_succeed));
								UserObjHandler.save(context, obj);
							}
							callback.callback(b);
						}
					}

				});
	}
}
