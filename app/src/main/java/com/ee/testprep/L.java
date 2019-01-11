package com.ee.testprep;

import android.util.Log;

public class L {

    public static String TAG = "TestPrep";

    public static void v(String className, String text) {
        Log.v(TAG, className + ": " + text);
    }
    public static void e(String className, String text) {
        Log.v(TAG, "Error! " + className + ": " + text);
    }
    public static void d(String className, String text) {
        Log.d(TAG, className + ": " + text);
    }

}
