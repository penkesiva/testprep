package com.ee.testprep.font;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtil {

    private FontUtil instance;

    private static Typeface robotoRegular;
    private static Typeface robotoMedium;
    private static Typeface robotoBold;
    private static Typeface robotoBoldItalic;

    private static Typeface robotoItalic;
    private static Typeface robotoThinItalic;

    private static Typeface segoePrint;

    private FontUtil() {

    }

    public FontUtil getInstance() {
        if (instance == null) {
            instance = new FontUtil();
        }
        return instance;
    }
	
	/*Typeface tf = Typeface.createFromAsset(getAssets(),
            "fonts/BPreplay.otf");*/

    public static Typeface getRobotoRegular(Context context) {
        if (robotoRegular == null) {
            robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
        }
        return robotoRegular;
    }

    public static Typeface getRobotoMedium(Context context) {
        if (robotoMedium == null) {
            robotoMedium = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_medium.ttf");
        }
        return robotoMedium;
    }

    public static Typeface getRobotoBold(Context context) {
        if (robotoBold == null) {
            robotoBold = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf");
        }
        return robotoBold;
    }

    public static Typeface getRobotoItalic(Context context) {
        if (robotoItalic == null) {
            robotoItalic = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_italic.ttf");
        }
        return robotoItalic;
    }

    public static Typeface getRobotoBoldItalic(Context context) {
        if (robotoBoldItalic == null) {
            robotoBoldItalic = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold_italic.ttf");
        }
        return robotoBoldItalic;
    }

    public static Typeface getRobotoThinItalic(Context context) {
        if (robotoThinItalic == null) {
            robotoThinItalic = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin_italic.ttf");
        }
        return robotoThinItalic;
    }

    public static Typeface getSegoePrint(Context context) {
        if (segoePrint == null) {
            segoePrint = Typeface.createFromAsset(context.getAssets(), "fonts/segoe_print.ttf");
        }
        return segoePrint;
    }

}
