package ee.testprep.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ee.testprep.R;
import ee.testprep.db.DataBaseHelper;
import ee.testprep.db.DataBaseHelper.Database;
import ee.testprep.db.DataBaseHelper.Test;

public class StatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String className = StatsFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private DataBaseHelper dbHelper;
    private Database database = Database.QUIZ;
    private FilterAdapter filterAdapter;
    private Button practiceButton;
    private Button quizButton;
    private Button modelTestsButton;
    private ProgressBar progressBar;
    private TextView progressDataView;

    public StatsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        dbHelper = DataBaseHelper.getInstance(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        practiceButton = view.findViewById(R.id.stats_practice_button);
        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(Database.QBANK);
            }
        });

        quizButton = view.findViewById(R.id.stats_quiz_button);
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(Database.QUIZ);
            }
        });

        modelTestsButton = view.findViewById(R.id.stats_model_tests_button);
        modelTestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(Database.MODELTEST);
            }
        });

        progressBar = view.findViewById(R.id.stats_percent);
        progressDataView = view.findViewById(R.id.stats_percent_value);

        filterAdapter = new FilterAdapter(getActivity());
        GridView gridView = view.findViewById(R.id.stats_gridview);
        gridView.setAdapter(filterAdapter);

        update(Database.QBANK);
    }

    private void update(Database database) {
        if (this.database == database) return;

        this.database = database;

        if (database == Database.QBANK) {
            practiceButton.setSelected(true);
            quizButton.setSelected(false);
            modelTestsButton.setSelected(false);
        } else if (database == Database.QUIZ) {
            practiceButton.setSelected(false);
            quizButton.setSelected(true);
            modelTestsButton.setSelected(false);
        } else if (database == Database.MODELTEST) {
            practiceButton.setSelected(false);
            quizButton.setSelected(false);
            modelTestsButton.setSelected(true);
        }

        int answered = 0;
        int total = 0;
        List<Test> practiceData = dbHelper.getTestsData(database);
        for (Test test : practiceData) {
            answered += test.answeredQuestions;
            total += test.maxQuestions;
        }
        progressBar.setProgress(progressBar.getMax() * answered / total);
        progressDataView.setText(answered + "/" + total + " questions");

        filterAdapter.update(database);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class FilterAdapter extends BaseAdapter {

        private final Context mContext;
        private List<Test> modelTestsData = new ArrayList<>();

        public FilterAdapter(Context context) {
            this.mContext = context;
        }

        protected synchronized void update(Database database) {
            modelTestsData.clear();
            modelTestsData.addAll(dbHelper.getTestsData(database));
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return modelTestsData.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return modelTestsData.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Test test = modelTestsData.get(position);
            final String filterName = test.name;

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_stats_item, null);

                final TextView titleView = convertView.findViewById(R.id.stats_item_title);
                final TextView dataView = convertView.findViewById(R.id.stats_item_data);

                final ViewHolder viewHolder = new ViewHolder(titleView, dataView);
                convertView.setTag(viewHolder);
            }

            final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.titleView.setText(filterName);
            viewHolder.dataView.setText(test.correctAnswers + "/" + test.maxQuestions);

            return convertView;
        }

        private class ViewHolder {
            private final TextView titleView;
            private final TextView dataView;

            public ViewHolder(TextView titleView, TextView dataView) {
                this.titleView = titleView;
                this.dataView = dataView;
            }
        }
    }
}
