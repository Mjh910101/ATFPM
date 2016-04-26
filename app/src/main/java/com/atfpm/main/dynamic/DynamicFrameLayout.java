package com.atfpm.main.dynamic;

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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.atfpm.R;
import com.atfpm.box.DynamicObj;
import com.atfpm.box.OriginObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.tool.ShowMessage;
import com.atfpm.tool.WinTool;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class DynamicFrameLayout extends Fragment {

	private final static int LIMIT = 20;

	private Context context;
	private long time;
	private boolean isShow = true;

	private ImageView titlePic;
	private ListView dataList;
	private ProgressBar progress;
	private SwipeRefreshLayout dataListRefresh;

	private DynamicListBaseAdapter lba;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		View contactsLayout = inflater.inflate(R.layout.dynamic_layout,
				container, false);
		initView(contactsLayout);
		setDataListScrollListener();
		setOnRefreshListener();
		downloadBanner();
		downloadData();
		return contactsLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (lba != null) {
			lba.notifyDataSetChanged();
		}
		// lba = null;
		// time = 0;
		// downloadData();
	}

	private void initView(View v) {
		// titlePic = (ImageView) v.findViewById(R.id.dynamic_titlePic);
		dataList = (ListView) v.findViewById(R.id.dynamic_dataList);
		dataListRefresh = (SwipeRefreshLayout) v
				.findViewById(R.id.dynamic_dataListRefresh);
		progress = (ProgressBar) v.findViewById(R.id.dynamic_progress);

	}

	private void setDataList(List<OriginObj> list) {
		if (!list.isEmpty() && list.size() > 0) {
			if (lba == null) {
				lba = new DynamicListBaseAdapter(context, list, progress);
				if (titlePic != null) {
					lba.addHeaderView(titlePic);
					// dataList.addHeaderView(titlePic);
				}
				// -------------------------------------
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

	private void setHeadPic(String img_url) {
		if (!img_url.equals("") && !img_url.equals("null")) {
			titlePic = new ImageView(context);
			int w = WinTool.getWinWidth(context);
			int h = w / 6;
			titlePic.setLayoutParams(new AbsListView.LayoutParams(w, h));
			titlePic.setScaleType(ScaleType.FIT_CENTER);
			DownloadImageLoader.loadImage(titlePic, img_url);
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
				if (lba != null) {
					lba.removeAll();
				}
				downloadData();
			}
		});
	}

	private void downloadBanner() {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getBannerURL();

		RequestParams params = HttpUtilsBox.getRequestParams(context);

		HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						ShowMessage.showFailure(context);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						Log.d("", result);

						JSONObject json = JsonHandle.getJSON(result);
						if (json != null) {
							String img_url = JsonHandle.getString(
									JsonHandle.getJSON(json, "banner"),
									"imgUrl");
							setHeadPic(img_url);
						}
					}

				});
	}

	private void downloadData() {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getNewsURL() + "?time=" + time + "&limit="
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
