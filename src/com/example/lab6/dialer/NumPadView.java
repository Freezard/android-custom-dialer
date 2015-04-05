package com.example.lab6.dialer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;

import com.example.lab6.R;

/**
 * NumPadView.java
 * 
 * Custom component consisting of 10 number pads as well as a star and a pound
 * pad.
 */
public class NumPadView extends TableLayout {

	public NumPadView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.num_pad_view, this, true);

	}

}