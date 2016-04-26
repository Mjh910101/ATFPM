package com.atfpm.main.dynamic;

import android.content.Context;
import android.widget.LinearLayout;

import com.atfpm.box.UserObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.tool.WinTool;
import com.atfpm.view.UserMessage;

public class DynamicUserMessage extends UserMessage {

	private UserObj obj;

	public DynamicUserMessage(Context context, UserObj obj) {
		super(context);
		this.obj = obj;
		setMessageContent(obj);
	}

	@Override
	public void setUserPic() {
		int w = WinTool.getWinWidth(context) / 10;
		userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(userPic, obj.getAvatar(), w / 2);
	}

	@Override
	public void setUserName() {
		userName.setText(obj.getUserName());
	}

}
