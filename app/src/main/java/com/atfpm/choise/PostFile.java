package com.atfpm.choise;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import com.atfpm.interfaces.PostFileCallback;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PostFile {

	private static PostFile postFile = new PostFile();

	private final static String LINEND = "\r\n";
	private final static String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
	private final static String PREFIX = "--";
	private final static String MUTIPART_FORMDATA = "multipart/form-data";
	private final static String CHARSET = "utf-8";
	private final static String CONTENTTYPE = "application/octet-stream";

	private PostFileCallback callback = null;

	private PostFile() {
	}

	public static PostFile getInstance() {
		return postFile;
	}

	public void post(String actionUrl, HttpFlieBox box,
			PostFileCallback callback) {
		setCallback(callback);
		post(actionUrl, box);
	}

	private void post(final String actionUrl, final HttpFlieBox box) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpURLConnection urlConn = getURLConnection(actionUrl, box);

					DataOutputStream dos = new DataOutputStream(urlConn
							.getOutputStream());
					// 构建表单数据
					String entryText = bulidFormText(box.getParamMap());
					Log.i("-------描述信息---------------", entryText);
					dos.write(entryText.getBytes());
					// ******************
					for (File file : box.getFileList()) {
						StringBuffer sb = new StringBuffer("");
						sb.append(PREFIX).append(BOUNDARY).append(LINEND);
						sb.append("Content-Disposition: form-data; name=\""
								+ box.getFileKey() + "\"; filename=\""
								+ file.getName() + "\"" + LINEND);
						sb.append("Content-Type:" + CONTENTTYPE + ";charset="
								+ CHARSET + LINEND);
						sb.append(LINEND);
						dos.write(sb.toString().getBytes());

						InputStream is = new FileInputStream(file);
						byte[] buffer = new byte[1024 * 1024];
						int len = 0;
						while ((len = is.read(buffer)) != -1) {
							dos.write(buffer, 0, len);
						}
						is.close();
						dos.write(LINEND.getBytes());
					}
					// 请求的结束标志
					byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND)
							.getBytes();
					dos.write(end_data);
					dos.flush();

					int code = urlConn.getResponseCode();
					if (code != 200) {
						urlConn.disconnect();
						sean(new Exception("404"));
					} else {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(urlConn.getInputStream()));
						String result = "";
						String line = null;
						while ((line = br.readLine()) != null) {
							result += line;
						}
						br.close();
						urlConn.disconnect();
						sean(result);
					}

				} catch (Exception e) {
					e.printStackTrace();
					sean(e);
				}
			}

		}).start();
	}

	private HttpURLConnection getURLConnection(String actionUrl, HttpFlieBox box)
			throws Exception {
		URL url = new URL(actionUrl);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setDoOutput(true); // 允许输出
		urlConn.setDoInput(true); // 允许输入
		urlConn.setUseCaches(false);
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty("connection", "Keep-Alive");
		urlConn.setRequestProperty("Charset", CHARSET);
		urlConn.setRequestProperty("Content-Type", MUTIPART_FORMDATA
				+ ";boundary=" + BOUNDARY);
		urlConn.setConnectTimeout(10 * 60 * 1000);
		urlConn.setReadTimeout(10 * 60 * 1000);
		// *****************************************
		Map<String, String> haedMap = box.getHeadMap();
		for (Entry<String, String> entry : haedMap.entrySet()) {
			urlConn.setRequestProperty(entry.getKey(), entry.getValue());
		}
		return urlConn;
	}

	private void setCallback(PostFileCallback callback) {
		this.callback = callback;
	}

	private void sean(String result) {
		Message.obtain(handler, 0, result).sendToTarget();
	}

	private void sean(Exception e) {
		Message.obtain(handler, 1, e).sendToTarget();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (callback != null) {
				switch (msg.what) {
				case 0:
					callback.callback((String) msg.obj);
					break;
				case 1:
					callback.onFailure((Exception) msg.obj);
					break;
				}
			}
		}
	};

	/**
	 * 封装表单文本数据
	 * 
	 * @param paramText
	 * @return
	 */
	private String bulidFormText(Map<String, String> paramText) {
		if (paramText == null || paramText.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer("");
		for (Entry<String, String> entry : paramText.entrySet()) {
			sb.append(PREFIX).append(BOUNDARY).append(LINEND);
			sb.append("Content-Disposition:form-data;name=\"" + entry.getKey()
					+ "\"" + LINEND);
			// sb.append("Content-Type:text/plain;charset=" + CHARSET + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}
		return sb.toString();
	}

	/**
	 * 封装文件文本数据
	 * 
	 * @param files
	 * @return
	 */
	private String buildFromFile(FileInfo[] files) {
		StringBuffer sb = new StringBuffer();
		for (FileInfo file : files) {
			sb.append(PREFIX).append(BOUNDARY).append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ file.getFileTextName() + "\"; filename=\""
					+ file.getFile().getAbsolutePath() + "\"" + LINEND);
			sb.append("Content-Type:" + CONTENTTYPE + ";charset=" + CHARSET
					+ LINEND);
			sb.append(LINEND);
		}
		return sb.toString();
	}

}
