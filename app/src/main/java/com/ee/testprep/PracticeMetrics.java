package com.ee.testprep;

import android.content.Context;

import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;

import java.util.ArrayList;

public class PracticeMetrics {
    private final static String TAG = PracticeMetrics.class.getSimpleName();
    private int mNumQuestions;
    private int mCurrIndex;
    private ArrayList<DBRow> mPList;

    public PracticeMetrics(Context context, PracticeType category, String subCategory) {
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
        switch (category) {
            case YEAR:
                mPList = (ArrayList<DBRow>) dbHelper.queryYearExt(subCategory);
                break;
            case SUBJECT:
                mPList = (ArrayList<DBRow>) dbHelper.querySubjectExt(subCategory);
                break;
            case EXAM:
                mPList = (ArrayList<DBRow>) dbHelper.queryQuestionsUserStatus();
                break;
            case EASY:
                mPList = (ArrayList<DBRow>) dbHelper.queryQuestionsDifficulty("0 AND 3");
                break;
            case MEDIUM:
                mPList = (ArrayList<DBRow>) dbHelper.queryQuestionsDifficulty("4 AND 6");
                break;
            case HARD:
                mPList = (ArrayList<DBRow>) dbHelper.queryQuestionsDifficulty("7 AND 9");
                break;
            case RANDOM:
                mPList = (ArrayList<DBRow>) dbHelper.queryQuestionsRandom();
                break;
            case STARRED:
                mPList = (ArrayList<DBRow>) dbHelper.queryQuestionsUserStatus();
                break;
            case ALL:
            default:
                mPList = (ArrayList<DBRow>) dbHelper.queryAllQuestions();
                break;
        }
        mNumQuestions = mPList.size();
        mCurrIndex = 0;
    }

    public DBRow getNextQuestion() {
        DBRow q = null;

        if (mNumQuestions < 1) return q;

        if (mCurrIndex < (mNumQuestions - 1)) {
            ++mCurrIndex;
            q = mPList.get(mCurrIndex);
        }

        return q;
    }

    public DBRow getPrevQuestion() {
        DBRow q = null;
        if (mCurrIndex > 0) {
            q = mPList.get(mCurrIndex - 1);
            mCurrIndex = mCurrIndex - 1;
        }

        return q;
    }

    public int getNumQuestions() {
        return mNumQuestions;
    }

    public enum PracticeType {
        YEAR, SUBJECT, EXAM, EASY, MEDIUM, HARD, RANDOM, STARRED, ALL
    }
}
