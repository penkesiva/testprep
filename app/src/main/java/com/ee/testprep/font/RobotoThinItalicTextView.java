package com.ee.testprep.font;

import android.content.Context;
import android.util.AttributeSet;

public class RobotoThinItalicTextView extends android.support.v7.widget.AppCompatTextView {

    private Context c;

    public RobotoThinItalicTextView(Context c) {
        super(c);
        this.c = c;
        setTypeface(FontUtil.getRobotoThinItalic(c));

    }

    public RobotoThinItalicTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.c = context;
        setTypeface(FontUtil.getRobotoThinItalic(c));
    }

    public RobotoThinItalicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        setTypeface(FontUtil.getRobotoThinItalic(c));
    }

}
