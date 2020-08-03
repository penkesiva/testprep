package com.ee.testprep.fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QuestionQuizFragment extends Fragment {

    private static final String QUIZ_QUESTION = "quiz_question";
    private static String QUIZ_NAME = "quiz_name";
    private static String TAG = QuestionQuizFragment.class.getSimpleName();
    private DataBaseHelper dbHelper;
    private String mQuizName;
    private DBRow mQuestion;
    private CheckBox cbA;
    private CheckBox cbB;
    private CheckBox cbC;
    private CheckBox cbD;

    public QuestionQuizFragment() {
    }

    public static QuestionQuizFragment newInstance(String quizName, DBRow question) {
        QuestionQuizFragment fragment = new QuestionQuizFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QUIZ_NAME, quizName);
        bundle.putSerializable(QUIZ_QUESTION, question);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mQuizName = args.getString(QUIZ_NAME);
            mQuestion = (DBRow) args.getSerializable(QUIZ_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quiz_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DataBaseHelper.getInstance(getActivity());

        TextView tvQNo = view.findViewById(R.id.q_no);
        tvQNo.setText(String.format("Question %s\n", mQuestion.qNo));

        TextView tvQuestion = view.findViewById(R.id.question);
        tvQuestion.append(mQuestion.question);
        tvQuestion.setMovementMethod(new ScrollingMovementMethod());

        configureCheckBox(view);
    }

    private void clearCheckBoxes() {
        cbA.setChecked(false);
        cbB.setChecked(false);
        cbC.setChecked(false);
        cbD.setChecked(false);
    }

    private void configureCheckBox(View view) {
        cbA = view.findViewById(R.id.rb_optA);
        String optionA = mQuestion.optionA.trim();
        optionA = optionA.substring(0, 1).toUpperCase() + optionA.substring(1);
        cbA.setText(optionA);
        cbA.setOnCheckedChangeListener((compoundButton, b) -> {
            clearCheckBoxes();
            cbA.setChecked(b);
            dbHelper.setUserStatus(mQuizName, mQuestion.qNo, "A");
        });

        cbB = view.findViewById(R.id.rb_optB);
        String optionB = mQuestion.optionB.trim();
        optionB = optionB.substring(0, 1).toUpperCase() + optionB.substring(1);
        cbB.setText(optionB);
        cbB.setOnCheckedChangeListener((compoundButton, b) -> {
            clearCheckBoxes();
            cbB.setChecked(b);
            dbHelper.setUserStatus(mQuizName, mQuestion.qNo, "B");
        });

        cbC = view.findViewById(R.id.rb_optC);
        String optionC = mQuestion.optionC.trim();
        optionC = optionC.substring(0, 1).toUpperCase() + optionC.substring(1);
        cbC.setText(optionC);
        cbC.setOnCheckedChangeListener((compoundButton, b) -> {
            clearCheckBoxes();
            cbC.setChecked(b);
            dbHelper.setUserStatus(mQuizName, mQuestion.qNo, "C");
        });

        cbD = view.findViewById(R.id.rb_optD);
        String optionD = mQuestion.optionD.trim();
        optionD = optionD.substring(0, 1).toUpperCase() + optionD.substring(1);
        cbD.setText(optionD);
        cbD.setOnCheckedChangeListener((compoundButton, b) -> {
            clearCheckBoxes();
            cbD.setChecked(b);
            dbHelper.setUserStatus(mQuizName, mQuestion.qNo, "D");
        });

    }

    private int getIndexMap(String option) {
        if (option.equals("a")) {
            return 0;
        } else if (option.equals("b")) {
            return 1;
        } else if (option.equals("c")) {
            return 2;
        } else {
            return 3;
        }
    }
}
