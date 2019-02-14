package com.ee.testprep.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    public static void savePrefs(Context context, String valueKey, Boolean value) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(valueKey, value);
        edit.apply();
    }

    public static Boolean readPrefs(Context context, String valueKey, Boolean valueDefault) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getBoolean(valueKey, valueDefault);
    }

    public static void savePrefs(Context context, String valueKey, String value) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(valueKey, value);
        edit.apply();
    }

    public static String readPrefs(Context context, String valueKey, String valueDefault) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getString(valueKey, valueDefault);
    }

}
