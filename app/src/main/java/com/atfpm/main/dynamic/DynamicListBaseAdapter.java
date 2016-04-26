package com.atfpm.main.dynamic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import com.atfpm.open.ImageListActivity;
import com.atfpm.tool.ColorBox;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.WinTool;
import com.atfpm.view.InsideGridView;

public class DynamicListBaseAdapter extends BaseAdapter {

	private static String R1 = "%@1@%";
	private static String R2 = "%@2@%";
	private static String PRICE_1 = "<font color=\"#666666\">" + R1;
	private static String PRICE_2 = "</font><font color=\"#44b13f\">" + R2
			+ "</font>";

	private LayoutInflater inflater;
	private List<OriginObj> itemList;
	private Context context;
	private View headerView;
	private ProgressBar progress;

	public DynamicListBaseAdapter(Context context, List<OriginObj> list,
			ProgressBar progress) {
		this.itemList = list;
		this.context = context;
		this.progress = progress;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public DynamicListBaseAdapter(Context context, List<DynamicObj> list,
			String postType, ProgressBar progress) {
		this.itemList = new ArrayList<OriginObj>();
		this.context = context;
		this.progress = progress;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		addDynamicObjItems(list);
	}

	public long getLsatTime() {
		return itemList.get(itemList.size() - 1).getCreateAt();
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

	public void addHeaderView(View headerView) {
		this.headerView = headerView;
	}

	private void addItem(OriginObj obj) {
		itemList.add(obj);
	}

	public void removeAll() {
		itemList.removeAll(itemList);
	}

	@Override
	public int getCount() {
		if (headerView != null) {
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
		convertView = inflater.inflate(R.layout.dynamic_list_item_v2, null);
		// }

		OriginObj obj = null;
		DynamicObj dynamicObj = null;
		AdObj adObj = null;
		if (headerView != null) {
			if (position == 0) {
				return headerView;
			}
			obj = itemList.get(position - 1);
		} else {
			obj = itemList.get(position);
		}
		if (obj.isContent()) {
			dynamicObj = (DynamicObj) obj;
			setView(convertView, dynamicObj);
			setContentView(convertView, dynamicObj);
			setPicList(convertView, dynamicObj);
			setOnClick(convertView, dynamicObj);
			setOnClickComment(convertView, dynamicObj);
			setOnClickGood(convertView, dynamicObj);
			setOnClickFavor(convertView, dynamicObj);
		} else if (obj.isAD()) {
			adObj = (AdObj) obj;
			setView(convertView, adObj);
			setOnClick(convertView, adObj);
		}
		return convertView;
	}

	private void setPicList(View convertView, final DynamicObj obj) {
		InsideGridView picGrid = (InsideGridView) convertView
				.findViewById(R.id.dynamic_listitem_picGrid);
		List<String> pisList = obj.getPicList();
		int w = 0, width = 0;
		switch (pisList.size()) {
		case 1:
			w = WinTool.dipToPx(context, 180);
			width = WinTool.dipToPx(context, 180);
			picGrid.setNumColumns(1);
			picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
					LayoutParams.WRAP_CONTENT));
			break;
		case 2:
		case 4:
			w = WinTool.dipToPx(context, 90);
			width = WinTool.dipToPx(context, 190);
			picGrid.setNumColumns(2);
			picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
					LayoutParams.WRAP_CONTENT));
			break;
		default:
			w = WinTool.dipToPx(context, 90);
			width = WinTool.dipToPx(context, 285);
			picGrid.setNumColumns(3);
			picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
					LayoutParams.WRAP_CONTENT));
			break;
		}
		picGrid.setAdapter(new PicBaseAdapter(obj, w));
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
				DynamicObjBox.putDynamicObj(obj);
				Passageway.jumpActivity(context, DynamicContentActivity.class);
				// b.putString(DynamicObj.ID, obj.getId());
				// Passageway.jumpActivity(context,
				// DynamicContentActivity.class,
				// b);
				DynamicObjHandler.watchNewsDynamicObj(context, obj);
			}
		});
	}

	private void setOnClickComment(View v, final DynamicObj obj) {
		LinearLayout commentBox = (LinearLayout) v
				.findViewById(R.id.dynamic_listitem_commentBox);
		commentBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle b = new Bundle();
				DynamicObjBox.putDynamicObj(obj);
				b.putString(DynamicObj.ID, obj.getId());
				b.putBoolean(DynamicObj.COMMENT, true);
				Passageway.jumpActivity(context, DynamicContentActivity.class,
						b);
				DynamicObjHandler.watchNewsDynamicObj(context, obj);
			}
		});
	}

	private void setOnClickFavor(View v, final DynamicObj obj) {
		LinearLayout favorBox = (LinearLayout) v
				.findViewById(R.id.dynamic_listitem_favorBox);
		favorBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				progress.setVisibility(View.VISIBLE);
				CommentObjHandler.sendFavor(context, obj,
						obj.getFavorOperate(), new CallbackForBoolean() {

							@Override
							public void callback(boolean b) {
								progress.setVisibility(View.GONE);
								if (b) {
									obj.setFavorOperate();
									notifyDataSetChanged();
								}
							}
						});
			}
		});
	}

	private void setOnClickGood(View v, final DynamicObj obj) {
		LinearLayout goodBox = (LinearLayout) v
				.findViewById(R.id.dynamic_listitem_likeBox);
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

	private void setContentView(View v, DynamicObj obj) {
		int max = 68;
		TextView content = (TextView) v
				.findViewById(R.id.dynamic_listitem_content);
		String c = obj.getContent();
		if (!c.equals("")) {
			String m = context.getResources().getString(R.string.mais);
			if (c.length() >= max) {
				c = c.subSequence(0, max) + "...";
				c = PRICE_1.replace(R1, c) + PRICE_2.replace(R2, m);
			} else {
				c = PRICE_1.replace(R1, c);
			}
			content.setVisibility(View.VISIBLE);
			content.setText(Html.fromHtml(c));
		} else {
			content.setVisibility(View.GONE);
		}
	}

	private void setView(View v, AdObj obj) {
		LinearLayout contentBox = (LinearLayout) v
				.findViewById(R.id.dynamic_listitem_contentBox);
		RelativeLayout adBox = (RelativeLayout) v
				.findViewById(R.id.dynamic_listitem_adBox);
		ImageView ad = (ImageView) v.findViewById(R.id.dynamic_listitem_ad);
		contentBox.setVisibility(View.GONE);
		adBox.setVisibility(View.VISIBLE);
		int w = WinTool.getWinWidth(context);
		int h = w / 3;
		ad.setLayoutParams(new LinearLayout.LayoutParams(w, h));
		DownloadImageLoader.loadImage(ad, obj.getImageUrl());
	}

	private void setView(View v, DynamicObj obj) {
		TextView title = (TextView) v.findViewById(R.id.dynamic_listitem_title);
		TextView likeSum = (TextView) v
				.findViewById(R.id.dynamic_listitem_likeSum);
		TextView commentSum = (TextView) v
				.findViewById(R.id.dynamic_listitem_commentSum);
		TextView collectSum = (TextView) v
				.findViewById(R.id.dynamic_listitem_collectSum);
		ImageView usetPic = (ImageView) v
				.findViewById(R.id.dynamic_listitem_userIcon);
		TextView userName = (TextView) v
				.findViewById(R.id.dynamic_listitem_userName);
		TextView time = (TextView) v.findViewById(R.id.dynamic_listitem_time);
		ImageView goodIcon = (ImageView) v
				.findViewById(R.id.dynamic_listitem_likeIcon);
		ImageView favorIcon = (ImageView) v
				.findViewById(R.id.dynamic_listitem_favorIcon);
		ImageView newIcon = (ImageView) v
				.findViewById(R.id.dynamic_listitem_newIcon);
		ImageView usetType = (ImageView) v
				.findViewById(R.id.dynamic_listitem_usetType);
		LinearLayout contentBox = (LinearLayout) v
				.findViewById(R.id.dynamic_listitem_contentBox);
		RelativeLayout adBox = (RelativeLayout) v
				.findViewById(R.id.dynamic_listitem_adBox);

		contentBox.setVisibility(View.VISIBLE);
		adBox.setVisibility(View.GONE);

		title.setText(obj.getTitle());
		likeSum.setText(String.valueOf(obj.getGood()));
		commentSum.setText(String.valueOf(obj.getComment()));
		collectSum.setText(String.valueOf(obj.getFavor()));
		userName.setText(obj.getUsetName());
		time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt()));

		// UserObjHandler.setUsetType(usetType, obj.getPoster());

		if (obj.isGood()) {
			goodIcon.setImageResource(R.drawable.like_click_icon);
			likeSum.setTextColor(ColorBox.getColorForID(context, R.color.text_green_02));
		} else {
			goodIcon.setImageResource(R.drawable.like_off_icon);
			likeSum.setTextColor(ColorBox.getColorForID(context,
					R.color.text_gray_03));
		}

		if (obj.isFavor()) {
			favorIcon.setImageResource(R.drawable.collect_click_icon);
			collectSum.setTextColor(ColorBox
					.getColorForID(context, R.color.text_green_02));
		} else {
			favorIcon.setImageResource(R.drawable.collect_off_icon);
			collectSum.setTextColor(ColorBox.getColorForID(context,
					R.color.text_gray_03));
		}

		if (DynamicObjHandler.isWatchNewsDynamicObj(context, obj)) {
			newIcon.setVisibility(View.GONE);
		} else {
			newIcon.setVisibility(View.VISIBLE);
		}

		int w = WinTool.getWinWidth(context) / 10;
		usetPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(usetPic, obj.getUsetPic(), w / 2);
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
			image.setScaleType(ScaleType.CENTER_CROP);
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
