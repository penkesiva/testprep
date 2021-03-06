package com.ee.testprep.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.db.UserDataViewModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ResultsFragment extends Fragment {

    private static final String ARG_QUIZ_NAME = "quiz_name";
    private static String TAG = ResultsFragment.class.getSimpleName();
    private ArrayList<DBRow> mAnswerKey = new ArrayList<>();
    private String unAttempted = "You didn't attempt this question!";
    private TextView tvScore;
    private int mUserScore = 0;
    private UserDataViewModel viewModel;
    private String quizName;

    public ResultsFragment() {
    }

    public static ResultsFragment newInstance(String quizName) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUIZ_NAME, quizName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quizName = getArguments().getString(ARG_QUIZ_NAME);
            DataBaseHelper dbHelper = DataBaseHelper.getInstance(getContext());
            mAnswerKey = (ArrayList<DBRow>) dbHelper.queryQuestionsQuiz(quizName);
            viewModel = ViewModelProviders.of(getActivity()).get(UserDataViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_results, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.results_listview);
        final FilterAdapter filterAdapter = new FilterAdapter(getActivity(), mAnswerKey);
        listView.setAdapter(filterAdapter);

        tvScore = view.findViewById(R.id.score);
        tvScore.setText(Integer.toString(getUserScore()) + " / " + mAnswerKey.size());
        viewModel.saveUserData(quizName, mAnswerKey, 0, true);
    }

    private int getUserScore() {
        for (int i = 0; i < mAnswerKey.size(); i++) {
            if (mAnswerKey.get(i).answer.equals(mAnswerKey.get(i).userstatus)) {
                ++mUserScore;
            }
        }

        return mUserScore;
    }

    public class FilterAdapter extends BaseAdapter {

        private final Context mContext;
        private ArrayList<DBRow> mAnswerKey;

        private FilterAdapter(Context context, ArrayList<DBRow> answerKey) {
            this.mContext = context;
            this.mAnswerKey = answerKey;
        }

        @Override
        public int getCount() {
            return mAnswerKey.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            DBRow row = mAnswerKey.get(position);

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.listview_result_item, null);

                final TextView question = convertView.findViewById(R.id.question);
                final ImageView questionImage = convertView.findViewById(R.id.ques_img);
                final TextView correctAnswer = convertView.findViewById(R.id.correct_answer);
                final TextView correctOption = convertView.findViewById(R.id.option_detail);
                final TextView userAnswer = convertView.findViewById(R.id.user_answer);
                final TextView userOption = convertView.findViewById(R.id.user_option_detail);
                final ImageView validateImage = convertView.findViewById(R.id.validate_image);
                final TextView explanation = convertView.findViewById(R.id.explanation);

                final ViewHolder viewHolder = new ViewHolder(question, questionImage,
                        correctAnswer, correctOption, userAnswer, userOption, validateImage,
                        explanation);
                convertView.setTag(viewHolder);
            }

            final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.question.setText("[" + row.qNo + "] " +row.question);
            viewHolder.correctAnswer.setText(row.answer);
            viewHolder.correctOption.setText(getCorrectOption(row));
            viewHolder.userAnswer.setText(row.userstatus);
            viewHolder.userOption.setText(getUserOption(row));
            viewHolder.explanation.setText(""); //TODO

            View resultView = convertView.findViewById(R.id.result_view);
            if (mAnswerKey.get(position).answer.trim().equals(mAnswerKey.get(position).userstatus.trim())) {
                resultView.setBackgroundResource(R.color.colorGreenLight);
            } else {
                resultView.setBackgroundResource(R.color.colorRed2);
            }

            return convertView;
        }

        private String getCorrectOption(DBRow row) {
            if (row.answer.equals("A")) return row.optionA;
            if (row.answer.equals("B")) return row.optionB;
            if (row.answer.equals("C")) return row.optionC;
            if (row.answer.equals("D")) return row.optionD;

            return unAttempted;
        }

        private String getUserOption(DBRow row) {
            if (row.userstatus.equals("A")) return row.optionA;
            if (row.userstatus.equals("B")) return row.optionB;
            if (row.userstatus.equals("C")) return row.optionC;
            if (row.userstatus.equals("D")) return row.optionD;

            return unAttempted;
        }

        private class ViewHolder {
            private final TextView question;
            private final ImageView questionImage;
            private final TextView correctAnswer;
            private final TextView correctOption;
            private final TextView userAnswer;
            private final TextView userOption;
            private final ImageView validateImage;
            private final TextView explanation;

            public ViewHolder(TextView question, ImageView questionImage, TextView correctAnswer,
                    TextView correctOption, TextView userAnswer, TextView userOption,
                    ImageView validateImage, TextView explanation) {
                this.question = question;
                this.questionImage = questionImage;
                this.correctAnswer = correctAnswer;
                this.correctOption = correctOption;
                this.userAnswer = userAnswer;
                this.userOption = userOption;
                this.validateImage = validateImage;
                this.explanation = explanation;
            }
        }
    }

}
