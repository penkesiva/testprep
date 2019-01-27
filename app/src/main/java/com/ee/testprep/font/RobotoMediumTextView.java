package com.ee.testprep.font;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class RobotoMediumTextView extends AppCompatTextView {

    private Context c;

    public RobotoMediumTextView(Context c) {
        super(c);
        this.c = c;
        setTypeface(FontUtil.getRobotoMedium(c));

    }

    public RobotoMediumTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.c = context;
        setTypeface(FontUtil.getRobotoMedium(c));
    }

    public RobotoMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        setTypeface(FontUtil.getRobotoMedium(c));
    }

}
