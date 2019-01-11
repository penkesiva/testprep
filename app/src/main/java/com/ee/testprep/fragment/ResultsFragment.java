package com.ee.testprep.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;

public class ResultsFragment extends Fragment {

    private static String TAG = ResultsFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private OnFragmentInteractionListener mListener;
    private ArrayList<DBRow> mAnswerKey = new ArrayList<>();

    public ResultsFragment() {
    }

    public static ResultsFragment newInstance(ArrayList<DBRow> rows) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, rows);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAnswerKey = ((ArrayList<DBRow>) getArguments().getSerializable(ARG_PARAM1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_results, container, false);
        ListView listView = view.findViewById(R.id.results_listview);
        final FilterAdapter filterAdapter = new FilterAdapter(getActivity(), mAnswerKey);
        listView.setAdapter(filterAdapter);

        return view;
    }

    public void onButtonPressed(int status) {
        if (mListener != null) {
            mListener.onFragmentInteraction(status);
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
                final TextView correctAnswer = convertView.findViewById(R.id.answeroption);
                final TextView userAnswer = convertView.findViewById(R.id.useranswer);
                final TextView explanation = convertView.findViewById(R.id.explanation);

                final ViewHolder viewHolder = new ViewHolder(question, questionImage, correctAnswer, userAnswer, explanation);
                convertView.setTag(viewHolder);
            }

            final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.question.setText(row.question);
            viewHolder.correctAnswer.setText(row.answer);
            viewHolder.userAnswer.setText(row.userstatus);
            viewHolder.explanation.setText(""); //TODO

            return convertView;
        }

        private class ViewHolder {
            private final TextView question;
            private final ImageView questionImage;
            private final TextView correctAnswer;
            private final TextView userAnswer;
            private final TextView explanation;

            public ViewHolder(TextView question, ImageView questionImage, TextView correctAnswer, TextView userAnswer, TextView explanation) {
                this.question = question;
                this.questionImage = questionImage;
                this.correctAnswer = correctAnswer;
                this.userAnswer = userAnswer;
                this.explanation = explanation;
            }
        }
    }

}
