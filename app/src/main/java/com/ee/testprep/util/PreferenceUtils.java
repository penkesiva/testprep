package com.ee.testprep.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    private Context mContext;
    private static PreferenceUtils prefs;

    private PreferenceUtils(Context context) {
        mContext = context;
    }

    public static PreferenceUtils getInstance(Context context) {
        if (prefs == null) {
            prefs = new PreferenceUtils(context);
        }
        return prefs;
    }

    public void savePrefs(String valueKey, Boolean value) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(valueKey, value);
        edit.commit();
    }

    public Boolean readPrefs(String valueKey, Boolean valueDefault) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return prefs.getBoolean(valueKey, valueDefault);
    }


}
