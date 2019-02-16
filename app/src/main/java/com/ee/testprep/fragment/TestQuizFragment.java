package com.ee.testprep.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ee.testprep.QuizMetrics;
import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.db.Test;
import com.ee.testprep.db.UserDataViewModel;
import com.ee.testprep.util.Constants;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class TestQuizFragment extends Fragment {
    private static String QUIZ_NAME = "quiz_name";

    private UserDataViewModel viewModel;
    private Test userdata;
    private AlertDialog alertDialog;
    private int saveQuizStatus;
    private ViewPager pager;
    private SlidePagerAdapter pagerAdapter;
    private ArrayList<DBRow> quizList;
    private QuizMetrics quiz;
    private String quizName;
    private DataBaseHelper dbHelper;
    private TextView tvTimer;
    private TextView tvProgress;
    //private ProgressBar progressBar;
    private int numQuestions;
    private View startDisplay;
    private TextView startTitle;
    private TextView startSubject;
    private TextView startTime;
    private TextView startCounter;
    private Button startButton;
    private int counter = 5;
    private Timer countDownTimer;
    private int quizTime;
    private boolean quizStarted;

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
        //userdata = TestDatabase.getDatabase(getContext()).testDao().findByName(quizName);
        dbHelper = DataBaseHelper.getInstance(getContext());
        quizList = (ArrayList<DBRow>) dbHelper.queryQuestionsQuiz(quizName);
        quizTime = Constants.getQuizTime(quizList.size());
        quiz = new QuizMetrics(quizList, quizTime);
        numQuestions = quizList.size();

        viewModel.getUserData().observe(getActivity(), data -> {
            userdata = data;
            if (userdata != null) {
                quizTime = Constants.getQuizTime(quizList.size()) - userdata.timeUsed;
                quiz.updateAllotedTime(quizTime);
            }
        });
        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startCounter = view.findViewById(R.id.quiz_q_counter);
        startDisplay = view.findViewById(R.id.quiz_q_root);
        startTitle = view.findViewById(R.id.quiz_q_title);
        startSubject = view.findViewById(R.id.quiz_q_subject);
        startTime = view.findViewById(R.id.quiz_q_time);

        startTitle.setText("Starting quiz: " + quizName.toUpperCase());
        startSubject.setText("Subject: " + quizList.get(0).subject.toUpperCase());
        startTime.setText("Time limit: " + Constants.getTime(quizTime));

        startButton = view.findViewById(R.id.quiz_q_start);
        startButton.setOnClickListener(v -> {
            if (startButton.getText().equals("SKIP")) {
                startTimeRefresh();
                return;
            }
            startButton.setText("SKIP");
            counter = 5;
            startCounter.setVisibility(View.VISIBLE);
            countDownTimer = new Timer();
            countDownTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() == null) return;
                    if (counter <= 0) {
                        startTimeRefresh();
                    } else {
                        getActivity().runOnUiThread(() -> startCounter.setText("" + counter));
                        counter--;
                    }
                }
            }, 0, 1000);
        });

        tvTimer = view.findViewById(R.id.timer);

        tvProgress = view.findViewById(R.id.tv_progress);

        //progressBar = view.findViewById(R.id.progressBar);
        //progressBar.setMax(numQuestions);

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
                if (startDisplay.getVisibility() == View.VISIBLE) {
                    getFragmentManager().popBackStack();
                    return true;
                } else {
                    showExitQuizAlert();
                }
                return true;
            }
            return false;
        });
    }

    private void startTimeRefresh() {
        countDownTimer.cancel();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (quiz != null && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (!quizStarted) {
                            startDisplay.setVisibility(View.GONE);
                            quizStarted = true;
                            quiz.startQuiz();
                        }
                        tvTimer.setText(Constants.getTime(quiz.getRemainingTimeInSec() + 1));
                    });
                }
            }
        }, 0, 1000);
    }

    public void uiRefreshCount(final int currentQuestion) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //progressBar.setProgress(currentQuestion);
                    tvProgress.setText("( " + currentQuestion + " / " + numQuestions + " )");
                }
            });
        }
    }

    private void showExitQuizAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exiting quiz " + quizName.toUpperCase());

        builder.setPositiveButton("EXIT", (dialog, id) -> {
            if (saveQuizStatus == 0) {
                viewModel.saveUserData(quizName, quizList, quiz.getRemainingTimeInSec());
            }
            getFragmentManager().popBackStack();
        });

        builder.setSingleChoiceItems(R.array.quiz_exit, 1,
                (dialog, which) -> saveQuizStatus = which);

        alertDialog = builder.create();
        alertDialog.show();
    }

    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
        private int currentPosition;

        public SlidePagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            int prevPosition = currentPosition;
            currentPosition = position;
            Fragment fragment;
            boolean isLastQuestion = (position == numQuestions - 1);
            if (currentPosition < prevPosition) {
                fragment = QuestionQuizFragment.newInstance(quizName, quiz.getPrevQuestion(),
                        isLastQuestion);
            } else {
                fragment = QuestionQuizFragment.newInstance(quizName, quiz.getNextQuestion(),
                        isLastQuestion);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return numQuestions;
        }
    }
}
