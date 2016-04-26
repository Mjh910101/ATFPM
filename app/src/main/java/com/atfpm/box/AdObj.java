package com.atfpm.box;

import android.content.Context;
import android.os.Bundle;

import com.atfpm.R;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.main.intro.IntroContentActivity;
import com.atfpm.main.intro.MyWebViewClient;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Passageway;

public class AdObj extends OriginObj {

	public final static String ID = "id";
	public final static String URL = "url";
	public final static String IMAGE_URL = "imageUrl";
	public final static String CREATE_AT = "createAt";

	private String id;
	private String url;
	private String imageUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static void jumpAD(Context context, AdObj obj) {
		Bundle b = new Bundle();
		b.putString(MyWebViewClient.KEY, obj.getUrl() + "&accesstoken="
				+ UserObjHandler.getAccesstoken(context));
		b.putInt(MyWebViewClient.COLOR,
				ColorBox.getColorForID(context, R.color.title_bg));
		Passageway.jumpActivity(context, IntroContentActivity.class, b);
	}
}
