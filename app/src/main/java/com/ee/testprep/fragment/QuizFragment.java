package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class QuizFragment extends Fragment {

    private static String TAG = QuizFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private OnFragmentInteractionListener mListener;
    private String[] mQuizList;

    public QuizFragment() {
    }

    public static QuizFragment newInstance(ArrayList<String> quizList) {
        QuizFragment fragment = new QuizFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1, quizList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuizList = ((ArrayList<String>) getArguments().getSerializable(ARG_PARAM1)).toArray(new String[0]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        GridView gridView = view.findViewById(R.id.quiz_gridview);
        final QuizFragment.QuizAdapter quizAdapter = new QuizFragment.QuizAdapter(getActivity(), mQuizList);
        gridView.setAdapter(quizAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                onButtonPressed(MainActivity.STATUS_QUIZ_XX, item);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int status, String quizItem) {
        if (mListener != null) {
            mListener.onFragmentInteraction(status, quizItem);
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

    public class QuizAdapter extends BaseAdapter {

        private final Context mContext;
        private String mFilter[];

        public QuizAdapter(Context context, String[] filter) {
            this.mContext = context;
            this.mFilter = filter;
        }

        @Override
        public int getCount() {
            return mFilter.length;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mFilter[position];
        }

        private boolean getIsLocked() {
            return false; //TODO
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String filterName = mQuizList[position];

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_quiz_item, null);

                final TextView nameTextView = convertView.findViewById(R.id.textview_quiz_num);
                final TextView fullNameTextView = convertView.findViewById(R.id.textview_subject);
                final ImageView lockImageView = convertView.findViewById(R.id.imageview_unlock);

                final QuizFragment.QuizAdapter.ViewHolder viewHolder = new QuizFragment.QuizAdapter.ViewHolder(nameTextView, fullNameTextView, lockImageView);
                convertView.setTag(viewHolder);
            }

            final QuizFragment.QuizAdapter.ViewHolder viewHolder = (QuizFragment.QuizAdapter.ViewHolder)convertView.getTag();
            viewHolder.nameTextView.setText(filterName.toUpperCase());
            viewHolder.fullNameTextView.setText("TBD");
            viewHolder.lockImageView.setImageResource(getIsLocked() ? R.drawable.lock : R.drawable.unlock);

            return convertView;
        }

        private class ViewHolder {
            private final TextView nameTextView;
            private final TextView fullNameTextView;
            private final ImageView lockImageView;

            public ViewHolder(TextView nameTextView, TextView fullName, ImageView lockImageView) {
                this.nameTextView = nameTextView;
                this.fullNameTextView = fullName;
                this.lockImageView = lockImageView;
            }
        }
    }

}
