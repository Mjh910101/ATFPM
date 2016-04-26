package com.atfpm.vip;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.ActiveObj;
import com.atfpm.box.DynamicObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.ActiveObjHandler;
import com.atfpm.handler.DateHandle;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.open.ImageListActivity;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.ShowMessage;
import com.atfpm.tool.WinTool;
import com.atfpm.view.LazyWebView;
import com.atfpm.view.VestrewWebView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
 * Created by Hua on 15/11/25.
 */
public class ActiveContentActivity extends Activity {

    private Context context;

    private DynamicObj obj;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.title_bg)
    private RelativeLayout titleBg;
    @ViewInject(R.id.titel_back)
    private ImageView back;
    @ViewInject(R.id.active_content_progress)
    private ProgressBar progress;
    @ViewInject(R.id.active_content_userPic)
    private ImageView userPic;
    @ViewInject(R.id.active_content_userName)
    private TextView userName;
    @ViewInject(R.id.active_content_time)
    private TextView time;
    @ViewInject(R.id.active_content_title)
    private TextView contentTitle;
    @ViewInject(R.id.active_content_contextWeb)
    private LazyWebView contextWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_content);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
    }

    @OnClick({R.id.titel_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titel_back:
                finish();
                break;
        }
    }

    private void initAcitvity() {
        int color = ColorBox.getColorForID(context, R.color.vip_title_bg);
        titleBg.setBackgroundColor(color);
        titleName.setText(getResources().getString(R.string.dynamic_content));
        titleName.setTextColor(ColorBox.getColorForID(context, R.color.white));
        back.setImageResource(R.drawable.back_white);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            downloadData(b.getString(ActiveObj.ID));
        }
    }


    public void setDataView(DynamicObj obj) {
        int w = WinTool.getWinWidth(context) / 10;
        userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(userPic, obj.getUsetPic(), w / 2);

        userName.setTextColor(ColorBox.getColorForID(context, R.color.text_blue_01));
        userName.setText(obj.getUsetName());
        time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt()));
        contentTitle.setText(obj.getTitle());

        setContextWeb(obj);
    }

    private void setContextWeb(DynamicObj obj) {
        Log.e("", obj.getContent());
        Log.e("", Html.fromHtml(obj.getContent()).toString());
        String contentStr = VestrewWebView.addJavaScript(Html.fromHtml(
                obj.getContent()).toString());
        // String contentStr = VestrewWebView.addJavaScript(obj.getContent());
        contextWeb.getSettings().setJavaScriptEnabled(true);
        contextWeb.addJavascriptInterface(this, "ImageOnClick");
        contextWeb.setWebChromeClient(new WebChromeClient());
        contextWeb.setFocusable(false);
        contextWeb.loadData(contentStr);
    }

    @JavascriptInterface
    public void onClickForImg(final String imgURL) {
        Log.d("OnClick", imgURL);
        Bundle b = new Bundle();
        b.putStringArrayList("DataList",
                (ArrayList<String>) getImageList(imgURL));
        b.putInt("position", 0);
        Passageway.jumpActivity(context, ImageListActivity.class, b);
    }

    private List<String> getImageList(String imgURL) {
        List<String> imgList = new ArrayList<String>();
        imgList.add(imgURL);
        return imgList;
    }

    private void downloadData(String id) {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getActive() + "/" + id;

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.GET, url, params,
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
                            obj = DynamicObjHandler.getDynamicObj(JsonHandle
                                    .getJSON(resultJson, "post"));
                            setDataView(obj);
                        }
                    }

                });
    }

}
