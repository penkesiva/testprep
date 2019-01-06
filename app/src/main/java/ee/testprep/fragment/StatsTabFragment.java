package ee.testprep.fragment;

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

import java.util.ArrayList;
import java.util.List;

import ee.testprep.R;
import ee.testprep.db.DataBaseHelper;
import ee.testprep.db.DataBaseHelper.Database;
import ee.testprep.db.DataBaseHelper.Test;

public class StatsTabFragment extends Fragment {
    public static final String DATABASE_TYPE = "database_type";

    private Database database;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle args = getArguments();
        int databaseIndex = args.getInt(DATABASE_TYPE, 0);
        database = Database.values()[databaseIndex];
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
        mAdapter = new StatsTabAdapter(getContext(), database);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class StatsTabAdapter extends RecyclerView.Adapter<StatsTabAdapter.StatsViewHolder> {
        private List<Test> modelTestData = new ArrayList<>();

        public StatsTabAdapter(Context context, Database database) {
            DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
            if (database == Database.ALL) {
                int maxQuestions = 0;
                int answeredQuestions = 0;
                int correctAnswers = 0;
                int wrongAnswers = 0;
                List<Test> testData = dbHelper.getTestsData(Database.QBANK);
                for (Test test : testData) {
                    maxQuestions += test.maxQuestions;
                    answeredQuestions += test.answeredQuestions;
                    correctAnswers += test.correctAnswers;
                    wrongAnswers += test.wrongAnswers;
                }
                modelTestData.add(new Test("Practice", maxQuestions, answeredQuestions,
                        correctAnswers, wrongAnswers));

                maxQuestions = 0;
                answeredQuestions = 0;
                correctAnswers = 0;
                wrongAnswers = 0;
                testData = dbHelper.getTestsData(Database.QUIZ);
                for (Test test : testData) {
                    maxQuestions += test.maxQuestions;
                    answeredQuestions += test.answeredQuestions;
                    correctAnswers += test.correctAnswers;
                    wrongAnswers += test.wrongAnswers;
                }
                modelTestData.add(new Test("Quiz", maxQuestions, answeredQuestions,
                        correctAnswers, wrongAnswers));

                maxQuestions = 0;
                answeredQuestions = 0;
                correctAnswers = 0;
                wrongAnswers = 0;
                testData = dbHelper.getTestsData(Database.MODELTEST);
                for (Test test : testData) {
                    maxQuestions += test.maxQuestions;
                    answeredQuestions += test.answeredQuestions;
                    correctAnswers += test.correctAnswers;
                    wrongAnswers += test.wrongAnswers;
                }
                modelTestData.add(new Test("Model Tests", maxQuestions, answeredQuestions,
                        correctAnswers, wrongAnswers));
            } else {
                modelTestData.addAll(dbHelper.getTestsData(database));
            }
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
            holder.progressBar.setProgress(holder.progressBar.getMax() * test.correctAnswers /
                    test.maxQuestions);
        }

        @Override
        public int getItemCount() {
            return modelTestData.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
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
