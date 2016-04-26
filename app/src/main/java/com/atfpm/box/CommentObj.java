package com.atfpm.box;

public class CommentObj {

	public final static String ID = "_id";
	public final static String CONTENT = "content";
	public final static String POST = "post";
	public final static String V = "__v";
	public final static String CREATE_AT = "createAt";
	public final static String COMMENTER = "commenter";

	private String id;
	private String content;
	private String post;
	private int v;
	private long createAt;
	private UserObj commenter;

	public String getUserName() {
		if (commenter.getS_name() != null
				&& !commenter.getS_name().equals("null")
				&& !commenter.getS_name().equals("")) {
			return commenter.getP_name() + "(" + commenter.getS_name() + ")";
		}
		return commenter.getP_name();
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

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	public UserObj getCommenter() {
		return commenter;
	}

	public void setCommenter(UserObj commenter) {
		this.commenter = commenter;
	}

	public String getUsetPic() {
		return commenter.getAvatar();
	}

}
