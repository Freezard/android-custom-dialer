package com.example.lab6;

import com.example.lab6.settings.UserSettingsActivity;
import com.example.lab6.sounddownloader.SoundDownloaderActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MainActivity.java
 * 
 * A simple activity using the custom component DialerView.
 * 
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;

		switch (item.getItemId()) {
		case R.id.action_settings:
			i = new Intent(this, UserSettingsActivity.class);
			startActivity(i);
			return true;
		case R.id.action_download:
			i = new Intent(this, SoundDownloaderActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}