package com.ee.testprep.db;

import android.content.Context;

import com.ee.testprep.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tests_data")
public class Test {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "test_name")
    public String testName;
    @ColumnInfo(name = "total_count")
    public int totalCount;
    @ColumnInfo(name = "answered_count")
    public int answeredCount;
    @ColumnInfo(name = "correct_count")
    public int correctCount;
    @ColumnInfo(name = "wrong_count")
    public int wrongCount;
    @ColumnInfo(name = "time_used")
    public int timeUsed;

    public Test(String testName, int totalCount, int answeredCount, int correctCount,
            int wrongCount, int timeUsed) {
        this.testName = testName;
        this.totalCount = totalCount;
        this.answeredCount = answeredCount;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.timeUsed = timeUsed;
    }

    @Override
    public String toString() {
        return testName + ": total - " + totalCount + ", answered - " + answeredCount + ", " +
                "correct - " + correctCount + ", wrong - " + wrongCount + ", time used = " + timeUsed;
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
                int total = 0;
                int answered = 0;
                int correct = 0;
                int wrong = 0;
                List<Test> testData = dbHelper.getTestsData(context, this);

                for (Test test : testData) {
                    total += test.totalCount;
                    answered += test.answeredCount;
                    correct += test.correctCount;
                    wrong += test.wrongCount;
                }

                return new Test(getTitle(context), total, answered, correct, wrong, 0);
            }
            return null;
        }
    }
}
