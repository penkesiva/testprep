package com.ee.testprep.db;

public class MetaData {

    //Column names
    public static final String KEY_NAME = "name";
    public static final String KEY_EXAM = "exam";
    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_TOTALQ = "totalQ";
    public static final String KEY_TIME = "time";

    public String mName;
    public String mExam;
    public String mSubject;
    public String mLanguage;
    public String mTotalQ;
    public String mTime;

    public MetaData() {

    }

    @Override
    public String toString() {
        return mName + " " +  mExam + " " + mSubject + " " + mLanguage + " " + mTotalQ + " " + mTime;
    }
}
