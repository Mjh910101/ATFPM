package com.atfpm.box;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.atfpm.http.JsonHandle;

public class DynamicObj extends OriginObj {

	public final static String NEWS_TYPE = "news";
	public final static String TALK_TYPE = "talk";
	public final static String TOPIC_TYPE = "topic";

	public final static String ID = "_id";
	public final static String CONTENT = "content";
	public final static String TITLE = "title";
	public final static String CREATE_AT = "createAt";
	public final static String FAVOR = "favor";
	public final static String COMMENT = "comment";
	public final static String GOOD = "good";
	public final static String PIC_LIST = "pic";
	public final static String POSTER = "poster";
	public final static String COMMENTS = "comments";
	public final static String USERS_GOOD_LIST = "users_good_this_post";
	public final static String USERS_FAVOR_LIST = "users_favor_this_post";
	public final static String IS_GOOD = "isgood";
	public final static String IS_FAVOR = "isfavor";
	public final static String TYPE = "type";

	private String id;
	private String content;
	private String title;
	private int favor;
	private int comment;
	private int good;
	private List<String> picList;
	private UserObj poster;
	private List<CommentObj> commentList;
	private List<UserObj> goodList;
	private List<UserObj> favorList;
	private int isGood;
	private int isFavor;
	private String tpye;

	public List<UserObj> getFavorList() {
		return favorList;
	}

	public void setFavorList(List<UserObj> favorList) {
		this.favorList = favorList;
	}

	public int getIsFavor() {
		return isFavor;
	}

	public void setIsFavor(int isFavor) {
		this.isFavor = isFavor;
	}

	public String getTpye() {
		return tpye;
	}

	public void setTpye(String tpye) {
		this.tpye = tpye;
	}

	public int getIsGood() {
		return isGood;
	}

	public void setIsGood(int isGood) {
		this.isGood = isGood;
	}

	public List<UserObj> getGoodList() {
		return goodList;
	}

	public void setGoodList(List<UserObj> goodList) {
		this.goodList = goodList;
	}

	public List<CommentObj> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<CommentObj> commentList) {
		this.commentList = commentList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getFavor() {
		return favor;
	}

	public void setFavor(int favor) {
		this.favor = favor;
	}

	public int getComment() {
		return comment;
	}

	public void setComment(int comment) {
		this.comment = comment;
	}

	public int getGood() {
		return good;
	}

	public void setGood(int good) {
		this.good = good;
	}

	public List<String> getPicList() {
		return picList;
	}

	public void setPicList(List<String> picList) {
		this.picList = picList;
	}

	public void setPicList(JSONArray array) {
		picList = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			picList.add(JsonHandle.getString(array, i));
		}
	}

	public UserObj getPoster() {
		return poster;
	}

	public void setPoster(UserObj poster) {
		this.poster = poster;
	}

	public String getUsetName() {
		if (poster.getS_name() != null && !poster.getS_name().equals("null")
				&& !poster.getS_name().equals("")) {
			return poster.getP_name() + "(" + poster.getS_name() + ")";
		}
		return poster.getP_name();
	}

	public String getUsetPic() {
		return poster.getAvatar();
	}

	public boolean isGood() {
		if (isGood == 1) {
			return true;
		}
		return false;
	}

	public boolean isFavor() {
		if (isFavor == 1) {
			return true;
		}
		return false;
	}

	public int getGoodOperate() {
		if (isGood()) {
			return 0;
		}
		return 1;
	}

	public void setGoodOperate() {
		if (isGood()) {
			setGood(getGood() - 1);
		} else {
			setGood(getGood() + 1);
		}
		setIsGood(getGoodOperate());
	}

	public int getFavorOperate() {
		if (isFavor()) {
			return 0;
		}
		return 1;
	}

	public void setFavorOperate() {
		if (isFavor()) {
			setFavor(getFavor() - 1);
		} else {
			setFavor(getFavor() + 1);
		}
		setIsFavor(getFavorOperate());
	}

	public boolean isNews() {
		return tpye.equals(NEWS_TYPE);
	}

	public boolean isTalk() {
		return tpye.equals(TALK_TYPE);
	}

}
