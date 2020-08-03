package com.ee.testprep.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.db.Test;
import com.ee.testprep.db.UserDataViewModel;
import com.ee.testprep.util.Constants;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class TestQuizFragment extends Fragment {
    private static String QUIZ_NAME = "quiz_name";
    private Context mContext;

    private OnFragmentInteractionListener mListener;
    private UserDataViewModel viewModel;
    private Test userdata;
    private AlertDialog alertDialog;
    private ViewPager pager;
    private SlidePagerAdapter pagerAdapter;
    private ArrayList<DBRow> quizList;
    private String quizName;
    private DataBaseHelper dbHelper;
    private TextView tvTimer;
    private TextView tvProgress;
    private ImageView submitButton;
    private ImageView pauseButton;
    private int numQuestions;
    private long quizTime;
    private CountDownTimer countDownTimer;
    private int currentQuestionPosition;
    private int pausedQuestionPosition = -1;

    public static TestQuizFragment newInstance(String quizName) {
        TestQuizFragment fragment = new TestQuizFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QUIZ_NAME, quizName);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quizName = getArguments().getString(QUIZ_NAME);
        }
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(UserDataViewModel.class);
        dbHelper = DataBaseHelper.getInstance(getContext());
        quizList = (ArrayList<DBRow>) dbHelper.queryQuestionsQuiz(quizName);
        numQuestions = quizList.size();
        quizTime = Constants.getQuizTime(numQuestions);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTimer = view.findViewById(R.id.quiz_q_timer);
        tvProgress = view.findViewById(R.id.quiz_q_progress);
        submitButton = view.findViewById(R.id.quiz_q_submit);
        submitButton.setOnClickListener(view1 -> {
            if (mListener != null) {
                alertSubmission();
            }
        });

        pauseButton = view.findViewById(R.id.quiz_q_pause);
        pauseButton.setOnClickListener(view1 -> {
            if (countDownTimer != null) {
                pauseButton.setImageResource(R.drawable.play);
                pauseQuiz();
            } else {
                pauseButton.setImageResource(R.drawable.pause);
                playQuiz();
            }
        });

        pagerAdapter = new SlidePagerAdapter(getActivity());
        pager = view.findViewById(R.id.quiz_q_pager);
        pager.setSaveFromParentEnabled(false);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
                currentQuestionPosition = position;
                if (pausedQuestionPosition != -1) {
                    playQuiz();
                }
                uiRefreshCount(position + 1);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewModel.getUserData().observe(getActivity(), data -> {
            userdata = data;
            if (userdata != null) {
                if (quizTime != userdata.timeUsed) {
                    quizTime -= userdata.timeUsed;
                }
            }
            startTimeRefresh();
        });
    }

    private void pauseQuiz() {
        //submitButton.setClickable(false);
        //submitButton.setAlpha(0.75f);
        countDownTimer.cancel();
        countDownTimer = null;
        pausedQuestionPosition = currentQuestionPosition;
    }

    private void playQuiz() {
        //submitButton.setClickable(true);
        //submitButton.setAlpha(1);
        startTimeRefresh();
        pausedQuestionPosition = -1;
    }

    @Override
    public void onPause() {
        viewModel.saveUserData(quizName, quizList, (int) quizTime, false);
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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

        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startTimeRefresh() {
        long remainingTimeInSec = quizTime * 1000;
        countDownTimer = new CountDownTimer(remainingTimeInSec, 1000) {
            public void onTick(long millisUntilFinished) {
                quizTime = millisUntilFinished / 1000;
                tvTimer.setText(Constants.getTime((int) quizTime));
            }

            public void onFinish() {
                if(quizTime == 0) {
                    alertTimeOut(); // show dialog only when there is no time left
                }
            }
        }.start();
    }

    public void uiRefreshCount(final int currentQuestion) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> tvProgress.setText(String.format("%d/%d", currentQuestion, numQuestions)));
        }
    }

    private void alertSubmission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to submit the Quiz?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onFragmentInteraction(MainActivity.STATUS_QUIZ_MODELTEST_END, quizName);
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    private void alertTimeOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Timeout! Tap continue to see Quiz results");
        builder.setCancelable(false);
        builder.setPositiveButton(
                "View-Results",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mListener != null)
                            mListener.onFragmentInteraction(MainActivity.STATUS_QUIZ_MODELTEST_END, quizName);
                    }
                });
        builder.create().show();
    }

    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
        public SlidePagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionQuizFragment.newInstance(quizName, quizList.get(position));
        }

        @Override
        public int getCount() {
            return numQuestions;
        }
    }
}
