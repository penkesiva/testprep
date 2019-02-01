package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.ee.testprep.MainActivity;
import com.ee.testprep.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class PracticeFragment extends Fragment {

    private static String TAG = PracticeFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private String[] mExamList = new String[0];
    private String[] mSubjectList = new String[0];
    private GridView examGridView, subjectGridView;

    public PracticeFragment() {
    }

    public static PracticeFragment newInstance() {
        PracticeFragment fragment = new PracticeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_practice2, container, false);

        /* EXAM expandable view - start */
        examGridView = view.findViewById(R.id.expanded_exam_grid);

        Switch examSwitch = view.findViewById(R.id.sw_exam);
        examSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ArrayList<String> examList = MainActivity.getExams();
        mExamList = new String[examList.size()];
        mExamList = examList.toArray(mExamList);

        PracticeFragment.LocalAdapter examAdapter = new PracticeFragment.LocalAdapter(getActivity(), mExamList);
        examGridView.setAdapter(examAdapter);

        /* EXAM - expandable view done */

        /* SUBJECT - expandable view start */
        subjectGridView = view.findViewById(R.id.expanded_subject_grid);

        ArrayList<String> subjectList = MainActivity.getSubjects();
        mSubjectList = new String[subjectList.size()];
        mSubjectList = subjectList.toArray(mSubjectList);

        PracticeFragment.LocalAdapter subjectAdapter = new PracticeFragment.LocalAdapter(getActivity(), mSubjectList);
        subjectGridView.setAdapter(subjectAdapter);

        setDynamicHeight(subjectGridView, 3);
        setDynamicHeight(examGridView, 3);

        /* SUBJECT - expandable view done */

        /* Year - range bar setup */
        final CrystalRangeSeekbar rangeSeekbar = view.findViewById(R.id.range_seekbar);
        ArrayList<String> yearList = MainActivity.getYears();
        final TextView tvMin = view.findViewById(R.id.textMin1);
        final TextView tvMax = view.findViewById(R.id.textMax1);
        tvMin.setText("" + yearList.get(0));
        tvMax.setText("" + yearList.get(yearList.size() - 1));
        rangeSeekbar.setBarColor(getResources().getColor(android.R.color.holo_orange_light));

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText("" + minValue.intValue());
                tvMax.setText("" + maxValue.intValue());
            }
        });

        /* Year - range bar done */

        /* Difficulty - start */
        final Button easyBtnView = view.findViewById(R.id.btn_easy);
        final Button mediumBtnView = view.findViewById(R.id.btn_medium);
        final Button hardBtnView = view.findViewById(R.id.btn_hard);

        //Log.v(TAG, "penke all button state focus " + allBtnView.isFocused() + " pressed " + allBtnView.isPressed() + " enabled " + allBtnView.isEnabled());

        return view;
    }

    private void setDynamicHeight(GridView gridView, int columns) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = gridViewAdapter.getCount();
        int rows = 0;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if (items > columns) {
            x = items / columns;
            rows = (int) (x + 2);
            totalHeight *= rows;
        } else if (items < columns && items > 0) {
            totalHeight *= 2;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        gridView.setLayoutParams(params);
    }

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class LocalAdapter extends BaseAdapter {

        private final Context mContext;
        private String mItems[];

        public LocalAdapter(Context context, String[] items) {
            this.mContext = context;
            this.mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String filterName = mItems[position];

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_item, null);

                final Button btn = convertView.findViewById(R.id.btn_item);
                btn.setText(filterName);

                final PracticeFragment.LocalAdapter.ViewHolder viewHolder = new PracticeFragment.LocalAdapter.ViewHolder(btn);
                convertView.setTag(viewHolder);
            }

            final PracticeFragment.LocalAdapter.ViewHolder viewHolder = (PracticeFragment.LocalAdapter.ViewHolder) convertView.getTag();
            //viewHolder.item.setText(filterName.toUpperCase());

            return convertView;
        }

        private class ViewHolder {
            private final Button item;

            public ViewHolder(Button button) {
                this.item = button;
            }
        }
    }

}
