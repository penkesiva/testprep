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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ee.testprep.L;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.util.SimpleVibaration;

public class QuestionPracticeFragment extends Fragment {

    private static String TAG = QuestionPracticeFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private DBRow mQuestion;
    private OnFragmentInteractionListener mListener;
    private RadioButton radioButtons[] = new RadioButton[4];
    private String recordedAnswer;
    private ImageView iv_fav;
    private DataBaseHelper dbHelper;

    public QuestionPracticeFragment() {
    }

    public static QuestionPracticeFragment newInstance(DBRow question) {
        QuestionPracticeFragment fragment = new QuestionPracticeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1, question);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestion = (DBRow) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.practice_question, container, false);

        TextView tvQuestion = view.findViewById(R.id.question);
        tvQuestion.append(mQuestion.question.trim());
        tvQuestion.setMovementMethod(new ScrollingMovementMethod());

        dbHelper = DataBaseHelper.getInstance(getActivity());

        iv_fav = view.findViewById(R.id.fav);
        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_fav.setActivated(!iv_fav.isActivated());

                //if activate - update user-status
                if (iv_fav.isActivated()) {
                    dbHelper.setUserStatus(mQuestion.qNo, true);
                } else {
                    dbHelper.setUserStatus(mQuestion.qNo, false);
                }
            }
        });

        if(mQuestion.userstatus.equals("Z")) {
            iv_fav.setActivated(true);
        }

        final RadioButton tvOptA = view.findViewById(R.id.rb_optA);
        tvOptA.setText(mQuestion.optionA.trim());
        radioButtons[0] = tvOptA;

        tvOptA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestion.answer.toLowerCase().equals("a")) {
                    tvOptA.setBackground(getResources().getDrawable(R.drawable.rectangle_green));
                } else {
                    tvOptA.setBackground(getResources().getDrawable(R.drawable.rectangle_red));

                    new SimpleVibaration(getActivity().getApplicationContext());

                    //get the right answer
                    String ans = mQuestion.answer.toLowerCase();
                    int i = getIndexMap(ans);
                    radioButtons[i].setBackground(getResources().getDrawable(R.drawable.rectangle_green));
                }
            }
        });

        final RadioButton tvOptB = view.findViewById(R.id.rb_optB);
        tvOptB.setText(mQuestion.optionB.trim());
        radioButtons[1] = tvOptB;
        tvOptB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestion.answer.toLowerCase().equals("b")) {
                    tvOptB.setBackground(getResources().getDrawable(R.drawable.rectangle_green));
                } else {
                    tvOptB.setBackground(getResources().getDrawable(R.drawable.rectangle_red));

                    new SimpleVibaration(getActivity().getApplicationContext());

                    //get the right answer
                    String ans = mQuestion.answer.toLowerCase();
                    int i = getIndexMap(ans);
                    radioButtons[i].setBackground(getResources().getDrawable(R.drawable.rectangle_green));
                }
            }
        });

        final RadioButton tvOptC = view.findViewById(R.id.rb_optC);
        tvOptC.setText(mQuestion.optionC.trim());
        radioButtons[2] = tvOptC;
        tvOptC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestion.answer.toLowerCase().equals("c")) {
                    tvOptC.setBackground(getResources().getDrawable(R.drawable.rectangle_green));
                } else {
                    tvOptC.setBackground(getResources().getDrawable(R.drawable.rectangle_red));

                    new SimpleVibaration(getActivity().getApplicationContext());

                    //get the right answer
                    String ans = mQuestion.answer.toLowerCase();
                    int i = getIndexMap(ans);
                    radioButtons[i].setBackground(getResources().getDrawable(R.drawable.rectangle_green));
                }
            }
        });

        final RadioButton tvOptD = view.findViewById(R.id.rb_optD);
        tvOptD.setText(mQuestion.optionD.trim());
        radioButtons[3] = tvOptD;
        tvOptD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestion.answer.toLowerCase().equals("d")) {
                    tvOptD.setBackground(getResources().getDrawable(R.drawable.rectangle_green));
                } else {
                    tvOptD.setBackground(getResources().getDrawable(R.drawable.rectangle_red));

                    new SimpleVibaration(getActivity().getApplicationContext());

                    //get the right answer
                    String ans = mQuestion.answer.toLowerCase();
                    int i = getIndexMap(ans);
                    radioButtons[i].setBackground(getResources().getDrawable(R.drawable.rectangle_green));
                }
            }
        });

        Button btnNext = view.findViewById(R.id.nextButton);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(MainActivity.STATUS_PRACTICE_NEXT);
            }
        });

        Button btnPrev = view.findViewById(R.id.previousButton);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(MainActivity.STATUS_PRACTICE_PREVIOUS);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });

        // populate the question
        return view;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int status) {
        if (mListener != null) {
            mListener.onFragmentInteraction(status);
        }
    }
}
