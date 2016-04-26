package com.atfpm.main.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.DynamicObj;
import com.atfpm.box.OriginObj;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.main.dynamic.DynamicListBaseAdapter;
import com.atfpm.main.speak.SpeakListBaseAdapter;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.ShowMessage;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class LikeActivity extends Activity {

    private final static int DYNAMIC = 1;
    private final static int SPEAK = 2;

    private int now_tap = 2;

    private Context context;

    private List<DynamicObj> dynamicObjList;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.like_dynamicTitle)
    private TextView dynamicTitle;
    @ViewInject(R.id.like_dynamicLine)
    private View dynamicLine;
    @ViewInject(R.id.like_speakTitle)
    private TextView speakTitle;
    @ViewInject(R.id.like_speakLine)
    private View speakLine;
    @ViewInject(R.id.like_dataBg)
    private LinearLayout dataBg;
    @ViewInject(R.id.like_dataList)
    private ListView dataList;
    @ViewInject(R.id.like_progress)
    private ProgressBar progress;
    @ViewInject(R.id.like_notContent)
    private TextView notContent;
    @ViewInject(R.id.like_speak_bg)
    private View speakBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
        downloadData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        VipHandler.judgeVipTime(context, vipHint);
    }

    @OnClick({R.id.titel_back, R.id.title_vip, R.id.like_dynamicTitle,
            R.id.like_speakTitle})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titel_back:
                finish();
                break;
            case R.id.title_vip:
                VipHandler.jumpOnlyVipActivity(context);
                break;
            case R.id.like_dynamicTitle:

                setTitleSelection(DYNAMIC);
                break;
            case R.id.like_speakTitle:
                setTitleSelection(SPEAK);
                break;
        }
    }

    private void initAcitvity() {
        titleName.setText(getResources().getString(R.string.my_like));
        VipHandler.judgeVipTime(context, vipHint);

        dynamicObjList = new ArrayList<DynamicObj>();
        initTitleSelection();
        speakTitle.performClick();
    }

    private void initTitleSelection() {
        dynamicTitle.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_01));
        dynamicLine.setVisibility(View.INVISIBLE);
        speakTitle.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_01));
        speakLine.setVisibility(View.INVISIBLE);
        dataBg.setVisibility(View.GONE);
        speakBg.setVisibility(View.GONE);
        dataList.setAdapter(null);
    }

    private void setTitleSelection(int i) {
        initTitleSelection();
        now_tap = i;
        switch (i) {
            case DYNAMIC:
                dynamicTitle.setTextColor(ColorBox.getColorForID(context,
                        R.color.text_green_01));
                dynamicLine.setVisibility(View.VISIBLE);
                dataList.setAdapter(new DynamicListBaseAdapter(context,
                        getNewsList(), OriginObj.CONTENT, progress));
                break;
            case SPEAK:
                speakTitle.setTextColor(ColorBox.getColorForID(context,
                        R.color.text_green_01));
                speakLine.setVisibility(View.VISIBLE);

                if(!dynamicObjList.isEmpty()) {
                    speakBg.setVisibility(View.VISIBLE);
                }else{
                    speakBg.setVisibility(View.INVISIBLE);
                }
//                dataBg.setVisibility(View.VISIBLE);
                dataList.setAdapter(new SpeakListBaseAdapter(context,
                        getTalkList(), OriginObj.CONTENT, progress));
                break;
        }
    }

    private List<DynamicObj> getTalkList() {
        List<DynamicObj> list = new ArrayList<DynamicObj>();
        for (DynamicObj obj : dynamicObjList) {
            if (obj.isTalk()) {
                list.add(obj);
            }
        }

        return list;
    }

    private List<DynamicObj> getNewsList() {
        List<DynamicObj> list = new ArrayList<DynamicObj>();
        for (DynamicObj obj : dynamicObjList) {
            if (obj.isNews()) {
                list.add(obj);
            }
        }
        Log.e("", list.size() + "");
        return list;
    }

    private void setDataList(List<DynamicObj> list) {
        for (DynamicObj obj : list) {
            obj.setIsGood(1);
            dynamicObjList.add(obj);
        }
        speakTitle.performClick();
    }

    private void downloadData() {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getGoodURL() + "?accesstoken="
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
                            List<DynamicObj> list = DynamicObjHandler
                                    .getDynamicObjList(JsonHandle.getArray(
                                            resultJson, "user_good_post"));
                            if (list.size() > 0) {
                                setDataList(list);
                            } else {
                                notContent.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                });
    }

}
