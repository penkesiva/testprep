package com.ee.testprep.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ee.testprep.db.DBRow;
import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.util.SimpleVibaration;

public class QuestionQuizFragment extends Fragment{

    private static String TAG = QuestionQuizFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DBRow mQuestion;
    private int mNumQuestions;
    private OnFragmentInteractionListener mListener;
    private TextView tvTimer;
    private TextView tvProgress;
    private static ProgressBar mProgressBar;
    private String recordedAnswer;
    private CheckBox cbA;
    private CheckBox cbB;
    private CheckBox cbC;
    private CheckBox cbD;

    public QuestionQuizFragment() {
    }

    public static QuestionQuizFragment newInstance(DBRow question, int numQuestions) {
        QuestionQuizFragment fragment = new QuestionQuizFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1, question);
        bundle.putSerializable(ARG_PARAM2, numQuestions);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestion = (DBRow) getArguments().getSerializable(ARG_PARAM1);
            mNumQuestions = (int) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.quiz_question, container, false);

        tvTimer = view.findViewById(R.id.timer);
        tvTimer.setText("");

        tvProgress = view.findViewById(R.id.tv_progress);

        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setMax(mNumQuestions);

        TextView tvQuestion = view.findViewById(R.id.question);
        tvQuestion.append(mQuestion.question);
        tvQuestion.setMovementMethod(new ScrollingMovementMethod());

        configureCheckBox(view);

        Button btnNext = view.findViewById(R.id.nextButton);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(MainActivity.STATUS_QUIZ_NEXT);
            }
        });

        Button btnPrev = view.findViewById(R.id.previousButton);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(MainActivity.STATUS_QUIZ_PREVIOUS);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });

        // populate the question
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

    public void uiRefresh(final int time, final int currQIndex) {

        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int p1 = time % 60;
                    int p2 = time / 60;
                    int p3 = p2 % 60;
                    p2 = p2 / 60;
                    tvTimer.setText(p2 + " : " + p3 + " : " + p1);
                    mProgressBar.setProgress(currQIndex);

                    tvProgress.setText("( " + currQIndex + " / " + mNumQuestions + " )");
                }
            });
        }
    }

    private int getIndexMap(String option) {
        if(option.equals("a")) {
            return 0;
        } else if(option.equals("b")) {
            return 1;
        } else if(option.equals("c")) {
            return 2;
        } else {
            return 3;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int status) {
        if (mListener != null) {
            mListener.onFragmentInteraction(status);
        }
    }
}
