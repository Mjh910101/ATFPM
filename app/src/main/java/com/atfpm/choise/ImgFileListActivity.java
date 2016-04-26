package com.atfpm.choise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.handler.VipHandler;
import com.atfpm.main.speak.SpeakShareActivity;
import com.atfpm.tool.Passageway;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ImgFileListActivity extends Activity {

	private final static String FILECOUNT = "filecount";
	private final static String FILENAME = "filename";
	private final static String IMGPATH = "imgpath";

	private Context context;

	private List<FileTraversal> locallist;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_vipHint)
	private ImageView vipHint;
	@ViewInject(R.id.image_file_dataList)
	private ListView dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imgfilelist);

		context = this;
		ViewUtils.inject(this);

		initActivity();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		VipHandler.judgeVipTime(context, vipHint);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == SpeakShareActivity.resultCode) {
			setResult(resultCode, data);
			finish();
		}
	}

	@OnClick({ R.id.titel_back, R.id.title_vip })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_vip:
			VipHandler.jumpOnlyVipActivity(context);
			break;
		}
	}

	private void initActivity() {
		titleName.setText(getResources().getString(R.string.image_title));
		VipHandler.judgeVipTime(context, vipHint);

		locallist = Util.LocalImgFileList(context);
		List<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
		Bitmap bitmap[] = null;
		if (locallist != null) {
			bitmap = new Bitmap[locallist.size()];
			for (int i = 0; i < locallist.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				int size = locallist.get(i).filecontent.size();
				// map.put(FILECOUNT,
				// size + getResources().getString(R.string.picture));
				map.put(FILECOUNT, size + "");
				map.put(IMGPATH,
						locallist.get(i).filecontent.get(size - 1) == null ? null
								: (locallist.get(i).filecontent.get(size - 1)));
				map.put(FILENAME, locallist.get(i).filename);
				listdata.add(map);
			}
		}

		dataList.setAdapter(new ImgFileListAdapter(listdata));
		dataList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", locallist.get(arg2));
				Passageway.jumpActivity(context, ImgListActivity.class,
						SpeakShareActivity.resultCode, bundle);
			}
		});
	}

	class ImgFileListAdapter extends BaseAdapter {

		private LayoutInflater infater;

		List<HashMap<String, String>> listdata;

		public ImgFileListAdapter(List<HashMap<String, String>> listdata) {
			this.listdata = listdata;
			infater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return listdata.size();
		}

		@Override
		public Object getItem(int position) {
			return listdata.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {
				convertView = infater.inflate(R.layout.img_file_item, null);
			}

			ImageView img = (ImageView) convertView
					.findViewById(R.id.img_file_imgview);
			TextView name = (TextView) convertView
					.findViewById(R.id.img_file_name);
			TextView sum = (TextView) convertView
					.findViewById(R.id.img_file_sum);

			sum.setText(listdata.get(position).get(FILECOUNT));
			name.setText(listdata.get(position).get(FILENAME));

			DownloadImageLoader.loadImageForFile(img, listdata.get(position)
					.get(IMGPATH));

			return convertView;
		}

	}

}
