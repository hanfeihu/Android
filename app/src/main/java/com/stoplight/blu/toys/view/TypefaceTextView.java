package com.stoplight.blu.toys.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.stoplight.blu.toys.R;

public class TypefaceTextView extends androidx.appcompat.widget.AppCompatTextView {
	public TypefaceTextView(Context context) {
		this(context, null);
	}

	public TypefaceTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initTypefaceTextView(context, attrs);
	}

	private void initTypefaceTextView(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTextView);
		String type = typedArray.getString(R.styleable.TypefaceTextView_typeface);
		if (null == type) {
			return;
		}
		Typeface typeface = null;
		switch (type) {
			case "OCRAEXT":
				typeface = Typeface.createFromAsset(context.getAssets(), "fonts/OCRAEXT.TTF");
				setTypeface(typeface);
				break;
			case "LittleOrion":
				typeface = Typeface.createFromAsset(context.getAssets(), "fonts/LittleOrion.otf");
				setTypeface(typeface);
				break;
			case "systemDefault":
				setTypeface(Typeface.DEFAULT);
				break;
		}
		if (typedArray != null) {
			typedArray.recycle();
		}
		typeface = null;
	}
}

