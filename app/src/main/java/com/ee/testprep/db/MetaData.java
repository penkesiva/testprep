package com.ee.testprep.db;

import android.os.Parcel;
import android.os.Parcelable;

public class MetaData implements Parcelable {

    //Column names
    public static final String KEY_NAME = "name";
    public static final String KEY_EXAM = "exam";
    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_TOTALQ = "totalQ";
    public static final String KEY_TIME = "time";

    public static final Creator<MetaData> CREATOR = new Creator<MetaData>() {
        @Override
        public MetaData createFromParcel(Parcel in) {
            return new MetaData(in);
        }

        @Override
        public MetaData[] newArray(int size) {
            return new MetaData[size];
        }
    };
    public String mName;
    public String mExam;
    public String mSubject;
    public String mLanguage;
    public String mTotalQ;
    public String mTime;

    public MetaData() {
    }

    public MetaData(Parcel parcel) {
        mName = parcel.readString();
        mExam = parcel.readString();
        mSubject = parcel.readString();
        mLanguage = parcel.readString();
        mTotalQ = parcel.readString();
        mTime = parcel.readString();
    }

    @Override
    public String toString() {
        return mName + " " + mExam + " " + mSubject + " " + mLanguage + " " + mTotalQ + " " + mTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mExam);
        dest.writeString(mSubject);
        dest.writeString(mLanguage);
        dest.writeString(mTotalQ);
        dest.writeString(mTime);
    }
}
