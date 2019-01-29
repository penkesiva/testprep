package com.ee.testprep.db;

import android.app.Application;

import com.ee.testprep.PracticeMetrics.PracticeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PracticeViewModel extends AndroidViewModel {
    private static int QUESTION_COUNT = 25;
    private List<DBRow> allQuestions;
    private MutableLiveData<List<DBRow>> questions = new MutableLiveData<>();

    public PracticeViewModel(@NonNull Application application) {
        super(application);
        allQuestions = DataBaseHelper.getInstance(application).queryAllQuestions();
    }

    public LiveData<List<DBRow>> getQuestions() {
        return questions;
    }

    public void clearData() {
        questions.setValue(new ArrayList<DBRow>());
    }

    public synchronized void setPracticeType(PracticeType practiceType, String value) {
        int i = 0;
        List<DBRow> filteredQuestions = new ArrayList<>();

        if (allQuestions != null) {
            switch (practiceType) {
                case YEAR:
                    for (DBRow question : allQuestions) {
                        if (question.year == value) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case SUBJECT:
                    for (DBRow question : allQuestions) {
                        if (question.subject == value) {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case EXAM:
                    for (DBRow question : allQuestions) {
                        if (question.exam == value) {
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
                    List<DBRow> randomizedQuestions = new ArrayList<>(allQuestions.size());
                    Collections.copy(randomizedQuestions, allQuestions);
                    Collections.shuffle(randomizedQuestions);
                    for (DBRow question : randomizedQuestions) {
                        if (question.userstatus == "Z" || question.userstatus == "") {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case STARRED:
                    for (DBRow question : allQuestions) {
                        if (question.userstatus == "Z") {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
                case ALL:
                default:
                    for (DBRow question : allQuestions) {
                        if (question.userstatus == "Z" || question.userstatus == "") {
                            filteredQuestions.add(question);
                            i++;
                            if (i == QUESTION_COUNT) break;
                        }
                    }
                    break;
            }
        }

        questions.setValue(filteredQuestions);
    }
}
