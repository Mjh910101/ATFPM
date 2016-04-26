package com.atfpm.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.atfpm.box.MessageObj;
import com.atfpm.http.JsonHandle;
import com.atfpm.system.SystemHandle;

public class MessageObjHandler {

	private final static int WATCH = 100;

	public static List<MessageObj> getMessageObjList(JSONArray array) {
		List<MessageObj> list = new ArrayList<MessageObj>();
		if (array != null) {
			for (int i = 0; i < array.length(); i++) {
				list.add(getMessageObj(JsonHandle.getJSON(array, i)));
			}
		}
		return list;
	}

	private static MessageObj getMessageObj(JSONObject json) {
		MessageObj obj = new MessageObj();

		obj.setContent(JsonHandle.getString(json, MessageObj.CONTENT));
		obj.setId(JsonHandle.getString(json, MessageObj.ID));

		JSONObject commenter = JsonHandle.getJSON(json, MessageObj.COMMENTER);
		if (commenter != null) {
			obj.setCommenter(UserObjHandler.getUserObj(commenter));
		}

		JSONObject post = JsonHandle.getJSON(json, MessageObj.POST);
		if (post != null) {
			obj.setPost(post);
		}

		return obj;
	}

	public static void watchMessage(Context context, MessageObj obj) {
		SystemHandle.saveIntMessage(context, "message_" + obj.getId(), WATCH);
	}

	public static boolean isWatchMessage(Context context, MessageObj obj) {
		int w = SystemHandle.getInt(context, "message_" + obj.getId());
		return w == WATCH;
	}

	// ****************************************************************

	private static List<MessageObj> userSendList;
	private static List<MessageObj> userReceiveList;

	public static List<MessageObj> getUserSendList() {
		if (userSendList != null) {
			return userSendList;
		}
		return new ArrayList<MessageObj>();
	}

	public static void setUserSendList(List<MessageObj> lins) {
		userSendList = lins;
	}

	public static List<MessageObj> getUserReceiveList() {
		if (userReceiveList != null) {
			return userReceiveList;
		}
		return new ArrayList<MessageObj>();
	}

	public static void setUserReceiveList(List<MessageObj> list) {
		userReceiveList = list;
	}

	public static void setMessageSum(Context context, TextView view) {
		int sum = 0;
		// sum += examineMessageList(context, getUserSendList());
		sum += examineMessageList(context, getUserReceiveList());
		if (sum <= 0) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			if (sum > 99) {
				view.setText("...");
			} else {
				view.setText(String.valueOf(sum));
			}
		}
	}

	private static int examineMessageList(Context context, List<MessageObj> list) {
		int sum = 0;
		for (MessageObj obj : list) {
			if (!isWatchMessage(context, obj)) {
				sum += 1;
			}
		}
		return sum;
	}
}
