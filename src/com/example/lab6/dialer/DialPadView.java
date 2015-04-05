package com.example.lab6.dialer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.lab6.R;

/**
 * DialPadView.java
 * 
 * Custom component consisting of a text field for a number input and two
 * buttons for erasing numbers and for calling the number.
 */
public class DialPadView extends LinearLayout {

	public DialPadView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dial_pad_view, this, true);
	}
}