package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.MetaData;
import com.ee.testprep.db.Test;
import com.ee.testprep.db.UserDataViewModel;
import com.ee.testprep.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TestsListFragment extends Fragment {

    private static final String TESTS_LIST = "tests_list";
    private OnFragmentInteractionListener mListener;
    private List<MetaData> mTestsList;
    private RecyclerView recyclerView;
    private TestsListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private UserDataViewModel viewModel;

    private OnClickListener onListItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String test = (String) v.getTag();
            if (mListener != null && test != null && !test.trim().isEmpty()) {
                mListener.onFragmentInteraction(MainActivity.STATUS_QUIZ_MODELTEST_START, test);
            }
        }
    };

    public static TestsListFragment newInstance(ArrayList<MetaData> testsList) {
        TestsListFragment fragment = new TestsListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TESTS_LIST, testsList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTestsList = (List<MetaData>) getArguments().getSerializable(TESTS_LIST);
        }
        viewModel = ViewModelProviders.of(getActivity()).get(UserDataViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tests_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.tests_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TestsListAdapter();
        recyclerView.setAdapter(adapter);
        viewModel.getUserDataList().observe(getActivity(), data -> {
            adapter.setUserData(data);
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

    public class TestsListAdapter extends RecyclerView.Adapter<TestsListAdapter.TestsListItemViewHolder> {
        private HashMap<String, Test> userTestData = new HashMap<>();

        public void setUserData(List<Test> userData) {
            for (Test t : userData) {
                userTestData.put(t.testName, t);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TestsListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_tests_item, parent, false);
            TestsListItemViewHolder holder = new TestsListItemViewHolder(root);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TestsListItemViewHolder holder, int position) {
            MetaData testData = mTestsList.get(position);
            holder.titleView.setText(testData.mName.toUpperCase());
            if (testData.mSubject != null && !testData.mSubject.isEmpty()) {
                holder.subjectView.setText(Constants.getAbbreviation(testData.mSubject));
                holder.subjectView.setVisibility(View.VISIBLE);
            } else {
                holder.subjectView.setVisibility(View.GONE);
            }
            if (testData.mExam != null && !testData.mExam.isEmpty()) {
                holder.examView.setText(testData.mExam);
                holder.examView.setVisibility(View.VISIBLE);
            } else {
                holder.examView.setVisibility(View.GONE);
            }
            //holder.totalTimeView.setText(testData.mTime);
            int numQuestions = Integer.valueOf(testData.mTotalQ);
            int quizTime = Constants.getQuizTime(numQuestions);
            holder.totalTimeView.setText("Total Time: " + Constants.getTime(quizTime));
            holder.countView.setText("( " + testData.mTotalQ + " questions )");
            Log.e("TestList","TestList: ("+testData.mName+") testData = "+testData.toString());

            Test userData = userTestData.get(testData.mName);
            if (userData != null) {
                Log.e("TestList","TestList: ("+testData.mName+") userData = "+userData.toString());
                if (userData.answeredCount == numQuestions) {
                    holder.completedMarkView.setVisibility(View.VISIBLE);
                } else {
                    if (userData.timeUsed > 0) {
                        holder.leftOverTimeView.setVisibility(View.VISIBLE);
                        holder.leftOverTimeView.setText("Leftover Time: " + Constants.getTime(quizTime - userData.timeUsed));
                    }
                }
            }

            //set view's tag with quizname; it is used to query with quizname later
            holder.cardView.setTag(testData.mName);
            holder.cardView.setOnClickListener(onListItemClickListener);

            if (position % 2 == 0)
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorGreenLight));
            else
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorOrangeLight));

        }

        @Override
        public int getItemCount() {
            return mTestsList.size();
        }

        private class TestsListItemViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;
            private TextView titleView;
            private TextView subjectView;
            private TextView examView;
            private TextView totalTimeView;
            private TextView leftOverTimeView;
            private TextView countView;
            private ImageView completedMarkView;

            public TestsListItemViewHolder(View root) {
                super(root);
                cardView = root.findViewById(R.id.tests_card_view);
                titleView = root.findViewById(R.id.tests_item_title);
                subjectView = root.findViewById(R.id.tests_item_subject);
                examView = root.findViewById(R.id.tests_item_exam);
                totalTimeView = root.findViewById(R.id.tests_item_time_total);
                leftOverTimeView = root.findViewById(R.id.tests_item_time_left);
                countView = root.findViewById(R.id.tests_item_count);
                completedMarkView = root.findViewById(R.id.tests_item_mark);
            }
        }
    }
}
