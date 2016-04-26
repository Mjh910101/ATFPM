package com.atfpm.main.user;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.DynamicObj;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.main.dynamic.DynamicListBaseAdapter;
import com.atfpm.main.speak.SpeakContentActivity;
import com.atfpm.main.speak.SpeakListBaseAdapter;
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

public class SpeechActivity extends Activity {

    private Context context;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.speak_dataList)
    private ListView dataList;
    @ViewInject(R.id.speak_progress)
    private ProgressBar progress;
    @ViewInject(R.id.speak_dataListRefresh)
    private SwipeRefreshLayout dataListRefresh;
    @ViewInject(R.id.speak_notContent)
    private TextView notContent;
    @ViewInject(R.id.speak_addNewContent)
    private ImageView addNewContent;

    private SpeakListBaseAdapter slba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
        setOnRefreshListener();
        downloadData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        VipHandler.judgeVipTime(context, vipHint);
    }

    @OnClick({R.id.titel_back, R.id.title_vip})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titel_back:
                finish();
                break;
            case R.id.title_vip:
                VipHandler.jumpOnlyVipActivity(context);
                break;
        }
    }

    private void initAcitvity() {
        titleName.setText(getResources().getString(R.string.my_speak));
        addNewContent.setVisibility(View.GONE);
        VipHandler.judgeVipTime(context, vipHint);
    }

    private void setOnRefreshListener() {
        dataListRefresh.setColorScheme(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        dataListRefresh.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (slba != null) {
                    slba.removeAll();
                }
                downloadData();
            }
        });
    }

    private void setDataList(List<DynamicObj> list) {
        slba = new SpeakListBaseAdapter(context, progress);
        slba.addDynamicObjItems(list);
        dataList.setAdapter(slba);

        if (dataListRefresh.isRefreshing()) {
            dataListRefresh.setRefreshing(false);
        }
    }

    private void downloadData() {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getUserPostURL() + "?accesstoken="
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
                            // JSONObject resultJson = JsonHandle.getJSON(json,
                            // "result");
                            JSONArray array = JsonHandle.getArray(json,
                                    "result");
                            if (array != null) {
                                List<DynamicObj> list = DynamicObjHandler
                                        .getDynamicObjList(array);
                                if (list.size() > 0) {
                                    setDataList(list);
                                } else {
                                    notContent.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                });
    }
}
