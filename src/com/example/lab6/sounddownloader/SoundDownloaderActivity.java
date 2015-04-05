package com.example.lab6.sounddownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.lab6.R;
import com.example.lab6.utils.Utils;
import com.example.lab6.utils.ZIP;

/**
 * SoundDownloaderActivity.java
 * 
 * Starts a WebView in the app that shows a download page for voice categories.
 * If user clicks any link, the archive file is downloaded and extracted to the
 * external storage in a background thread.
 */
public class SoundDownloaderActivity extends Activity implements
		OnSharedPreferenceChangeListener {
	// Default locations
	public static final String DEFAULT_DOWNLOAD_LOCATION = "http://dt031g.programvaruteknik.nu/dialpad/sounds/";
	public static final String DEFAULT_STORAGE_LOCATION = Environment.getExternalStorageDirectory()
			+ "/dialpad/sounds/";

	private WebView webView;
	private ProgressDialog progressDialog;
	private String downloadLocation;
	private String storageLocation;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sound_downloader_activity);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		sharedPref.registerOnSharedPreferenceChangeListener(this);

		// Get the URL to the download and storage locations from preferences
		downloadLocation = sharedPref.getString("pref_sound_download_location",	"");
		storageLocation = sharedPref.getString("pref_sound_storage_location", "");

		// If the user has entered a blank field, use default locations
		if (downloadLocation.isEmpty())
			downloadLocation = DEFAULT_DOWNLOAD_LOCATION;
		if (storageLocation.isEmpty())
			storageLocation = DEFAULT_STORAGE_LOCATION;

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Downloading");
		progressDialog.setIndeterminate(false);
		progressDialog.setMax(100);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(downloadLocation);
	}

	// Called when a user setting has changed
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPref,
			String key) {		
		if (key.equals("pref_sound_download_location"))
			// Save the new download location
			downloadLocation = sharedPref.getString(key, "");
		else if (key.equals("pref_sound_storage_location"))
			// Save the new storage location
			storageLocation = sharedPref.getString(key, "");
	}

	private class MyWebViewClient extends WebViewClient implements
			DownloadListener {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.setDownloadListener(this);

			return false;
		}

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {

			// Download and decompress the archive file in a background thread
			if (Utils.isExternalStorageWritable())
				new DownloadArchiveTask().execute(url);
		}

		// Background thread for downloading and decompressing archive files
		private class DownloadArchiveTask extends
				AsyncTask<String, Integer, String> {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog.show();
			}

			@Override
			protected String doInBackground(String... urls) {
				// Get the file name of the archive
				String fileName = urls[0].substring(
						urls[0].lastIndexOf('/') + 1, urls[0].length());
				File downloadedFile = null;

				try {
					URL url = new URL(urls[0]);

					URLConnection conn = url.openConnection();
					InputStream in = conn.getInputStream();
					int fileLength = conn.getContentLength();

					// Make sure the directories exist
					Utils.makeDirs(storageLocation);

					// Download the file to the external cache dir
					downloadedFile = new File(getExternalCacheDir(), fileName);
					FileOutputStream out = new FileOutputStream(downloadedFile);

					byte[] data = new byte[1024];
					long total = 0;
					int count;
					while ((count = in.read(data)) > 0) {
						total += count;
						// Update the progress bar
						publishProgress((int) (total * 100 / fileLength));
						// Write bytes to the file
						out.write(data, 0, count);
					}
					out.flush();
					out.close();
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Pass the location of the downloaded file to onPostExecute()
				return downloadedFile.getAbsolutePath();
			}

			@Override
			protected void onProgressUpdate(Integer... progress) {
				super.onProgressUpdate(progress);
				progressDialog.setProgress(progress[0]);
			}

			protected void onPostExecute(String archiveLocation) {
				progressDialog.dismiss();

				// Decompress the downloaded archive file
				boolean decompressed = ZIP.decompress(archiveLocation, storageLocation);
				if (!decompressed)
					Toast.makeText(getApplicationContext(), R.string.decompression_failed, Toast.LENGTH_SHORT).show();
			}
		}
	}
}