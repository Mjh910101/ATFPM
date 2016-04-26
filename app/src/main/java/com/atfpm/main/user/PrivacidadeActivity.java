package com.atfpm.main.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.http.UrlHandle;
import com.atfpm.tool.Passageway;
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
 * Created by Hua on 15/11/26.
 */
public class PrivacidadeActivity extends Activity {

    public final static String KEY = "privacidade_key";
    private Context context;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.privacidade_toolBox)
    private LinearLayout toolBox;
    @ViewInject(R.id.privacidade_content)
    private WebView content;
    @ViewInject(R.id.titel_back)
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacidade);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
    }

    @OnClick({R.id.titel_back, R.id.privacidade_concorda, R.id.privacidade_sair})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacidade_concorda:
                Passageway.jumpActivity(context, RegisterActivityV2.class);
            case R.id.privacidade_sair:
            case R.id.titel_back:
                finish();
                break;
        }
    }

    private void initAcitvity() {
        titleName.setText(getResources().getString(R.string.privacidade));

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getBoolean(KEY)) {
                setRegisterPrivacidade();
            } else {
                setPrivacidade();
            }
        }
    }

    private void setPrivacidade() {
        content.loadUrl(UrlHandle.getPrivacidadeURL());
    }

    private void setRegisterPrivacidade() {
        toolBox.setVisibility(View.VISIBLE);
        back.setVisibility(View.GONE);
        content.loadUrl("file:///android_asset/privacidade_index.html");
    }

}
