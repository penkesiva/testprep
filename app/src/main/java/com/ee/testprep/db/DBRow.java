package com.ee.testprep.db;

import java.io.Serializable;

public class DBRow implements Serializable {
    //Column names
    public static final String KEY_EXAM = "examName";
    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_YEAR = "year";
    public static final String KEY_QNO = "qno";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_OPTA = "optionA";
    public static final String KEY_OPTB = "optionB";
    public static final String KEY_OPTC = "optionC";
    public static final String KEY_OPTD = "optionD";
    public static final String KEY_ANSWER = "answer";
    public static final String KEY_DIFFICULTY = "difficulty";
    public static final String KEY_USER_STATUS = "userstatus";

    public String exam;
    public String subject;
    public String year;
    public Integer qNo;
    public String question;
    public String optionA;
    public String optionB;
    public String optionC;
    public String optionD;
    public String answer;
    public Integer difficulty;

    /*
        KEY_USER_STATUS == 'Z' => user not seen the question (default value)
        KEY_USER_STATUS == KEY_ANSWER => correct answer
        KEY_USER_STATUS != KEY_ANSWER => incorrect answer
     */
    public String userstatus = "Z"; // default value

    @Override
    public String toString() {
        return exam + " " +  year + " " + subject + " " + userstatus;
    }
}
