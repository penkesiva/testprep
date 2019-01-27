package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.Test.TestType;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class StatsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager pager;
    private SlidePagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pagerAdapter = new SlidePagerAdapter((MainActivity) getActivity());
        pager = view.findViewById(R.id.stats_sliding_pager);
        pager.setAdapter(pagerAdapter);

        tabLayout = view.findViewById(R.id.stats_sliding_tabs);
        tabLayout.setupWithViewPager(pager);
    }

    private class SlidePagerAdapter extends FragmentPagerAdapter {
        private final Context context;

        public SlidePagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            context = activity;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new StatsTabFragment();
            Bundle args = new Bundle();
            args.putInt(StatsTabFragment.TEST_TYPE, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return TestType.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            TestType testType = TestType.values()[position];
            return testType.getTitle(context);
        }
    }
}
