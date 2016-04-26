package com.atfpm.main.user;

import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.dialog.RegisterDialog;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.ImageHandle;
import com.atfpm.handler.MessageObjHandler;
import com.atfpm.handler.PushHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.interfaces.CallbackForBoolean;
import com.atfpm.interfaces.CallbackForString;
import com.atfpm.tool.Nationality;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.ShowMessage;
import com.atfpm.tool.WinTool;
import com.atfpm.vip.VipInscreverActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class UserFrameLayout extends Fragment implements View.OnClickListener {

    private Context context;

    private ImageView usetPic, usetType, pushIcon;
    private TextView loginBtn, registerBtn, usetName, myReserve, messageSum,
            vipContent;
    private LinearLayout loginMessageBox, compileBox, loginBox, logoutBox;
    private RelativeLayout contentBox, messageBox, likeBox, collectBox,
            aboutBox, vipLevelBox, helpBox, myReserveBox, nationalityBox,
            pushBox, privacidadeBox;
    private View pushLine;

    private ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View contactsLayout = inflater.inflate(R.layout.user_layout, container,
                false);
        initView(contactsLayout);
        initViewListener(this);
        return contactsLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("", "onResume");
        isLogin();
        setPushIcon();
        setSaveSize();
        if (UserObjHandler.isLogin(context)) {
            downloadMessageData();
        }
    }

    private void isLogin() {
        if (UserObjHandler.isLogin(context)) {
            usetName.setText(UserObjHandler.getUserName(context));
            loginBox.setVisibility(View.GONE);
            compileBox.setVisibility(View.VISIBLE);
            loginMessageBox.setVisibility(View.VISIBLE);
            logoutBox.setVisibility(View.VISIBLE);
            usetPic.setVisibility(View.VISIBLE);
            usetType.setVisibility(View.VISIBLE);
            pushBox.setVisibility(View.VISIBLE);
            pushLine.setVisibility(View.VISIBLE);
            vipContent.setVisibility(View.GONE);
            setUserPic();
            setUsetType();
        } else {
            usetName.setText(getResources().getString(R.string.welcome));
            loginBox.setVisibility(View.VISIBLE);
            loginMessageBox.setVisibility(View.GONE);
            usetPic.setVisibility(View.GONE);
            compileBox.setVisibility(View.GONE);
            logoutBox.setVisibility(View.GONE);
            pushBox.setVisibility(View.GONE);
            pushLine.setVisibility(View.GONE);
            usetType.setVisibility(View.GONE);
            vipContent.setVisibility(View.GONE);
        }
    }

    private void initView(View v) {
        usetName = (TextView) v.findViewById(R.id.user_usetName);
        usetPic = (ImageView) v.findViewById(R.id.user_usetPic);
        loginBtn = (TextView) v.findViewById(R.id.user_loginBtn);
        registerBtn = (TextView) v.findViewById(R.id.user_registerBtn);
        loginMessageBox = (LinearLayout) v
                .findViewById(R.id.user_loginMessageBox);
        compileBox = (LinearLayout) v.findViewById(R.id.user_compileBox);
        loginBox = (LinearLayout) v.findViewById(R.id.user_loginBox);
        logoutBox = (LinearLayout) v.findViewById(R.id.user_logoutBox);
        contentBox = (RelativeLayout) v
                .findViewById(R.id.peraonal_item_contentBox);
        messageBox = (RelativeLayout) v.findViewById(R.id.uset_myMseeageBox);
        messageSum = (TextView) v.findViewById(R.id.uset_myMseeageSum);
        likeBox = (RelativeLayout) v.findViewById(R.id.uset_myLikeBox);
        collectBox = (RelativeLayout) v.findViewById(R.id.user_collectBox);
        aboutBox = (RelativeLayout) v.findViewById(R.id.user_aboutBox);
        vipLevelBox = (RelativeLayout) v.findViewById(R.id.uset_vipLevelBox);
        helpBox = (RelativeLayout) v.findViewById(R.id.uesr_helpBox);
        myReserve = (TextView) v.findViewById(R.id.uset_myReserve);
        myReserveBox = (RelativeLayout) v.findViewById(R.id.uset_myReserveBox);
        nationalityBox = (RelativeLayout) v
                .findViewById(R.id.uesr_nationalityBox);
        pushBox = (RelativeLayout) v.findViewById(R.id.uesr_pushBox);
        usetType = (ImageView) v.findViewById(R.id.user_usetType);
        progress = (ProgressBar) v.findViewById(R.id.user_progress);
        pushIcon = (ImageView) v.findViewById(R.id.uesr_pushIcon);
        vipContent = (TextView) v.findViewById(R.id.user_vip_content);
        privacidadeBox = (RelativeLayout) v.findViewById(R.id.uesr_privacidadeBox);
        pushLine = v.findViewById(R.id.uesr_pushLine);
    }

    private void setUserPic() {
        int w = WinTool.getWinWidth(context) / 5;
        int r = w / 2;
        usetPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));

        DownloadImageLoader.loadImage(usetPic,
                UserObjHandler.getUserAvatar(context), r);
    }

    private void setUsetType() {
        UserObjHandler.setUsetType(context, usetType);
    }

    private void initViewListener(View.OnClickListener l) {
        loginBtn.setOnClickListener(l);
        registerBtn.setOnClickListener(l);
        compileBox.setOnClickListener(l);
        contentBox.setOnClickListener(l);
        messageBox.setOnClickListener(l);
        likeBox.setOnClickListener(l);
        collectBox.setOnClickListener(l);
        aboutBox.setOnClickListener(l);
        vipLevelBox.setOnClickListener(l);
        helpBox.setOnClickListener(l);
        logoutBox.setOnClickListener(l);
        nationalityBox.setOnClickListener(l);
        myReserveBox.setOnClickListener(l);
        usetPic.setOnClickListener(l);
        pushBox.setOnClickListener(l);
        vipContent.setOnClickListener(l);
        privacidadeBox.setOnClickListener(l);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.user_loginBtn:
                Passageway.jumpActivity(context, LoginActivity.class);
                break;
            case R.id.user_registerBtn:
//			Passageway.jumpActivity(context, RegisterActivityV2.class);
                RegisterDialog.showWarningDialog(context, RegisterDialog.COM_MAIN);
                break;
            case R.id.user_compileBox:
                Passageway.jumpActivity(context, UserCompileActivity.class);
                break;
            case R.id.peraonal_item_contentBox:
                Passageway.jumpActivity(context, SpeechActivity.class);
                break;
            case R.id.uset_myMseeageBox:
                Passageway.jumpActivity(context, MessageActivity.class);
                break;
            case R.id.uset_myLikeBox:
                Passageway.jumpActivity(context, LikeActivity.class);
                break;
            case R.id.user_collectBox:
                Passageway.jumpActivity(context, CollectActivity.class);
                break;
            case R.id.user_aboutBox:
                Passageway.jumpActivity(context, AboutActivity.class);
                break;
            case R.id.uset_vipLevelBox:
                Passageway.jumpActivity(context, VipLevelActivity.class);
                break;
            case R.id.uesr_helpBox:
                Passageway.jumpActivity(context, HelpActivity.class);
                break;
            case R.id.uesr_privacidadeBox:
                b.putBoolean(PrivacidadeActivity.KEY, false);
                Passageway.jumpActivity(context, PrivacidadeActivity.class, b);
                break;
            case R.id.user_logoutBox:
                logoutBtn();
                break;
            case R.id.uset_myReserveBox:
                deleteReserveBtn();
                break;
            case R.id.uesr_nationalityBox:
                Nationality.setNationality(context);
                break;
            case R.id.user_usetPic:
                Passageway.jumpActivity(context, PicCompileActivity.class);
                break;
            case R.id.uesr_pushBox:
                // Passageway.jumpActivity(context, PushActivity.class);
                onPushSwitch();
                break;
            case R.id.user_vip_content:
                Passageway.jumpActivity(context, VipInscreverActivity.class);
                break;
        }
    }

    private void onPushSwitch() {
        PushHandler.revamPush(context);
        setPushIcon();
    }

    private void setPushIcon() {
        if (PushHandler.isPush(context)) {
            pushIcon.setImageResource(R.drawable.push_open);
        } else {
            pushIcon.setImageResource(R.drawable.push_close);
        }
    }

    private void logoutBtn() {
        UserObjHandler.logout(context);
        isLogin();
    }

    private void setMessageSum() {
        MessageObjHandler.setMessageSum(context, messageSum);
    }

    private void downloadMessageData() {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getUserSendAndReceiveURL() + "?accesstoken="
                + UserObjHandler.getAccesstoken(context);

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
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
                            JSONObject resultJson = JsonHandle.getJSON(json,
                                    "result");
                            MessageObjHandler.setUserSendList(MessageObjHandler
                                    .getMessageObjList(JsonHandle.getArray(
                                            resultJson, "user_send_comment")));
                            MessageObjHandler.setUserReceiveList(MessageObjHandler.getMessageObjList(JsonHandle
                                    .getArray(resultJson,
                                            "user_receive_comment")));
                            setMessageSum();
                        }
                    }

                });
    }

    private void deleteReserveBtn() {
        progress.setVisibility(View.VISIBLE);
        ImageHandle.delete(new CallbackForBoolean() {

            @Override
            public void callback(boolean b) {
                if (b) {
                    setSaveSize();
                }
            }
        });
    }

    private void setSaveSize() {
        progress.setVisibility(View.VISIBLE);
        ImageHandle.getFileSum(new CallbackForString() {

            @Override
            public void callback(String result) {
                Log.e("", result);
                Message.obtain(handler, 1, result).sendToTarget();
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            progress.setVisibility(View.GONE);
            String s = (String) msg.obj;
            myReserve.setText(s + "MB");
        }

    };

}
