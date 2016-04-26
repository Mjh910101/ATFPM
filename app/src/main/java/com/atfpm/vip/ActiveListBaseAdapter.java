package com.atfpm.vip;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.box.ActiveObj;
import com.atfpm.box.OriginObj;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.WinTool;

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
public class ActiveListBaseAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ActiveObj> itemList;
    private Context context;

    public ActiveListBaseAdapter(Context context, List<ActiveObj> list) {
        this.itemList = list;
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        HolderView holder;
        if (view == null) {
            view = inflater.inflate(R.layout.active_list_item, null);
            holder = new HolderView();

            holder.pic = (ImageView) view.findViewById(R.id.active_list_item_pic);
            holder.title = (TextView) view.findViewById(R.id.active_list_item_title);
            holder.tag = (TextView) view.findViewById(R.id.active_list_item_tag);
            holder.intro = (TextView) view.findViewById(R.id.active_list_item_intro);

            view.setTag(holder);
        } else {
            holder = (HolderView) view.getTag();
        }

        setViewMessage(holder, i);
        setViewPic(holder.pic, i);
        setViewTag(holder.tag, i);
        setOnClick(view, i);

        return view;
    }

    private void setViewTag(TextView tag, int i) {
        ActiveObj obj = itemList.get(i);
        if (obj.getTag_id() != null) {
            tag.setVisibility(View.VISIBLE);
            tag.setText(obj.getTagTitle());
            tag.setBackgroundColor(ColorBox.getColor(context, obj.getTagColor()));
        } else {
            tag.setVisibility(View.GONE);
        }
    }

    private void setViewPic(ImageView pic, int i) {
        ActiveObj obj = itemList.get(i);
        if (!obj.getCover().equals("")) {
            pic.setVisibility(View.VISIBLE);
            double w = WinTool.getWinWidth(context);
            double h = w / 320 * 139;
            pic.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
            pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            DownloadImageLoader.loadImage(pic, obj.getCover());
        } else {
            pic.setVisibility(View.GONE);
        }
    }

    private void setViewMessage(HolderView holder, int i) {
        ActiveObj obj = itemList.get(i);
        holder.title.setText(obj.getTitle());
        holder.intro.setText(obj.getIntro());
    }

    public void addItems(List<ActiveObj> list) {
        for (ActiveObj obj : list) {
            itemList.add(obj);
        }

        notifyDataSetChanged();
    }

    public void removeAll() {
        itemList.removeAll(itemList);
    }

    public void setOnClick(View v, final int p) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString(ActiveObj.ID, itemList.get(p).getId());
                Passageway.jumpActivity(context, ActiveContentActivity.class, b);
            }
        });
    }

    class HolderView {
        ImageView pic;
        TextView title;
        TextView tag;
        TextView intro;
    }
}
