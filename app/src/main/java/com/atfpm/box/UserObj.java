package com.atfpm.box;

public class UserObj {

	public final static String V = "";
	public final static String AVATAR = "avatar_url";
	public final static String PASS = "pass";
	public final static String MOBILE = "mobile";
	public final static String S_NAME = "s_name";
	public final static String P_NAME = "p_name";
	public final static String ID = "_id";
	public final static String CREATE_AT = "createAt";
	public final static String ACCESS_TOKEN = "accesstoken";
	public final static String USER_TYPE = "userType";

	private int v;
	private String avatar;
	private String pass;
	private String mobile;
	private String s_name;
	private String p_name;
	private String id;
	private long createAt;
	private String accesstoken;
	private String userType;

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}

	public String getAvatar() {
		return avatar;
		// return "http://58.96.190.181:18883/avator/1426770681661.png";
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getS_name() {
		return s_name;
	}

	public void setS_name(String s_name) {
		this.s_name = s_name;
	}

	public String getP_name() {
		return p_name;
	}

	public void setP_name(String p_name) {
		this.p_name = p_name;
	}

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

	public String getUserName() {
		if (s_name != null && !s_name.equals("null") && !s_name.equals("")) {
			return p_name + "(" + s_name + ")";
		}
		return p_name;
	}
}
