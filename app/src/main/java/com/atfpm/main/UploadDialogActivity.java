package com.atfpm.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.dialog.WarningDialog;
import com.atfpm.download.DownloadNewAppService;
import com.atfpm.tool.Nationality;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * *
 * * ┏┓      ┏┓
 * *┏┛┻━━━━━━┛┻┓
 * *┃          ┃
 * *┃          ┃
 * *┃ ┳┛   ┗┳  ┃
 * *┃          ┃
 * *┃    ┻     ┃
 * *┃          ┃
 * *┗━┓      ┏━┛
 * *  ┃      ┃
 * *  ┃      ┃
 * *  ┃      ┗━━━┓
 * *  ┃          ┣┓
 * *  ┃         ┏┛
 * *  ┗┓┓┏━━━┳┓┏┛
 * *   ┃┫┫   ┃┫┫
 * *   ┗┻┛   ┗┻┛
 * Created by Hua on 15/11/13.
 */
public class UploadDialogActivity extends Activity {

    public final static int UPLOAD_REQUEST_CODE = 1024;

    private Context context;

    @ViewInject(R.id.messageDialog_title)
    private TextView title;
    @ViewInject(R.id.messageDialog_message)
    private TextView message;
    @ViewInject(R.id.messageDialog_commit)
    private TextView commit;
    @ViewInject(R.id.messageDialog_cancel)
    private TextView cancel;
    @ViewInject(R.id.messageDialog_commitBox)
    private FrameLayout commitBox;
    @ViewInject(R.id.messageDialog_cancelBox)
    private FrameLayout cancelBox;


    private boolean isMust = false;
    private String update_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Nationality.initNationality(context);
        setContentView(R.layout.activity_upload_dialog);
        ViewUtils.inject(this);

        initActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.messageDialog_commit, R.id.messageDialog_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.messageDialog_commit:
                downloadApp(update_url);
                break;
            case R.id.messageDialog_cancel:
                close();
                break;
        }
    }

    private void close() {
        Intent i = new Intent();
        Bundle b = new Bundle();
        if (isMust) {
            b.putBoolean("isFinish", true);
        } else {
            b.putBoolean("isFinish", false);
        }
        i.putExtras(b);
        setResult(UPLOAD_REQUEST_CODE, i);
        finish();
    }

    private void downloadApp(String update_url) {
        Bundle b = new Bundle();
        b.putString(DownloadNewAppService.KEY, update_url);
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(context, DownloadNewAppService.class);
        startService(i);
    }

    private void initActivity() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            commitBox.setVisibility(View.VISIBLE);
            cancelBox.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);

            commit.setText(getResources().getText(R.string.confirm));
            update_url = mBundle.getString("update_url");

            isMust = mBundle.getBoolean("must");
            if (isMust) {
                title.setText(getResources().getText(R.string.new_version_short));
                cancel.setText(getResources().getText(R.string.close));
            } else {
                title.setText(getResources().getText(R.string.new_version));
                cancel.setText(getResources().getText(R.string.cancel));
            }

            String m = mBundle.getString("changelog");
            if (!m.equals("")) {
                message.setVisibility(View.VISIBLE);
                message.setText(m);
            }
        }
    }

}