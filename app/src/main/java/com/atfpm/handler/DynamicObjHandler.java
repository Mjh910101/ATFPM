package com.atfpm.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.atfpm.box.AdObj;
import com.atfpm.box.DynamicObj;
import com.atfpm.box.OriginObj;
import com.atfpm.http.JsonHandle;
import com.atfpm.system.SystemHandle;

public class DynamicObjHandler {

	private final static int WATCH = 100;

	public static List<OriginObj> getOriginObjList(JSONArray array) {
		List<OriginObj> list = new ArrayList<OriginObj>();
		for (int i = 0; i < array.length(); i++) {
			OriginObj obj = getOriginObj(JsonHandle.getJSON(array, i));
			if (obj != null) {
				list.add(obj);
			}
		}
		return list;
	}

	public static List<DynamicObj> getDynamicObjList(JSONArray array) {
		List<DynamicObj> list = new ArrayList<DynamicObj>();
		for (int i = 0; i < array.length(); i++) {
			list.add(getDynamicObj(JsonHandle.getJSON(array, i)));
		}
		return list;
	}

	public static OriginObj getOriginObj(JSONObject json) {
		String postType = JsonHandle.getString(json, OriginObj.POST_TYPE);
		if (postType.equals(OriginObj.CONTENT)) {
			DynamicObj obj = new DynamicObj();
			obj.setPostType(postType);
			return getDynamicObj(json, obj);

		} else if (postType.equals(OriginObj.AD)) {
			AdObj obj = new AdObj();
			obj.setPostType(postType);
			return getAdObj(json, obj);
		}
		return null;
	}

	private static AdObj getAdObj(JSONObject json, AdObj obj) {
		obj.setCreateAt(JsonHandle.getLong(json, AdObj.CREATE_AT));
		obj.setId(JsonHandle.getString(json, AdObj.ID));
		obj.setImageUrl(JsonHandle.getString(json, AdObj.IMAGE_URL));
		obj.setUrl(JsonHandle.getString(json, AdObj.URL));
		return obj;
	}

	public static DynamicObj getDynamicObj(JSONObject json) {
		DynamicObj obj = new DynamicObj();
		obj.setPostType(OriginObj.CONTENT);
		return getDynamicObj(json, obj);
	}

	public static DynamicObj getDynamicObj(JSONObject json, DynamicObj obj) {
		if (obj == null) {
			obj = new DynamicObj();
		}

		obj.setComment(JsonHandle.getInt(json, DynamicObj.COMMENT));
		obj.setContent(JsonHandle.getString(json, DynamicObj.CONTENT));
		obj.setCreateAt(JsonHandle.getLong(json, DynamicObj.CREATE_AT));
		obj.setFavor(JsonHandle.getInt(json, DynamicObj.FAVOR));
		obj.setGood(JsonHandle.getInt(json, DynamicObj.GOOD));
		obj.setId(JsonHandle.getString(json, DynamicObj.ID));
		obj.setPoster(UserObjHandler.getUserObj(JsonHandle.getJSON(json,
				DynamicObj.POSTER)));
		obj.setTitle(JsonHandle.getString(json, DynamicObj.TITLE));
		obj.setIsGood(JsonHandle.getInt(json, DynamicObj.IS_GOOD));
		obj.setIsFavor(JsonHandle.getInt(json, DynamicObj.IS_FAVOR));
		obj.setTpye(JsonHandle.getString(json, DynamicObj.TYPE));

		JSONArray commentArray = JsonHandle.getArray(json, DynamicObj.COMMENTS);
		if (commentArray != null) {
			obj.setCommentList(CommentObjHandler
					.getCommentObjList(commentArray));
		}

		JSONArray goodArray = JsonHandle.getArray(json,
				DynamicObj.USERS_GOOD_LIST);
		if (goodArray != null) {
			obj.setGoodList(UserObjHandler.getUserObjList(goodArray));
		}

		JSONArray favorArray = JsonHandle.getArray(json,
				DynamicObj.USERS_FAVOR_LIST);
		if (favorArray != null) {
			obj.setFavorList(UserObjHandler.getUserObjList(favorArray));
		}

		JSONArray picArray = JsonHandle.getArray(json, DynamicObj.PIC_LIST);
		if (picArray != null) {
			Log.e("", picArray.toString());
			obj.setPicList(picArray);
		}

		return obj;
	}

	public static void watchTopicDynamicObj(Context context, DynamicObj obj) {
		SystemHandle.saveIntMessage(context, "Topic_" + obj.getId(), WATCH);
	}

	public static boolean isWatchTopicDynamicObj(Context context, DynamicObj obj) {
		int w = SystemHandle.getInt(context, "Topic_" + obj.getId());
		return w == WATCH;
	}

	public static void watchNewsDynamicObj(Context context, DynamicObj obj) {
		SystemHandle.saveIntMessage(context, "News_" + obj.getId(), WATCH);
	}

	public static boolean isWatchNewsDynamicObj(Context context, DynamicObj obj) {
		int w = SystemHandle.getInt(context, "News_" + obj.getId());
		return w == WATCH;
	}

}
