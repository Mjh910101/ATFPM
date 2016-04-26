package com.atfpm.main.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.OriginObj;
import com.atfpm.dialog.MessageDialog;
import com.atfpm.dialog.MessageDialog.CallBackListener;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.MsmHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.interfaces.CallbackForInteger;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.ShowMessage;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONObject;

import java.util.List;

public class RegisterActivityV2 extends Activity {

    private Context context;
    private boolean isCorrect = false;
    private String code, textCode;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.register_v2_telInput)
    private EditText telInput;
    @ViewInject(R.id.register_v2_pNameInput)
    private EditText pNameInput;
    @ViewInject(R.id.register_v2_sNameInput)
    private EditText sNameInput;
    @ViewInject(R.id.register_v2_passwordInput)
    private EditText passwordInput;
    @ViewInject(R.id.register_v2_passwordAgainInput)
    private EditText passwordAgainInput;
    @ViewInject(R.id.register_v2_registerBtn)
    private TextView registerBtn;
    @ViewInject(R.id.register_v2_passwordJudge)
    private ImageView passwordJudge;
    @ViewInject(R.id.register_v2_progress)
    private ProgressBar progress;
    @ViewInject(R.id.register_v2_telCodeName)
    private TextView telCodeName;
    @ViewInject(R.id.register_v2_telCode)
    private TextView telCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_v2);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
        setTextChangedListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        VipHandler.judgeVipTime(context, vipHint);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle b = data.getExtras();
            if (b != null) {
                switch (requestCode) {
                    case SMSActivity.RC:
                        if (b.getBoolean("ok")) {
                            finish();
                        }
                        break;
                }
            }
        }
    }

    @OnClick({R.id.titel_back, R.id.title_vip, R.id.register_v2_registerBtn,
            R.id.register_v2_telCodeName, R.id.register_v2_telCode})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titel_back:
                finish();
                break;
            case R.id.title_vip:
                VipHandler.jumpOnlyVipActivity(context);
                break;
            case R.id.register_v2_registerBtn:
                submit();
                break;
            case R.id.register_v2_telCodeName:
            case R.id.register_v2_telCode:
                showMsmDialog();
                break;

        }
    }

    private void showMsmDialog() {
        MsmHandler.showMsmDialog(context, new CallbackForInteger() {
            @Override
            public void callback(int i) {
                setCode(i);
            }
        });
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
        titleName.setText(getResources().getString(R.string.register));
        VipHandler.judgeVipTime(context, vipHint);
        passwordInput.setTypeface(Typeface.DEFAULT);
        passwordInput
                .setTransformationMethod(new PasswordTransformationMethod());
        passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        passwordAgainInput.setTypeface(Typeface.DEFAULT);
        passwordAgainInput
                .setTransformationMethod(new PasswordTransformationMethod());
        passwordAgainInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        setCode(0);
    }

    private void submit() {
        if (isCommit()) {
            isHavePhone();
//            showMessageDialog();
        }
    }

    private void showMessageDialog() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage(getResources().getString(R.string.phone_message)
                + "\n(" + MsmHandler.getCodeName(context, code) + ")+" + code + " " + telInput.getText().toString());
        dialog.setCommitStyle(getResources().getString(R.string.confirm));
        dialog.setCancelStyle(getResources().getString(R.string.cancel));
        dialog.setCancelListener(null);
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                Bundle b = new Bundle();
                b.putBoolean(SMSActivity.IS_REGISTER, true);
                b.putString(SMSActivity.TEL, telInput.getText().toString());
                b.putString(SMSActivity.P_NAME, pNameInput.getText().toString());
                b.putString(SMSActivity.S_NAME, sNameInput.getText().toString());
                b.putString(SMSActivity.PASS, passwordInput.getText()
                        .toString());
                b.putString(SMSActivity.RE_PASS, passwordAgainInput.getText()
                        .toString());
                b.putString(SMSActivity.CODE_KEY, textCode);
                Passageway.jumpActivity(context, SMSActivity.class,
                        SMSActivity.RC, b);
            }
        });
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
        if (passwordAgainInput.getText().toString().equals("")) {
            ShowMessage.showToast(context,
                    getResources().getString(R.string.input_password_again));
            return false;
        }
        if (!isCorrect) {
            ShowMessage.showToast(context,
                    getResources().getString(R.string.pass_not_consistent));
            return false;
        }
        return true;
    }

    public void setCode(int i) {
        code = String.valueOf(MsmHandler.getCode(i));
        textCode = MsmHandler.getCodeForText(i);
        telCode.setText("+" + code);
        telCodeName.setText(MsmHandler.getCodeName(context, i));
    }

    public void isHavePhone() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getCheckUser();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("mobile", telInput.getText().toString());

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.POST, url, params,
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
                            showMessageDialog();
                        }
                    }

                });
    }
}
