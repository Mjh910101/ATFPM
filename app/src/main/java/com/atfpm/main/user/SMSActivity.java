package com.atfpm.main.user;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.atfpm.dialog.MessageDialog;
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

public class SMSActivity extends Activity {

    //	private final static String CODE = "86";
//    private final static String CODE = "853";

    public final static int RC = 18;

    public final static String IS_REGISTER = "ISREGISTER";
    public final static String TEL = "TEL";
    public final static String P_NAME = "P_NAME";
    public final static String S_NAME = "S_NAME";
    public final static String PASS = "PASS";
    public final static String RE_PASS = "RE_PASS";
    public final static String CODE_KEY = "CODE_KEY";

    private Context context;

    private String tel = "", pName = "", sName = "", pass = "", re_pass = "", smsId;
    private boolean isRegister = false;
    private String country;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.sms_telInput)
    private TextView telText;
    @ViewInject(R.id.sms_getCode)
    private TextView getCodeText;
    @ViewInject(R.id.sms_progress)
    private ProgressBar progress;
    @ViewInject(R.id.sms_codeInput)
    private EditText codeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
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

    @OnClick({R.id.titel_back, R.id.title_vip, R.id.sms_confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titel_back:
                finish();
                break;
            case R.id.title_vip:
                VipHandler.jumpOnlyVipActivity(context);
                break;
            case R.id.sms_confirmBtn:
                String code = codeInput.getText().toString();
                verifyCode(code);
//                SMSSDK.submitVerificationCode(country, tel, code);
                break;
        }
    }

    private void initAcitvity() {
        SMSSDK.registerEventHandler(eh);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            isRegister = b.getBoolean(IS_REGISTER);
            pName = b.getString(P_NAME);
            sName = b.getString(S_NAME);
            pass = b.getString(PASS);
            re_pass = b.getString(RE_PASS);
            country = b.getString(CODE_KEY);
            getCode(country, b.getString(TEL));
            if (isRegister) {
                titleName.setText(getResources().getString(R.string.register));
            } else {
                titleName.setText(getResources().getString(
                        R.string.forget_password));
            }
        }
        VipHandler.judgeVipTime(context, vipHint);
    }

    private void close() {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putBoolean("ok", true);
        i.putExtras(b);
        setResult(RC, i);
        finish();
    }

    private void verifyCode(final String code) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getVerify();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("verify", code);
        params.addBodyParameter("smsId", smsId);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                        Message.obtain(msgHandler, 404).sendToTarget();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(result);
//                        if (!ShowMessage.showException(context, json)) {
                        if (json != null) {
                            Message.obtain(msgHandler, 200).sendToTarget();
                        }
                    }

                });
    }

    @Deprecated
    private void getCode(String tel) {
        this.tel = tel;
        telText.setText(tel);
        Log.e("phone ==>>", tel);
        SMSSDK.getVerificationCode(country, tel);
        startClock();
    }

    private void getCode(String coad, String tel) {
        this.tel = tel;
        telText.setText(tel);
        Log.e("phone ==>>", tel);
//        SMSSDK.getVerificationCode(country, tel);
        getMsmCode(coad + tel);
        startClock();
    }

    private void getMsmCode(String tel) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getSendVerify() + "?mobile=" + tel;


        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url,
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

                        JSONObject json = JsonHandle.getJSON(JsonHandle.getJSON(result), "result");
//                        if (!ShowMessage.showException(context, json)) {
                        if (json != null) {
                            smsId = JsonHandle.getString(json, "smsid");
                            Log.e("", "smsId : " + smsId);
                        }
                    }

                });

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
        if (isRegister) {
            submitRegister();
        } else {
            submitForget();
        }
    }

    private void submitForget() {
        String url = UrlHandle.getForgetURL();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("mobile", tel);
        params.addBodyParameter("newPass", pass);
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
                                close();
                            }
                        }
                    }

                });
    }

    private void submitRegister() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getRegisterURL();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("mobile", tel);
        params.addBodyParameter("p_name", pName);
        params.addBodyParameter("s_name", sName);
        params.addBodyParameter("pass", pass);
        params.addBodyParameter("re_pass", re_pass);

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
                                close();
                            }
                        }
                    }

                });
    }

    private Handler clockHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int time = msg.what;
            getCodeText.setText(time
                    + getResources().getString(R.string.get_code_time));
            if (time == 0) {
                getCodeText
                        .setText(getResources().getString(R.string.get_code));
                getCodeText.setClickable(true);
            }
        }

    };

    private void showSmsException() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage(getResources().getString(R.string.sms_exception_v2));
        dialog.setCommitStyle(getResources().getString(R.string.confirm));
        dialog.setCommitListener(null);
    }

    private Handler msgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    submit();
                    break;
                default:
                    showSmsException();
                    break;
            }

        }

    };

    EventHandler eh = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) { // 回调完成
                Log.e("", "回调完成");
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    Log.e("", "提交验证码成功");
                    Message.obtain(msgHandler, 200).sendToTarget();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {// 获取验证码成功
                    Log.e("", "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表

                }
            } else {
                Log.e("", "回调失败");
                ((Throwable) data).printStackTrace();
                Message.obtain(msgHandler, 404).sendToTarget();
            }
        }

    };

}
