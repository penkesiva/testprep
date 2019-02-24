package com.ee.testprep.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
    private List<String> mSubjectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TestsListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private UserDataViewModel viewModel;
    private String selectedQuiz;
    private Spinner subjectSelector;
    private int subjectSelection;

    private OnClickListener onListItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            selectedQuiz = (String) v.getTag();
            if (mListener != null && selectedQuiz != null && !selectedQuiz.trim().isEmpty()) {
                v.findViewById(R.id.quiz_content).setAlpha(0.2f);
                v.setClickable(false); //dont make the view clickable again
                startCountDownTimer(v);
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

        mSubjectList.addAll(Constants.getSubjects());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tests_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subjectSelector = view.findViewById(R.id.tests_list_header);
        ArrayAdapter<String> subjectSelectorAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.fragment_tests_list_header, mSubjectList);
        subjectSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSelector.setAdapter(subjectSelectorAdapter);
        subjectSelector.setSelection(0);
        subjectSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectSelection = position;
                adapter.onSubjectSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

    private void startCountDownTimer(View v) {
        new CountDownTimer(2399, 800) {
            public void onTick(long millisUntilFinished) {
                int count = 1 + (int) (millisUntilFinished / 800);

                TextView timerView = v.findViewById(R.id.tests_list_counter);
                TextView timerViewShadow = v.findViewById(R.id.tests_list_counter_shadow);

                timerView.setVisibility(View.VISIBLE);
                timerView.setText("" + count);
                timerViewShadow.setVisibility(View.VISIBLE);
                timerViewShadow.setText("" + count);
            }

            public void onFinish() {
                TextView timerView = v.findViewById(R.id.tests_list_counter);
                TextView timerViewShadow = v.findViewById(R.id.tests_list_counter_shadow);

                timerView.setVisibility(View.GONE);
                timerViewShadow.setVisibility(View.GONE);

                if(mListener != null) {
                    mListener.onFragmentInteraction(MainActivity.STATUS_QUIZ_MODELTEST_START,
                            selectedQuiz);
                }

                v.setClickable(true);
            }

        }.start();
    }

    public class TestsListAdapter extends RecyclerView.Adapter<TestsListAdapter.TestsListItemViewHolder> {
        private HashMap<String, Test> userTestData = new HashMap<>();
        private List<Integer> tests = new ArrayList<>();

        public void setUserData(List<Test> userData) {
            for (Test t : userData) {
                userTestData.put(t.testName, t);
            }
            onSubjectSelected();
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
            MetaData testData = mTestsList.get(tests.get(position));
            holder.titleView.setText(testData.mName.toUpperCase());
            String text = "";
            if (testData.mExam != null && !testData.mExam.isEmpty()) {
                text = testData.mExam + " : ";
            }
            if (testData.mSubject != null && !testData.mSubject.isEmpty()) {
                text += Constants.getAbbreviation(testData.mSubject);
            }
            if (text.trim().isEmpty()) {
                holder.subjectView.setVisibility(View.GONE);
            } else {
                holder.subjectView.setText(text);
            }

            //Log.e("TLF", "TLF: (" + testData.mName + ") testData = " + testData.toString());
            int numQuestions = Integer.valueOf(testData.mTotalQ);
            int quizTime = Constants.getQuizTime(numQuestions);
            Test userData = userTestData.get(testData.mName);
            if (userData != null) {
                //Log.e("TLF", "TLF: (" + testData.mName + ") userData = " + userData.toString());
                if (userData.answeredCount == numQuestions) {
                    holder.markView.setVisibility(View.VISIBLE);
                } else if (userData.timeUsed > 0) {
                    quizTime -= userData.timeUsed;
                }
            }

            holder.timeView.setText(Constants.getTime(quizTime));

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
            return tests.size();
        }

        private void onSubjectSelected() {
            String subject = mSubjectList.get(subjectSelection);
            tests.clear();
            for (MetaData test : mTestsList) {
                if (Constants.getAbbreviation(test.mSubject).equals(subject)) {
                    tests.add(mTestsList.indexOf(test));
                }
            }
            notifyDataSetChanged();
        }

        private class TestsListItemViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;
            private TextView titleView;
            private TextView subjectView;
            private TextView timeView;
            private ImageView markView;
            private TextView countDownTimerView;
            private TextView countDownTimerViewShadow;

            public TestsListItemViewHolder(View root) {
                super(root);
                cardView = root.findViewById(R.id.tests_card_view);
                titleView = root.findViewById(R.id.tests_item_title);
                subjectView = root.findViewById(R.id.tests_item_subject);
                timeView = root.findViewById(R.id.tests_item_time);
                markView = root.findViewById(R.id.tests_item_mark);
                countDownTimerView = root.findViewById(R.id.tests_list_counter);
                countDownTimerViewShadow = root.findViewById(R.id.tests_list_counter_shadow);
                countDownTimerViewShadow.getPaint().setStrokeWidth(8);
                countDownTimerViewShadow.getPaint().setStyle(Paint.Style.STROKE);
            }
        }
    }
}
