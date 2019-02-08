package com.ee.testprep.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Test.class}, version = 1)
public abstract class TestDatabase extends RoomDatabase {
    private static volatile TestDatabase INSTANCE;

    public static TestDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TestDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TestDatabase.class, "test_database").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract TestDao testDao();
}
