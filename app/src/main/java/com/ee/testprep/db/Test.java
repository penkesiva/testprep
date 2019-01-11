package com.ee.testprep.db;

import android.content.Context;

import java.util.List;

import com.ee.testprep.R;

public class Test {
    public String name;
    public int maxQuestions;
    public int answeredQuestions;
    public int correctAnswers;
    public int wrongAnswers;

    public Test(String name, int maxQuestions, int answeredQuestions, int correctAnswers,
            int wrongAnswers) {
        this.name = name;
        this.maxQuestions = maxQuestions;
        this.answeredQuestions = answeredQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    public enum TestType {
        ALL, QBANK, QUIZ, MODELTEST;

        public String getTitle(Context context) {
            int id = R.string.all_tests;
            if (this == QBANK) {
                id = R.string.practice;
            } else if (this == QUIZ) {
                id = R.string.quiz;
            } else if (this == MODELTEST) {
                id = R.string.model_tests;
            }
            return context.getString(id);
        }

        public Test getSummary(Context context) {
            DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
            if (this != ALL) {
                int maxQuestions = 0;
                int answeredQuestions = 0;
                int correctAnswers = 0;
                int wrongAnswers = 0;
                List<Test> testData = dbHelper.getTestsData(context, this);

                for (Test test : testData) {
                    maxQuestions += test.maxQuestions;
                    answeredQuestions += test.answeredQuestions;
                    correctAnswers += test.correctAnswers;
                    wrongAnswers += test.wrongAnswers;
                }

                return new Test(getTitle(context), maxQuestions, answeredQuestions, correctAnswers,
                        wrongAnswers);
            }
            return null;
        }
    }
}
