package com.example.lab5_3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * DialPadView.java
 * 
 * Custom component consisting of a text field for a number input and two
 * buttons for erasing and calling.
 */
public class DialPadView extends LinearLayout {

	public DialPadView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dial_pad_view, this, true);
	}
}