package com.atfpm.main.user;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.DynamicObj;
import com.atfpm.box.MessageObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.DateHandle;
import com.atfpm.handler.MessageObjHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.main.court.CourtContentActivity;
import com.atfpm.main.dynamic.DynamicContentActivity;
import com.atfpm.main.speak.SpeakContentActivity;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.ShowMessage;
import com.atfpm.tool.WinTool;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class MessageActivity extends Activity {

	private final static int GATHER = 1;
	private final static int SEND = 2;

	private Context context;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.message_gather)
	private RelativeLayout gatherBox;
	@ViewInject(R.id.message_gatherTitle)
	private TextView gatherTitle;
	@ViewInject(R.id.message_gatherLine)
	private View gatherLine;
	@ViewInject(R.id.message_send)
	private RelativeLayout sendBox;
	@ViewInject(R.id.message_sendTitle)
	private TextView sendTitle;
	@ViewInject(R.id.message_sendLine)
	private View sendLine;
	@ViewInject(R.id.message_dataList)
	private ListView dataList;
	@ViewInject(R.id.message_progress)
	private ProgressBar progress;
	@ViewInject(R.id.message_notContent)
	private TextView notContent;

	private ListBaseAdapter lba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meaasge);
		context = this;
		ViewUtils.inject(this);

		initAcitvity();
		// downloadData();
		setTitleSelection(GATHER);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		VipHandler.judgeVipTime(context, vipHint);
		if (lba != null) {
			lba.notifyDataSetChanged();

		}
	}

	@OnClick({ R.id.titel_back, R.id.title_vip, R.id.message_gather,
			R.id.message_send })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_vip:
			VipHandler.jumpOnlyVipActivity(context);
			break;
		case R.id.message_gather:
			setTitleSelection(GATHER);
			break;
		case R.id.message_send:
			setTitleSelection(SEND);
			break;
		}
	}

	private void setTitleSelection(int i) {
		initTitleSelection();
		switch (i) {
		case GATHER:
			gatherTitle.setTextColor(ColorBox.getColorForID(context,
					R.color.text_green_01));
			gatherLine.setVisibility(View.VISIBLE);
			lba = new ListBaseAdapter(MessageObjHandler.getUserSendList(), i);
			dataList.setAdapter(lba);
			break;
		case SEND:
			sendTitle.setTextColor(ColorBox.getColorForID(context,
					R.color.text_green_01));
			sendLine.setVisibility(View.VISIBLE);
			lba = new ListBaseAdapter(MessageObjHandler.getUserReceiveList(), i);
			dataList.setAdapter(lba);
			break;
		}
	}

	private void initTitleSelection() {
		gatherTitle.setTextColor(ColorBox.getColorForID(context,
				R.color.text_gray_01));
		gatherLine.setVisibility(View.INVISIBLE);
		sendTitle.setTextColor(ColorBox.getColorForID(context,
				R.color.text_gray_01));
		sendLine.setVisibility(View.INVISIBLE);
		notContent.setVisibility(View.INVISIBLE);
		lba = null;
		dataList.setAdapter(null);
	}

	private void initAcitvity() {
		titleName.setText(getResources().getString(R.string.my_message));
		VipHandler.judgeVipTime(context, vipHint);
	}

	private void downloadData() {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getUserSendAndReceiveURL() + "?accesstoken="
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
							MessageObjHandler.setUserSendList(MessageObjHandler
									.getMessageObjList(JsonHandle.getArray(
											resultJson, "user_send_comment")));
							MessageObjHandler.setUserReceiveList(MessageObjHandler.getMessageObjList(JsonHandle
									.getArray(resultJson,
											"user_receive_comment")));
							gatherBox.performClick();
						}
					}

				});
	}

	private void setNotContent(boolean isShow) {
		if (isShow) {
			notContent.setVisibility(View.VISIBLE);
		}
	}

	class ListBaseAdapter extends BaseAdapter {

		LayoutInflater inflater;
		List<MessageObj> dataObjList;
		int tap;

		public ListBaseAdapter(List<MessageObj> dataObjList, int tap) {
			this.dataObjList = dataObjList;
			this.tap = tap;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			setNotContent(dataObjList.size() <= 0);
		}

		@Override
		public int getCount() {
			return dataObjList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataObjList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.message_list_item, null);
			}
			MessageObj obj = dataObjList.get(position);
			setView(convertView, obj);
			setTap(convertView, obj);
			setCilikListener(convertView, obj);
			return convertView;
		}

		private void setCilikListener(View convertView, final MessageObj obj) {
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String t = obj.getPostType();
					Bundle b = new Bundle();
					b.putString(DynamicObj.ID, obj.getPostId());
					if (t.equals(DynamicObj.TALK_TYPE)) {
						Passageway.jumpActivity(context,
								SpeakContentActivity.class, b);
					} else if (t.equals(DynamicObj.TOPIC_TYPE)) {
						Passageway.jumpActivity(context,
								CourtContentActivity.class, b);
					} else if (t.equals(DynamicObj.NEWS_TYPE)) {
						Passageway.jumpActivity(context,
								DynamicContentActivity.class, b);
					}
					MessageObjHandler.watchMessage(context, obj);
				}
			});
		}

		private void setTap(View v, MessageObj obj) {
			TextView tap = (TextView) v
					.findViewById(R.id.message_listItem_messageTap);
			tap.setText(obj.getPostType(context));
		}

		private void setView(View v, MessageObj obj) {

			TextView title = (TextView) v
					.findViewById(R.id.message_listItem_messageTitle);
			TextView userName = (TextView) v
					.findViewById(R.id.message_listItem_userName);
			TextView content = (TextView) v
					.findViewById(R.id.message_listItem_content);
			TextView time = (TextView) v
					.findViewById(R.id.message_listItem_time);
			ImageView usetPic = (ImageView) v
					.findViewById(R.id.message_listItem_userIcon);

			title.setText(obj.getPostTitle());
			userName.setText(obj.getUserName());
			content.setText(obj.getContent());
			time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt()));

			int w = WinTool.getWinWidth(context) / 10;
			usetPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
			DownloadImageLoader.loadImage(usetPic, obj.getUsetPic(), w / 2);

			LinearLayout box = (LinearLayout) v
					.findViewById(R.id.message_listItem_box);
			View line = v.findViewById(R.id.message_listItem_line);

			if (MessageObjHandler.isWatchMessage(context, obj) || tap == GATHER) {
				box.setBackgroundResource(R.drawable.white_background_gray02_frame_0);
				line.setBackgroundResource(R.color.line_gray_02);
			} else {
				box.setBackgroundResource(R.drawable.green_background_green_frame_0);
				line.setBackgroundResource(R.color.line_green_01);
			}

		}
	}

}
