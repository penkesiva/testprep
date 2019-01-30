package com.ee.testprep.font;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class RobotoRegularTextView extends AppCompatTextView {

    private Context c;

    public RobotoRegularTextView(Context c) {
        super(c);
        this.c = c;
        setTypeface(FontUtil.getRobotoRegular(c));

    }

    public RobotoRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.c = context;
        setTypeface(FontUtil.getRobotoRegular(c));
    }

    public RobotoRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        setTypeface(FontUtil.getRobotoRegular(c));
    }

}
