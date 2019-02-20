package com.ee.testprep.fragment;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.Test.TestType;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
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
        pager.setSaveFromParentEnabled(false);
        pager.setAdapter(pagerAdapter);

        tabLayout = view.findViewById(R.id.stats_sliding_tabs);
        tabLayout.setupWithViewPager(pager);

        int tabCount = tabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View root = getLayoutInflater().inflate(R.layout.tab_layout_title, tabLayout, false);
            TextView title = root.findViewById(R.id.tab_title);
            title.setText(tab.getText());
            tab.setCustomView(root);
        }

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(android.R.color.black));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }

    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
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
