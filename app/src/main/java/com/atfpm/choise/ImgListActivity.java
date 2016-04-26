package com.atfpm.choise;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.dialog.MessageDialog;
import com.atfpm.download.DownloadImageLoader;
import com.atfpm.main.speak.SpeakShareActivity;
import com.atfpm.tool.WinTool;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ImgListActivity extends Activity {

	private final static int MAX_SIZE = 9;

	private Context context;

	private FileTraversal fileTraversal;
	// private List<String> fileList;

	@ViewInject(R.id.title_name)
	private TextView titleName;
	@ViewInject(R.id.title_confirm)
	private TextView confirm;
	@ViewInject(R.id.title_vip)
	private ImageView vip;
	@ViewInject(R.id.image_dataGrid)
	private GridView dataGrid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imglist);

		context = this;
		ViewUtils.inject(this);

		initActivity();
	}

	@OnClick({ R.id.titel_back, R.id.title_confirm })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titel_back:
			finish();
			break;
		case R.id.title_confirm:
			confirm();
			break;
		}
	}

	private void initActivity() {
		titleName.setText(getResources().getString(R.string.image_title));
		confirm.setVisibility(View.VISIBLE);
		vip.setVisibility(View.GONE);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			fileTraversal = bundle.getParcelable("data");
			dataGrid.setAdapter(new ImgListAdapter(fileTraversal.filecontent));
		}

		setConfirmSize();
	}

	private void setConfirmSize() {
		confirm.setText(getResources().getString(R.string.select) + "("
				+ ChoiseImageFileList.getChoiseSiza() + "/" + MAX_SIZE + ")");
	}

	private void showMessage() {
		MessageDialog dialog = new MessageDialog(context);
		dialog.setMessage(getResources().getString(R.string.max_image)+" "
				+ MAX_SIZE+" " + getResources().getString(R.string.a_picture));
		dialog.setCancelStyle(getResources().getString(R.string.i_know));
		dialog.setCancelListener(null);
	}

	private void confirm() {
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putBoolean("data", true);
		i.putExtras(b);
		setResult(SpeakShareActivity.resultCode, i);
		finish();
	}

	class ImgListAdapter extends BaseAdapter {

		private LayoutInflater infater;

		List<String> listdata;

		public ImgListAdapter(List<String> listdata) {
			Collections.reverse(listdata);
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
				convertView = infater.inflate(R.layout.img_item, null);
			}

			ImageView img = (ImageView) convertView
					.findViewById(R.id.img_imgview);
			final CheckBox checkBox = (CheckBox) convertView
					.findViewById(R.id.img_checkBox);

			final String path = listdata.get(position);

			int width = WinTool.getWinWidth(context) / 3;
			img.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
			DownloadImageLoader.loadImageForFile(img, path);

			if (ChoiseImageFileList.isChoise(path)) {
				checkBox.setChecked(true);
			} else {
				checkBox.setChecked(false);
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (checkBox.isChecked()) {
						checkBox.setChecked(false);
						ChoiseImageFileList.remove(path);
					} else {
						if (ChoiseImageFileList.getChoiseSiza() < MAX_SIZE) {
							checkBox.setChecked(true);
							ChoiseImageFileList.add(path);
							Log.i("img", "img choise ->" + path);
						} else {
							showMessage();
						}
					}
					setConfirmSize();
				}

			});

			return convertView;
		}
	}

}
