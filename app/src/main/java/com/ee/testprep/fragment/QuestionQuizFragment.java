package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QuestionQuizFragment extends Fragment {

    private static String QUIZ_NAME = "quiz_name";
    private static final String QUIZ_QUESTION = "quiz_question";
    private static final String QUIZ_LAST_QUESTION = "is_last_question";
    private static String TAG = QuestionQuizFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private DataBaseHelper dbHelper;
    private String mQuizName;
    private boolean isLastQuestion;
    private DBRow mQuestion;
    private CheckBox cbA;
    private CheckBox cbB;
    private CheckBox cbC;
    private CheckBox cbD;

    public QuestionQuizFragment() {
    }

    public static QuestionQuizFragment newInstance(String quizName, DBRow question, boolean isLastQuestion) {
        QuestionQuizFragment fragment = new QuestionQuizFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QUIZ_NAME, quizName);
        bundle.putSerializable(QUIZ_QUESTION, question);
        bundle.putBoolean(QUIZ_LAST_QUESTION, isLastQuestion);
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
            isLastQuestion = args.getBoolean(QUIZ_LAST_QUESTION);
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

        TextView tvQuestion = view.findViewById(R.id.question);
        tvQuestion.append(mQuestion.question);
        tvQuestion.setMovementMethod(new ScrollingMovementMethod());

        configureCheckBox(view);

        if (isLastQuestion) {
            Button submit = view.findViewById(R.id.quiz_submit);
            submit.setVisibility(View.VISIBLE);
            submit.setOnClickListener(view1 -> {
                if (mListener != null) {
                    mListener.onFragmentInteraction(MainActivity.STATUS_QUIZ_END, mQuizName);
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void clearCheckBoxes() {
        cbA.setChecked(false);
        cbB.setChecked(false);
        cbC.setChecked(false);
        cbD.setChecked(false);
    }

    private void configureCheckBox(View view) {
        cbA = view.findViewById(R.id.rb_optA);
        cbA.setText(mQuestion.optionA.trim());
        cbA.setOnCheckedChangeListener((compoundButton, b) -> {
            clearCheckBoxes();
            cbA.setChecked(b);
            dbHelper.setUserStatus(mQuizName, mQuestion.qNo, "A");
        });

        cbB = view.findViewById(R.id.rb_optB);
        cbB.setText(mQuestion.optionB.trim());
        cbB.setOnCheckedChangeListener((compoundButton, b) -> {
            clearCheckBoxes();
            cbB.setChecked(b);
            dbHelper.setUserStatus(mQuizName, mQuestion.qNo, "B");
        });

        cbC = view.findViewById(R.id.rb_optC);
        cbC.setText(mQuestion.optionC.trim());
        cbC.setOnCheckedChangeListener((compoundButton, b) -> {
            clearCheckBoxes();
            cbC.setChecked(b);
            dbHelper.setUserStatus(mQuizName, mQuestion.qNo, "C");
        });

        cbD = view.findViewById(R.id.rb_optD);
        cbD.setText(mQuestion.optionD.trim());
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
