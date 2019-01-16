package com.ee.testprep;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {
    private static final String PRACTICE_Q_COUNT = "PRACTICE_Q_COUNT";
    private static final String QUIZ_COMPLETE = "QUIZ_COMPLETE";
    private static final String QUIZ_INCOMPLETE = "QUIZ_INCOMPLETE";
    private static final String MODEL_TEST_COMPLETE = "MODEL_TEST_COMPLETE";
    private static final String MODEL_TEST_INCOMPLETE = "MODEL_TEST_INCOMPLETE";
    private static Analytics analytics;
    private Context appContext;

    public Analytics(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Analytics getAnalytics(Context context) {
        if (analytics == null) {
            analytics = new Analytics(context);
        }
        return analytics;
    }

    public void logPractice(int questionCount) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(appContext);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT, "Question Count");
        params.putLong(FirebaseAnalytics.Param.VALUE, questionCount);
        firebaseAnalytics.logEvent(PRACTICE_Q_COUNT, params);
    }

    public void logQuizCompleted() {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(appContext);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT, "Quizzes Complete");
        params.putLong(FirebaseAnalytics.Param.VALUE, 1);
        firebaseAnalytics.logEvent(QUIZ_COMPLETE, params);
    }

    public void logQuizIncompleteExit() {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(appContext);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT, "Quizzes Incomplete");
        params.putLong(FirebaseAnalytics.Param.VALUE, 1);
        firebaseAnalytics.logEvent(QUIZ_INCOMPLETE, params);
    }

    public void logModelTestCompleted() {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(appContext);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT, "Model Tests Complete");
        params.putLong(FirebaseAnalytics.Param.VALUE, 1);
        firebaseAnalytics.logEvent(MODEL_TEST_COMPLETE, params);
    }

    public void logModelTestIncompleteExit() {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(appContext);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT, "Model Tests Incomplete");
        params.putLong(FirebaseAnalytics.Param.VALUE, 1);
        firebaseAnalytics.logEvent(MODEL_TEST_INCOMPLETE, params);
    }
}
