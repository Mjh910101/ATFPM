package com.atfpm.box;

/**
 * *
 * * ┏┓      ┏┓
 * *┏┛┻━━━━━━┛┻┓
 * *┃          ┃
 * *┃          ┃
 * *┃ ┳┛   ┗┳  ┃
 * *┃          ┃
 * *┃    ┻     ┃
 * *┃          ┃
 * *┗━┓      ┏━┛
 * *  ┃      ┃
 * *  ┃      ┃
 * *  ┃      ┗━━━┓
 * *  ┃          ┣┓
 * *  ┃         ┏┛
 * *  ┗┓┓┏━━━┳┓┏┛
 * *   ┃┫┫   ┃┫┫
 * *   ┗┻┛   ┗┻┛
 * Created by Hua on 15/11/25.
 */
public class ActiveObj {

    public final static String TAG = "tag";
    public final static String TAG_ID = "tag_id";
    public final static String TAG_TITLE = "tagTitle";
    public final static String TAG_COLOR = "tagColor";
    public final static String TITLE = "title";
    public final static String ID = "id";
    public final static String INTRO = "intro";
    public final static String COVER = "cover";
    public final static String CONTENT = "content";
    public final static String CREATE_AT = "createAt";
    public final static String POSTER = "poster";

    private String tag_id;
    private String tagTitle;
    private String tagColor;
    private String title;
    private String id;
    private String intro;
    private String cover;
    private String content;
    private long createAt;
    private UserObj poster;

    public UserObj getPoster() {
        return poster;
    }

    public void setPoster(UserObj poster) {
        this.poster = poster;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getUsetPic() {
        if (poster != null) {
            return poster.getAvatar();
        }
        return "";
    }

    public String getUsetName() {
        if (poster != null) {
            return poster.getP_name();
        }
        return "";
    }
}
