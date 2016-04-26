package com.atfpm.main;

import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;

import com.atfpm.R;
import com.atfpm.box.VersionObj;
import com.atfpm.dialog.MessageDialog;
import com.atfpm.dialog.MessageDialog.CallBackListener;
import com.atfpm.dialog.WarningDialog;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.download.DownloadNewAppService;
import com.atfpm.handler.PushHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VersionObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.main.court.CourtFrameLayout;
import com.atfpm.main.dynamic.DynamicFrameLayout;
import com.atfpm.main.intro.IntroFrameLayout;
import com.atfpm.main.speak.SpeakFrameLayoutV2;
import com.atfpm.main.user.UserFrameLayout;
import com.atfpm.nationality.NationalityActivity;
import com.atfpm.system.SystemHandle;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Nationality;
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

public class MainActivity extends Activity {

    private final static int INTRO = 1;
    private final static int SPEAK = 2;
    private final static int DYNAMIC = 3;
    private final static int COURT = 4;
    private final static int USER = 5;

    private final static long EXITTIME = 2000;
    private long EXIT = 0;
    private long talkTime = 0, topicTime = 0, messageTime = 0;
    private boolean isRun = true, isDown = false;

    private Context context;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipBox)
    private RelativeLayout activityBox;
    @ViewInject(R.id.titel_switchover)
    private ImageView titleSwitchover;
    @ViewInject(R.id.titel_back)
    private ImageView titleBack;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.main_content)
    private FrameLayout content;
    @ViewInject(R.id.main_intro_icon)
    private ImageView introIcon;
    @ViewInject(R.id.main_intro_name)
    private TextView introName;
    @ViewInject(R.id.main_intro_line)
    private View introLine;
    @ViewInject(R.id.main_speak_icon)
    private ImageView speakIcon;
    @ViewInject(R.id.main_speak_name)
    private TextView speakName;
    @ViewInject(R.id.main_speak_line)
    private View speakLine;
    @ViewInject(R.id.main_dynamic_icon)
    private ImageView dynamicIcon;
    @ViewInject(R.id.main_dynamic_name)
    private TextView dynamicName;
    @ViewInject(R.id.main_dynamic_line)
    private View dynamicLine;
    @ViewInject(R.id.main_court_icon)
    private ImageView courtIcon;
    @ViewInject(R.id.main_court_name)
    private TextView courtName;
    @ViewInject(R.id.main_court_line)
    private View courtLine;
    @ViewInject(R.id.main_user_icon)
    private ImageView userIcon;
    @ViewInject(R.id.main_user_name)
    private TextView userName;
    @ViewInject(R.id.main_user_line)
    private View userLine;
    @ViewInject(R.id.main_progress)
    private ProgressBar progress;
    @ViewInject(R.id.main_protect)
    private View protect;
    @ViewInject(R.id.main_speakHint)
    private ImageView talkHint;
    @ViewInject(R.id.main_courtHint)
    private ImageView topicHint;
    @ViewInject(R.id.main_messageHint)
    private ImageView messageHint;

    private IntroFrameLayout introFrameLayout;
    private SpeakFrameLayoutV2 speakFrameLayout;
    private DynamicFrameLayout dynamicFrameLayout;
    private CourtFrameLayout courtFrameLayout;
    private UserFrameLayout userFrameLayout;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Nationality.initNationality(context);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        initAcitvity();
        isFirst();
        WarningDialog.showWarningDialog(context, WarningDialog.COM_MAIN);
        // detectionVersion();
        // setTabSelection(DYNAMIC);

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(context);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        VipHandler.judgeVipTime(context, vipHint);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
        isDown = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - EXIT < EXITTIME) {
                finish();
            } else {
                ShowMessage.showToast(context,
                        getResources().getString(R.string.exit));
            }
            EXIT = System.currentTimeMillis();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle b = data.getExtras();
            switch (resultCode) {
                case UploadDialogActivity.UPLOAD_REQUEST_CODE:
                    if (b != null) {
                        if (b.getBoolean("isFinish")) {
                            finish();
                        } else {
                            staterActivity();
                        }
                    }
                    break;
                case NationalityActivity.resultCode:
                    if (b != null) {
                        if (b.getBoolean(NationalityActivity.KEY)) {
                            finish();
                        }
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.titel_switchover, R.id.title_vip, R.id.main_intro,
            R.id.main_speak, R.id.main_dynamic, R.id.main_court,
            R.id.main_user, R.id.main_protect, R.id.title_activityBtn})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titel_switchover:
                Nationality.setNationality(context);
                break;
            case R.id.title_vip:
                VipHandler.jumpOnlyVipActivity(context);
                break;
            case R.id.main_intro:
                setTabSelection(INTRO);
                break;
            case R.id.main_speak:
                setTabSelection(SPEAK);
                break;
            case R.id.main_dynamic:
                setTabSelection(DYNAMIC);
                break;
            case R.id.main_court:
                setTabSelection(COURT);
                break;
            case R.id.main_user:
                setTabSelection(USER);
                break;
            case R.id.main_protect:
                Log.e("", "Protect");
                break;
            case R.id.title_activityBtn:
                VipHandler.jumpActivityListActivity(context);
                break;
        }

    }

    private void setTabSelection(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        clearSelection();
        hideFragments(transaction);
        switch (index) {
            case INTRO:
                onIntro(transaction);
                break;
            case SPEAK:
                onSpeak(transaction);
                break;
            case DYNAMIC:
                onDynamic(transaction);
                break;
            case COURT:
                onCourt(transaction);
                break;
            case USER:
                onUser(transaction);
                break;
        }
        setTalkHint();
        setTopicHint();
        setMessageHint();
        transaction.commit();
    }

    private void onUser(FragmentTransaction transaction) {
        titleName.setText(getResources().getString(R.string.user_name));
        userIcon.setImageResource(R.drawable.user_g_icon);
        userName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_green_01));
        if (userFrameLayout == null) {
            userFrameLayout = new UserFrameLayout();
            transaction.add(R.id.main_content, userFrameLayout);
        } else {
            transaction.show(userFrameLayout);
        }
        SystemHandle.setMessageTime(context, messageTime);
    }

    private void onCourt(FragmentTransaction transaction) {
        titleName.setText(getResources().getString(R.string.court_name));
        courtIcon.setImageResource(R.drawable.court_g_icon);
        courtName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_green_01));
        if (courtFrameLayout == null) {
            courtFrameLayout = new CourtFrameLayout();
            transaction.add(R.id.main_content, courtFrameLayout);
        } else {
            transaction.show(courtFrameLayout);
        }
        SystemHandle.setTopicTime(context, topicTime);
    }

    private void onDynamic(FragmentTransaction transaction) {
        titleName.setText(getResources().getString(R.string.main_title_name));
        dynamicName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_green_01));
        if (dynamicFrameLayout == null) {
            dynamicFrameLayout = new DynamicFrameLayout();
            transaction.add(R.id.main_content, dynamicFrameLayout);
        } else {
            transaction.show(dynamicFrameLayout);
        }
    }

    private void onSpeak(FragmentTransaction transaction) {
        titleName.setText(getResources().getString(R.string.speak_name));
        speakIcon.setImageResource(R.drawable.speak_g_icon);
        speakName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_green_01));
        if (speakFrameLayout == null) {
            speakFrameLayout = new SpeakFrameLayoutV2();
            transaction.add(R.id.main_content, speakFrameLayout);
        } else {
            transaction.show(speakFrameLayout);
        }
        SystemHandle.setTalkTime(context, talkTime);
    }

    private void onIntro(FragmentTransaction transaction) {
        titleName.setText(getResources().getString(R.string.intro_name));
        introIcon.setImageResource(R.drawable.intro_g_icon);
        introName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_green_01));
        if (introFrameLayout == null) {
            introFrameLayout = new IntroFrameLayout();
            transaction.add(R.id.main_content, introFrameLayout);
        } else {
            transaction.show(introFrameLayout);
        }
    }

    private void clearSelection() {
        introIcon.setImageResource(R.drawable.intro_w_icon);
        speakIcon.setImageResource(R.drawable.speak_w_icon);
        dynamicIcon.setImageResource(R.drawable.dynamic_w_icon);
        courtIcon.setImageResource(R.drawable.court_w_icon);
        userIcon.setImageResource(R.drawable.user_w_icon);

        introName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_02));
        speakName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_02));
        dynamicName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_02));
        courtName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_02));
        userName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_02));

        introLine.setVisibility(View.INVISIBLE);
        speakLine.setVisibility(View.INVISIBLE);
        dynamicLine.setVisibility(View.INVISIBLE);
        courtLine.setVisibility(View.INVISIBLE);
        userLine.setVisibility(View.INVISIBLE);
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (introFrameLayout != null) {
            transaction.hide(introFrameLayout);
        }
        if (speakFrameLayout != null) {
            transaction.hide(speakFrameLayout);
        }
        if (dynamicFrameLayout != null) {
            transaction.hide(dynamicFrameLayout);
        }
        if (courtFrameLayout != null) {
            transaction.hide(courtFrameLayout);
        }
        if (userFrameLayout != null) {
            transaction.hide(userFrameLayout);
        }
    }

    private void initAcitvity() {
        activityBox.setVisibility(View.VISIBLE);
        VipHandler.judgeVipTime(context, vipHint);
        titleSwitchover.setVisibility(View.INVISIBLE);
        titleBack.setVisibility(View.INVISIBLE);
        fragmentManager = getFragmentManager();
        WinTool.initWinTool(context);
        DownloadImageLoader.initLoader(context);
        SMSSDK.initSDK(context, "7aa14eff138b",
                "60c5be227613e94c81a4a1507d1736ce");
        clearSelection();

        PushHandler.init(context);
    }

    private void isFirst() {
        if (SystemHandle.isFlist(context)) {
            Passageway.jumpActivity(context, NationalityActivity.class,
                    NationalityActivity.resultCode);
        } else {
            detectionVersion();
        }
    }

    private void staterActivity() {
        protect.setVisibility(View.GONE);
        setTabSelection(DYNAMIC);
        startVipTipsRun();
    }

    private void showMessageVersionShort(final VersionObj obj) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setTitel(getResources().getString(R.string.new_version_short));
        dialog.setMessage(obj.getChangelog());
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                downloadApp(obj.getUpdate_url());
            }

        });
        dialog.setCancelStyle(getResources().getString(R.string.close));
        dialog.setCancelListener(new CallBackListener() {

            @Override
            public void callback() {
                finish();
            }
        });
    }

    private void showMessageVersion(final VersionObj obj) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setTitel(getResources().getString(R.string.new_version));
        dialog.setMessage(obj.getChangelog());
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                downloadApp(obj.getUpdate_url());
            }

        });
        dialog.setCancelListener(new CallBackListener() {

            @Override
            public void callback() {
                staterActivity();
            }
        });
    }

    private boolean isDetectionVersionShort(VersionObj obj) {
        if (obj != null) {
            if (VersionObjHandler.detectionVersionShort(context, obj)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDetectionVersion(VersionObj obj) {
        if (obj != null) {
            if (VersionObjHandler.detectionVersion(context, obj)) {
                return true;
            }
        }
        return false;
    }

    private void detectionVersion(VersionObj obj) {
        if (isDetectionVersion(obj)) {
            Bundle b = new Bundle();
            b.putString("changelog", obj.getChangelog());
            b.putString("update_url", obj.getUpdate_url());
            if (isDetectionVersionShort(obj)) {
                b.putBoolean("must", true);
//                showMessageVersionShort(obj);
            } else {
                b.putBoolean("must", false);
//                showMessageVersion(obj);
            }
            Passageway.jumpActivity(context, UploadDialogActivity.class, UploadDialogActivity.UPLOAD_REQUEST_CODE, b);
        } else {
            staterActivity();
        }
    }

    private void setTalkHint() {
        Log.e("", talkTime + "========" + SystemHandle.getTalkTime(context));
        if (talkTime > SystemHandle.getTalkTime(context) && talkTime > 0) {
            talkHint.setVisibility(View.VISIBLE);
        } else {
            talkHint.setVisibility(View.INVISIBLE);
        }
    }

    private void setTopicHint() {
        if (topicTime > SystemHandle.getTopicTime(context) && topicTime > 0) {
            topicHint.setVisibility(View.VISIBLE);
        } else {
            topicHint.setVisibility(View.INVISIBLE);
        }
    }

    private void setMessageHint() {
        if (messageTime > SystemHandle.getMessageTime(context) && messageTime > 0) {
            messageHint.setVisibility(View.VISIBLE);
        } else {
            messageHint.setVisibility(View.INVISIBLE);
        }
    }

    private void detectionVersion() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getVersionURL();

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        // staterActivity();
                        ShowMessage.showException(context, exception);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(result);
                        JSONObject versionJson = JsonHandle.getJSON(json,
                                "android");
                        VersionObj obj = null;
                        if (versionJson != null) {
                            obj = VersionObjHandler.getVersionObj(versionJson);
                        }
                        detectionVersion(obj);
                    }

                });

    }

    private void downloadVipTips() {
        isDown = true;
        String url = UrlHandle.getTipsURL() + "?accesstoken=" + UserObjHandler.getAccesstoken(context);

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        isDown = false;
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("", result);
                        JSONObject json = JsonHandle.getJSON(result);
                        if (json != null) {
                            long vipTime = JsonHandle.getLong(json, "vip");
                            talkTime = JsonHandle.getLong(json, "talk");
                            topicTime = JsonHandle.getLong(json, "topic");
                            messageTime = JsonHandle.getLong(json, "message");
                            VipHandler.setVipTime(vipTime);
                            VipHandler.judgeVipTime(context, vipHint);
                            setTalkHint();
                            setTopicHint();
                            setMessageHint();
                        }
                        isDown = false;
                    }

                });
    }

    private void startVipTipsRun() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (isRun) {
                    try {
                        if (!isDown) {
                            downloadVipTips();
                        }
                        Thread.sleep(1000 * 60);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private void downloadApp(String update_url) {
        Bundle b = new Bundle();
        b.putString(DownloadNewAppService.KEY, update_url);
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(context, DownloadNewAppService.class);
        startService(i);
    }
}
