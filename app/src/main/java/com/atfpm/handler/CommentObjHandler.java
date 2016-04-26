package com.atfpm.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.atfpm.box.CommentObj;
import com.atfpm.box.DynamicObj;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.interfaces.CallbackForBoolean;
import com.atfpm.tool.ShowMessage;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class CommentObjHandler {

	public static List<CommentObj> getCommentObjList(JSONArray array) {
		List<CommentObj> list = new ArrayList<CommentObj>();
		for (int i = 0; i < array.length(); i++) {
			list.add(getCommentObj(JsonHandle.getJSON(array, i)));
		}
		return list;
	}

	public static CommentObj getCommentObj(JSONObject json) {
		CommentObj obj = new CommentObj();

		obj.setCommenter(UserObjHandler.getUserObj(JsonHandle.getJSON(json,
				CommentObj.COMMENTER)));
		obj.setContent(JsonHandle.getString(json, CommentObj.CONTENT));
		obj.setCreateAt(JsonHandle.getLong(json, CommentObj.CREATE_AT));
		obj.setId(JsonHandle.getString(json, CommentObj.ID));
		obj.setPost(JsonHandle.getString(json, CommentObj.POST));
		obj.setV(JsonHandle.getInt(json, CommentObj.V));

		return obj;
	}

	public static void sendComment(final Context context, DynamicObj obj,
			String comment, final CallbackForBoolean callback) {
		if (!UserObjHandler.isLogin(context)) {
			ShowMessage.showPleaseLoginDialog(context);
			if (callback != null) {
				callback.callback(false);
			}
		} else {
			String url = UrlHandle.getCommentURL();

			RequestParams params = HttpUtilsBox.getRequestParams(context);
			params.addBodyParameter("accesstoken",
					UserObjHandler.getAccesstoken(context));
			params.addBodyParameter("post_id", obj.getId());
			params.addBodyParameter("comment", comment);

			HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException exception,
								String msg) {
							ShowMessage.showFailure(context);
							if (callback != null) {
								callback.callback(false);
							}
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String result = responseInfo.result;
							Log.d("", result);
							JSONObject json = JsonHandle.getJSON(result);
							if (!ShowMessage.showException(context, json)) {
								int r = JsonHandle.getInt(json, "result");
								if (callback != null) {
									callback.callback(r == 1);
								}
							}
						}

					});
		}
	}

	public static void sendGood(final Context context, DynamicObj obj,
			int good, final CallbackForBoolean callback) {
		if (!UserObjHandler.isLogin(context)) {
			ShowMessage.showPleaseLoginDialog(context);
			if (callback != null) {
				callback.callback(false);
			}
		} else {
			String url = UrlHandle.getGoodURL();

			RequestParams params = HttpUtilsBox.getRequestParams(context);
			params.addBodyParameter("accesstoken",
					UserObjHandler.getAccesstoken(context));
			params.addBodyParameter("post_id", obj.getId());
			params.addBodyParameter("good", String.valueOf(good));

			HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException exception,
								String msg) {
							ShowMessage.showFailure(context);
							if (callback != null) {
								callback.callback(false);
							}
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String result = responseInfo.result;
							Log.d("", result);
							// ShowMessage.showException(context, result);
							JSONObject json = JsonHandle.getJSON(result);
							if (!ShowMessage.showException(context, json)) {
								int r = JsonHandle.getInt(json, "result");
								if (callback != null) {
									callback.callback(r == 1);
								}
							}
						}

					});
		}
	}

	public static void sendFavor(final Context context, DynamicObj obj,
			int favor, final CallbackForBoolean callback) {
		if (!UserObjHandler.isLogin(context)) {
			ShowMessage.showPleaseLoginDialog(context);
			if (callback != null) {
				callback.callback(false);
			}
		} else {
			String url = UrlHandle.getFavorURL();

			RequestParams params = HttpUtilsBox.getRequestParams(context);
			params.addBodyParameter("accesstoken",
					UserObjHandler.getAccesstoken(context));
			params.addBodyParameter("post_id", obj.getId());
			params.addBodyParameter("favor", String.valueOf(favor));

			HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException exception,
								String msg) {
							ShowMessage.showFailure(context);
							if (callback != null) {
								callback.callback(false);
							}
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String result = responseInfo.result;
							Log.d("", result);
							JSONObject json = JsonHandle.getJSON(result);
							if (!ShowMessage.showException(context, json)) {
								int r = JsonHandle.getInt(json, "result");
								if (callback != null) {
									callback.callback(r == 1);
								}
							}
						}

					});
		}
	}
}
