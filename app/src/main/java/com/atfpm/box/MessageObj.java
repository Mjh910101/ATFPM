package com.atfpm.box;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.atfpm.R;
import com.atfpm.http.JsonHandle;

public class MessageObj {

	public final static String POSTER = "poster";
	public final static String POST = "post";
	public final static String CONTENT = "content";
	public final static String COMMENTER = "commenter";
	public final static String ID = "_id";
	public final static String CREATE_AT = "createAt";
	public final static String TITLE = "title";
	public final static String TYPE = "type";

	private UserObj commenter;
	private String content;
	private String id;
	private Post p;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserObj getCommenter() {
		return commenter;
	}

	public void setCommenter(UserObj commenter) {
		this.commenter = commenter;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Post getP() {
		return p;
	}

	public void setPost(JSONObject postJson) {
		p = new Post();
		p.setContent(JsonHandle.getString(postJson, CONTENT));
		p.setCreateAt(JsonHandle.getLong(postJson, CREATE_AT));
		p.setId(JsonHandle.getString(postJson, ID));
		p.setTitle(JsonHandle.getString(postJson, TITLE));
		p.setType(JsonHandle.getString(postJson, TYPE));
	}

	public String getPostType() {
		if (p != null) {
			return p.getType();
		}
		return "";
	}

	public String getPostId() {
		if (p != null) {
			return p.getId();
		}
		return "";
	}

	public String getPostType(Context context) {
		String t = getPostType();
		if (t.equals(DynamicObj.TALK_TYPE)) {
			return context.getResources().getString(R.string.speak_name);
		} else if (t.equals(DynamicObj.TOPIC_TYPE)) {
			return context.getResources().getString(R.string.court_name);
		} else if (t.equals(DynamicObj.NEWS_TYPE)) {
			return context.getResources().getString(R.string.dynamic_name);
		}
		return "";
	}

	public String getPostTitle() {
		if (p != null) {
			return p.getContent();
		}
		return "";
	}

	public String getUserName() {
		if (commenter != null) {
			if (commenter.getS_name() != null && !commenter.getS_name().equals("null")
					&& !commenter.getS_name().equals("")) {
				return commenter.getP_name() + "(" + commenter.getS_name() + ")";
			}
			return commenter.getP_name();
		}
		return "";
	}

	public long getCreateAt() {
		if (p != null) {
			return p.getCreateAt();
		}
		return System.currentTimeMillis();
	}

	public String getUsetPic() {
		if (commenter != null) {
			return commenter.getAvatar();
		}
		return "";
	}

	class Post {

		private String id;
		private long createAt;
		private String content;
		private String type;
		private String title;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getCreateAt() {
			return createAt;
		}

		public void setCreateAt(long createAt) {
			this.createAt = createAt;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

}
