package com.atfpm.main.court;

import java.util.List;

import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.AdObj;
import com.atfpm.box.DynamicObj;
import com.atfpm.box.OriginObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.DateHandle;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.ShowMessage;
import com.atfpm.tool.WinTool;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class CourtFrameLayout extends Fragment {

    private final static int LIMIT = 20;

    private Context context;
    private long time;
    private boolean isShow = true;

    private ListView dataList;
    private SwipeRefreshLayout dataListRefresh;
    private ProgressBar progress;

    private ListBaseAdapter lba;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View contactsLayout = inflater.inflate(R.layout.court_layout,
                container, false);
        initView(contactsLayout);
        setDataListScrollListener();
        setOnRefreshListener();
        downloadData();
        return contactsLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("", "onResume");
        if (lba != null) {
            lba.notifyDataSetChanged();
        }
    }

    private void initView(View v) {
        dataList = (ListView) v.findViewById(R.id.court_dataList);
        progress = (ProgressBar) v.findViewById(R.id.court_progress);
        dataListRefresh = (SwipeRefreshLayout) v
                .findViewById(R.id.court_dataListRefresh);
    }

    private void setDataList(List<OriginObj> list) {
        if (!list.isEmpty() && list.size() > 0) {
            if (lba == null) {
                lba = new ListBaseAdapter(list);
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

    private void setDataListScrollListener() {
        dataList.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                        if (progress.getVisibility() == View.GONE) {
                            downloadData();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

            }
        });
    }

    private void setOnRefreshListener() {
        dataListRefresh.setColorScheme(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        dataListRefresh.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                time = 0;
                isShow = true;
                if (lba != null) {
                    lba = null;
                }
                downloadData();
            }
        });
    }

    private void downloadData() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getTopicURL() + "?time=" + time + "&limit="
                + LIMIT;

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
                            List<OriginObj> list = DynamicObjHandler
                                    .getOriginObjList(JsonHandle.getArray(
                                            resultJson, "post"));
                            setDataList(list);
                        }
                    }

                });

    }

    class ListBaseAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<OriginObj> itemList;

        public ListBaseAdapter(List<OriginObj> list) {
            this.itemList = list;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public long getLsatTime() {
            return itemList.get(itemList.size() - 1).getCreateAt();
        }

        public void addItems(List<OriginObj> list) {
            for (OriginObj obj : list) {
                addItem(obj);
            }
            notifyDataSetChanged();
        }

        private void addItem(OriginObj obj) {
            itemList.add(obj);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.court_list_item_v2, null);
            }

            OriginObj obj = itemList.get(position);
            DynamicObj dynamicObj = null;
            AdObj adObj = null;
            if (obj.isContent()) {
                dynamicObj = (DynamicObj) obj;
                setView(convertView, dynamicObj);
                setOnClick(convertView, dynamicObj);
            } else if (obj.isAD()) {
                // adObj = (AdObj) obj;
                // setView(convertView, adObj);
                // setOnClick(convertView, adObj);
            }

            return convertView;
        }

        private void setOnClick(View convertView, final AdObj obj) {
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    AdObj.jumpAD(context, obj);
                }
            });
        }

        private void setOnClick(View convertView, final DynamicObj obj) {
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString(DynamicObj.ID, obj.getId());
                    Passageway.jumpActivity(context,
                            CourtContentActivity.class, b);
                    DynamicObjHandler.watchTopicDynamicObj(context, obj);
                }
            });

        }

        private void setView(View v, AdObj obj) {
            RelativeLayout contentBox = (RelativeLayout) v
                    .findViewById(R.id.court_item_messageBox);
            RelativeLayout adBox = (RelativeLayout) v
                    .findViewById(R.id.court_listitem_adBox);
            ImageView ad = (ImageView) v.findViewById(R.id.court_listitem_ad);
            contentBox.setVisibility(View.GONE);
            adBox.setVisibility(View.VISIBLE);
            int w = WinTool.getWinWidth(context);
            int h = w / 3;
            ad.setLayoutParams(new LinearLayout.LayoutParams(w, h));
            DownloadImageLoader.loadImage(ad, obj.getImageUrl());
        }

        private void setView(View v, DynamicObj obj) {
            ImageView ball = (ImageView) v.findViewById(R.id.court_item_ball);
            TextView time = (TextView) v.findViewById(R.id.court_item_time);
            TextView title = (TextView) v.findViewById(R.id.court_item_title);
            TextView content = (TextView) v
                    .findViewById(R.id.court_item_content);
            RelativeLayout adBox = (RelativeLayout) v
                    .findViewById(R.id.court_listitem_adBox);
            RelativeLayout messageBox = (RelativeLayout) v
                    .findViewById(R.id.court_item_messageBox);

            time.setText(DateHandle.format(obj.getCreateAt(),
                    DateHandle.DATESTYP_4));
            title.setText(obj.getTitle());
            content.setText(obj.getContent());

            messageBox.setVisibility(View.VISIBLE);
            adBox.setVisibility(View.GONE);

            if (DynamicObjHandler.isWatchTopicDynamicObj(context, obj)) {
                ball.setImageResource(R.drawable.court_gray_ball);
                messageBox
                        .setBackgroundResource(R.drawable.court_white_bg);
            } else {
                ball.setImageResource(R.drawable.court_green_ball);
                messageBox
                        .setBackgroundResource(R.drawable.court_green_bg);
            }
        }

    }
}
