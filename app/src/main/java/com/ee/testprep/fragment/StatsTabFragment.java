package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ee.testprep.R;
import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.db.Test;
import com.ee.testprep.db.Test.TestType;

import java.util.ArrayList;
import java.util.List;

public class StatsTabFragment extends Fragment {
    public static final String TEST_TYPE = "test_type";

    private TestType testType;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle args = getArguments();
        int testTypeIndex = args.getInt(TEST_TYPE, 0);
        testType = TestType.values()[testTypeIndex];
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_stats_tab, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.stats_tab);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StatsTabAdapter(getContext(), testType);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class StatsTabAdapter extends RecyclerView.Adapter<StatsTabAdapter.StatsViewHolder> {
        private List<Test> modelTestData = new ArrayList<>();

        public StatsTabAdapter(Context context, TestType testType) {
            DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
            modelTestData.addAll(dbHelper.getTestsData(context, testType));
        }

        @Override
        public StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gridview_stats_item, parent, false);
            StatsViewHolder vh = new StatsViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(StatsViewHolder holder, int position) {
            Test test = modelTestData.get(position);
            holder.titleView.setText(test.name);
            holder.summaryView.setText(test.correctAnswers + "/" + test.maxQuestions);
            if (test.maxQuestions != 0) {
                holder.progressBar.setProgress(
                        holder.progressBar.getMax() * test.correctAnswers / test.maxQuestions);
            }
        }

        @Override
        public int getItemCount() {
            return modelTestData.size();
        }

        public class StatsViewHolder extends RecyclerView.ViewHolder {
            private final TextView titleView;
            private final TextView summaryView;
            private final ProgressBar progressBar;

            public StatsViewHolder(View root) {
                super(root);
                titleView = root.findViewById(R.id.stats_item_title);
                progressBar = root.findViewById(R.id.stats_item_progress);
                summaryView = root.findViewById(R.id.stats_item_summary);
            }
        }
    }
}
