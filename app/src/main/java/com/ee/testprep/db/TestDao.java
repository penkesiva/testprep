package com.ee.testprep.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TestDao {

    @Query("SELECT * FROM tests_data")
    List<Test> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Test testData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Test> testsData);

    @Query("SELECT * FROM tests_data WHERE test_name LIKE :testName LIMIT 1")
    Test findByName(String testName);

//    @Query("DELETE FROM test")
//    void deleteAll();
//    @Delete
//    void delete(Test test);
//    @Update
//    void update(Test test);
}
