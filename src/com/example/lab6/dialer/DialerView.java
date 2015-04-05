package com.example.lab6.dialer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lab6.R;
import com.example.lab6.utils.Utils;

/**
 * DialerView.java
 * 
 * Custom component consisting of DialPadView and NumPadView. Handles the logic
 * of both components. When a numpad is clicked or pressed using a keyboard, a
 * corresponding sound will play and the number/symbol will be appended to the
 * dialpad. The arrowpad erases a number or clears the whole number when
 * performing a long click. The callpad calls the number entered in the dialpad.
 */
public class DialerView extends LinearLayout implements
		OnSharedPreferenceChangeListener {
	private SoundPool soundPool;
	private int soundID1, soundID2, soundID3, soundID4, soundID5, soundID6,
			soundID7, soundID8, soundID9, soundIDStar, soundID0, soundIDPound;
	private boolean soundFilesLoaded;

	private EditText editText;
	private SparseArray<ImageButton> imageButtons;

	public DialerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dialer_view, this, true);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getContext());

		sharedPref.registerOnSharedPreferenceChangeListener(this);

		// Get the path to the selected sound files
		String soundFilesLocation = sharedPref.getString("pref_sound_files", "");

		// Initialize components
		initComponents();
		
		// Register an onClickListener to all pads
		registerOnClickListener();
		
		// Initialize SoundPool and load the sound files
		initSound();
		loadSoundFiles(soundFilesLocation);
	}
	
	// ************* EVENT HANDLING *************
	// ******************************************
	
	// OnClickListener for all pads. Plays a corresponding sound file.
	// Swapping images is done in XML. The arrow pad erases one letter
	// and the call pad calls the number entered in the dialpad.
	final OnClickListener onClickListener = new OnClickListener() {
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.imageButton_dialpad_1:
				playSound(soundID1);
				appendText("1");
				break;
			case R.id.imageButton_dialpad_2:
				playSound(soundID2);
				appendText("2");
				break;
			case R.id.imageButton_dialpad_3:
				playSound(soundID3);
				appendText("3");
				break;
			case R.id.imageButton_dialpad_4:
				playSound(soundID4);
				appendText("4");
				break;
			case R.id.imageButton_dialpad_5:
				playSound(soundID5);
				appendText("5");
				break;
			case R.id.imageButton_dialpad_6:
				playSound(soundID6);
				appendText("6");
				break;
			case R.id.imageButton_dialpad_7:
				playSound(soundID7);
				appendText("7");
				break;
			case R.id.imageButton_dialpad_8:
				playSound(soundID8);
				appendText("8");
				break;
			case R.id.imageButton_dialpad_9:
				playSound(soundID9);
				appendText("9");
				break;
			case R.id.imageButton_dialpad_star:
				playSound(soundIDStar);
				appendText("*");
				break;
			case R.id.imageButton_dialpad_0:
				playSound(soundID0);
				appendText("0");
				break;
			case R.id.imageButton_dialpad_pound:
				playSound(soundIDPound);
				appendText("#");
				break;
			case R.id.imageButton_dialpad_arrow:
				eraseLetter();
				break;
			case R.id.imageButton_dialpad_call:
				call();
				break;
			}
		}
	};

	// OnLongClickListener for the arrow pad. Clears the number.
	final OnLongClickListener onLongClickListener = new OnLongClickListener() {
		public boolean onLongClick(final View v) {
			switch (v.getId()) {
			case R.id.imageButton_dialpad_arrow:
				clearText();
				break;
			}
			return true;
		}
	};

	// Registers keyboard presses so that the keyboard can simulate clicks
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		ImageButton dialpad = null;

		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_1:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_1);
			break;
		case KeyEvent.KEYCODE_2:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_2);
			break;
		case KeyEvent.KEYCODE_3:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_3);
			break;
		case KeyEvent.KEYCODE_4:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_4);
			break;
		case KeyEvent.KEYCODE_5:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_5);
			break;
		case KeyEvent.KEYCODE_6:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_6);
			break;
		case KeyEvent.KEYCODE_7:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_7);
			break;
		case KeyEvent.KEYCODE_8:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_8);
			break;
		case KeyEvent.KEYCODE_9:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_9);
			break;
		case KeyEvent.KEYCODE_STAR:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_star);
			break;
		case KeyEvent.KEYCODE_0:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_0);
			break;
		case KeyEvent.KEYCODE_POUND:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_pound);
			break;
		case KeyEvent.KEYCODE_DEL:
			dialpad = imageButtons.get(R.id.imageButton_dialpad_arrow);
			break;
		default:
			return super.dispatchKeyEvent(event);
		}

		if (event.getAction() == KeyEvent.ACTION_DOWN)
			dialpad.setPressed(true);
		else if (event.getAction() == KeyEvent.ACTION_UP) {
			dialpad.performClick();
			dialpad.setPressed(false);
			dialpad.requestFocus();
		}

		return true;
	}
	
	// Called when a user setting has changed
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPref,
			String key) {		
		if (key.equals("pref_sound_files")) {
			// Load the new set of selected sound files
			String soundFilesLocation = sharedPref.getString(key, "");
			loadSoundFiles(soundFilesLocation);
		}
	}
	
	// **************** DIAL PAD ****************
	// ******************************************
	
	// Call the phone number entered in the dialpad
	private void call() {
		String number = editText.getText().toString();
		// The system uses special symbols for the # symbol
		number = number.replace("#", Uri.encode("#"));

		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));
		getContext().startActivity(intent);
	}
	
	// Appends text to the dialpad
	private void appendText(String str) {
		editText.append(str);
	}

	// Erases the last letter in the dialpad
	private void eraseLetter() {
		Editable text = editText.getText();
		int length = text.length();

		if (length > 0)
			text.delete(length - 1, length);
	}

	// Clears the dialpad
	private void clearText() {
		editText.getText().clear();
	}

	// ****************** SOUND *****************
	// ******************************************
	
	// Loads the sound files from the specified location
	private void loadSoundFiles(String soundLocation) {
		// Load the sound files from the external sound directory
		if (Utils.isExternalStorageReadable()
				&& Utils.isDirectory(soundLocation)) {
			soundID1 = soundPool.load(soundLocation + "one.mp3", 1);
			soundID2 = soundPool.load(soundLocation + "two.mp3", 1);
			soundID3 = soundPool.load(soundLocation + "three.mp3", 1);
			soundID4 = soundPool.load(soundLocation + "four.mp3", 1);
			soundID5 = soundPool.load(soundLocation + "five.mp3", 1);
			soundID6 = soundPool.load(soundLocation + "six.mp3", 1);
			soundID7 = soundPool.load(soundLocation + "seven.mp3", 1);
			soundID8 = soundPool.load(soundLocation + "eight.mp3", 1);
			soundID9 = soundPool.load(soundLocation + "nine.mp3", 1);
			soundIDStar = soundPool.load(soundLocation + "star.mp3", 1);
			soundID0 = soundPool.load(soundLocation + "zero.mp3", 1);
			soundIDPound = soundPool.load(soundLocation + "pound.mp3", 1);
		} else {
			// Disable sound
			soundID1 = 0;
			soundID2 = 0;
			soundID3 = 0;
			soundID4 = 0;
			soundID5 = 0;
			soundID6 = 0;
			soundID7 = 0;
			soundID8 = 0;
			soundID9 = 0;
			soundIDStar = 0;
			soundID0 = 0;
			soundIDPound = 0;
			
			Toast.makeText(getContext(), R.string.sound_files_unavailable,
					Toast.LENGTH_SHORT).show();
		}
	}

	// Plays a sound file
	private void playSound(int soundID) {
		AudioManager audioManager = (AudioManager) getContext()
				.getSystemService(Context.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		// Make sure the sound clip is loaded
		if (soundFilesLoaded)
			soundPool.play(soundID, volume, volume, 1, 0, 1f);
	}

	// ****************** INIT ******************
	// ******************************************
	
	// Initializes components
	private void initComponents() {
		editText = (EditText) findViewById(R.id.editText_dialpad);

		imageButtons = new SparseArray<ImageButton>();
		imageButtons.put(R.id.imageButton_dialpad_0,
				(ImageButton) findViewById(R.id.imageButton_dialpad_0));
		imageButtons.put(R.id.imageButton_dialpad_1,
				(ImageButton) findViewById(R.id.imageButton_dialpad_1));
		imageButtons.put(R.id.imageButton_dialpad_2,
				(ImageButton) findViewById(R.id.imageButton_dialpad_2));
		imageButtons.put(R.id.imageButton_dialpad_3,
				(ImageButton) findViewById(R.id.imageButton_dialpad_3));
		imageButtons.put(R.id.imageButton_dialpad_4,
				(ImageButton) findViewById(R.id.imageButton_dialpad_4));
		imageButtons.put(R.id.imageButton_dialpad_5,
				(ImageButton) findViewById(R.id.imageButton_dialpad_5));
		imageButtons.put(R.id.imageButton_dialpad_6,
				(ImageButton) findViewById(R.id.imageButton_dialpad_6));
		imageButtons.put(R.id.imageButton_dialpad_7,
				(ImageButton) findViewById(R.id.imageButton_dialpad_7));
		imageButtons.put(R.id.imageButton_dialpad_8,
				(ImageButton) findViewById(R.id.imageButton_dialpad_8));
		imageButtons.put(R.id.imageButton_dialpad_9,
				(ImageButton) findViewById(R.id.imageButton_dialpad_9));
		imageButtons.put(R.id.imageButton_dialpad_star,
				(ImageButton) findViewById(R.id.imageButton_dialpad_star));
		imageButtons.put(R.id.imageButton_dialpad_pound,
				(ImageButton) findViewById(R.id.imageButton_dialpad_pound));
		imageButtons.put(R.id.imageButton_dialpad_arrow,
				(ImageButton) findViewById(R.id.imageButton_dialpad_arrow));
		imageButtons.put(R.id.imageButton_dialpad_call,
				(ImageButton) findViewById(R.id.imageButton_dialpad_call));
	}
	
	// Registers an onClickListener to all pads
	private void registerOnClickListener() {
		findViewById(R.id.imageButton_dialpad_1).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_2).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_3).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_4).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_5).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_6).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_7).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_8).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_9).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_star).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_0).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_pound).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_pound).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_arrow).setOnClickListener(
				onClickListener);
		findViewById(R.id.imageButton_dialpad_call).setOnClickListener(
				onClickListener);

		findViewById(R.id.imageButton_dialpad_arrow).setOnLongClickListener(
				onLongClickListener);

		editText = (EditText) findViewById(R.id.editText_dialpad);
	}
	
	// Sets up SoundPool
	private void initSound() {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		// Register onLoadCompleteListener to SoundPool
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				soundFilesLoaded = true;
			}
		});
	}
}