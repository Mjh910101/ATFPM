package com.atfpm.main.user;

import org.json.JSONObject;

import com.atfpm.R;
import com.atfpm.box.UserObj;
import com.atfpm.dialog.MessageDialog;
import com.atfpm.dialog.RegisterDialog;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.ShowMessage;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends Activity {

    private Context context;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.login_passwordInput)
    private EditText passwordInput;
    @ViewInject(R.id.login_telInput)
    private EditText telInput;
    @ViewInject(R.id.login_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        VipHandler.judgeVipTime(context, vipHint);
        if (UserObjHandler.isLogin(context)) {
            finish();
        }
    }

    @OnClick({R.id.titel_back, R.id.title_vip, R.id.login_register,
            R.id.login_login, R.id.login_finish, R.id.login_forgetPassword})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titel_back:
            case R.id.login_finish:
                finish();
                break;
            case R.id.title_vip:
                VipHandler.jumpOnlyVipActivity(context);
                break;
            case R.id.login_register:
//			Passageway.jumpActivity(context, RegisterActivityV2.class);
                RegisterDialog.showWarningDialog(context, RegisterDialog.COM_LGOIN);
                break;
            case R.id.login_login:
                submit();
                break;
            case R.id.login_forgetPassword:
                Passageway.jumpActivity(context, ForgetActivityV2.class);
                // showForget();
                break;
        }
    }

    private void showForget() {
        MessageDialog dialog = new MessageDialog(context);
        String str = getResources().getString(
                R.string.forget_password_message_1)
                + "\n"
                + getResources().getString(R.string.forget_password_message_2)
                + "\n"
                + getResources().getString(R.string.forget_password_message_3);
        dialog.setMessage(str);
        dialog.setCommitListener(null);
    }

    private void submit() {
        if (isCommit()) {
            submitLogin();
        }
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
        return true;
    }

    private void initAcitvity() {
        titleName.setText(getResources().getString(R.string.login));
        VipHandler.judgeVipTime(context, vipHint);

        passwordInput.setTypeface(Typeface.DEFAULT);
        passwordInput
                .setTransformationMethod(new PasswordTransformationMethod());
    }

    private void submitLogin() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getLoginURL();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("mobile", telInput.getText().toString());
        params.addBodyParameter("pass", passwordInput.getText().toString());

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
                                        .getString(R.string.login_succeed));
                                UserObjHandler.save(context, obj);
                                finish();
                            }
                        }
                    }

                });
    }

}
