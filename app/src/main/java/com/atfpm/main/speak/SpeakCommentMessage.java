package com.atfpm.main.speak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.atfpm.box.CommentObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.DateHandle;
import com.atfpm.tool.WinTool;
import com.atfpm.view.CommentMessage;

public class SpeakCommentMessage extends CommentMessage {

	private CommentObj obj;

	public SpeakCommentMessage(Context context, CommentObj obj) {
		super(context);
		this.obj = obj;
		setMessageContent(obj.getCommenter());
	}

	@Override
	public void setUserPic() {
		int w = WinTool.getWinWidth(context) / 10;
		userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(userPic, obj.getUsetPic(), w / 2);
	}

	@Override
	public void setUserName() {
		userName.setText(obj.getUserName());
	}

	@Override
	public void setUserContent() {
		content.setText(obj.getContent());
	}

	@Override
	public void setTime() {
		time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt()));
	}

}
