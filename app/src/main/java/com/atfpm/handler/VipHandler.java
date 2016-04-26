package com.atfpm.handler;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.atfpm.R;
import com.atfpm.dialog.MessageDialog;
import com.atfpm.dialog.MessageDialog.CallBackListener;
import com.atfpm.system.SystemHandle;
import com.atfpm.tool.Passageway;
import com.atfpm.vip.ActiveListActicity;
import com.atfpm.vip.VipActicity;
import com.atfpm.vip.VipInscreverActivity;

public class VipHandler {

    private static long VIP_TIME = 0;

    public static void jumpOnlyVipActivity(Context context) {
        if (!UserObjHandler.isLogin(context)) {
            showVipDialog(context);
        } else if (!UserObjHandler
                .isMenber(UserObjHandler.getUserType(context))) {
            showVipDialog(context);
        } else {
            jumpVipActivity(context);
        }
    }

    private static void showVipDialog(final Context context) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitel(context.getResources().getString(
                R.string.vip_dialog_title));
        dialog.setCommitStyle(context.getResources().getString(
                R.string.vip_dialog_commit));
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                Passageway.jumpActivity(context, VipInscreverActivity.class);
            }
        });
        dialog.setCancelStyle(context.getResources().getString(
                R.string.look_look));
        dialog.setCancelListener(new CallBackListener() {

            @Override
            public void callback() {
                jumpVipActivity(context);
            }
        });
    }

    private static void jumpVipActivity(Context context) {
        Activity a = (Activity) context;
        Passageway.jumpActivity(context, VipActicity.class);
        a.overridePendingTransition(R.anim.circulation_in,
                R.anim.circulation_out);
        SystemHandle.setVipTime(context, VIP_TIME);

    }

    public static void setVipTime(long time) {
        VIP_TIME = time;
    }

    public static void judgeVipTime(Context context, View view) {
        if (VIP_TIME != SystemHandle.getVipTime(context)) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void jumpActivityListActivity(Context context) {
        Activity a = (Activity) context;
        Passageway.jumpActivity(context, ActiveListActicity.class);
        a.overridePendingTransition(R.anim.circulation_in,
                R.anim.circulation_out);
        SystemHandle.setVipTime(context, VIP_TIME);

    }
}
