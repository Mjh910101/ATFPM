package com.atfpm.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.atfpm.R;
import com.atfpm.box.UserObj;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.system.SystemHandle;

public class UserObjHandler {

	public static List<UserObj> getUserObjList(JSONArray array) {
		List<UserObj> list = new ArrayList<UserObj>();
		for (int i = 0; i < array.length(); i++) {
			list.add(getUserObj(JsonHandle.getJSON(array, i)));
		}
		return list;
	}

	public static UserObj getUserObj(JSONObject json) {
		UserObj obj = new UserObj();

		obj.setV(JsonHandle.getInt(json, UserObj.V));
		obj.setId(JsonHandle.getString(json, UserObj.ID));
		obj.setAvatar(JsonHandle.getString(json, UserObj.AVATAR));
		obj.setCreateAt(JsonHandle.getLong(json, UserObj.CREATE_AT));
		obj.setMobile(JsonHandle.getString(json, UserObj.MOBILE));
		obj.setP_name(JsonHandle.getString(json, UserObj.P_NAME));
		obj.setPass(JsonHandle.getString(json, UserObj.PASS));
		obj.setS_name(JsonHandle.getString(json, UserObj.S_NAME));
		obj.setAccesstoken(JsonHandle.getString(json, UserObj.ACCESS_TOKEN));
		obj.setUserType(JsonHandle.getString(json, UserObj.USER_TYPE));

		return obj;
	}

	public static void logout(Context context) {
		setUserAccessToken(context, "");
		setUserMobile(context, "");
		setUserAvatar(context, "");
		setUserSName(context, "");
		setUserPName(context, "");
		setUserType(context, "");
	}

	public static void save(Context context, UserObj obj) {
		setUserAccessToken(context, obj.getAccesstoken());
		setUserMobile(context, obj.getMobile());
		setUserAvatar(context, obj.getAvatar());
		setUserSName(context, obj.getS_name());
		setUserPName(context, obj.getP_name());
		setUserType(context, obj.getUserType());
	}

	private static void setUserType(Context context, String v) {
		SystemHandle.saveStringMessage(context, UserObj.USER_TYPE, v);
	}

	public static void setUserAccessToken(Context context, String v) {
		SystemHandle.saveStringMessage(context, UserObj.ACCESS_TOKEN, v);
	}

	public static void setUserMobile(Context context, String v) {
		SystemHandle.saveStringMessage(context, UserObj.MOBILE, v);
	}

	public static void setUserAvatar(Context context, String v) {
		SystemHandle.saveStringMessage(context, UserObj.AVATAR, v);
	}

	public static void setUserSName(Context context, String v) {
		SystemHandle.saveStringMessage(context, UserObj.S_NAME, v);
	}

	public static void setUserPName(Context context, String v) {
		SystemHandle.saveStringMessage(context, UserObj.P_NAME, v);
	}

	public static String getUserType(Context context) {
		return SystemHandle.getString(context, UserObj.USER_TYPE);
	}

	public static String getAccesstoken(Context context) {
		return SystemHandle.getString(context, UserObj.ACCESS_TOKEN);
	}

	public static boolean isLogin(Context context) {
		return !getAccesstoken(context).equals("");
	}

	public static String getUserAvatar(Context context) {
		return SystemHandle.getString(context, UserObj.AVATAR);
	}

	public static String getUserTel(Context context) {
		return SystemHandle.getString(context, UserObj.MOBILE);
	}

	public static String getUserPName(Context context) {
		return SystemHandle.getString(context, UserObj.P_NAME);
	}

	public static String getUserSName(Context context) {
		return SystemHandle.getString(context, UserObj.S_NAME);
	}

	public static String getUserName(Context context) {
		String p = getUserPName(context);
		String s = getUserSName(context);
		if (s.equals("")) {
			return p;
		}
		return p + "(" + s + ")";
	}

	public static boolean isMenber(String userType) {
		if (userType.equals("member")) {
			return true;
		}
		return false;
	}

	public static void setUsetType(ImageView v, UserObj obj) {
		Log.e(UserObj.USER_TYPE, obj.getUserType());
		setUsetType(v, obj.getUserType());
	}

	public static void setUsetType(Context context, ImageView v) {
		String userType = UserObjHandler.getUserType(context);
		if (UserObjHandler.isMenber(userType)) {
			v.setVisibility(View.VISIBLE);
			v.setImageResource(R.drawable.mvip_icon);
		} else {
			v.setVisibility(View.INVISIBLE);
		}
	}

	public static void setUsetType(ImageView v, String userType) {
		if (UserObjHandler.isMenber(userType)) {
			v.setVisibility(View.VISIBLE);
			v.setImageResource(R.drawable.socio_icon);
		} else {
			v.setImageResource(R.drawable.nosocio_icon);
			v.setVisibility(View.VISIBLE);
		}
		v.setVisibility(View.GONE);
	}

}
