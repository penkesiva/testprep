package com.ee.testprep.db;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PracticeViewModel extends AndroidViewModel {
    private static int QUESTION_COUNT = 25;
    private static String USER_STATUS = "z";
    private List<DBRow> allQuestions;
    private MutableLiveData<List<DBRow>> questions = new MutableLiveData<>();

    public PracticeViewModel(@NonNull Application application) {
        super(application);
        //allQuestions = DataBaseHelper.getInstance(application).queryAllQuestions();
    }

    public LiveData<List<DBRow>> getQuestions() {
        return questions;
    }

    public void clearData() {
        questions.setValue(new ArrayList<>());
    }

    public synchronized void practiceQuery(String query) {
        List<DBRow> filteredQuestions;
        filteredQuestions = DataBaseHelper.getInstance(getApplication()).queryPracticeQuestions(query);

        Log.d("PracticeViewModel", "practice questions size = " + filteredQuestions.size());

        if(filteredQuestions.size() > 0) {
            questions.setValue(filteredQuestions);
        }
    }

    /*public synchronized void setPracticeType(PracticeType practiceType, String value) {
        int i = 0;
        List<DBRow> filteredQuestions = new ArrayList<>();

        if (allQuestions != null) {
            switch (practiceType) {
                case YEAR:
                    for (DBRow question : allQuestions) {
                        if (question.year.toLowerCase().contains(value.toLowerCase())) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case SUBJECT:
                    for (DBRow question : allQuestions) {
                        if (question.subject.toLowerCase().contains(value.toLowerCase())) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case EXAM:
                    for (DBRow question : allQuestions) {
                        if (question.exam.toLowerCase().contains(value.toLowerCase())) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case EASY:
                    for (DBRow question : allQuestions) {
                        if (question.difficulty <= 3) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case MEDIUM:
                    for (DBRow question : allQuestions) {
                        if (question.difficulty > 3 && question.difficulty < 7) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case HARD:
                    for (DBRow question : allQuestions) {
                        if (question.difficulty >= 7) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case RANDOM:
                    Collections.shuffle(allQuestions);
                    for (DBRow question : allQuestions) {
                        if (question.userstatus.isEmpty() || question.userstatus.toLowerCase().contains(USER_STATUS)) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case STARRED:
                    for (DBRow question : allQuestions) {
                        if (question.userstatus.toLowerCase().contains(USER_STATUS)) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case ALL:
                default:
                    for (DBRow question : allQuestions) {
                        if (question.userstatus.isEmpty() || question.userstatus.toLowerCase().contains(USER_STATUS)) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
            }
        }

        Log.e("PracticeViewModel", "setPracticeType:  practice size = " + filteredQuestions.size());
        questions.setValue(filteredQuestions);
    }*/

    public enum PracticeType {
        YEAR, SUBJECT, EXAM, EASY, MEDIUM, HARD, RANDOM, STARRED, ALL
    }
}
