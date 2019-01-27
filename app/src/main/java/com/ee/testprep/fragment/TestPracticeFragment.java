package com.ee.testprep.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ee.testprep.MainActivity;
import com.ee.testprep.PracticeMetrics;
import com.ee.testprep.PracticeMetrics.PracticeType;
import com.ee.testprep.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class TestPracticeFragment extends Fragment {
    public static String PRACTICE_CATEGORY = "practice_type";
    public static String PRACTICE_SUB_CATEGORY = "practice_sub_type";
    private ViewPager pager;
    private PracticePagerAdapter pagerAdapter;
    private PracticeMetrics practice;
    private int numQuestions;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle args = getArguments();
        category = PracticeType.values()[args.getInt(PRACTICE_CATEGORY, 0)];
        subCategory = args.getString(PRACTICE_SUB_CATEGORY);

        practice = new PracticeMetrics(getContext(), category, subCategory);
        numQuestions = practice.getNumQuestions();

        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.progress_header).setVisibility(View.GONE);

        pagerAdapter = new PracticePagerAdapter((MainActivity) getActivity());
        pager = view.findViewById(R.id.questions_sliding_pager);
        pager.setAdapter(pagerAdapter);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    private class PracticePagerAdapter extends FragmentPagerAdapter {
        private int currentPosition;

        public PracticePagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            int prevPosition = currentPosition;
            currentPosition = position;
            Fragment fragment;
            boolean isLastQuestion = (position == numQuestions - 1);
            if (currentPosition < prevPosition) {
                fragment = QuestionPracticeFragment.newInstance(practice.getPrevQuestion(),
                        isLastQuestion);
            } else {
                fragment = QuestionPracticeFragment.newInstance(practice.getNextQuestion(),
                        isLastQuestion);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return numQuestions;
        }
    }
}
