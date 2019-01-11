package com.ee.testprep;

import java.util.ArrayList;

import com.ee.testprep.db.DBRow;

public class PracticeMetrics {
    private int mNumQuestions;
    private ArrayList<DBRow> mPList;
    private int mCurrIndex;
    private final static String TAG = PracticeMetrics.class.getSimpleName();

    PracticeMetrics(ArrayList<DBRow> qList) {
        mPList = qList;
        mNumQuestions = qList.size();
        mCurrIndex = 0;
    }

    public DBRow getNextQuestion() {
        DBRow q = null;

        if(mNumQuestions < 1) return q;

        if(mCurrIndex < (mNumQuestions- 1)) {
            ++mCurrIndex;
            q = mPList.get(mCurrIndex);
        }

        return q;
    }

    public DBRow getPrevQuestion() {
        DBRow q = null;
        if(mCurrIndex > 0) {
            q = mPList.get(mCurrIndex - 1);
            mCurrIndex = mCurrIndex - 1;
        }

        return q;
    }

}
