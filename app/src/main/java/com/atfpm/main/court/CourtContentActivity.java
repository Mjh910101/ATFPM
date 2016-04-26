package com.atfpm.main.court;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.atfpm.R;
import com.atfpm.box.DynamicObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.DateHandle;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.open.ImageListActivity;
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
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CourtContentActivity extends Activity {

	private Context context;
	private String id = "";
	private DynamicObj obj;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.court_content_title)
	private TextView title;
	@ViewInject(R.id.court_content_time)
	private TextView time;
	@ViewInject(R.id.court_content_contentWeb)
	private LazyWebView contextWeb;
	@ViewInject(R.id.court_content_progress)
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_court_content);
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

	@OnClick({ R.id.titel_back, R.id.title_vip })
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
		titleName.setText(getResources().getString(R.string.court_content));
		VipHandler.judgeVipTime(context, vipHint);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			id = b.getString(DynamicObj.ID);
		}
	}

	private void setDataView(DynamicObj obj) {
		title.setText(obj.getTitle());
		time.setText(DateHandle.format(obj.getCreateAt(),
				DateHandle.DATESTYP_10));
		setContentWebView(obj.getContent());

	}

	private void setContentWebView(String content) {
		String contentStr = VestrewWebView.addJavaScript(Html.fromHtml(content)
				.toString());
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

	private void downloadData() {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getTopicURL() + "/" + id;

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
							obj = DynamicObjHandler.getDynamicObj(JsonHandle
									.getJSON(resultJson, "post"));
							setDataView(obj);
						}
					}

				});
	}

}
