package com.ee.testprep.util;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;

public class Constants {
    public static final int TIME_PER_QUESTION = 30; //30 seconds per question
    public static HashMap<String, String> abbreviations = new HashMap<>();

    /*
     * Get total quiz time in seconds
     */
    public static int getQuizTime(int numQuestions) {
        return numQuestions * TIME_PER_QUESTION;
    }

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

        if (p2 != 0 || p3 != 0) {
            if (!timeString.isEmpty()) timeString += " : ";
            timeString += p2 + "m";
        }

        if (p1 != 0 || p2 != 0) {
            if (!timeString.isEmpty()) timeString += " : ";
            timeString += p1 + "s";
        }
        return timeString;
    }

    private static void updateAbbreviations() {
        if (abbreviations.size() > 0) return;

        abbreviations.put("GEOG", "Geography");
        abbreviations.put("POLI", "Politics");
        abbreviations.put("ENVI", "Environment");
        abbreviations.put("HIST", "History");
        abbreviations.put("All", "All");
    }

    public static Collection<String> getSubjects() {
        updateAbbreviations();
        return abbreviations.values();
    }

    public static String getAbbreviation(String abbreviation) {
        updateAbbreviations();
        String subject = abbreviations.get(abbreviation);
        if (subject == null) {
            return abbreviation;
        } else {
            return subject;
        }
    }
}
