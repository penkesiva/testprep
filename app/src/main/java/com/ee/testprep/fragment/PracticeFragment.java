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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DataBaseHelper;

public class PracticeFragment extends Fragment {

    private static String TAG = PracticeFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private String[] mExamList = new String[0];
    private String[] mSubjectList = new String[0];
    private GridView examGridView, subjectGridView;
    private PracticeFragment.LocalAdapter examAdapter;
    private PracticeFragment.LocalAdapter subjectAdapter;
    private CheckBox cbEasy, cbMedium, cbHard;
    private CheckBox cbRandom, cbTRL, cbCompletedQ;
    private Button btnStart;
    private Switch cbAllDifficulty;

    //Query strings
    private String mWhereClause = " WHERE ";
    private String mAnd = " AND ";
    private String mOR = " OR ";
    private String mQRandom = "SELECT * FROM " + DataBaseHelper.TABLE_QBANK;
    private String mQTRL = "";
    private String mQCompletedQ = "";
    private String mQEasy = " DIFFICULTY BETWEEN 0 and 3 ";
    private String mQMedium = " DIFFICULTY BETWEEN 4 and 6 ";
    private String mQHard = " DIFFICULTY BETWEEN 7 and 9 ";
    private String mQAllDifficulty = " DIFFICULTY BETWEEN 0 and 9 ";
    private List<String> mExamListChecked = new ArrayList<>();
    private List<String> mSubjectListChecked = new ArrayList<>();
    private List<String> mCheckedList = new ArrayList<>();
    private HashSet<String> mExamHash = new HashSet<>();
    private HashSet<String> mSubjectHash = new HashSet<>();


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

        View view = inflater.inflate(R.layout.fragment_practice2, container, false);

        // EXAM expandable view - start
        examGridView = view.findViewById(R.id.expanded_exam_grid);

        Switch cbAllExams = view.findViewById(R.id.sw_exam);
        cbAllExams.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (examGridView == null) return;
            examAdapter.isAll = isChecked;
            examAdapter.notifyDataSetChanged();
        });

        ArrayList<String> examList = MainActivity.getExams();
        mExamList = new String[examList.size()];
        mExamList = examList.toArray(mExamList);
        mExamHash.addAll(Arrays.asList(mExamList));

        examAdapter = new PracticeFragment.LocalAdapter(getActivity(), mExamList);
        examGridView.setAdapter(examAdapter);

        // SUBJECT - expandable view start
        subjectGridView = view.findViewById(R.id.expanded_subject_grid);

        Switch cbAllSubjects = view.findViewById(R.id.sw_subject);
        cbAllSubjects.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (subjectGridView == null) return;
            subjectAdapter.isAll = isChecked;
            subjectAdapter.notifyDataSetChanged();
        });

        ArrayList<String> subjectList = MainActivity.getSubjects();
        mSubjectList = new String[subjectList.size()];
        mSubjectList = subjectList.toArray(mSubjectList);
        mSubjectHash.addAll(Arrays.asList(mSubjectList));

        subjectAdapter = new PracticeFragment.LocalAdapter(getActivity(), mSubjectList);
        subjectGridView.setAdapter(subjectAdapter);

        setDynamicHeight(subjectGridView, 3);
        setDynamicHeight(examGridView, 3);

        // Year - range bar setup
        final CrystalRangeSeekbar rangeSeekbar = view.findViewById(R.id.range_seekbar);
        ArrayList<String> yearList = MainActivity.getYears();
        final TextView tvMin = view.findViewById(R.id.textMin1);
        final TextView tvMax = view.findViewById(R.id.textMax1);
        tvMin.setText("" + yearList.get(0));
        tvMax.setText("" + yearList.get(yearList.size() - 1));
        rangeSeekbar.setBarColor(getResources().getColor(android.R.color.holo_orange_light));

        rangeSeekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            tvMin.setText("" + minValue.intValue());
            tvMax.setText("" + maxValue.intValue());
        });

        // Difficulty
        cbEasy = view.findViewById(R.id.btn_easy);
        cbEasy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedList.add(mQEasy);
            } else {
                mCheckedList.remove(mQEasy);
                cbAllDifficulty.setChecked(false);
            }
        });

        cbMedium = view.findViewById(R.id.btn_medium);
        cbMedium.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedList.add(mQMedium);
            } else {
                mCheckedList.remove(mQMedium);
                cbAllDifficulty.setChecked(false);
            }
        });

        cbHard = view.findViewById(R.id.btn_hard);
        cbHard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedList.add(mQHard);
            } else {
                mCheckedList.remove(mQHard);
                cbAllDifficulty.setChecked(false);
            }
        });

        cbAllDifficulty = view.findViewById(R.id.sw_difficulty);
        cbAllDifficulty.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setDifficultyGUI(true);
                mCheckedList.add(mQAllDifficulty);
            } else {
                setDifficultyGUI(false);
                mCheckedList.remove(mQAllDifficulty);
            }
        });

        //Others
        cbRandom = view.findViewById(R.id.cb_random);
        cbTRL = view.findViewById(R.id.cb_trl);
        cbCompletedQ = view.findViewById(R.id.cb_completed);
        cbRandom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mQRandom = "SELECT DISTINCT * FROM " + DataBaseHelper.TABLE_QBANK;
            } else {
                mQRandom = "SELECT * FROM " + DataBaseHelper.TABLE_QBANK;
            }
        });

        cbTRL.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mQTRL = " userstatus " + "IN ('Z')";
                cbCompletedQ.setChecked(false);
            } else {
                mQTRL = "";
            }
        });

        cbCompletedQ.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mQCompletedQ = " userstatus " + "IN ('A','B','C','D')";
                cbTRL.setChecked(false);
            } else {
                mQCompletedQ = "";
            }
        });

        //Start
        btnStart = view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> {

            //separate exam and subject lists; it is done this way as its not known at this point
            //on how to add in separate lists in the adapter
            for (int i = 0; i < mCheckedList.size(); i++) {
                String str = mCheckedList.get(i);
                if(mExamHash.contains(str)) {
                    mExamListChecked.add(str);
                } else if(mSubjectHash.contains(str)) {
                    mSubjectListChecked.add(str);
                }
            }

            //create a query string
            StringBuffer finalQuery = new StringBuffer();

            finalQuery.append(mQRandom);
            finalQuery.append(mWhereClause);

            for (int i = 0; i < mExamListChecked.size(); i++) {
                //exam item
                finalQuery.append(" examName=\"");
                finalQuery.append(mExamListChecked.get(i));
                finalQuery.append("\"");

                if (i != mExamListChecked.size() - 1)
                    finalQuery.append(mOR);
            }

            if(mSubjectListChecked.size() > 0)
                finalQuery.append(mAnd);

            for (int i = 0; i < mSubjectListChecked.size(); i++) {
                //subject item
                finalQuery.append(" subject=\"");
                finalQuery.append(mSubjectListChecked.get(i));
                finalQuery.append("\"");

                if (i != mSubjectListChecked.size() - 1)
                    finalQuery.append(mOR);
            }
            Log.d(TAG, finalQuery.toString());

            clearLists();

            onButtonPressed(MainActivity.STATUS_PRACTICE_MORE, finalQuery.toString());

        });

        return view;
    }

    private void clearLists() {
        mSubjectListChecked.clear();
        mSubjectHash.clear();

        mExamListChecked.clear();
        mExamHash.clear();
    }

    private void setDifficultyGUI(boolean checked) {
        if (checked) {
            cbEasy.setChecked(true);
            cbMedium.setChecked(true);
            cbHard.setChecked(true);
        } else {
            cbEasy.setChecked(false);
            cbMedium.setChecked(false);
            cbHard.setChecked(false);
        }
    }

    private void setDynamicHeight(GridView gridView, int columns) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
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

    public void onButtonPressed(int status, String query) {
        if (mListener != null) {
            mListener.onFragmentInteraction(status, query);
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
        private String[] mItems;
        public boolean isAll;

        private class ViewHolder {
            private final CheckBox item;

            public ViewHolder(CheckBox box) {
                this.item = box;
            }
        }

        public LocalAdapter(Context context, String[] items) {
            mContext = context;
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String titleName = mItems[position];

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_item, null);

                final CheckBox cb = convertView.findViewById(R.id.btn_item);
                cb.setText(titleName);
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        mCheckedList.add(titleName);
                    }
                });

                final PracticeFragment.LocalAdapter.ViewHolder viewHolder = new PracticeFragment.LocalAdapter.ViewHolder(cb);
                convertView.setTag(viewHolder);
            }

            PracticeFragment.LocalAdapter.ViewHolder viewHolder = (PracticeFragment.LocalAdapter.ViewHolder) convertView.getTag();
            viewHolder.item.setChecked(isAll);

            return convertView;
        }

    }
}
