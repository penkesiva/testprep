package ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ee.testprep.MainActivity;
import ee.testprep.R;
import ee.testprep.db.Test.TestType;

public class StatsFragment extends android.app.Fragment {
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
