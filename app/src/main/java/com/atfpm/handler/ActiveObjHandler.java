package com.atfpm.handler;

import com.atfpm.box.ActiveObj;
import com.atfpm.box.DynamicObj;
import com.atfpm.http.JsonHandle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
public class ActiveObjHandler {

    public static List<ActiveObj> getActiveObjList(JSONArray array) {
        List<ActiveObj> list = new ArrayList<ActiveObj>();

        for (int i = 0; i < array.length(); i++) {
            list.add(getActiveObj(JsonHandle.getJSON(array, i)));
        }

        return list;
    }

    public static ActiveObj getActiveObj(JSONObject json) {
        ActiveObj obj = new ActiveObj();

        obj.setTitle(JsonHandle.getString(json, ActiveObj.TITLE));
        obj.setContent(JsonHandle.getString(json, ActiveObj.CONTENT));
        obj.setCover(JsonHandle.getString(json, ActiveObj.COVER));
        obj.setCreateAt(JsonHandle.getLong(json, ActiveObj.CREATE_AT));
        obj.setId(JsonHandle.getString(json, ActiveObj.ID));
        obj.setIntro(JsonHandle.getString(json, ActiveObj.INTRO));

        obj.setPoster(UserObjHandler.getUserObj(JsonHandle.getJSON(json,
                ActiveObj.POSTER)));

        JSONObject tag = JsonHandle.getJSON(json, ActiveObj.TAG);
        if (tag != null) {
            obj.setTag_id(JsonHandle.getString(tag, ActiveObj.TAG_ID));
            obj.setTagTitle(JsonHandle.getString(tag, ActiveObj.TAG_TITLE));
            obj.setTagColor(JsonHandle.getString(tag, ActiveObj.TAG_COLOR));
        }

        return obj;
    }

}
