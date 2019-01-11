package com.ee.testprep.font;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoMediumTextView extends android.support.v7.widget.AppCompatTextView {

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
