package com.atfpm.main.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.UserObj;
import com.atfpm.choise.HttpFlieBox;
import com.atfpm.dialog.ListDialog;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.handler.VipHandler;
import com.atfpm.http.HttpUtilsBox;
import com.atfpm.http.JsonHandle;
import com.atfpm.http.UrlHandle;
import com.atfpm.tool.ShowMessage;
import com.atfpm.tool.WinTool;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class PicCompileActivity extends Activity {

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "h.jpg";
    private static final String RESULT_IMAGE_FILE_NAME = "rh.png";

    private Context context;

    @ViewInject(R.id.title_name)
    private TextView titleName;
    @ViewInject(R.id.title_vipHint)
    private ImageView vipHint;
    @ViewInject(R.id.pic_compile_userPic)
    private ImageView userPic;
    @ViewInject(R.id.pic_compile_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_compile);
        context = this;
        ViewUtils.inject(this);

        initAcitvity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        VipHandler.judgeVipTime(context, vipHint);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                resizeImage(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                if (isSdcardExisting()) {
                    resizeImage(getImageUri());
                } else {
                    ShowMessage.showToast(context,
                            getResources().getString(R.string.not_sd));
                }
                break;
            case RESULT_REQUEST_CODE:
                if (data != null) {
                    getResizeImage(data);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.titel_back, R.id.title_vip, R.id.pic_compile_resetPic})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titel_back:
                finish();
                break;
            case R.id.title_vip:
                VipHandler.jumpOnlyVipActivity(context);
                break;
            case R.id.pic_compile_resetPic:
                showListMessage();
                break;
        }
    }

    private void getResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
//            Drawable drawable = new BitmapDrawable(photo);
            FileOutputStream foutput = null;
            try {
                foutput = new FileOutputStream(getImageFile());
                photo.compress(Bitmap.CompressFormat.PNG, 100, foutput);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (null != foutput) {
                    try {
                        foutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            DownloadImageLoader.loadImageForFile(userPic, getImageFile()
                    .toString());
            upLoadUserPic(getImageFile());
        }
    }

    private boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    private void showListMessage() {
        final List<String> msgList = getMsgList();
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(msgList);
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (msgList.get(position).equals(
                        getResources().getString(R.string.photo))) {
                    takePhoto();
                } else {
                    selectImage();
                }
                dialog.dismiss();
            }

        });
    }

    private void takePhoto() {
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
    }

    private void initAcitvity() {
        titleName.setText(getResources().getString(R.string.head));
        int w = WinTool.getWinWidth(context);
        userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(userPic,
                UserObjHandler.getUserAvatar(context));
        VipHandler.judgeVipTime(context, vipHint);
    }

    private void upLoadUserPic(File imageFile) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getUserAvatorURL();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("accesstoken",
                UserObjHandler.getAccesstoken(context));
        params.addBodyParameter("avator", imageFile);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(result);
                        if (!ShowMessage.showException(context, json)) {
                            UserObj obj = UserObjHandler.getUserObj(JsonHandle
                                    .getJSON(
                                            JsonHandle.getJSON(json, "result"),
                                            "user"));
                            if (!obj.getAccesstoken().equals("")) {
                                ShowMessage.showToast(context, getResources()
                                        .getString(R.string.send_succeed));
                                UserObjHandler.save(context, obj);
                                finish();
                            }
                        }
                    }

                });
    }

    private static File getImageFile() {
        return new File(HttpFlieBox.getImagePath(), RESULT_IMAGE_FILE_NAME);
    }

    private static Uri getImageUri() {
        return Uri.fromFile(new File(HttpFlieBox.getImagePath(),
                IMAGE_FILE_NAME));
    }

    public List<String> getMsgList() {
        List<String> list = new ArrayList<String>();
        list.add(getResources().getString(R.string.photo));
        list.add(getResources().getString(R.string.sd_card_pic));
        return list;
    }

}
