package com.atfpm.main.speak;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.AdObj;
import com.atfpm.box.DynamicObj;
import com.atfpm.box.OriginObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.CommentObjHandler;
import com.atfpm.handler.DateHandle;
import com.atfpm.handler.DynamicObjHandler;
import com.atfpm.handler.UserObjHandler;
import com.atfpm.interfaces.CallbackForBoolean;
import com.atfpm.main.dynamic.DynamicContentActivity;
import com.atfpm.main.dynamic.DynamicObjBox;
import com.atfpm.open.ImageListActivity;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.WinTool;
import com.atfpm.view.InsideGridView;

@Deprecated
public class SpeakListBaseAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<OriginObj> itemList;
    private Context context;
    private ProgressBar progress;
    private View headView;

    public SpeakListBaseAdapter(Context context, ProgressBar progress) {
        this.context = context;
        this.progress = progress;
        itemList = new ArrayList<OriginObj>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SpeakListBaseAdapter(Context context, List<OriginObj> itemList) {
        this.context = context;
        this.itemList = itemList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SpeakListBaseAdapter(Context context, List<DynamicObj> itemList,
                                String postType, ProgressBar progress) {
        this.context = context;
        this.itemList = new ArrayList<OriginObj>();
        this.progress = progress;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addDynamicObjItems(itemList);
    }

    public void removeAll() {
        itemList.removeAll(itemList);
    }

    public void addDynamicObjItems(List<DynamicObj> list) {
        for (OriginObj obj : list) {
            obj.setPostType(OriginObj.CONTENT);
            addItem(obj);
        }
        notifyDataSetChanged();
    }

    public void addItems(List<OriginObj> list) {
        for (OriginObj obj : list) {
            addItem(obj);
        }
        notifyDataSetChanged();
    }

    private void addItem(OriginObj obj) {
        itemList.add(obj);
    }

    public long getLsatTime() {
        return itemList.get(itemList.size() - 1).getCreateAt();
    }

    public void addHeaderView(View headView) {
        this.headView = headView;
    }

    @Override
    public int getCount() {
        if (headView != null) {
            return itemList.size() + 1;
        }
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if (convertView == null) {
        convertView = inflater.inflate(R.layout.speak_list_item, null);
        // }

        OriginObj obj = null;
        DynamicObj dynamicObj = null;
        AdObj adObj = null;
        if (headView != null) {
            if (position == 0) {
                return headView;
            }
            obj = itemList.get(position - 1);
        } else {
            obj = itemList.get(position);
        }
        if (obj.isContent()) {
            dynamicObj = (DynamicObj) obj;
            setView(convertView, dynamicObj);
            setPicList(convertView, dynamicObj);
            setOnClick(convertView, dynamicObj);
            setOnClickGood(convertView, dynamicObj);
            setOnClickComment(convertView, dynamicObj);
        } else if (obj.isAD()) {
            adObj = (AdObj) obj;
            setView(convertView, adObj);
            setOnClick(convertView, adObj);
        }

        return convertView;
    }

    private void setOnClickGood(View v, final DynamicObj obj) {
        LinearLayout goodBox = (LinearLayout) v
                .findViewById(R.id.speak_item_likeBox);
        goodBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                progress.setVisibility(View.VISIBLE);
                CommentObjHandler.sendGood(context, obj, obj.getGoodOperate(),
                        new CallbackForBoolean() {

                            @Override
                            public void callback(boolean b) {
                                progress.setVisibility(View.GONE);
                                if (b) {
                                    obj.setGoodOperate();
                                    notifyDataSetChanged();
                                }
                            }
                        });
            }
        });
    }

    private void setOnClick(View convertView, final AdObj obj) {
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AdObj.jumpAD(context, obj);
            }
        });
    }

    private void setOnClick(View convertView, final DynamicObj obj) {
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(DynamicObj.ID, obj.getId());
                Passageway.jumpActivity(context, SpeakContentActivity.class, b);
                DynamicObjHandler.watchNewsDynamicObj(context, obj);
            }
        });
    }

    private void setOnClickComment(View v, final DynamicObj obj) {
        LinearLayout commentBox = (LinearLayout) v
                .findViewById(R.id.speak_item_commentBox);
        commentBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Bundle b = new Bundle();
                DynamicObjBox.putDynamicObj(obj);
                b.putString(DynamicObj.ID, obj.getId());
                b.putBoolean(DynamicObj.COMMENT, true);
                Passageway.jumpActivity(context, SpeakContentActivity.class, b);
                DynamicObjHandler.watchNewsDynamicObj(context, obj);
            }
        });
    }

    private void setView(View v, AdObj obj) {
//		ImageView hollowBall = (ImageView) v
//				.findViewById(R.id.speak_item_hollowBall);
        RelativeLayout contentBox = (RelativeLayout) v
                .findViewById(R.id.speak_item_userMessageBox);
        RelativeLayout adBox = (RelativeLayout) v
                .findViewById(R.id.speak_listitem_adBox);
        ImageView ad = (ImageView) v.findViewById(R.id.speak_listitem_ad);
//		hollowBall.setImageResource(R.drawable.hollow_ball_red);
        contentBox.setVisibility(View.GONE);
        adBox.setVisibility(View.VISIBLE);
        int w = WinTool.getWinWidth(context) / 11 * 10;
        int h = w / 3;
        ad.setLayoutParams(new LinearLayout.LayoutParams(w, h));
        DownloadImageLoader.loadImage(ad, obj.getImageUrl());
    }

    private void setView(View v, DynamicObj obj) {

        TextView userName = (TextView) v.findViewById(R.id.speak_item_userName);
        ImageView usetPic = (ImageView) v.findViewById(R.id.speak_item_userPic);
//		ImageView hollowBall = (ImageView) v
//				.findViewById(R.id.speak_item_hollowBall);
        TextView time = (TextView) v.findViewById(R.id.speak_item_time);
        TextView dayTime = (TextView) v.findViewById(R.id.speak_item_dayTime);
        TextView content = (TextView) v.findViewById(R.id.speak_item_content);
        TextView commentSum = (TextView) v
                .findViewById(R.id.speak_item_commentSum);
        TextView likeSum = (TextView) v.findViewById(R.id.speak_item_likeSum);
        ImageView goodIcon = (ImageView) v
                .findViewById(R.id.speak_item_goodIcon);
        ImageView userType = (ImageView) v
                .findViewById(R.id.speak_item_userType);
        RelativeLayout contentBox = (RelativeLayout) v
                .findViewById(R.id.speak_item_userMessageBox);
        RelativeLayout adBox = (RelativeLayout) v
                .findViewById(R.id.speak_listitem_adBox);

        userName.setText(obj.getUsetName());
        time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt()));
        dayTime.setText(DateHandle.format(obj.getCreateAt(),
                DateHandle.DATESTYP_4));
        dayTime.setVisibility(View.GONE);
        content.setText(obj.getContent());
        commentSum.setText(String.valueOf(obj.getComment()));
        likeSum.setText(String.valueOf(obj.getGood()));
        UserObjHandler.setUsetType(userType, obj.getPoster());
//		hollowBall.setImageResource(R.drawable.hollow_ball_green);

        contentBox.setVisibility(View.VISIBLE);
        adBox.setVisibility(View.GONE);

        if (obj.isGood()) {
            goodIcon.setImageResource(R.drawable.like_click_icon);
            likeSum.setTextColor(ColorBox.getColorForID(context, R.color.text_green_02));
        } else {
            goodIcon.setImageResource(R.drawable.like_off_icon);
            likeSum.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_03));
        }

        if (DynamicObjHandler.isWatchNewsDynamicObj(context, obj)) {
            userName.setTextColor(ColorBox.getColorForID(context, R.color.text_green_03));
            time.setTextColor(ColorBox.getColorForID(context, R.color.text_gray_03));
            content.setTextColor(ColorBox.getColorForID(context, R.color.text_gray_03));
        } else {
            userName.setTextColor(ColorBox.getColorForID(context, R.color.text_green_03));
            time.setTextColor(ColorBox.getColorForID(context, R.color.text_gray_04));
            content.setTextColor(ColorBox.getColorForID(context, R.color.text_gray_04));
        }

        int w = WinTool.getWinWidth(context) / 8;
        usetPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(usetPic, obj.getUsetPic(), w / 2);

    }

    private void setPicList(View convertView, final DynamicObj obj) {
        InsideGridView picGrid = (InsideGridView) convertView
                .findViewById(R.id.speak_listitem_picGrid);
        List<String> pisList = obj.getPicList();
        int w = 0, width = 0;
        switch (pisList.size()) {
            case 1:
                w = WinTool.dipToPx(context, 160);
                width = WinTool.dipToPx(context, 160);
                picGrid.setNumColumns(1);
                picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                break;
            case 2:
            case 4:
                w = WinTool.dipToPx(context, 80);
                width = WinTool.dipToPx(context, 170);
                picGrid.setNumColumns(2);
                picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                break;
            default:
                w = WinTool.dipToPx(context, 80);
                width = WinTool.dipToPx(context, 255);
                picGrid.setNumColumns(3);
                picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                break;
        }
        picGrid.setAdapter(new PicBaseAdapter(obj, w));
    }

    class PicBaseAdapter extends BaseAdapter {

        private DynamicObj obj;
        private List<String> picList;
        private LayoutInflater inflater;
        private int w;
        private int size;

        public PicBaseAdapter(DynamicObj obj, int w) {
            this.obj = obj;
            this.picList = obj.getPicList();
            this.w = w;
            this.size = picList.size();
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // return picList.size() > 9 ? 9 : picList.size();
            switch (size) {
                case 1:
                case 2:
                case 3:
                case 4:
                    return size;
                case 5:
                case 6:
                    return 6;
                case 7:
                case 8:
                case 9:
                    return 9;
                default:
                    if (size <= 0) {
                        return 0;
                    } else {
                        return 9;

                    }
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.image_layout, null);
            }
            String url = "";
            try {
                url = picList.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ImageView image = (ImageView) convertView
                    .findViewById(R.id.image_layout_item);
            image.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (!url.equals("")) {
                DownloadImageLoader.loadImage(image, url);
                setOnClick(convertView, position);
            } else {
                setOnClick(convertView, obj);
            }

            return convertView;
        }

        private void setOnClick(View convertView, final DynamicObj obj) {
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    DynamicObjBox.putDynamicObj(obj);
                    Passageway.jumpActivity(context,
                            DynamicContentActivity.class);
                    DynamicObjHandler.watchNewsDynamicObj(context, obj);
                }
            });
        }

        private void setOnClick(View convertView, final int p) {
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("DataList",
                            (ArrayList<String>) picList);
                    b.putInt("position", p);
                    Passageway
                            .jumpActivity(context, ImageListActivity.class, b);
                }
            });

        }

    }

}
