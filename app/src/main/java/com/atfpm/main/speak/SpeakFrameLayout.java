package com.atfpm.main.speak;

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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.atfpm.R;
import com.atfpm.box.DynamicObj;
import com.atfpm.box.OriginObj;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.tool.ShowMessage;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

@Deprecated
public class SpeakFrameLayout extends Fragment {

	private final static int LIMIT = 20;
	public static boolean UPLOAD = true;

	private Context context;
	private long time;
	private boolean isShow = true;

	private ListView dataList;
	private ProgressBar progress;
	private SwipeRefreshLayout dataListRefresh;

	private SpeakHeadView headView;
	private SpeakListBaseAdapter alba;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		View contactsLayout = inflater.inflate(R.layout.speak_layout,
				container, false);
		initView(contactsLayout);
		setDataListScrollListener();
		setOnRefreshListener();
		// downloadData();
		UPLOAD = true;
		return contactsLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("", "onResume");
		if (UPLOAD) {
			alba.removeAll();
			time = 0;
			isShow = true;
			downloadData();
		} else {
			if (alba != null) {
				alba.notifyDataSetChanged();
			}
		}
	}

	private void initView(View v) {
		dataList = (ListView) v.findViewById(R.id.speak_dataList);
		progress = (ProgressBar) v.findViewById(R.id.speak_progress);
		dataListRefresh = (SwipeRefreshLayout) v
				.findViewById(R.id.speak_dataListRefresh);
		headView = new SpeakHeadView(context);
		alba = new SpeakListBaseAdapter(context, progress);
		alba.addHeaderView(headView);
		dataList.setAdapter(alba);
	}

	private void setDataList(List<OriginObj> list) {
		if (!list.isEmpty() && list.size() > 0) {
			alba.addItems(list);
			time = alba.getLsatTime();
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
						if (progress.getVisibility() == View.GONE && isShow) {
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
				if (alba != null) {
					alba.removeAll();
				}
				downloadData();
			}
		});
	}

	private void downloadData() {
		UPLOAD = false;
		progress.setVisibility(View.VISIBLE);

		String url = UrlHandle.getTalkURL() + "?time=" + time + "&limit="
				+ LIMIT + "&accesstoken="
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
							List<OriginObj> list = DynamicObjHandler
									.getOriginObjList(JsonHandle.getArray(
											resultJson, "post"));
							setDataList(list);
						}
					}

				});

	}
}
