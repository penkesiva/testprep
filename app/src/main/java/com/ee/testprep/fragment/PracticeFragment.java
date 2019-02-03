package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class PracticeFragment extends Fragment {

    private static String TAG = PracticeFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private String[] mExamList = new String[0];
    private String[] mSubjectList = new String[0];
    private GridView examGridView, subjectGridView;
    private PracticeFragment.LocalAdapter examAdapter;
    private PracticeFragment.LocalAdapter subjectAdapter;

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

        /**
         * EXAM expandable view - start
         */
        examGridView = view.findViewById(R.id.expanded_exam_grid);

        Switch examSwitch = view.findViewById(R.id.sw_exam);
        examSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(examGridView == null) return;

                if(isChecked) {
                    //set all checkbox
                    for (int i = 0; i < examAdapter.getCount(); i++) {
                        examAdapter.getViewHolder(i).item.setChecked(true);
                    }
                } else {
                    //unset all checkbox
                    for (int i = 0; i < examAdapter.getCount(); i++) {
                        examAdapter.getViewHolder(i).item.setChecked(false);
                    }
                }
            }
        });

        ArrayList<String> examList = MainActivity.getExams();
        mExamList = new String[examList.size()];
        mExamList = examList.toArray(mExamList);

        examAdapter = new PracticeFragment.LocalAdapter(getActivity(), mExamList);
        examGridView.setAdapter(examAdapter);

        /**
         *  SUBJECT - expandable view start
         */
        subjectGridView = view.findViewById(R.id.expanded_subject_grid);

        Switch subjectSwitch = view.findViewById(R.id.sw_subject);
        subjectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            }
        });

        ArrayList<String> subjectList = MainActivity.getSubjects();
        mSubjectList = new String[subjectList.size()];
        mSubjectList = subjectList.toArray(mSubjectList);

        subjectAdapter = new PracticeFragment.LocalAdapter(getActivity(), mSubjectList);
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
        private ArrayList<Obj> list;

        public class Obj {
            private String mTitle;
            private ViewHolder mViewHolder;

            public Obj(String title) {
                mTitle = title;
            }

            public Obj(String title, ViewHolder holder) {
                mTitle = title;
                mViewHolder = holder;
            }
        }

        private class ViewHolder {
            private final CheckBox item;

            public ViewHolder(CheckBox box) {
                this.item = box;
            }
        }

        public LocalAdapter(Context context, String[] items) {
            mContext = context;
            list = new ArrayList<>(items.length);

            for (int i = 0; i < items.length; i++) {
                list.add(i, new Obj(items[0]));
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).mTitle;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public ViewHolder getViewHolder(int position) {
            return list.get(position).mViewHolder;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String titleName = list.get(position).mTitle;

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_item, null);

                final CheckBox cb = convertView.findViewById(R.id.btn_item);
                cb.setText(titleName);

                final PracticeFragment.LocalAdapter.ViewHolder viewHolder = new PracticeFragment.LocalAdapter.ViewHolder(cb);
                convertView.setTag(viewHolder);
                list.set(position, new Obj(titleName, viewHolder));
            }

            return convertView;
        }

    }
}
