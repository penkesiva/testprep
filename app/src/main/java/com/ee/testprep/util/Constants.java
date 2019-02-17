package com.ee.testprep.util;

import android.util.Log;

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

    private static void updateAbbreviations() {
        if (abbreviations.size() > 0) return;

        abbreviations.put("IYBA", "BioTechnology - IYBA");
        abbreviations.put("ECON", "Economics - ECON");
        abbreviations.put("INTA", "TBD - INTA");
        abbreviations.put("GEOG", "Geography - GEOG");
        abbreviations.put("POLI", "Politics - POLI");
        abbreviations.put("ENVI", "Environment - ENVI");
        abbreviations.put("HIST", "History - HIST");
        abbreviations.put("CURR", "Current Affairs - CURR");
        abbreviations.put("SNTC", "TBD - SNTC");
        abbreviations.put("CSP", "TBD - CSP");
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
