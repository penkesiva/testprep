package com.ee.testprep.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.util.SimpleVibaration;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QuestionPracticeFragment extends Fragment {

    private static final String ARG_QUESTION = "question";
    private static final String ARG_LAST_QUESTION = "is_last_question";
    private static String TAG = QuestionPracticeFragment.class.getSimpleName();
    private Context mContext;
    private DBRow mQuestion;
    private OnFragmentInteractionListener mListener;
    private CheckBox[] cb = new CheckBox[4];
    private String recordedAnswer;
    private ImageView ivFav;
    private DataBaseHelper dbHelper;
    private boolean isLastQuestion;
    private Button more;
    private Button wrong;
    private Button correct;
    private String reviewLater = "Z";

    public QuestionPracticeFragment() {
    }

    public static QuestionPracticeFragment newInstance(DBRow question, boolean isLastQuestion) {
        QuestionPracticeFragment fragment = new QuestionPracticeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_QUESTION, question);
        bundle.putBoolean(ARG_LAST_QUESTION, isLastQuestion);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        if (getArguments() != null) {
            mQuestion = (DBRow) getArguments().getSerializable(ARG_QUESTION);
            isLastQuestion = getArguments().getBoolean(ARG_LAST_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.practice_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        correct = view.findViewById(R.id.practice_q_correct);
        wrong = view.findViewById(R.id.practice_q_wrong);

        TextView tvQNo = view.findViewById(R.id.q_no);
        tvQNo.setText(String.format("Question %d\n", mQuestion.qNo));

        TextView tvQuestion = view.findViewById(R.id.question);
        tvQuestion.append(mQuestion.question.trim());
        tvQuestion.setMovementMethod(new ScrollingMovementMethod());

        ImageView ivExplanation = view.findViewById(R.id.explanation);
        ivExplanation.setOnClickListener(v -> {

            Dialog statusDialog = new Dialog(getContext());
            statusDialog.setCancelable(true);
            statusDialog.setCanceledOnTouchOutside(true);
            statusDialog.setContentView(R.layout.explanation_dialog);
            TextView tvExplanation = statusDialog.findViewById(R.id.explanation);
            tvExplanation.setText("There is no explanation to this Question");
            statusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            statusDialog.show();
        });

        dbHelper = DataBaseHelper.getInstance(getActivity());

        if (isLastQuestion) {
            more = view.findViewById(R.id.practice_q_more);
            more.setVisibility(View.VISIBLE);
            more.setOnClickListener(view1 -> {
                if (mListener != null) {
                    mListener.onFragmentInteraction(MainActivity.STATUS_PRACTICE_MORE, null);
                }
            });
        }

        ivFav = view.findViewById(R.id.fav);
        ivFav.setOnClickListener(view12 -> {
            ivFav.setActivated(!ivFav.isActivated());

            //if activate - update user-status
            if (ivFav.isActivated()) {
                dbHelper.setUserStatus(dbHelper.TABLE_QBANK, mQuestion.qNo, reviewLater);
                ivFav.setActivated(true);
            }
        });

        int green = getResources().getColor(R.color.colorGreen);
        int red = getResources().getColor(R.color.colorRed);

        cb[0] = view.findViewById(R.id.rb_optA);
        String optionA = mQuestion.optionA.trim();
        optionA = optionA.substring(0, 1).toUpperCase() + optionA.substring(1);
        cb[0].setText(optionA);

        cb[0].setOnClickListener(v -> {

            clearCheckBoxes();
            cb[0].setChecked(true);

            if (mQuestion.answer.toLowerCase().equals("a")) {
                wrong.setVisibility(View.INVISIBLE);
                correct.setVisibility(View.VISIBLE);
                cb[0].setTypeface(null, Typeface.BOLD);
                cb[0].setTextColor(green);
            } else {
                correct.setVisibility(View.INVISIBLE);
                wrong.setVisibility(View.VISIBLE);
                cb[0].setTextColor(red);
                int i = getIndexMap(mQuestion.answer.toLowerCase()); // right answer
                cb[i].setTypeface(null, Typeface.BOLD);
                cb[i].setTextColor(green);

                new SimpleVibaration(mContext);
            }
        });

        cb[1] = view.findViewById(R.id.rb_optB);
        String optionB = mQuestion.optionB.trim();
        optionB = optionB.substring(0, 1).toUpperCase() + optionB.substring(1);
        cb[1].setText(optionB);
        cb[1].setOnClickListener(v -> {

            clearCheckBoxes();
            cb[1].setChecked(true);

            if (mQuestion.answer.toLowerCase().equals("b")) {
                wrong.setVisibility(View.INVISIBLE);
                correct.setVisibility(View.VISIBLE);
                cb[1].setTypeface(null, Typeface.BOLD);
                cb[1].setTextColor(green);
            } else {
                correct.setVisibility(View.INVISIBLE);
                wrong.setVisibility(View.VISIBLE);
                cb[1].setTextColor(red);
                int i = getIndexMap(mQuestion.answer.toLowerCase()); // right answer
                cb[i].setTypeface(null, Typeface.BOLD);
                cb[i].setTextColor(green);

                new SimpleVibaration(getActivity().getApplicationContext());
            }
        });

        cb[2] = view.findViewById(R.id.rb_optC);
        String optionC = mQuestion.optionC.trim();
        optionC = optionC.substring(0, 1).toUpperCase() + optionC.substring(1);
        cb[2].setText(optionC);
        cb[2].setOnClickListener(v -> {

            clearCheckBoxes();
            cb[2].setChecked(true);

            if (mQuestion.answer.toLowerCase().equals("c")) {
                wrong.setVisibility(View.INVISIBLE);
                correct.setVisibility(View.VISIBLE);
                cb[2].setTypeface(null, Typeface.BOLD);
                cb[2].setTextColor(green);
            } else {
                correct.setVisibility(View.INVISIBLE);
                wrong.setVisibility(View.VISIBLE);
                cb[2].setTextColor(red);
                int i = getIndexMap(mQuestion.answer.toLowerCase()); // right answer
                cb[i].setTypeface(null, Typeface.BOLD);
                cb[i].setTextColor(green);

                new SimpleVibaration(getActivity().getApplicationContext());
            }
        });

        cb[3] = view.findViewById(R.id.rb_optD);
        String optionD = mQuestion.optionD.trim();
        optionD = optionD.substring(0, 1).toUpperCase() + optionD.substring(1);
        cb[3].setText(optionD);
        cb[3].setOnClickListener(v -> {

            clearCheckBoxes();
            cb[3].setChecked(true);

            if (mQuestion.answer.toLowerCase().equals("d")) {
                wrong.setVisibility(View.INVISIBLE);
                correct.setVisibility(View.VISIBLE);
                cb[3].setTypeface(null, Typeface.BOLD);
                cb[3].setTextColor(green);
            } else {
                correct.setVisibility(View.INVISIBLE);
                wrong.setVisibility(View.VISIBLE);
                cb[3].setTextColor(red);
                int i = getIndexMap(mQuestion.answer.toLowerCase()); // right answer
                cb[i].setTypeface(null, Typeface.BOLD);
                cb[i].setTextColor(green);

                new SimpleVibaration(getActivity().getApplicationContext());
            }
        });
    }

    private void clearCheckBoxes() {
        for (int i = 0; i < 4; i++) {
            cb[i].setChecked(false);
            cb[i].setTextColor(Color.BLACK);
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
