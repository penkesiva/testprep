package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

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
        View view =  inflater.inflate(R.layout.fragment_practice2, container, false);

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

        examGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
            }
        });

        ImageButton examView = view.findViewById(R.id.ib_exam);
        examView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(examGridView.getVisibility() == View.VISIBLE)
                    examGridView.setVisibility(View.GONE);
                else
                    examGridView.setVisibility(View.VISIBLE);
            }
        });
        /* EXAM - expandable view done */

        /* SUBJECT - expandable view start */
        subjectGridView = view.findViewById(R.id.expanded_subject_grid);

        ArrayList<String> subjectList = MainActivity.getSubjects();
        mSubjectList = new String[subjectList.size()];
        mSubjectList = subjectList.toArray(mSubjectList);

        PracticeFragment.LocalAdapter subjectAdapter = new PracticeFragment.LocalAdapter(getActivity(), mSubjectList);
        subjectGridView.setAdapter(subjectAdapter);

        subjectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
            }
        });

        ImageButton subjectView = view.findViewById(R.id.ib_subject);
        subjectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subjectGridView.getVisibility() == View.VISIBLE)
                    subjectGridView.setVisibility(View.GONE);
                else
                    subjectGridView.setVisibility(View.VISIBLE);
            }
        });
        /* SUBJECT - expandable view done */

        /* Year - range bar setup */
        final RangeSeekBar<Integer> seekBar = view.findViewById(R.id.rangeSeekbar);
        ArrayList<String> yearList = MainActivity.getYears();
        seekBar.setRangeValues(Integer.valueOf(yearList.get(0)), Integer.valueOf(yearList.get(yearList.size() - 1)) + 1);
        seekBar.setNotifyWhileDragging(true);

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                //Toast.makeText(getContext(), "" + minValue + "   " + maxValue, Toast.LENGTH_SHORT).show();

            }
        });
        /* Year - range bar done */

        /* Difficulty - start */
        final Button allBtnView = view.findViewById(R.id.btn_all);
        final Button easyBtnView = view.findViewById(R.id.btn_easy);
        final Button mediumBtnView = view.findViewById(R.id.btn_medium);
        final Button hardBtnView = view.findViewById(R.id.btn_hard);

        allBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allBtnView.isPressed()) {
                    allBtnView.setPressed(true);
                    easyBtnView.setPressed(true);
                    mediumBtnView.setPressed(true);
                    hardBtnView.setPressed(true);

                } else {
                    allBtnView.setPressed(false);
                    easyBtnView.setPressed(false);
                    mediumBtnView.setPressed(false);
                    hardBtnView.setPressed(false);
                }
            }
        });

        return view;
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

            final PracticeFragment.LocalAdapter.ViewHolder viewHolder = (PracticeFragment.LocalAdapter.ViewHolder)convertView.getTag();
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
