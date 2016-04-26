package com.atfpm.vip;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.ActiveObj;
import com.atfpm.box.OriginObj;
import com.atfpm.handler.ActiveObjHandler;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.interfaces.CallbackForString;
import com.atfpm.main.dynamic.DynamicListBaseAdapter;
import com.atfpm.main.intro.MyWebChromeClient;
import com.atfpm.main.intro.MyWebViewClient;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Nationality;
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
 * Created by Hua on 15/11/24.
 */
public class ActiveListActicity extends Activity {

    private final static int LIMIT = 20;

    private Context context;
    private long time;
    private boolean isShow = true;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.title_vipBox)
    private RelativeLayout vipBox;
    @ViewInject(R.id.title_activityBtn)
    private TextView activityBtn;
    @ViewInject(R.id.title_activityPic)
    private ImageView activityPic;
    @ViewInject(R.id.title_bg)
    private RelativeLayout titleBg;
    @ViewInject(R.id.titel_back)
    private ImageView back;
    @ViewInject(R.id.activity_dataList)
    private ListView dataList;
    @ViewInject(R.id.activity_progress)
    private ProgressBar progress;
    @ViewInject(R.id.activity_dataListRefresh)
    private SwipeRefreshLayout dataListRefresh;

    private ActiveListBaseAdapterV2 lba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_list);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
        setDataListScrollListener();
        setOnRefreshListener();
        dowmloadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lba != null) {
            lba.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
        }
        return false;
    }

    @OnClick({R.id.title_vipBox})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_vipBox:
                close();
                break;
        }
    }

    private void close() {
        finish();
        overridePendingTransition(R.anim.circulation_in, R.anim.circulation_out);
    }

    private void initAcitvity() {
        int color = ColorBox.getColorForID(context, R.color.vip_title_bg);
        titleBg.setBackgroundColor(color);
        titleName.setText(getResources().getString(R.string.activity_text));
        titleName.setTextColor(ColorBox.getColorForID(context, R.color.white));
        back.setVisibility(View.INVISIBLE);
        vipBox.setVisibility(View.VISIBLE);
        activityBtn.setText(getResources().getString(R.string.dynamic_text));
        activityBtn.setTextColor(ColorBox.getColorForID(context, R.color.white));
        activityPic.setImageResource(R.drawable.activity_while_icon);
//        activityBtn.setBackgroundResource(R.drawable.dynamic_btn_sype);
    }

    private void setOnRefreshListener() {
        dataListRefresh.setColorScheme(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        dataListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                isShow = true;
                time = 0;
                if (lba != null) {
                    lba.removeAll();
                }
                dowmloadData();
            }
        });
    }

    private void setDataListScrollListener() {
        dataList.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                        if (progress.getVisibility() == View.GONE && isShow) {
                            dowmloadData();
                        } else {
                            if (isShow) {
                                isShow = false;
                                ShowMessage.showLast(context);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

            }
        });
    }

    private void setDataList(List<OriginObj> list) {
        if (!list.isEmpty() && list.size() > 0) {
            if (lba == null) {
                lba = new ActiveListBaseAdapterV2(context, list);
                dataList.setAdapter(lba);
            } else {
                lba.addItems(list);
            }
            time = lba.getLsatTime();
        } else {
            if (isShow) {
                isShow = false;
                ShowMessage.showLast(context);
            }
        }
        if (dataListRefresh.isRefreshing()) {
            dataListRefresh.setRefreshing(false);
        }
    }


    private void dowmloadData() {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getActive() + "?time=" + time + "&limit="
                + LIMIT;

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
                            List<OriginObj> list = DynamicObjHandler.getOriginObjList(JsonHandle.getArray(resultJson, "post"));
                            setDataList(list);
                        }
                    }

                });
    }

}
