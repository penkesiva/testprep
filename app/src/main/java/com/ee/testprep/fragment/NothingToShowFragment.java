package com.ee.testprep.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ee.testprep.R;

import androidx.fragment.app.Fragment;

public class NothingToShowFragment extends Fragment {
    // private static String TAG = NothingToShowFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatsFragment.
     */
    public static NothingToShowFragment newInstance() {
        NothingToShowFragment fragment = new NothingToShowFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nothing_to_show, container, false);
    }
}
