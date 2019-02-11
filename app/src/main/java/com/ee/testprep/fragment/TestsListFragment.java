package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.MetaData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TestsListFragment extends Fragment {

    private static final String TESTS_LIST = "tests_list";
    private OnFragmentInteractionListener mListener;
    private List<MetaData> mTestsList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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
            holder.titleView.setText(mTestsList.get(position).mName.toUpperCase());
            holder.subjectView.setText(mTestsList.get(position).mSubject);
            //holder.totalTimeView.setText(mTestsList.get(position).mTime);
            holder.totalTimeView.setText("Time: " + "2h 10m 30s");
            holder.countView.setText("( " + mTestsList.get(position).mTotalQ + " questions )");

            //set view's tag with quizname; it is used to query with quizname later
            holder.cardView.setTag(mTestsList.get(position).mName);
            holder.cardView.setOnClickListener(onListItemClickListener);
        }

        @Override
        public int getItemCount() {
            return mTestsList.size();
        }

        private class TestsListItemViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;
            private TextView titleView;
            private TextView subjectView;
            private TextView totalTimeView;
            private TextView leftOverTimeView;
            private TextView countView;
            private ImageView completedMarkView;

            public TestsListItemViewHolder(View root) {
                super(root);
                cardView = root.findViewById(R.id.tests_card_view);
                titleView = root.findViewById(R.id.tests_item_title);
                subjectView = root.findViewById(R.id.tests_item_subject);
                totalTimeView = root.findViewById(R.id.tests_item_time_total);
                leftOverTimeView = root.findViewById(R.id.tests_item_time_left);
                countView = root.findViewById(R.id.tests_item_count);
                completedMarkView = root.findViewById(R.id.tests_item_mark);
            }
        }
    }
}
