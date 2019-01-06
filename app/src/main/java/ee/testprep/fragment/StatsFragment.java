package ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ee.testprep.MainActivity;
import ee.testprep.R;
import ee.testprep.db.DataBaseHelper;
import ee.testprep.db.DataBaseHelper.Database;
import ee.testprep.db.DataBaseHelper.Test;

public class StatsFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String className = StatsFragment.class.getSimpleName();
    TabLayout tabLayout;
    private OnFragmentInteractionListener mListener;
//    private DataBaseHelper dbHelper;
//    private Database database = Database.QUIZ;
//    private FilterAdapter filterAdapter;
//    private Button practiceButton;
//    private Button quizButton;
//    private Button modelTestsButton;
//    private ProgressBar progressBar;
//    private TextView progressDataView;
    private ViewPager pager;
    //private PagerAdapter pagerAdapter;
    private SlidePagerAdapter pagerAdapter;

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
        //dbHelper = DataBaseHelper.getInstance(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pagerAdapter = new SlidePagerAdapter((MainActivity) getActivity());
        pager = view.findViewById(R.id.stats_sliding_pager);
        pager.setAdapter(pagerAdapter);

        tabLayout = view.findViewById(R.id.stats_sliding_tabs);
        tabLayout.setupWithViewPager(pager);

//        practiceButton = view.findViewById(R.id.stats_practice_button);
//        practiceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                update(Database.QBANK);
//            }
//        });
//
//        quizButton = view.findViewById(R.id.stats_quiz_button);
//        quizButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                update(Database.QUIZ);
//            }
//        });
//
//        modelTestsButton = view.findViewById(R.id.stats_model_tests_button);
//        modelTestsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                update(Database.MODELTEST);
//            }
//        });
//
//        progressBar = view.findViewById(R.id.stats_percent);
//        progressDataView = view.findViewById(R.id.stats_percent_value);
//
//        filterAdapter = new FilterAdapter(getActivity());
//        GridView gridView = view.findViewById(R.id.stats_gridview);
//        gridView.setAdapter(filterAdapter);
//
//        update(Database.QBANK);
    }

//    private void update(Database database) {
//        if (this.database == database) return;
//
//        this.database = database;
//
//        if (database == Database.QBANK) {
//            practiceButton.setSelected(true);
//            quizButton.setSelected(false);
//            modelTestsButton.setSelected(false);
//        } else if (database == Database.QUIZ) {
//            practiceButton.setSelected(false);
//            quizButton.setSelected(true);
//            modelTestsButton.setSelected(false);
//        } else if (database == Database.MODELTEST) {
//            practiceButton.setSelected(false);
//            quizButton.setSelected(false);
//            modelTestsButton.setSelected(true);
//        }
//
//        int answered = 0;
//        int total = 0;
//        List<Test> practiceData = dbHelper.getTestsData(database);
//        for (Test test : practiceData) {
//            answered += test.answeredQuestions;
//            total += test.maxQuestions;
//        }
//        progressBar.setProgress(progressBar.getMax() * answered / total);
//        progressDataView.setText(answered + "/" + total + " questions");
//
//        filterAdapter.update(database);
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    static public class SlideFragment extends Fragment {
//        public static final String DATABASE_TYPE = "database_type";
//        private int databaseType;
//        private RecyclerView mRecyclerView;
//        private RecyclerView.Adapter mAdapter;
//        private RecyclerView.LayoutManager mLayoutManager;
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            Bundle args = getArguments();
//            databaseType = args.getInt(DATABASE_TYPE, 0);
//            ViewGroup rootView = (ViewGroup) inflater.inflate(
//                    R.layout.fragment_stats_tab, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//            super.onViewCreated(view, savedInstanceState);
//
//            mRecyclerView = view.findViewById(R.id.stats_tab);
//            mRecyclerView.setHasFixedSize(true);
//            mLayoutManager = new LinearLayoutManager(getContext());
//            mRecyclerView.setLayoutManager(mLayoutManager);
//            mAdapter = new MyAdapter(myDataset);
//            mRecyclerView.setAdapter(mAdapter);
//        }
//    }

//    private class FilterAdapter extends BaseAdapter {
//
//        private final Context mContext;
//        private List<Test> modelTestsData = new ArrayList<>();
//
//        public FilterAdapter(Context context) {
//            this.mContext = context;
//        }
//
//        protected synchronized void update(Database database) {
//            modelTestsData.clear();
//            modelTestsData.addAll(dbHelper.getTestsData(database));
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return modelTestsData.size();
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return modelTestsData.get(position);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            Test test = modelTestsData.get(position);
//            final String filterName = test.name;
//
//            if (convertView == null) {
//                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//                convertView = layoutInflater.inflate(R.layout.gridview_stats_item, null);
//
//                final TextView titleView = convertView.findViewById(R.id.stats_item_title);
//                final ProgressBar progressBar = convertView.findViewById(R.id.stats_item_progress);
//                final TextView summaryView = convertView.findViewById(R.id.stats_item_summary);
//
//                final ViewHolder viewHolder = new ViewHolder(titleView, summaryView,progressBar);
//                convertView.setTag(viewHolder);
//            }
//
//            final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//            viewHolder.titleView.setText(filterName);
//            viewHolder.summaryView.setText(test.correctAnswers + "/" + test.maxQuestions);
//
//            return convertView;
//        }
//
//        private class ViewHolder {
//            private final TextView titleView;
//            private final TextView summaryView;
//            private final ProgressBar progressBar;
//
//            public ViewHolder(TextView titleView, TextView summaryView, ProgressBar progressBar) {
//                this.titleView = titleView;
//                this.summaryView=summaryView;
//                this.progressBar = progressBar;
//            }
//        }
//    }

    private class SlidePagerAdapter extends FragmentPagerAdapter {
        List<Database> databaseList = new ArrayList<>();

        public SlidePagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());

            // List of tabs in the same order of display
            databaseList.add(Database.ALL);
            databaseList.add(Database.QBANK);
            databaseList.add(Database.QUIZ);
            databaseList.add(Database.MODELTEST);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new StatsTabFragment();
            Bundle args = new Bundle();
            args.putInt(StatsTabFragment.DATABASE_TYPE, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return databaseList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (databaseList.get(position) == Database.ALL) {
                return "Summary";
            } else if (databaseList.get(position) == Database.QBANK) {
                return "Practice";
            } else if (databaseList.get(position) == Database.QUIZ) {
                return "Quiz";
            } else if (databaseList.get(position) == Database.MODELTEST) {
                return "Model Tests";
            }
            return "";
        }
    }
}
