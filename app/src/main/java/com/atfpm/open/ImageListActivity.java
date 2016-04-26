package com.atfpm.open;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.atfpm.R;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.tool.WinTool;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ImageListActivity extends Activity {

	private Context context;

	private List<String> list;
	private int position;
	private List<ImageView> ballList;

	@ViewInject(R.id.imageList_dataViewPager)
	private ViewPager dataPager;
	@ViewInject(R.id.imageList_sizeBox)
	private LinearLayout sizeBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagelist);

		context = this;
		ViewUtils.inject(this);

		initActivity();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initActivity() {

		Bundle b = getIntent().getExtras();
		if (b != null) {
			list = b.getStringArrayList("DataList");
			position = b.getInt("position");
			setImageList(list);
			setBallList(list.size(), position);
		}

	}

	private void setBallList(int size, int position) {
		if (size > 1) {
			int w = WinTool.pxToDip(context, 40);
			ballList = new ArrayList<ImageView>(size);
			for (int i = 0; i < size; i++) {
				ImageView v = new ImageView(context);
				v.setImageResource(R.drawable.ppt_off);
				v.setLayoutParams(new LinearLayout.LayoutParams(w, w));

				View l = new View(context);
				l.setLayoutParams(new LinearLayout.LayoutParams(w, w));

				sizeBox.addView(v);
				sizeBox.addView(l);

				ballList.add(v);
			}
			setOnBall(position);
		}
	}

	private void setOnBall(int p) {
		for (ImageView v : ballList) {
			v.setImageResource(R.drawable.ppt_off);
		}
		ballList.get(p).setImageResource(R.drawable.ppt_on);
	}

	private void setImageList(List<String> list) {
		// imageList = new ArrayList<ImageView>();
		// for (String pic : list) {
		// ImageView view = getImageView(pic);
		// imageList.add(view);
		// }
		dataPager.setAdapter(new ContentPagerAdapter(list));
		dataPager.setOffscreenPageLimit(1);
		dataPager.setCurrentItem(position);
		setContentBoxListener();
	}

	private PhotoView getImageView(String pic) {
		PhotoView view = new PhotoView(context);
		DownloadImageLoader.loadImage(view, pic);
		// view.setOnClickListener(onClickListener);
		return view;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}

	};

	private void setContentBoxListener() {
		dataPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				setOnBall(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		// contentBox.setOnTouchListener(new OnTouchListener() {
	}

	class ContentPagerAdapter extends PagerAdapter {

		private List<String> iamgePhatList;
//		private Map<Integer, PhotoView> imageMap;

		public ContentPagerAdapter(List<String> iamgePhatList) {
			this.iamgePhatList = iamgePhatList;
//			imageMap = new HashMap<Integer, PhotoView>();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			container.removeView(imageMap.get(position));
			container.removeView((View)object);
		}

		@Override
		public int getCount() {
			return iamgePhatList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PhotoView v= getImageView(iamgePhatList.get(position));
//			if (imageMap.containsKey(position)) {
//				v = imageMap.get(position);
//			} else {
//				v = getImageView(iamgePhatList.get(position));
//				imageMap.put(position, v);
//			}
			container.addView(v, 0);
			return v;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

}
