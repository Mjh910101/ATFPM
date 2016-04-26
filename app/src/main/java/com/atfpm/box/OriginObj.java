package com.atfpm.box;

public class OriginObj {

	public final static String CONTENT = "content";
	public final static String AD = "ad";

	public final static String POST_TYPE = "postType";

	private String postType;
	private long createAt;

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	public boolean isContent() {
		return postType.equals(CONTENT);
	}

	public boolean isAD() {
		return postType.equals(AD);
	}
}
