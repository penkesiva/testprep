package com.ee.testprep.util;

import android.util.Log;

public class Constants {
    public static final int TIME_PER_QUESTION = 30; //30 seconds per question

    /*
     * Get time in readable string format
     */
    public static String getTime(int time) {
        if (time <= 1) return "";
        int p1 = time % 60;
        int p3 = time / 60;
        int p2 = p3 % 60;
        p3 = p3 / 60;
        Log.e("Quiz", "Quiz time: " + (p3 + "h : " + p2 + "m : " + p1 + "s"));

        String timeString = "";

        if (p3 != 0) {
            timeString += p3 + "h";
        }

        if (p2 != 0) {
            if (!timeString.isEmpty()) timeString += " : ";
            timeString += p2 + "m";
        }

        if (p1 != 0) {
            if (!timeString.isEmpty()) timeString += " : ";
            timeString += p1 + "s";
        }
        return timeString;
    }
}
