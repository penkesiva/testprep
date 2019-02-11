package com.ee.testprep.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.PracticeViewModel;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class TestPracticeFragment extends Fragment {
    public static String PRACTICE_QUERY = "practice_query";
    private MainActivity mainActivity;
    private PracticeViewModel model;
    private ViewPager pager;
    private PracticePagerAdapter pagerAdapter;
    private String mQuery;

    public static TestPracticeFragment newInstance(String query) {
        TestPracticeFragment fragment = new TestPracticeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PRACTICE_QUERY, query);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        model = ViewModelProviders.of(mainActivity).get(PracticeViewModel.class);
        model.clearData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle args = getArguments();
        mQuery = args.getString(PRACTICE_QUERY);

        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.progress_header).setVisibility(View.GONE);
        view.findViewById(R.id.quiz_q_root).setVisibility(View.GONE);

        pagerAdapter = new PracticePagerAdapter(mainActivity);
        pager = view.findViewById(R.id.questions_sliding_pager);
        pager.setSaveFromParentEnabled(false);
        pager.setAdapter(pagerAdapter);

        model.getQuestions().observe(mainActivity, data -> {
            if (mainActivity != null) pagerAdapter.addQuestions(data);
        });
        view.post(() -> model.practiceQuery(mQuery));
    }

    private class PracticePagerAdapter extends FragmentStatePagerAdapter {
        private List<DBRow> questions;

        public PracticePagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
        }

        private void addQuestions(List<DBRow> questionsList) {
            questions = questionsList;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = QuestionPracticeFragment.newInstance(questions.get(position),
                    (position == questions.size() - 1));
            return fragment;
        }

        @Override
        public int getCount() {
            return questions == null ? 0 : questions.size();
        }
    }
}
