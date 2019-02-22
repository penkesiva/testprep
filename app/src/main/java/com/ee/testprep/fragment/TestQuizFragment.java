package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.db.Test;
import com.ee.testprep.db.UserDataViewModel;
import com.ee.testprep.util.Constants;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class TestQuizFragment extends Fragment {
    private static String QUIZ_NAME = "quiz_name";

    private OnFragmentInteractionListener mListener;
    private UserDataViewModel viewModel;
    private Test userdata;
    private AlertDialog alertDialog;
    private int saveQuizStatus;
    private ViewPager pager;
    private SlidePagerAdapter pagerAdapter;
    private ArrayList<DBRow> quizList;
    private String quizName;
    private DataBaseHelper dbHelper;
    private TextView tvTimer;
    private TextView tvProgress;
    private Button submitButton;
    private int numQuestions;
    private int quizTime;
    private long remainingTimeInSec;

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
        viewModel = ViewModelProviders.of(getActivity()).get(UserDataViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(UserDataViewModel.class);
        dbHelper = DataBaseHelper.getInstance(getContext());
        quizList = (ArrayList<DBRow>) dbHelper.queryQuestionsQuiz(quizName);
        quizTime = Constants.getQuizTime(quizList.size());
        numQuestions = quizList.size();

        viewModel.getUserData().observe(getActivity(), data -> {
            userdata = data;
            if (userdata != null) {
                quizTime = Constants.getQuizTime(quizList.size()) - userdata.timeUsed;
            }
            startTimeRefresh();
        });
        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTimer = view.findViewById(R.id.timer);

        tvProgress = view.findViewById(R.id.tv_progress);

        submitButton = view.findViewById(R.id.quiz_q_submit);
        submitButton.setOnClickListener(view1 -> {
            if (mListener != null) {
                mListener.onFragmentInteraction(MainActivity.STATUS_QUIZ_MODELTEST_END, quizName);
            }
        });

        pagerAdapter = new SlidePagerAdapter(getActivity());
        pager = view.findViewById(R.id.questions_sliding_pager);
        pager.setSaveFromParentEnabled(false);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
                uiRefreshCount(position + 1);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                showExitQuizAlert();
                return true;
            }
            return false;
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

    private void startTimeRefresh() {
        new CountDownTimer(quizTime * 1000 + 1, 1000) {
            public void onTick(long millisUntilFinished) {
                remainingTimeInSec = millisUntilFinished;
                tvTimer.setText(Constants.getTime((int) (millisUntilFinished / 1000)));
            }

            public void onFinish() {
                tvTimer.setText("Done!");
            }
        }.start();
    }

    public void uiRefreshCount(final int currentQuestion) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> tvProgress.setText("( " + currentQuestion + " / " + numQuestions + " )"));
        }
    }

    private void showExitQuizAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exiting quiz " + quizName.toUpperCase());

        builder.setPositiveButton("EXIT", (dialog, id) -> {
            if (saveQuizStatus == 0) {
                viewModel.saveUserData(quizName, quizList, (int) (remainingTimeInSec / 1000),
                        false);
            }
            getFragmentManager().popBackStack();
        });

        builder.setSingleChoiceItems(R.array.quiz_exit, 0,
                (dialog, which) -> saveQuizStatus = which);

        alertDialog = builder.create();
        alertDialog.show();
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
