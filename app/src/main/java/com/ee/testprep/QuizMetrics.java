package com.ee.testprep;

import com.ee.testprep.db.DBRow;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class QuizMetrics {
    private int mNumQuestions;
    private ArrayList<DBRow> mQList;
    private int mTimeAllotedInMS;
    private long mCurrTime;
    private long mEndTime;
    private long mRemainingTime;
    private int mCurrIndex;

    private final static String TAG = QuizMetrics.class.getSimpleName();

    private Timer mQTimer;
    private TimerTask mQTimerTask;

    public QuizStatus mQStatus;

    public enum QuizStatus {
        QSTARTED,
        QRUNNING,
        QSTOPPED
    }

    public QuizMetrics(ArrayList<DBRow> qList, int timeInSec) {
        mQList = qList;//TODO
        mNumQuestions = qList.size();
        mCurrIndex = -1;
        mTimeAllotedInMS = timeInSec * 1000;
        mCurrTime = System.currentTimeMillis();
        mQStatus = QuizStatus.QSTARTED;
    }

    public void updateAllotedTime(int timeInSec) {
        mTimeAllotedInMS = timeInSec * 1000;
    }

    public void startQuiz() {
        startTimer();
        mCurrTime = System.currentTimeMillis();
        mEndTime = mCurrTime + mTimeAllotedInMS;
    }

    public QuizStatus getQStatus() {
        if(mCurrIndex > mNumQuestions) {
            mQStatus = QuizStatus.QSTOPPED;
        }

        return mQStatus;
    }

    public DBRow getNextQuestion() {
        DBRow q = null;
        if(mCurrIndex < mNumQuestions - 1) {
            ++mCurrIndex;
            q = mQList.get(mCurrIndex);
        }

        return q;
    }

    public DBRow getPrevQuestion() {
        DBRow q = null;
        if(mCurrIndex > 0) {
            q = mQList.get(mCurrIndex - 1);
            mCurrIndex = mCurrIndex - 1;
        }

        return q;
    }

    public int getRemainingTimeInSec() {
        return (int)(mRemainingTime / 1000);
    }

    public int getProgress() {
        if(mNumQuestions != 0) {
            return mCurrIndex + 1;
        }

        return 0;
    }

    private void startTimer() {
        mQTimer = new Timer();
        initializeTimerTask();
        //schedule the timer, with a small delay of 1sec, and run mQTimerTask for every sec
        mQTimer.schedule(mQTimerTask, 0, 1000);
    }

    private void stoptimertask() {
        if (mQTimer != null) {
            mQTimer.cancel();
            mQTimer = null;
        }
    }

    private void initializeTimerTask() {

        mQTimerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                MainActivity.mUIHandler.post(new Runnable() {
                    public void run() {
                        mCurrTime = System.currentTimeMillis();
                        mRemainingTime = mEndTime - mCurrTime;

                        if(mRemainingTime > 0) { //TODO
                            mQStatus = QuizStatus.QRUNNING;
                        } else {
                            mQStatus = QuizStatus.QSTOPPED;
                            stoptimertask();
                        }
                    }
                });
            }
        };
    }
}
