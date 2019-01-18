package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.QuizMetrics;
import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionQuizBaseFragment extends android.app.Fragment {
    public static final int TIME_INSEC_PER_QUESTION = 30; //30s/question
    public static String QUIZ_NAME = "quiz_name";
    private ViewPager pager;
    private SlidePagerAdapter pagerAdapter;
    private ArrayList<DBRow> quizList;
    private QuizMetrics quiz;
    private String quizName;
    private DataBaseHelper dbHelper;
    private TextView tvTimer;
    private TextView tvProgress;
    private int mNumQuestions;
    private ProgressBar mProgressBar;

    public static QuestionQuizBaseFragment newInstance(String quizName) {
        QuestionQuizBaseFragment fragment = new QuestionQuizBaseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QUIZ_NAME, quizName);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle args = getArguments();
        quizName = args.getString(QUIZ_NAME);

        dbHelper = DataBaseHelper.getInstance(getContext());
        quizList = (ArrayList<DBRow>) dbHelper.queryQuestionsQuiz(quizName);

        quiz = new QuizMetrics(quizList, quizList.size() * TIME_INSEC_PER_QUESTION);
        quiz.startQuiz();
        uiRefresh();

        mNumQuestions = quizList.size();
        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTimer = view.findViewById(R.id.timer);
        tvTimer.setText("");

        tvProgress = view.findViewById(R.id.tv_progress);

        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setMax(mNumQuestions);

        pagerAdapter = new SlidePagerAdapter((MainActivity) getActivity());
        pager = view.findViewById(R.id.questions_sliding_pager);
        pager.setAdapter(pagerAdapter);
    }

    private void uiRefresh() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (quiz != null) {
                    uiRefresh(quiz.getRemainingTimeInSec(), quiz.getProgress());
                }
            }
        }, 0, 1000);
    }

    public void uiRefresh(final int time, final int currQIndex) {

        if (getActivity() != null) {
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

    private class SlidePagerAdapter extends FragmentPagerAdapter {
        private final Context context;
        private int currentPosition;

        public SlidePagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            context = activity;
        }

        @Override
        public Fragment getItem(int position) {
            int prevPosition = currentPosition;
            currentPosition = position;
            Fragment fragment;
            if (currentPosition < prevPosition) {
                fragment = QuestionQuizFragment.newInstance(quiz.getPrevQuestion());
            } else {
                fragment = QuestionQuizFragment.newInstance(quiz.getNextQuestion());
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mNumQuestions;
        }
    }
}
