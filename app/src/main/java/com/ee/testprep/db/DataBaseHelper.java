package com.ee.testprep.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.aspose.cells.Cell;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.Row;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ee.testprep.R;
import com.ee.testprep.db.Test.TestType;

public class DataBaseHelper extends SQLiteOpenHelper implements Serializable {

    private static final String className = DataBaseHelper.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eeTestPrep2.db";

    // Table Names
    public static final String TABLE_USERDATA = "userdata";
    public static final String TABLE_QBANK = "qbank";
    public static final String TABLE_QUIZ = "quiz";
    public static final String TABLE_MODELTEST = "modeltest";

    private static final int MAX_QUESTIONS = 500;
    private Workbook workbook;
    private Context mContext;
    private static DataBaseHelper dbHelperInstance = null;
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_QBANK;
    private boolean dataBaseInitDone = false;

    ArrayList<String> tableList = new ArrayList<>();

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static DataBaseHelper getInstance(Context context) {
        if (dbHelperInstance == null) {
            dbHelperInstance = new DataBaseHelper(context);
        }
        return dbHelperInstance;
    }

    public static DataBaseHelper getInstance() {
        return dbHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        new Thread(() -> {

            updateTableList(db, R.raw.qbank_v1);
            updateTableList(db, R.raw.quiz_v1);
            updateTableList(db, R.raw.modeltest_v1);

            for (int i = 0; i < tableList.size(); i++) {
                if (tableList.get(i).contains(TABLE_QBANK)) {
                    convertXLStoSQL(R.raw.qbank_v1, tableList.get(i));
                } else if (tableList.get(i).contains(TABLE_QUIZ)) {
                    convertXLStoSQL(R.raw.quiz_v1, tableList.get(i));
                } else if (tableList.get(i).contains(TABLE_MODELTEST)) {
                    convertXLStoSQL(R.raw.modeltest_v1, tableList.get(i));
                }
            }

            //update init is done
            dataBaseInitDone = true;

        }).start();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL(SQL_DELETE_ENTRIES);

        // create new tables
        onCreate(db);
    }

    public boolean isDataBaseReady() {
        return dataBaseInitDone;
    }

    private String createMetaTable(String tableName) {
        return "CREATE TABLE "
                + tableName + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, "
                + MetaData.KEY_NAME + " TEXT, "
                + MetaData.KEY_EXAM + " TEXT, "
                + MetaData.KEY_SUBJECT + " TEXT, "
                + MetaData.KEY_LANGUAGE + " TEXT, "
                + MetaData.KEY_TOTALQ + " TEXT, "
                + MetaData.KEY_TIME + " TEXT)";
    }

    private String createTable(String tableName) {
        return "CREATE TABLE "
                + tableName + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, "
                + DBRow.KEY_EXAM + " TEXT, "
                + DBRow.KEY_YEAR + " TEXT, "
                + DBRow.KEY_QNO + " INTEGER, "
                + DBRow.KEY_QUESTION + " TEXT, "
                + DBRow.KEY_OPTA + " TEXT, "
                + DBRow.KEY_OPTB + " TEXT, "
                + DBRow.KEY_OPTC + " TEXT, "
                + DBRow.KEY_OPTD + " TEXT, "
                + DBRow.KEY_ANSWER + " TEXT, "
                + DBRow.KEY_IPC + " TEXT, "
                + DBRow.KEY_SUBJECT + " TEXT, "
                + DBRow.KEY_CHAPTER + " INTEGER, "
                + DBRow.KEY_DIFFICULTY + " INTEGER, "
                + DBRow.KEY_USER_STATUS + " TEXT)";
    }

    public void dummyDBCall() {
        this.getReadableDatabase();
    }

    private boolean isTableExists(SQLiteDatabase db, String tableName) {

        Cursor cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'",
                null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    private void updateTableList(SQLiteDatabase db, final int resourceId) {

        try {
            InputStream fstream = mContext.getResources().openRawResource(resourceId);
            LoadOptions loadOptions = new LoadOptions(FileFormatType.XLSX);
            workbook = new Workbook(fstream, loadOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (workbook != null) {
            int worksheetCount = workbook.getWorksheets().getCount();

            for (int i = 0; i < worksheetCount; i++) {
                Worksheet worksheet = workbook.getWorksheets().get(i);
                String sheetName = worksheet.getName();
                tableList.add(sheetName);//sheetName is the table name

                if (sheetName.contains("metadata")) {
                    db.execSQL(createMetaTable(sheetName));
                } else {
                    db.execSQL(createTable(sheetName));
                }
            }
        }
    }

    private void convertXLStoSQL(final int resourceId, final String tableName) {

        try {
            InputStream fstream = mContext.getResources().openRawResource(resourceId);
            LoadOptions loadOptions = new LoadOptions(FileFormatType.XLSX);
            workbook = new Workbook(fstream, loadOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (workbook != null) {
            Worksheet worksheet = workbook.getWorksheets().get(tableName);
            Iterator<Row> rowIterator = worksheet.getCells().getRows().iterator();
            rowIterator.hasNext();//skip header

            //metadata
            if (tableName.contains("metadata")) {
                while (rowIterator.hasNext()) {
                    int colIndex = 0;
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.iterator();
                    MetaData metaData = new MetaData();

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (colIndex) {
                            case 0:
                                metaData.mName = cell.getDisplayStringValue();
                                break;
                            case 1:
                                metaData.mExam = cell.getDisplayStringValue();
                                break;
                            case 2:
                                metaData.mSubject = cell.getDisplayStringValue();
                                break;
                            case 3:
                                metaData.mLanguage = cell.getDisplayStringValue();
                                break;
                            case 4:
                                metaData.mTotalQ = cell.getDisplayStringValue();
                                break;
                            case 6:
                                metaData.mTime = cell.getDisplayStringValue();
                                break;

                            default:
                                break;
                        }
                        colIndex++;
                    }
                    insertMetaRow(tableName, metaData);
                }
            } else {
                //main data
                while (rowIterator.hasNext()) {
                    int colIndex = 0;
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.iterator();
                    DBRow dbRow = new DBRow();

                    //dont change the case numbers as they relate to column numbers
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (colIndex) {
                            case 0:
                                dbRow.exam = cell.getDisplayStringValue();
                                break;
                            case 1:
                                dbRow.year = cell.getDisplayStringValue();
                                break;
                            case 2:
                                dbRow.qNo = Integer.valueOf(cell.getDisplayStringValue());
                                break;
                            case 3:
                                dbRow.question = cell.getDisplayStringValue();
                                break;
                            case 4:
                                dbRow.optionA = cell.getDisplayStringValue();
                                break;
                            case 5:
                                dbRow.optionB = cell.getDisplayStringValue();
                                break;
                            case 6:
                                dbRow.optionC = cell.getDisplayStringValue();
                                break;
                            case 7:
                                dbRow.optionD = cell.getDisplayStringValue();
                                break;
                            case 8:
                                dbRow.answer = cell.getDisplayStringValue();
                                break;
                            case 9:
                                dbRow.ipc = cell.getDisplayStringValue();
                                break;
                            case 10:
                                dbRow.subject = cell.getDisplayStringValue();
                                break;
                            case 11:
                                dbRow.chapter = Integer.valueOf(cell.getDisplayStringValue());
                                break;
                            case 12:
                                dbRow.difficulty = Integer.valueOf(cell.getDisplayStringValue());
                                break;
                            case 13:
                                dbRow.userstatus = "";
                                break;

                            default:
                                break;
                        }
                        colIndex++;
                    }

                    insertRow(tableName, dbRow);
                }
            }
        }
    }

    private long getNumofQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_QBANK);
        db.close();
        return count;
    }

    private void insertRow(String tableName, DBRow row) {
        SQLiteDatabase db = this.getWritableDatabase();

        //avoid any empty rows
        if (row.question == null) return;

        ContentValues values = new ContentValues();
        values.put(DBRow.KEY_EXAM, row.exam);
        values.put(DBRow.KEY_YEAR, row.year);
        values.put(DBRow.KEY_QNO, row.qNo);
        values.put(DBRow.KEY_QUESTION, row.question);
        values.put(DBRow.KEY_OPTA, row.optionA);
        values.put(DBRow.KEY_OPTB, row.optionB);
        values.put(DBRow.KEY_OPTC, row.optionC);
        values.put(DBRow.KEY_OPTD, row.optionD);
        values.put(DBRow.KEY_ANSWER, row.answer);
        values.put(DBRow.KEY_IPC, row.ipc);
        values.put(DBRow.KEY_SUBJECT, row.subject);
        values.put(DBRow.KEY_CHAPTER, row.chapter);
        values.put(DBRow.KEY_DIFFICULTY, row.difficulty);
        values.put(DBRow.KEY_USER_STATUS, row.userstatus);

        // insert row
        db.insert(tableName, null, values);
    }

    private void insertMetaRow(String tableName, MetaData row) {
        SQLiteDatabase db = this.getWritableDatabase();

        //avoid any empty rows
        if (row.mExam == null || row.mName.compareTo("") == 0) return;

        ContentValues values = new ContentValues();
        values.put(MetaData.KEY_NAME, row.mName);
        values.put(MetaData.KEY_EXAM, row.mExam);
        values.put(MetaData.KEY_SUBJECT, row.mSubject);
        values.put(MetaData.KEY_LANGUAGE, row.mLanguage);
        values.put(MetaData.KEY_TOTALQ, row.mTotalQ);
        values.put(MetaData.KEY_TIME, row.mTime);

        // insert row
        db.insert(tableName, null, values);
    }

    public ArrayList<String> queryYears() {
        ArrayList<String> yearList = new ArrayList<>();
        String query = queryStringAllYears();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                String year = c.getString(c.getColumnIndex(DBRow.KEY_YEAR));

                if (year != null && !year.equals("")) {
                    yearList.add(year);
                }
            } while (c.moveToNext());
        }

        return new ArrayList<>(yearList);
    }

    public ArrayList<String> querySubjects() {
        ArrayList<String> subList = new ArrayList<>();
        String query = queryStringAllSubjects();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                String sub = c.getString(c.getColumnIndex(DBRow.KEY_SUBJECT));

                if (sub != null && !sub.equals("")) {
                    subList.add(sub);
                }
            } while (c.moveToNext());
        }

        return new ArrayList<>(subList);
    }

    public ArrayList<String> queryExams() {
        ArrayList<String> examList = new ArrayList<>();
        String query = queryStringAllExams();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                String exam = c.getString(c.getColumnIndex(DBRow.KEY_EXAM));

                if (exam != null && !exam.equals("")) {
                    examList.add(exam);
                }
            } while (c.moveToNext());
        }

        return new ArrayList<>(examList);
    }

    public List<DBRow> queryYearExt(String year) {
        List<DBRow> questions = new ArrayList<>();
        String selectQuery = queryStringYearExt(year);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }

    public List<DBRow> querySubjectExt(String subject) {
        List<DBRow> questions = new ArrayList<>();
        String selectQuery = queryStringSubjects(subject);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }


    public List<DBRow> queryExamExt(String exam) {
        List<DBRow> questions = new ArrayList<>();
        String selectQuery = queryStringExams(exam);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }

    public List<DBRow> queryQuestionsQuiz(String quizName) {

        List<DBRow> questions = new ArrayList<>();
        String selectQuery = queryStringForQuiz(quizName);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }

    public List<DBRow> queryPracticeQuestions(String query) {
        List<DBRow> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }

    public List<DBRow> queryQuestionsRandom() {

        long numQ = Math.min(getNumofQuestions(), MAX_QUESTIONS);
        List<DBRow> questions = new ArrayList<>();
        String selectQuery = queryStringRandom(numQ);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }

    public List<DBRow> queryAllQuestions() {//TODO: length of list

        List<DBRow> questions = new ArrayList<>();
        String selectQuery = queryStringAllQuestions();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }

    /*
        return list of all quiz names aka table names starting with suffix quiz
     */
    public List<MetaData> queryAllQuizzes() {

        List<MetaData> quizTableList = new ArrayList<>();
        String selectQuery = queryStringMetaData(TABLE_QUIZ);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                quizTableList.add(setMetaRow(c));
            } while (c.moveToNext());
        }

        return quizTableList;
    }

    /*
    return list of all modeltest names aka table names starting with suffix modeltest
 */
    public List<MetaData> queryAllModelTests() {

        List<MetaData> modeltestTableList = new ArrayList<>();
        String selectQuery = queryStringMetaData(TABLE_MODELTEST);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                modeltestTableList.add(setMetaRow(c));
            } while (c.moveToNext());
        }

        return modeltestTableList;
    }

    private String queryStringMetaData(String type) {
        return "SELECT * FROM metadata_" + type;
    }

    private String queryStringAllTables() {
        return "SELECT name FROM sqlite_master WHERE type=" + "\"table\"";
    }

    private String queryStringRandom(long numQ) {
        return "SELECT DISTINCT * FROM " + TABLE_QBANK + " ORDER BY RANDOM() LIMIT " + numQ;
    }

    private String queryStringDifficulty(String difficulty) {
        return "SELECT * FROM " + TABLE_QBANK + " WHERE difficulty BETWEEN " + difficulty;
    }

    private String queryStringUserStatus() {
        return "SELECT * FROM " + TABLE_QBANK + " WHERE userstatus=" + "\"Z\"";
    }

    private String queryStringUserStatus(int qNo) {
        return "SELECT * FROM " + TABLE_QBANK + " WHERE userstatus=" + "\"Z\"" + " and qno=" + qNo;
    }

    private String queryStringExams(String exam) {
        return "SELECT * FROM " + TABLE_QBANK + " WHERE examName=\"" + exam + "\"";
    }

    private String queryStringSubjects(String subject) {
        return "SELECT * FROM " + TABLE_QBANK + " WHERE subject=\"" + subject + "\"";
    }

    private String queryStringYearExt(String year) {
        return "SELECT * FROM " + TABLE_QBANK + " WHERE year=\"" + year + "\"";
    }

    private String queryStringAllSubjects() {
        return "SELECT DISTINCT subject FROM " + TABLE_QBANK;
    }

    private String queryStringAllExams() {
        return "SELECT DISTINCT examName FROM " + TABLE_QBANK;
    }

    private String queryStringAllQuestions() {
        return "SELECT * FROM " + TABLE_QBANK;
    }

    private String queryStringForQuiz(String tableName) {
        return "SELECT * FROM " + tableName;
    }

    public void setUserStatus(String tableName, int qNo, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userstatus", status);
        db.update(tableName, values, "qno=" + qNo, null);
        db.close();
    }

    public String getUserStatus(int qNo) {
        String selectQuery = queryStringUserStatus(qNo);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        String ret = c.getString(c.getColumnIndex("userstatus"));
        c.close();
        db.close();
        return ret == null ? "" : ret;
    }

    private String queryStringAllYears() {
        return "SELECT DISTINCT year FROM " + TABLE_QBANK;
    }

    /* Difficulty level definition
        Easy =>     "0 AND 3"
        Medium =>   "4 AND 6"
        Hard =>     "7 AND 9"
     */
    public List<DBRow> queryQuestionsDifficulty(String difficulty) {

        List<DBRow> questions = new ArrayList<>();
        String selectQuery = queryStringDifficulty(difficulty);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }

    public List<DBRow> queryQuestionsUserStatus() {

        List<DBRow> questions = new ArrayList<>();
        String selectQuery = queryStringUserStatus();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // loop through all rows and add to the list
        if (c != null && c.moveToFirst()) {
            do {
                questions.add(setRow(c));
            } while (c.moveToNext());
        }

        return questions;
    }

    private MetaData setMetaRow(Cursor c) {

        MetaData row = new MetaData();
        row.mName = c.getString(c.getColumnIndex(MetaData.KEY_NAME));
        row.mExam = c.getString(c.getColumnIndex(MetaData.KEY_EXAM));
        row.mSubject = c.getString(c.getColumnIndex(MetaData.KEY_SUBJECT));
        row.mLanguage = c.getString(c.getColumnIndex(MetaData.KEY_LANGUAGE));
        row.mTotalQ = c.getString(c.getColumnIndex(MetaData.KEY_TOTALQ));
        row.mTime = c.getString(c.getColumnIndex(MetaData.KEY_TIME));

        return row;
    }

    private DBRow setRow(Cursor c) {

        DBRow row = new DBRow();
        row.exam = c.getString(c.getColumnIndex(DBRow.KEY_EXAM));
        row.year = c.getString(c.getColumnIndex(DBRow.KEY_YEAR));
        row.qNo = c.getInt(c.getColumnIndex(DBRow.KEY_QNO));
        row.question = c.getString(c.getColumnIndex(DBRow.KEY_QUESTION));
        row.optionA = c.getString(c.getColumnIndex(DBRow.KEY_OPTA));
        row.optionB = c.getString(c.getColumnIndex(DBRow.KEY_OPTB));
        row.optionC = c.getString(c.getColumnIndex(DBRow.KEY_OPTC));
        row.optionD = c.getString(c.getColumnIndex(DBRow.KEY_OPTD));
        row.answer = c.getString(c.getColumnIndex(DBRow.KEY_ANSWER));
        row.ipc = c.getString(c.getColumnIndex(DBRow.KEY_IPC));
        row.subject = c.getString(c.getColumnIndex(DBRow.KEY_SUBJECT));
        row.chapter = c.getInt(c.getColumnIndex(DBRow.KEY_CHAPTER));
        row.difficulty = c.getInt(c.getColumnIndex(DBRow.KEY_DIFFICULTY));
        row.userstatus = c.getString(c.getColumnIndex(DBRow.KEY_USER_STATUS));

        return row;
    }

/*    public DBUSer checkQuizLevelExists(String quizlevel) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USERDATA + " WHERE "
                + KEY_QUIZLEVEL + " = " + "'" + quizlevel + "'";

        Log.e(className, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        DBUSer td = null;
        if (c != null && c.moveToFirst()) {

            td = new DBUSer();
            td.setQuizName(c.getString(c.getColumnIndex(KEY_QUIZNAME)));
            td.setQuizlevelname(c.getString(c.getColumnIndex(KEY_QUIZLEVEL)));
            td.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
            td.setAttempts(c.getInt(c.getColumnIndex(KEY_ATTEMPTS)));
            td.setTotalNoQuestions(c.getInt(c.getColumnIndex(KEY_TOTALQUESTIONS)));
            td.setDateofquiz(c.getString(c.getColumnIndex(KEY_DATEOFQUIZ)));
        }

        return td;
    }

    public List<DBUSer> getAllData() {
        List<DBUSer> todos = new ArrayList<DBUSer>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERDATA;

        Log.e(className, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                DBUSer td = new DBUSer();
                td.setQuizName(c.getString(c.getColumnIndex(KEY_QUIZNAME)));
                td.setQuizlevelname(c.getString(c.getColumnIndex(KEY_QUIZLEVEL)));
                td.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
                td.setAttempts(c.getInt(c.getColumnIndex(KEY_ATTEMPTS)));
                td.setTotalNoQuestions(c.getInt(c.getColumnIndex(KEY_TOTALQUESTIONS)));
                td.setDateofquiz(c.getString(c.getColumnIndex(KEY_DATEOFQUIZ)));
                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }

        return todos;
    }

    */

    /**
     * getting todo count
     *//*
    public int getToDoCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERDATA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateToDo(DBUSer todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ATTEMPTS, todo.getAttempts());
        values.put(KEY_SCORE, todo.getScore());

        // updating row
        return db.update(TABLE_USERDATA, values, KEY_QUIZLEVEL + " = ?",
                new String[]{String.valueOf(todo.getQuizlevelname())});
    }*/
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private int runCountQuery(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery(query, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    public List<Test> getTestsData(Context context, TestType testType) {
        List<Test> tests = new ArrayList<>();
        String query;
        int maxQuestions;
        int answeredQuestions;
        int correctAnswers;
        int wrongAnswers;

        if (testType == TestType.QBANK) {
            List<String> subjects = querySubjects();
            for (String subject : subjects) {
                query = "SELECT COUNT(*) FROM " + TABLE_QBANK + " WHERE " + DBRow.KEY_SUBJECT + " = '" + subject + "'";
                maxQuestions = runCountQuery(query);
                answeredQuestions = runCountQuery(query + " AND " + DBRow.KEY_USER_STATUS +
                        " != ''" + " AND " + DBRow.KEY_USER_STATUS + " != 'Z'");
                correctAnswers = runCountQuery(query + " AND " + DBRow.KEY_USER_STATUS + " = " + DBRow.KEY_ANSWER);
                wrongAnswers = runCountQuery(query + " AND " + DBRow.KEY_USER_STATUS + " != " + DBRow.KEY_ANSWER);

                tests.add(new Test(subject, maxQuestions, answeredQuestions, correctAnswers, wrongAnswers));
            }
        } else if (testType == TestType.QUIZ) {
            List<MetaData> quizzes = queryAllQuizzes();
            for (MetaData quiz : quizzes) {
                query = "SELECT COUNT(*) FROM " + quiz.mName;
                maxQuestions = runCountQuery(query);
                answeredQuestions = runCountQuery(query + " WHERE " + DBRow.KEY_USER_STATUS +
                        " != ''" + " AND " + DBRow.KEY_USER_STATUS + " != 'Z'");
                correctAnswers = runCountQuery(query + " WHERE " + DBRow.KEY_USER_STATUS + " = " + DBRow.KEY_ANSWER);
                wrongAnswers = runCountQuery(query + " WHERE " + DBRow.KEY_USER_STATUS + " != " + DBRow.KEY_ANSWER);

                tests.add(new Test(quiz.mName, maxQuestions, answeredQuestions, correctAnswers, wrongAnswers));
            }
        } else if (testType == TestType.MODELTEST) {
            List<MetaData> quizzes = queryAllModelTests();
            for (MetaData quiz : quizzes) {
                query = "SELECT COUNT(*) FROM " + quiz.mName;
                maxQuestions = runCountQuery(query);
                answeredQuestions = runCountQuery(query + " WHERE " + DBRow.KEY_USER_STATUS +
                        " != ''" + " AND " + DBRow.KEY_USER_STATUS + " != 'Z'");
                correctAnswers = runCountQuery(query + " WHERE " + DBRow.KEY_USER_STATUS + " = " + DBRow.KEY_ANSWER);
                wrongAnswers = runCountQuery(query + " WHERE " + DBRow.KEY_USER_STATUS + " != " + DBRow.KEY_ANSWER);

                tests.add(new Test(quiz.mName, maxQuestions, answeredQuestions, correctAnswers, wrongAnswers));
            }
        } else if (testType == TestType.ALL) {
            tests.add(TestType.QBANK.getSummary(context));
            tests.add(TestType.QUIZ.getSummary(context));
            tests.add(TestType.MODELTEST.getSummary(context));
        }

        return tests;
    }
}