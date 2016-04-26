package com.atfpm.http;

public class UrlHandle {

    /**
     * 服务器地址ַ
     */
    private static String RootIndex = "http://58.96.190.181:18883";

    public final static String getIndex() {
        return RootIndex;
    }

    /**
     * 注册
     *
     * @return
     */
    public final static String getRegisterURL() {
        return RootIndex + "/api/v1/sign";
    }

    /**
     * 登录
     *
     * @return
     */
    public static String getLoginURL() {
        return RootIndex + "/api/v1/login";
    }

    /**
     * 修改密码
     *
     * @return
     */
    public static String getResetPassURL() {
        return RootIndex + "/api/v1/reset_pass";
    }

    /**
     * 修改用用户名
     *
     * @return
     */
    public static String getUserModURL() {
        return RootIndex + "/api/v1/user_mod";
    }

    /**
     * 上传头像
     *
     * @return
     */
    public static String getUserAvatorURL() {
        return RootIndex + "/api/v1/user_avator";
    }

    /**
     * 动态
     *
     * @return
     */
    public static String getNewsURL() {
        return RootIndex + "/api/v1/news";
    }

    /**
     * get自由讲 ,post发表自由讲
     *
     * @return
     */
    public static String getTalkURL() {
        return RootIndex + "/api/v1/talk";
    }

    /**
     * 立法会
     *
     * @return
     */
    public static String getTopicURL() {
        return RootIndex + "/api/v1/topic";
    }

    /**
     * 评论
     *
     * @return
     */
    public static String getCommentURL() {
        return RootIndex + "/api/v1/comment";
    }

    /**
     * past:点赞good=1 ,取消点赞goog=0 ;get:获取赞过的
     *
     * @return
     */
    public static String getGoodURL() {
        return RootIndex + "/api/v1/good";
    }

    /**
     * post:收藏;get:获取收藏过的
     *
     * @return
     */
    public static String getFavorURL() {
        return RootIndex + "/api/v1/favor";
    }

    /**
     * 我的发言
     *
     * @return
     */
    public static String getUserPostURL() {
        return RootIndex + "/api/v1/user_post";
    }

    /**
     * 会员等级说明
     *
     * @return
     */
    public static String getMemberIntroURL() {
        return RootIndex + "/member_intro";
    }

    /**
     * 帮助
     *
     * @return
     */
    public static String getHelpURL() {
        return RootIndex + "/help";
    }

    /**
     * 中澳
     *
     * @return
     */
    public static String getAboutURL() {
        return RootIndex + "/about";
    }

    /**
     * 协会简介
     *
     * @return
     */
    public static String getIntroURL() {
        return RootIndex + "/intro";
    }

    /**
     * VIP
     *
     * @return
     */
    public static String getVipURL() {
        return RootIndex + "/vip";
    }

    /**
     * 我的信息
     *
     * @return
     */
    public static String getUserSendAndReceiveURL() {
        return RootIndex + "/api/v1/user_send_and_receive";
    }

    /**
     * 检测更新
     *
     * @return
     */
    public static String getVersionURL() {
        return RootIndex + "/version";
    }

    /**
     * 动态顶端图片
     *
     * @return
     */
    public static String getBannerURL() {
        return RootIndex + "/banner";
    }

    /**
     * VIP 红点
     *
     * @return
     */
    public static String getTipsURL() {
        return RootIndex + "/api/v1/tips";
    }

    public static String getForgetURL() {
        return RootIndex + "/api/v1/user_forget";
    }

    public static String getCheckUser() {
        return RootIndex + "/api/v1/checkUser";
    }

    /**
     * 发现短信验证码
     *
     * @return
     */
    public static String getSendVerify() {
        return RootIndex + "/sendVerify";
    }

    /**
     * 校验验证码
     *
     * @return
     */
    public static String getVerify() {
        return RootIndex + "/verify";
    }

    /**
     * 活动列表
     *
     * @return
     */
    public static String getActive() {
        return RootIndex + "/api/v1/active";
    }

    public static String getPrivacidadeURL() {
        return RootIndex + "/private.html";
    }
}
