package com.ee.testprep.db;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserDataViewModel extends AndroidViewModel {
    private HashMap<String, Test> userDataMap = new HashMap<>();
    private MutableLiveData<List<Test>> userDataList = new MutableLiveData<>();
    private MutableLiveData<Test> userData = new MutableLiveData<>();
    private TestDao testDao;

    public UserDataViewModel(@NonNull Application application) {
        super(application);
        testDao = TestDatabase.getDatabase(application).testDao();
        clearUserDataList();
        clearUserData();
        loadUserDataList();
    }

    public LiveData<List<Test>> getUserDataList() {
        return userDataList;
    }

    public LiveData<Test> getUserData() {
        return userData;
    }

    public void clearUserDataList() {
        userDataMap.clear();
        userDataList.setValue(null);
    }

    public void clearUserData() {
        userData.setValue(null);
    }

    private void loadUserDataList() {
        new AsyncTask<Void, Void, List<Test>>() {
            @Override
            protected List<Test> doInBackground(Void... voids) {
                return testDao.getAll();
            }

            @Override
            protected void onPostExecute(List<Test> data) {
                for (Test t : data) {
                    userDataMap.put(t.testName, t);
                }
                userDataList.setValue(data);
            }
        }.execute();
    }

    private void loadUserData(String quizName) {
        new AsyncTask<String, Void, Test>() {
            @Override
            protected Test doInBackground(String... names) {
                return testDao.findByName(names[0]);
            }

            @Override
            protected void onPostExecute(Test data) {
                userData.setValue(data);
            }
        }.execute(quizName);
    }

    public void updateUserData(Test test) {
        new AsyncTask<Test, Void, Test>() {
            @Override
            protected Test doInBackground(Test... updatedTest) {
                Test updatedTestData = updatedTest[0];
                return updatedTestData;
            }

            @Override
            protected void onPostExecute(Test updatedTest) {
                userData.setValue(updatedTest);
                userDataMap.put(updatedTest.testName, updatedTest);
                List<Test> newValues = new ArrayList<>();
                newValues.addAll(userDataMap.values());
                userDataList.setValue(newValues);
            }
        }.execute(test);
    }
}
