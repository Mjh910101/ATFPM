package com.atfpm.tool;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.atfpm.R;
import com.atfpm.dialog.MessageDialog;
import com.atfpm.dialog.MessageDialog.CallBackListener;
import com.atfpm.http.JsonHandle;
import com.atfpm.main.user.LoginActivity;

public class ShowMessage {

	public static void logException(Exception e) {
		if (e != null) {
			e.printStackTrace();
		}
	}

	public static void showToast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}

	public static void showToast(Context context, String msg) {
		if (context != null) {
			showToast(context, msg, 0);
		}
	}

	public static void showFailure(Context context) {
		showToast(context, context.getResources().getString(R.string.exception));
	}

	public static void showLast(Context context) {
		showToast(context, context.getResources().getString(R.string.last));
	}

	// public static void showPleaseLogin(Context context) {
	// showToast(context,
	// context.getResources().getString(R.string.please_login));
	// }

	public static void showPleaseLoginDialog(final Context context) {
		MessageDialog dialog = new MessageDialog(context);
		dialog.setTitel(context.getResources().getString(R.string.please_login));
		dialog.setCommitStyle(context.getResources().getString(R.string.login));
		dialog.setCancelStyle(context.getResources().getString(R.string.ok));
		dialog.setCancelListener(null);
		dialog.setCommitListener(new CallBackListener() {

			@Override
			public void callback() {
				Passageway.jumpActivity(context, LoginActivity.class);
			}
		});
	}

	public static void showException(Context context, Exception e) {
		// showToast(context, "出错了请稍后再试o(>﹏<)o");
		showToast(context, context.getResources().getString(R.string.exception));
		logException(e);
	}

	public static void showException(Context context, String s) {
		MessageDialog jpushDialog = new MessageDialog(context);
		jpushDialog.setMessage(s);
		jpushDialog.setCommitListener(null);
		jpushDialog.setCanceledOnTouchOutside(false);
	}

	public static void showException(Context context, Exception e,
			CallBackListener commitCallback) {
		showException(context, e, commitCallback, null);
	}

	public static void showException(Context context, Exception e,
			CallBackListener commitCallback, CallBackListener cancelCallback) {
		MessageDialog jpushDialog = new MessageDialog(context);
		jpushDialog.setMessage(context.getResources().getString(
				R.string.exception)
				+ "\n"
				+ context.getResources().getString(R.string.click_connect));
		jpushDialog.setCancelListener(cancelCallback);
		jpushDialog.setCommitListener(commitCallback);
		jpushDialog.setCanceledOnTouchOutside(false);
		logException(e);
	}

	public static boolean showException(Context context, JSONObject error) {
		if (error != null) {
			String msg = JsonHandle.getString(error, "error");
			Log.e("error", msg);
			if (!msg.equals("")) {
				showToast(context, msg);
				return true;
			}
		}
		return false;
	}

}
