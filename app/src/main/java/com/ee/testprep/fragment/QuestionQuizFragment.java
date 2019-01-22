package com.ee.testprep.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;

public class QuestionQuizFragment extends Fragment {

    private static final String QUIZ_QUESTION = "quiz_question";
    private static String TAG = QuestionQuizFragment.class.getSimpleName();
    private DBRow mQuestion;
    private CheckBox cbA;
    private CheckBox cbB;
    private CheckBox cbC;
    private CheckBox cbD;

    public QuestionQuizFragment() {
    }

    public static QuestionQuizFragment newInstance(DBRow question) {
        QuestionQuizFragment fragment = new QuestionQuizFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUIZ_QUESTION, question);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestion = (DBRow) getArguments().getSerializable(QUIZ_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_question, container, false);

        TextView tvQuestion = view.findViewById(R.id.question);
        tvQuestion.append(mQuestion.question);
        tvQuestion.setMovementMethod(new ScrollingMovementMethod());

        configureCheckBox(view);

        return view;
    }

    private void clearCheckBoxes() {
        cbA.setChecked(false);
        cbB.setChecked(false);
        cbC.setChecked(false);
        cbD.setChecked(false);
    }

    private void configureCheckBox(View view) {
        cbA = view.findViewById(R.id.rb_optA);
        cbA.setText(mQuestion.optionA);
        cbA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                clearCheckBoxes();
                cbA.setChecked(b);
            }
        });

        cbB = view.findViewById(R.id.rb_optB);
        cbB.setText(mQuestion.optionB);
        cbB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                clearCheckBoxes();
                cbB.setChecked(b);
            }
        });

        cbC = view.findViewById(R.id.rb_optC);
        cbC.setText(mQuestion.optionC);
        cbC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                clearCheckBoxes();
                cbC.setChecked(b);
            }
        });

        cbD = view.findViewById(R.id.rb_optD);
        cbD.setText(mQuestion.optionD);
        cbD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                clearCheckBoxes();
                cbD.setChecked(b);
            }
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
