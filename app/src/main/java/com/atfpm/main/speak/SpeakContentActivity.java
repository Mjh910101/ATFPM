package com.atfpm.main.speak;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.CommentObj;
import com.atfpm.box.DynamicObj;
import com.atfpm.box.UserObj;
import com.atfpm.dialog.WarningDialog;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.CommentObjHandler;
import com.atfpm.handler.DateHandle;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.interfaces.CallbackForBoolean;
import com.atfpm.main.dynamic.DynamicCommentMessage;
import com.atfpm.main.dynamic.DynamicObjBox;
import com.atfpm.main.dynamic.DynamicUserMessage;
import com.atfpm.open.ImageListActivity;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.LineViewTool;
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

public class SpeakContentActivity extends Activity {

	private final static int LIKE = 1;
	private final static int COMMENT = 2;

	private Context context;
	private String id = "", comment = "";
	private boolean isSend = false;
	private DynamicObj obj;
	private int now_tap;

	private InputMethodManager imm = null;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.speak_content_likeIcon)
	private ImageView likeIcon;
	@ViewInject(R.id.speak_content_likeCorner)
	private ImageView likeCorner;
	@ViewInject(R.id.speak_content_commentIcon)
	private ImageView commentIcon;
	@ViewInject(R.id.speak_content_commentCorner)
	private ImageView commentCorner;
	@ViewInject(R.id.speak_content_likeTitle)
	private TextView likeTitle;
	@ViewInject(R.id.speak_content_commentTitle)
	private TextView commentTitle;
	@ViewInject(R.id.speak_content_contentBox)
	private LinearLayout dataBox;
	@ViewInject(R.id.speak_content_sendBox)
	private RelativeLayout sendBox;
	@ViewInject(R.id.speak_content_commentBox)
	private LinearLayout commentBox;
	@ViewInject(R.id.speak_content_goodIcon)
	private ImageView goodIcon;
	@ViewInject(R.id.speak_content_progress)
	private ProgressBar progress;
	@ViewInject(R.id.speak_content_contextInput)
	private EditText contextInput;
	@ViewInject(R.id.speak_content_sendBtn)
	private ImageView sendBtn;
	@ViewInject(R.id.speak_content_userPic)
	private ImageView userPic;
	@ViewInject(R.id.speak_content_userName)
	private TextView userName;
	@ViewInject(R.id.speak_content_time)
	private TextView time;
	@ViewInject(R.id.speak_content_contextWeb)
	private LazyWebView contextWeb;
	// @ViewInject(R.id.speak_content_sendBg)
	// private View sendBg;
	@ViewInject(R.id.speak_content_goodTitle)
	private TextView goodTitle;
	@ViewInject(R.id.speak_content_userType)
	private ImageView userType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speak_content);
		context = this;
		ViewUtils.inject(this);

		initAcitvity();
		setTextChangedListener();
		downloadData();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		VipHandler.judgeVipTime(context, vipHint);
	}

	@OnClick({ R.id.titel_back, R.id.title_vip, R.id.speak_content_commentBox,
			R.id.speak_content_likeBox, R.id.speak_content_commentHandleBox,
			R.id.speak_content_sendBtn, R.id.speak_content_goodHandleBox,
			R.id.speak_content_sendBg, R.id.speak_content_closeSend })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_vip:
			VipHandler.jumpOnlyVipActivity(context);
			break;
		case R.id.speak_content_commentBox:
			setIconIondition(COMMENT);
			break;
		case R.id.speak_content_likeBox:
			setIconIondition(LIKE);
			break;
		case R.id.speak_content_commentHandleBox:
			showSendBox();
			break;
		case R.id.speak_content_sendBtn:
			sendComment();
			break;
		case R.id.speak_content_goodHandleBox:
			sendGood();
			break;
		case R.id.speak_content_sendBg:
			closeSendBox();
			break;
		case R.id.speak_content_closeSend:
			closeSendBox(false);
			break;
		}
	}

	private void setTextChangedListener() {
		contextInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				comment = contextInput.getText().toString();
				if (comment.equals("")) {
					isSend = false;
					sendBtn.setImageResource(R.drawable.send_gray_icon);
				} else {
					isSend = true;
					sendBtn.setImageResource(R.drawable.send_green_icon);
				}
			}
		});
	}

	private void showSendBox() {
		// sendBg.setVisibility(View.VISIBLE);
		WarningDialog.showWarningDialog(context, WarningDialog.COM_SPEAK_COMMENR);
		sendBox.setVisibility(View.VISIBLE);
	}

	private void closeSendBox() {
		closeSendBox(true);
	}

	private void closeSendBox(boolean b) {
		// sendBg.setVisibility(View.GONE);
		sendBox.setVisibility(View.GONE);
		if (b) {
			contextInput.setText("");
		}
		closeKeyboard();
	}

	private void closeKeyboard() {
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(
					contextInput.getApplicationWindowToken(), 0);
		}
	}

	private void setIconIondition(int i) {
		initIconIondition();
		switch (i) {
		case LIKE:
			likeIcon.setImageResource(R.drawable.like_on_icon);
			likeTitle.setTextColor(ColorBox.getColorForID(context,
					R.color.black));
			likeCorner.setVisibility(View.VISIBLE);
			setTextBold(likeTitle, true);
			setDynamicGood(obj.getGoodList());
			break;
		case COMMENT:
			commentIcon.setImageResource(R.drawable.comment_on_icon);
			commentTitle.setTextColor(ColorBox.getColorForID(context,
					R.color.black));
			commentCorner.setVisibility(View.VISIBLE);
			setTextBold(commentTitle, true);
			setDynamicComment(obj.getCommentList());
			break;
		}
		now_tap = i;
	}

	private void setTextBold(TextView v, boolean b) {
		TextPaint tp = v.getPaint();
		tp.setFakeBoldText(b);
	}

	private void setDynamicComment(List<CommentObj> commentList) {
		if (commentList != null && !commentList.isEmpty()) {
			for (CommentObj obj : commentList) {
				SpeakCommentMessage m = new SpeakCommentMessage(context, obj);
				dataBox.addView(m);
				dataBox.addView(LineViewTool.getGrayLine(context));
			}
		} else {
			dataBox.addView(LineViewTool.getFirst(context,
					R.string.first_comment));
		}
	}

	private void setDynamicGood(List<UserObj> goodList) {
		if (goodList != null && !goodList.isEmpty()) {
			for (UserObj obj : goodList) {
				DynamicUserMessage m = new DynamicUserMessage(context, obj);
				dataBox.addView(m);
				dataBox.addView(LineViewTool.getGrayLine(context));
			}
		} else {
			dataBox.addView(LineViewTool.getFirst(context, R.string.first_like));
		}
	}

	private void initIconIondition() {
		likeIcon.setImageResource(R.drawable.like_off_icon);
		commentIcon.setImageResource(R.drawable.comment_icon);

		likeTitle.setTextColor(ColorBox.getColorForID(context,
				R.color.text_gray_01));
		commentTitle.setTextColor(ColorBox.getColorForID(context,
				R.color.text_gray_01));

		setTextBold(likeTitle, false);
		setTextBold(commentTitle, false);

		likeCorner.setVisibility(View.INVISIBLE);
		commentCorner.setVisibility(View.INVISIBLE);

		dataBox.removeAllViews();
	}

	private void initAcitvity() {
		titleName.setText(getResources().getString(R.string.speak_content));
		VipHandler.judgeVipTime(context, vipHint);
		now_tap = COMMENT;
		imm = (InputMethodManager) contextInput.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);

		closeSendBox();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			id = b.getString(DynamicObj.ID);
			if (b.getBoolean(DynamicObj.COMMENT)) {
				showSendBox();
			}
		}
		// obj = DynamicObjBox.getDynamicObj();
		// if (obj != null) {
		// id = obj.getId();
		// }
	}

	private void setDataView(DynamicObj obj) {
		int w = WinTool.getWinWidth(context) / 8;
		userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(userPic, obj.getUsetPic(), w / 2);

		userName.setText(obj.getUsetName());
		time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt()));
		likeTitle.setText(String.valueOf(obj.getGood()));
		commentTitle.setText(String.valueOf(obj.getComment()));

		UserObjHandler.setUsetType(userType, obj.getPoster());

		if (obj.isGood()) {
			goodTitle
					.setTextColor(ColorBox.getColorForID(context, R.color.text_green_02));
			goodIcon.setImageResource(R.drawable.like_click_icon);
		} else {
			goodTitle.setTextColor(ColorBox.getColorForID(context,
					R.color.text_gray_01));
			goodIcon.setImageResource(R.drawable.like_off_icon);
		}

		setIconIondition(now_tap);
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

	private void sendComment() {
		if (isSend) {
			progress.setVisibility(View.VISIBLE);
			CommentObjHandler.sendComment(context, obj, comment,
					new CallbackForBoolean() {

						@Override
						public void callback(boolean b) {
							progress.setVisibility(View.GONE);
							if (b) {
								closeSendBox();
								downloadData();
							}
						}
					});
		}
	}

	private void sendGood() {
		progress.setVisibility(View.VISIBLE);
		CommentObjHandler.sendGood(context, obj, obj.getGoodOperate(),
				new CallbackForBoolean() {

					@Override
					public void callback(boolean b) {
						progress.setVisibility(View.GONE);
						if (b) {
							downloadData();
						}
					}
				});
	}

	private void downloadData() {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getTalkURL() + "/" + id + "?accesstoken="
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
							obj = DynamicObjHandler.getDynamicObj(JsonHandle
									.getJSON(resultJson, "post"));
							setDataView(obj);
						}
					}

				});
	}

}
