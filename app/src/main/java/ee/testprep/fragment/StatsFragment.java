package ee.testprep.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ee.testprep.R;
import ee.testprep.db.DataBaseHelper;
import ee.testprep.db.DataBaseHelper.Database;
import ee.testprep.db.DataBaseHelper.Test;

public class StatsFragment extends Fragment {
    private static String className = StatsFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private DataBaseHelper dbHelper;

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

    private TextView practiceDataView;
    private TextView quizDataView;
    private TextView modelTestsDataView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Test> practiceData = dbHelper.getMaxQuestions(Database.QBANK);
        int answered = 0;
        int total = 0;
        for (Test test : practiceData) {
            answered += test.answeredQuestions;
            total += test.maxQuestions;
        }
        practiceDataView = (TextView) view.findViewById(R.id.stats_practice_percent_value);
        practiceDataView.setText(answered + "/" + total + " questions");

        List<Test> quizData = dbHelper.getMaxQuestions(Database.QUIZ);
        answered = 0;
        total = 0;
        for (Test test : quizData) {
            answered += test.answeredQuestions;
            total += test.maxQuestions;
        }
        quizDataView = (TextView) view.findViewById(R.id.stats_quiz_percent_value);
        quizDataView.setText(answered + "/" + total + " questions");

        List<Test> modelTestsData = dbHelper.getMaxQuestions(Database.MODELTEST);
        answered = 0;
        total = 0;
        for (Test test : modelTestsData) {
            answered += test.answeredQuestions;
            total += test.maxQuestions;
        }
        modelTestsDataView = (TextView) view.findViewById(R.id.stats_model_tests_percent_value);
        modelTestsDataView.setText(answered + "/" + total + " questions");
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
