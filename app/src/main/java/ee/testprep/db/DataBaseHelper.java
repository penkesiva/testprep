package ee.testprep.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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

import ee.testprep.R;

public class DataBaseHelper extends SQLiteOpenHelper implements Serializable {

    private static final String className = DataBaseHelper.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eeTestPrep2.db";

    // Table Names
    private static final String TABLE_USERDATA = "userdata";
    private static final String TABLE_QBANK = "qbank";
    private static final String TABLE_QUIZ = "quiz";
    private static final String TABLE_MODELTEST = "nav_modeltest";

    private static final int MAX_QUESTIONS = 500;
    private Workbook workbook;
    private Context mContext;
    private static DataBaseHelper dbHelperInstance = null;
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_QBANK;

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

    @Override
    public void onCreate(SQLiteDatabase db) {
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL(SQL_DELETE_ENTRIES);

        // create new tables
        onCreate(db);
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
                db.execSQL(createTable(sheetName));
            }
        }
    }

    private void convertXLStoSQL(final int resourceId, final String tableName) {

        Thread mThread = new Thread() {

            @Override
            public void run() {

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
                    rowIterator.hasNext();//skip header TODO

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
        };
        mThread.start();

    }

    private long getNumofQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_QBANK);
        db.close();
        return count;
    }

    private void insertRow(String tableName, DBRow row) {
        SQLiteDatabase db = this.getWritableDatabase();

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

    public ArrayList<String> queryYear() {
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

    public ArrayList<String> querySubject() {
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

    public ArrayList<String> queryExam() {
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

    public List<DBRow> queryQuestionsQuiz() {

        //setPreferences TODO
        //get number of questions - 10, 20, 30 - 10; TODO
        int numQ = 10;
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

    private String queryStringAllQuestions() {
        return "SELECT * FROM " + TABLE_QBANK;
    }

    public void setUserStatus(int qNo, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (status) {
            values.put("userstatus", "Z");
        } else {
            values.put("userstatus", "");
        }
        db.update(TABLE_QBANK, values, "qno=" + qNo, null);
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

    private String queryStringAllExams() {
        return "SELECT DISTINCT examName FROM " + TABLE_QBANK;
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

    public List<Test> getTestsData(Database database) {
        List<Test> tests = new ArrayList<>();
        if (database == Database.QBANK) {
            tests.add(new Test("Practice 1", 100, 100, 94, 6));
            tests.add(new Test("Practice 2", 100, 100, 100, 0));
            tests.add(new Test("Practice 3", 100, 60, 59, 1));
            tests.add(new Test("Practice 4", 100, 0, 0, 0));
            tests.add(new Test("Practice 5", 100, 0, 0, 0));
            tests.add(new Test("Practice 6", 100, 0, 0, 0));
        } else if (database == Database.QUIZ) {
            tests.add(new Test("Quiz 1", 100, 100, 91, 9));
            tests.add(new Test("Quiz 2", 100, 100, 87, 13));
            tests.add(new Test("Quiz 3", 100, 100, 94, 6));
            tests.add(new Test("Quiz 4", 100, 100, 93, 7));
            tests.add(new Test("Quiz 5", 100, 0, 0, 0));
            tests.add(new Test("Quiz 6", 100, 0, 0, 0));
        } else if (database == Database.MODELTEST) {
            tests.add(new Test("Model Test 1", 100, 100, 90, 10));
            tests.add(new Test("Model Test 2", 100, 100, 89, 11));
            tests.add(new Test("Model Test 3", 100, 100, 91, 9));
            tests.add(new Test("Model Test 4", 100, 100, 94, 6));
            tests.add(new Test("Model Test 5", 100, 100, 87, 13));
            tests.add(new Test("Model Test 6", 100, 100, 96, 4));
            tests.add(new Test("Model Test 7", 100, 40, 40, 0));
            tests.add(new Test("Model Test 8", 100, 0, 0, 0));
        }

        return tests;
    }

    public enum Database {
        ALL, QBANK, QUIZ, MODELTEST;
    }

    public static class Test {
        public String name;
        public int maxQuestions;
        public int answeredQuestions;
        public int correctAnswers;
        public int wrongAnswers;

        Test(String name, int maxQuestions, int answeredQuestions, int correctAnswers,
                int wrongAnswers) {
            this.name = name;
            this.maxQuestions = maxQuestions;
            this.answeredQuestions = answeredQuestions;
            this.correctAnswers = correctAnswers;
            this.wrongAnswers = wrongAnswers;
        }
    }
}