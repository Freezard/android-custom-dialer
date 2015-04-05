package com.example.lab6.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.example.lab6.R;
import com.example.lab6.sounddownloader.SoundDownloaderActivity;
import com.example.lab6.utils.Utils;

/**
 * UserSettingsActivity.java
 * 
 * Uses preferences.xml for user settings. Settings for choice of voice files,
 * download and storage location of sound files, and deletion of sound files.
 */
public class UserSettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		sharedPref.registerOnSharedPreferenceChangeListener(this);

		// Make sure that the sound files lists are updated whenever
		// this activity is called
		updateSoundFilesLists();
	}

	// Called when a user setting has changed
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPref,
			String key) {
		if (key.equals("pref_delete_sound_files")) {
			// Get all the selected sound files in the delete sound files list
			Set<String> soundFiles = sharedPref.getStringSet(key, new HashSet<String>());
			
			ListPreference soundFilesList = (ListPreference) getPreferenceScreen()
					.findPreference("pref_sound_files");
			MultiSelectListPreference deleteSoundFilesList = (MultiSelectListPreference) getPreferenceScreen()
					.findPreference("pref_delete_sound_files");

			// Delete all the selected sound files directories
			boolean changeSelectedSoundFile = false;
			for (String s : soundFiles) {
				Utils.deleteDir(new File(s));

				// If the selected sound file was deleted
				if (!changeSelectedSoundFile && soundFilesList.getValue().equals(s))
					changeSelectedSoundFile = true;
			}
			
			// Update the lists with the new sound files
			updateSoundFilesLists();

			// Unmark sound files in the deleted sound files list 
			deleteSoundFilesList.setValues(new HashSet<String>());
			
			// Select the first sound file in the list if the current selected
			// sound file was deleted and there is at least one sound file available
			if (changeSelectedSoundFile)
				if (soundFilesList.getEntries().length != 0)				
					soundFilesList.setValueIndex(0);
				else soundFilesList.setValue("");
		}
	}

	// Updates the sound files lists with currently installed sound files
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private void updateSoundFilesLists() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		String storageLocation = sharedPref.getString(
				"pref_sound_storage_location", "");

		// If the user has entered a blank field, use default location
		if (storageLocation.isEmpty())
			storageLocation = SoundDownloaderActivity.DEFAULT_STORAGE_LOCATION;

		if (Utils.isExternalStorageReadable()
				&& Utils.isDirectory(storageLocation)) {
			ListPreference soundFilesList = (ListPreference) getPreferenceScreen()
					.findPreference("pref_sound_files");
			MultiSelectListPreference deleteSoundFilesList = (MultiSelectListPreference) getPreferenceScreen()
					.findPreference("pref_delete_sound_files");
			
			// Scan sound files location for directories and add them to the lists
			// Entry = directory name. EntryValue = directory location.
			List<String> entries = new ArrayList<String>();
			List<String> entryValues = new ArrayList<String>();
			
			File[] soundFilesDirs = new File(storageLocation).listFiles();

			for (File file : soundFilesDirs)
				if (file.isDirectory()) {
					entries.add(file.getName());
					entryValues.add(storageLocation + file.getName() + "/");
				}

			String[] arrayEntries = entries.toArray(new String[entries.size()]);
			String[] arrayEntryValues = entryValues.toArray(new String[entryValues.size()]);

			soundFilesList.setEntries(arrayEntries);
			soundFilesList.setEntryValues(arrayEntryValues);

			deleteSoundFilesList.setEntries(arrayEntries);
			deleteSoundFilesList.setEntryValues(arrayEntryValues);
		}
	}
}