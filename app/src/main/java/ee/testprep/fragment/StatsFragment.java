package ee.testprep.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView practiceDataView;
    private TextView quizDataView;
    private TextView modelTestsDataView;

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

        List<Test> practiceData = dbHelper.getTestsData(Database.QBANK);
        int answered = 0;
        int total = 0;
        for (Test test : practiceData) {
            answered += test.answeredQuestions;
            total += test.maxQuestions;
        }
        practiceDataView = (TextView) view.findViewById(R.id.stats_practice_percent_value);
        practiceDataView.setText(answered + "/" + total + " questions");

        GridView gridView1 = view.findViewById(R.id.stats_practice_gridview);
        final FilterAdapter filterAdapter1 = new FilterAdapter(getActivity(), Database.QBANK);
        gridView1.setAdapter(filterAdapter1);

        List<Test> quizData = dbHelper.getTestsData(Database.QUIZ);
        answered = 0;
        total = 0;
        for (Test test : quizData) {
            answered += test.answeredQuestions;
            total += test.maxQuestions;
        }
        quizDataView = (TextView) view.findViewById(R.id.stats_quiz_percent_value);
        quizDataView.setText(answered + "/" + total + " questions");

        GridView gridView2 = view.findViewById(R.id.stats_quiz_gridview);
        final FilterAdapter filterAdapter2 = new FilterAdapter(getActivity(), Database.QUIZ);
        gridView2.setAdapter(filterAdapter2);

        List<Test> modelTestsData = dbHelper.getTestsData(Database.MODELTEST);
        answered = 0;
        total = 0;
        for (Test test : modelTestsData) {
            answered += test.answeredQuestions;
            total += test.maxQuestions;
        }
        modelTestsDataView = (TextView) view.findViewById(R.id.stats_model_tests_percent_value);
        modelTestsDataView.setText(answered + "/" + total + " questions");

        GridView gridView3 = view.findViewById(R.id.stats_model_tests_gridview);
        final FilterAdapter filterAdapter3 = new FilterAdapter(getActivity(), Database.MODELTEST);
        gridView3.setAdapter(filterAdapter3);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class FilterAdapter extends BaseAdapter {

        private final Context mContext;
        private List<Test> modelTestsData;

        public FilterAdapter(Context context, Database database) {
            this.mContext = context;
            this.modelTestsData = dbHelper.getTestsData(database);
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

        private boolean getIsLocked() {
            return false; //TODO
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String filterName = modelTestsData.get(position).name;

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_year_item, null);

                final TextView nameTextView = convertView.findViewById(R.id.textview_filter_name);
                final ImageView lockImageView = convertView.findViewById(R.id.imageview_unlock);

                final ViewHolder viewHolder = new ViewHolder(nameTextView, lockImageView);
                convertView.setTag(viewHolder);
            }

            final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.nameTextView.setText(filterName);
            viewHolder.lockImageView.setImageResource(getIsLocked() ? R.drawable.lock : R
                    .drawable.unlock);

            return convertView;
        }

        private class ViewHolder {
            private final TextView nameTextView;
            private final ImageView lockImageView;

            public ViewHolder(TextView nameTextView, ImageView lockImageView) {
                this.nameTextView = nameTextView;
                this.lockImageView = lockImageView;
            }
        }
    }
}
