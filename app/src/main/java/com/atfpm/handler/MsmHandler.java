package com.atfpm.handler;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.atfpm.R;
import com.atfpm.dialog.ListDialog;
import com.atfpm.interfaces.CallbackForInteger;

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
 * Created by Hua on 15/8/13.
 */
public class MsmHandler {

    private static List<String> getListString(Context context) {
        List<String> list = new ArrayList<String>();
        list.add(context.getResources().getString(R.string.cade_mo_l_name));
        list.add(context.getResources().getString(R.string.cade_hk_l_name));
        list.add(context.getResources().getString(R.string.cade_cn_l_name));
        return list;
    }

    public static void showMsmDialog(Context context, final CallbackForInteger callback) {
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(getListString(context));
        dialog.setItemListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callback.callback(i);
                dialog.dismiss();
            }
        });
    }

    public static int getCode(int i) {
        switch (i) {
            case 0:
                return 853;
            case 1:
                return 852;
            default:
                return 86;
        }
    }

    public static String getCodeForText(int i) {
        switch (i) {
            case 0:
                return "00853";
            case 1:
                return "00852";
            default:
                return "";
        }
    }

    public static String getCodeName(Context context, int i) {
        switch (i) {
            case 0:
                return context.getResources().getString(R.string.cade_mo_s_name);
            case 1:
                return context.getResources().getString(R.string.cade_hk_s_name);
            default:
                return context.getResources().getString(R.string.cade_cn_s_name);
        }
    }

    public static String getCodeName(Context context, String code) {
        if (code.equals("853")) {
            return context.getResources().getString(R.string.cade_mo_s_name);
        } else if (code.equals("852")) {
            return context.getResources().getString(R.string.cade_hk_s_name);
        } else {
            return context.getResources().getString(R.string.cade_cn_s_name);
        }
    }
}
