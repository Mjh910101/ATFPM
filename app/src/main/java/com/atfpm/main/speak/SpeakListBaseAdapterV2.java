package com.atfpm.main.speak;

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

import java.util.ArrayList;
import java.util.List;

public class SpeakListBaseAdapterV2 extends BaseAdapter {

    private LayoutInflater inflater;
    private List<OriginObj> itemList;
    private Context context;
    private ProgressBar progress;

    public SpeakListBaseAdapterV2(Context context, ProgressBar progress) {
        this.context = context;
        this.progress = progress;
        itemList = new ArrayList<OriginObj>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SpeakListBaseAdapterV2(Context context, List<OriginObj> itemList) {
        this.context = context;
        this.itemList = itemList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SpeakListBaseAdapterV2(Context context, List<DynamicObj> itemList,
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


    @Override
    public int getCount() {
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.speak_list_item, null);
            initViewHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OriginObj obj = null;
        DynamicObj dynamicObj = null;
        AdObj adObj = null;

        obj = itemList.get(position);

        if (obj.isContent()) {
            dynamicObj = (DynamicObj) obj;
            setView(holder, dynamicObj);
            setPicList(holder, dynamicObj);
            setOnClick(convertView, dynamicObj);
            setOnClickGood(holder, dynamicObj);
            setOnClickComment(holder, dynamicObj);
        } else if (obj.isAD()) {
            adObj = (AdObj) obj;
            setView(holder, adObj);
            setOnClick(convertView, adObj);
        }

        return convertView;
    }

    private void setOnClickGood(ViewHolder holder, final DynamicObj obj) {
        holder.goodBox.setOnClickListener(new OnClickListener() {

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

    private void setOnClickComment(ViewHolder holder, final DynamicObj obj) {
        holder.commentBox.setOnClickListener(new OnClickListener() {

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

    private void setView(ViewHolder holder, AdObj obj) {
        holder.contentBox.setVisibility(View.GONE);
        holder.adBox.setVisibility(View.VISIBLE);
        int w = WinTool.getWinWidth(context) / 11 * 10;
        int h = w / 3;
        holder.ad.setLayoutParams(new LinearLayout.LayoutParams(w, h));
        DownloadImageLoader.loadImage(holder.ad, obj.getImageUrl());
    }

    private void setView(ViewHolder holder, DynamicObj obj) {
        holder.userName.setText(obj.getUsetName());
        holder.time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt()));
        holder.dayTime.setText(DateHandle.format(obj.getCreateAt(),
                DateHandle.DATESTYP_4));
        holder.dayTime.setVisibility(View.GONE);
        holder.content.setText(obj.getContent());
        holder.commentSum.setText(String.valueOf(obj.getComment()));
        holder.likeSum.setText(String.valueOf(obj.getGood()));
        UserObjHandler.setUsetType(holder.userType, obj.getPoster());
//		hollowBall.setImageResource(R.drawable.hollow_ball_green);

        holder.contentBox.setVisibility(View.VISIBLE);
        holder.adBox.setVisibility(View.GONE);

        if (obj.isGood()) {
            holder.goodIcon.setImageResource(R.drawable.like_click_icon);
            holder.likeSum.setTextColor(ColorBox.getColorForID(context, R.color.text_green_02));
        } else {
            holder.goodIcon.setImageResource(R.drawable.like_off_icon);
            holder.likeSum.setTextColor(ColorBox.getColorForID(context,
                    R.color.text_gray_03));
        }

        if (DynamicObjHandler.isWatchNewsDynamicObj(context, obj)) {
            holder.userName.setTextColor(ColorBox.getColorForID(context, R.color.text_green_03));
            holder.time.setTextColor(ColorBox.getColorForID(context, R.color.text_gray_03));
            holder.content.setTextColor(ColorBox.getColorForID(context, R.color.text_gray_03));
        } else {
            holder.userName.setTextColor(ColorBox.getColorForID(context, R.color.text_green_03));
            holder.time.setTextColor(ColorBox.getColorForID(context, R.color.text_gray_04));
            holder.content.setTextColor(ColorBox.getColorForID(context, R.color.text_gray_04));
        }

        int w = WinTool.getWinWidth(context) / 8;
        holder.usetPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(holder.usetPic, obj.getUsetPic(), w / 2);

    }

    private void setPicList(ViewHolder holder, final DynamicObj obj) {
        List<String> pisList = obj.getPicList();
        int w = 0, width = 0;
        switch (pisList.size()) {
            case 1:
                w = WinTool.dipToPx(context, 160);
                width = WinTool.dipToPx(context, 160);
                holder.picGrid.setNumColumns(1);
                holder.picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                break;
            case 2:
            case 4:
                w = WinTool.dipToPx(context, 80);
                width = WinTool.dipToPx(context, 170);
                holder.picGrid.setNumColumns(2);
                holder.picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                break;
            default:
                w = WinTool.dipToPx(context, 80);
                width = WinTool.dipToPx(context, 255);
                holder.picGrid.setNumColumns(3);
                holder.picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                break;
        }
        holder.picGrid.setAdapter(new PicBaseAdapter(obj, w));
    }

    private void initViewHolder(ViewHolder holder, View v) {
        holder.userName = (TextView) v.findViewById(R.id.speak_item_userName);
        holder.usetPic = (ImageView) v.findViewById(R.id.speak_item_userPic);
        holder.time = (TextView) v.findViewById(R.id.speak_item_time);
        holder.dayTime = (TextView) v.findViewById(R.id.speak_item_dayTime);
        holder.content = (TextView) v.findViewById(R.id.speak_item_content);
        holder.commentSum = (TextView) v
                .findViewById(R.id.speak_item_commentSum);
        holder.likeSum = (TextView) v.findViewById(R.id.speak_item_likeSum);
        holder.goodIcon = (ImageView) v
                .findViewById(R.id.speak_item_goodIcon);
        holder.userType = (ImageView) v
                .findViewById(R.id.speak_item_userType);
        holder.contentBox = (RelativeLayout) v
                .findViewById(R.id.speak_item_userMessageBox);
        holder.adBox = (RelativeLayout) v
                .findViewById(R.id.speak_listitem_adBox);
        holder.picGrid = (InsideGridView) v
                .findViewById(R.id.speak_listitem_picGrid);
        holder.goodBox = (LinearLayout) v
                .findViewById(R.id.speak_item_likeBox);
        holder.commentBox = (LinearLayout) v
                .findViewById(R.id.speak_item_commentBox);
        holder.ad = (ImageView) v.findViewById(R.id.speak_listitem_ad);
    }

    static class ViewHolder {
        TextView userName;
        ImageView usetPic;
        TextView time;
        TextView dayTime;
        TextView content;
        TextView commentSum;
        TextView likeSum;
        ImageView goodIcon;
        ImageView userType;
        RelativeLayout contentBox;
        RelativeLayout adBox;
        InsideGridView picGrid;
        LinearLayout goodBox;
        LinearLayout commentBox;
        ImageView ad;
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
