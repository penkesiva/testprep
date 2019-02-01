package com.ee.testprep.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DBRow;
import com.ee.testprep.db.PracticeViewModel;
import com.ee.testprep.db.PracticeViewModel.PracticeType;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class TestPracticeFragment extends Fragment {
    public static String PRACTICE_CATEGORY = "practice_type";
    public static String PRACTICE_SUB_CATEGORY = "practice_sub_type";
    private MainActivity mainActivity;
    private PracticeViewModel model;
    private ViewPager pager;
    private PracticePagerAdapter pagerAdapter;
    private PracticeType category;
    private String subCategory;

    public static TestPracticeFragment newInstance(PracticeType category, String subCategory) {
        TestPracticeFragment fragment = new TestPracticeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PRACTICE_SUB_CATEGORY, subCategory);
        bundle.putInt(PRACTICE_CATEGORY, category.ordinal());
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
        category = PracticeType.values()[args.getInt(PRACTICE_CATEGORY, 0)];
        subCategory = args.getString(PRACTICE_SUB_CATEGORY);

        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.progress_header).setVisibility(View.GONE);

        pagerAdapter = new PracticePagerAdapter(mainActivity);
        pager = view.findViewById(R.id.questions_sliding_pager);
        pager.setSaveFromParentEnabled(false);
        pager.setAdapter(pagerAdapter);

        model.getQuestions().observe(mainActivity, data -> {
            if (mainActivity != null) pagerAdapter.addQuestions(data);
        });
        view.post(new Runnable() {
            @Override
            public void run() {
                model.setPracticeType(category, subCategory);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                getFragmentManager().popBackStack();
                return true;
            }
            return false;
        });
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
