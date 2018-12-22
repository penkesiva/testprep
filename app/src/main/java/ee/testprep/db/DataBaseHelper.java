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

import ee.testprep.L;
import ee.testprep.R;

public class DataBaseHelper extends SQLiteOpenHelper implements Serializable {

    private static final String className = DataBaseHelper.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eeTestPrep2.db";

    // Table Names
    private static final String TABLE_USERDATA = "userData";
    private static final String TABLE_QBANK = "qBank";

    private static final int MAX_QUESTIONS = 500;
    private Workbook workbook;
    private Context mContext;

    private static DataBaseHelper dbHelperInstance = null;

    public static final String CREATE_TABLE_MAIN = "CREATE TABLE "
            + TABLE_QBANK + "(" +
            BaseColumns._ID + " INTEGER PRIMARY KEY, "
            + DBRow.KEY_EXAM + " TEXT, "
            + DBRow.KEY_YEAR + " TEXT, "
            //+ DBRow.KEY_QNO + " INTEGER, "
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

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_QBANK;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static DataBaseHelper getInstance(Context context) {
        if(dbHelperInstance == null){
            dbHelperInstance = new DataBaseHelper(context);
        }
        return dbHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!isTableExists(db, TABLE_QBANK)) {
            db.execSQL(CREATE_TABLE_MAIN);
            convertXLStoSQL();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL(SQL_DELETE_ENTRIES);
        // create new tables
        onCreate(db);
    }

    public boolean isTableExists(SQLiteDatabase db, String tableName) {

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

    public void convertXLStoSQL() {

        Thread mThread = new Thread() {

            @Override
            public void run() {

                try {
                    InputStream fstream = mContext.getResources().openRawResource(R.raw.qbank_v1);
                    LoadOptions loadOptions = new LoadOptions(FileFormatType.XLSX);
                    //loadOptions.setPassword("penke999");
                    workbook = new Workbook(fstream, loadOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (workbook != null) {
                    Worksheet worksheet = workbook.getWorksheets().get(0);

                    Iterator<Row> rowIterator = worksheet.getCells().getRows().iterator();
                    rowIterator.hasNext();//skip header TODO

                    L.d(className, "rows: " + worksheet.getCells().getRows().getCount());

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
                                /*case 2:
                                    dbRow.qNo = cell.getIntValue()+1;
                                    L.d(getLocalClassName(), dbRow.qNo+"");
                                    break;*/
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
                                    dbRow.userStatus = "";
                                    break;

                                default:
                                    break;
                            }

                            colIndex++;
                        }

                        insertRow(dbRow);
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

    private long insertRow(DBRow row) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBRow.KEY_EXAM, row.exam);
        values.put(DBRow.KEY_YEAR, row.year);
        //values.put(DBRow.KEY_QNO, row.qNo);
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
        values.put(DBRow.KEY_USER_STATUS, row.userStatus);

        // insert row
        return db.insert(TABLE_QBANK, null, values);
    }

    public ArrayList<String> queryYear() {
        ArrayList<String> yearList = new ArrayList<>();
        String query = queryAllYears();
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
        String query = queryAllSubjects();
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
        String query = queryAllExams();
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

    private String queryStringRandom(long numQ) {
        return "SELECT DISTINCT * FROM " + TABLE_QBANK + " ORDER BY RANDOM() LIMIT " + numQ;
    }

    private String queryStringDifficulty(String difficulty) {
        return "SELECT * FROM " + TABLE_QBANK + " WHERE difficulty BETWEEN " + difficulty;
    }

    private String queryStringUserStatus() {
        return "SELECT * FROM " + TABLE_QBANK + " WHERE userstatus=" + "\"Z\"";
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

    private String queryAllSubjects() {
        return "SELECT DISTINCT subject FROM " + TABLE_QBANK;
    }

    private String queryAllExams() {
        return "SELECT DISTINCT examName FROM " + TABLE_QBANK;
    }

    private String queryAllYears() {
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
        //row.qNo = c.getInt(c.getColumnIndex(DBRow.KEY_QNO));
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
        row.userStatus = c.getString(c.getColumnIndex(DBRow.KEY_USER_STATUS));

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

}