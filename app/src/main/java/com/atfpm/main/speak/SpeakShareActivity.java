package com.atfpm.main.speak;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.choise.ChoiseImageFileList;
import com.atfpm.choise.ChoiseImageListActivity;
import com.atfpm.choise.HttpFlieBox;
import com.atfpm.choise.ImgFileListActivity;
import com.atfpm.choise.PostFile;
import com.atfpm.dialog.MessageDialog;
import com.atfpm.dialog.WarningDialog;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.interfaces.PostFileCallback;
import com.atfpm.main.user.SMSActivity;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.ShowMessage;
import com.atfpm.tool.WinTool;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class SpeakShareActivity extends Activity {

    public final static int resultCode = 2046;
    private final static String NULL = "null";

    private Context context;

    private GridBaseAdapter gba = null;

    private List<String> localList;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.speak_share_shareTitle)
    private TextView confirm;
    @ViewInject(R.id.speak_share_edit)
    private EditText input;
    @ViewInject(R.id.speak_share_imgGrid)
    private GridView imgGrid;
    @ViewInject(R.id.speak_share_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_share);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
        WarningDialog.showWarningDialog(context, WarningDialog.COM_SPEAK_SHARE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        VipHandler.judgeVipTime(context, vipHint);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChoiseImageFileList.removeAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SpeakShareActivity.resultCode) {
            Bundle b = data.getExtras();
            if (b != null) {
                if (gba != null) {
                    gba = null;
                    imgGrid.setAdapter(null);
                }
                gba = new GridBaseAdapter(
                        checkImgList(ChoiseImageFileList
                                .getChoiseImageFileList()));
                imgGrid.setAdapter(gba);
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
        }
        return false;
    }

    @OnClick({R.id.titel_back, R.id.title_vip, R.id.speak_share_shareTitle})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titel_back:
                close();
                break;
            case R.id.title_vip:
                VipHandler.jumpOnlyVipActivity(context);
                break;
            case R.id.speak_share_shareTitle:
                if (!UserObjHandler.isLogin(context)) {
                    ShowMessage.showPleaseLoginDialog(context);
                } else {
                    uploadSpeak();
                }
                break;
        }
    }

    private void close() {
        if (ifShowCloseDialog()) {
            MessageDialog dialog = new MessageDialog(context);
            dialog.setMessage(getResources().getString(R.string.give_up));
            dialog.setCommitStyle(getResources().getString(R.string.confirm));
            dialog.setCancelStyle(getResources().getString(R.string.cancel));
            dialog.setCancelListener(null);
            dialog.setCommitListener(new MessageDialog.CallBackListener() {

                @Override
                public void callback() {
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    private boolean ifShowCloseDialog() {
        if (!input.getText().toString().equals("")) {
            return true;
        }
        if (localList.size() > 1) {
            return true;
        }
        return false;
    }

    private void initAcitvity() {
        titleName.setText(getResources().getString(R.string.speak_publish));
        VipHandler.judgeVipTime(context, vipHint);

        gba = new GridBaseAdapter(
                checkImgList(ChoiseImageFileList.getChoiseImageFileList()));
        imgGrid.setAdapter(gba);
    }

    private List<String> checkImgList(List<String> arrayList) {
        localList = new ArrayList<String>();
        localList.addAll(arrayList);
        if (localList.size() < 9) {
            localList.add(NULL);
        }
        return localList;
    }

    private void uploadSpeak() {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getTalkURL();
        HttpFlieBox box = new HttpFlieBox();
        box.addParams("accesstoken", UserObjHandler.getAccesstoken(context));
        box.addParams("content", input.getText().toString());
        box.addFileList("pic", localList);
        PostFile.getInstance().post(url, box, new PostFileCallback() {

            @Override
            public void callback(String result) {
                progress.setVisibility(View.GONE);
                Log.d("", result);
                JSONObject json = JsonHandle.getJSON(result);
                if (!ShowMessage.showException(context, json)) {
                    int r = JsonHandle.getInt(json, "result");
                    if (r == 1) {
                        ShowMessage.showToast(context, getResources()
                                .getString(R.string.send_succeed));
                        SpeakFrameLayout.UPLOAD = true;
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Exception exception) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    class GridBaseAdapter extends BaseAdapter {

        private List<String> list;
        private LayoutInflater infater;

        public GridBaseAdapter(List<String> list) {
            this.list = list;
            infater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            if (convertView == null) {
                convertView = infater.inflate(R.layout.choise_image, null);
            }

            final String path = list.get(position);

            ImageView img = (ImageView) convertView
                    .findViewById(R.id.choise_image_img);

            int width = WinTool.getWinWidth(context) / 4 - 20;
            img.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
            int p = 0;
            if (!path.equals(NULL)) {
                img.setPadding(p, p, p, p);
                DownloadImageLoader.loadImageForFile(img, path);
            } else {
                p = 28;
                img.setPadding(p, p, p, p);
                DownloadImageLoader.loadImageForID(img, R.drawable.check_bg);
            }

            final int i = position;
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (path.equals(NULL)) {
                        Passageway.jumpActivity(context,
                                ImgFileListActivity.class,
                                SpeakShareActivity.resultCode);
                    } else {
                        Bundle b = new Bundle();
                        b.putInt("position", i);
                        Passageway.jumpActivity(context,
                                ChoiseImageListActivity.class,
                                SpeakShareActivity.resultCode, b);
                    }
                }
            });

            return convertView;
        }
    }
}
