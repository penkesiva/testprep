package com.ee.testprep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.db.DataBaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PracticeFragment extends Fragment {

    private static String TAG = PracticeFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private String[] mExamList = new String[0];
    private String[] mSubjectList = new String[0];
    private ListView examListView, subjectListView;
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
    private List<String> mDifficultyListChecked = new ArrayList<>();
    private List<String> mCheckedList = new ArrayList<>();
    private HashSet<String> mExamHash = new HashSet<>();
    private HashSet<String> mSubjectHash = new HashSet<>();
    private StringBuffer finalQuery = new StringBuffer();
    private boolean checked = false;

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
        return inflater.inflate(R.layout.fragment_practice2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpExamSection(view);
        setUpSubjectSection(view);
        setUpYearSection(view);
        setUpDifficultySection(view);
        setUpOtherSection(view);
        setUpStartButton(view);

        updateAbreviations();
    }

    private void setUpExamSection(View view) {
        // EXAM expandable view - start
        examListView = view.findViewById(R.id.exam_list);

        Switch cbAllExams = view.findViewById(R.id.sw_exam);
        cbAllExams.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (examListView == null) return;
            examAdapter.isAll = isChecked;
            examAdapter.notifyDataSetChanged();
        });

        ArrayList<String> examList = MainActivity.getExams();
        mExamList = new String[examList.size()];
        mExamList = examList.toArray(mExamList);
        mExamHash.addAll(Arrays.asList(mExamList));

        examAdapter = new PracticeFragment.LocalAdapter(getActivity(), mExamList);
        examListView.setAdapter(examAdapter);

        setDynamicHeight(examListView);
    }

    private void setUpSubjectSection(View view) {
        // SUBJECT - expandable view start
        subjectListView = view.findViewById(R.id.subject_list);

        Switch cbAllSubjects = view.findViewById(R.id.sw_subject);
        cbAllSubjects.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (subjectListView == null) return;
            subjectAdapter.isAll = isChecked;
            subjectAdapter.notifyDataSetChanged();
        });

        ArrayList<String> subjectList = MainActivity.getSubjects();
        mSubjectList = new String[subjectList.size()];
        mSubjectList = subjectList.toArray(mSubjectList);
        mSubjectHash.addAll(Arrays.asList(mSubjectList));

        subjectAdapter = new PracticeFragment.LocalAdapter(getActivity(), mSubjectList);
        subjectListView.setAdapter(subjectAdapter);

        setDynamicHeight(subjectListView);
    }

    private void setUpYearSection(View view) {
        // Year - range bar setup
        final CrystalRangeSeekbar rangeSeekbar = view.findViewById(R.id.range_seekbar);
        ArrayList<String> yearList = MainActivity.getYears();
        final TextView tvMin = view.findViewById(R.id.textMin1);
        final TextView tvMax = view.findViewById(R.id.textMax1);
        tvMin.setText(yearList.get(0));
        tvMax.setText(yearList.get(yearList.size() - 1));
        rangeSeekbar.setBarColor(getResources().getColor(android.R.color.holo_orange_light));

        rangeSeekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            tvMin.setText(tvMin.getText());
            tvMax.setText(tvMax.getText());
        });
    }

    private void setUpDifficultySection(View view) {
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
    }

    private void setUpOtherSection(View view) {
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
    }

    private void setUpStartButton(View view) {
        //Start
        btnStart = view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> setUpStart());
    }

    private void setUpStart() {

        finalQuery.setLength(0);

        if (mCheckedList.size() == 0) {
            Toast t = Toast.makeText(getContext(), "select a filter", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.BOTTOM, 0, 200);
            t.show();
            return;
        }

        //create a query string
        finalQuery.append(mQRandom);
        finalQuery.append(mWhereClause);

        if (!mQTRL.equals("")) {
            finalQuery.append(mQTRL);
            checked = true;
        }

        if (!mQCompletedQ.equals("")) {
            if (checked) finalQuery.append(mAnd);

            finalQuery.append(mQCompletedQ);
            checked = true;
        }

        //separate exam and subject lists; it is done this way as its not known at this point
        //on how to add in separate lists in the adapter
        for (int i = 0; i < mCheckedList.size(); i++) {
            String str = mCheckedList.get(i);
            if (mExamHash.contains(str)) {
                mExamListChecked.add(str);
            } else if (mSubjectHash.contains(str)) {
                mSubjectListChecked.add(str);
            } else {
                mDifficultyListChecked.add(str);
            }
        }

        int size = mExamListChecked.size();

        if (checked && size > 0) {
            finalQuery.append(mAnd);
            //finalQuery.append(" (");
        }

        for (int i = 0; i < size; i++) {
            finalQuery.append("examName=\"");
            finalQuery.append(mExamListChecked.get(i));
            finalQuery.append("\"");
            checked = true;

            if (i < size - 1)
                finalQuery.append(mOR);
        }
        //finalQuery.append(")");

        size = mSubjectListChecked.size();

        if (checked && size > 0) {
            finalQuery.append(mAnd);
            //finalQuery.append(" (");
        }

        for (int i = 0; i < size; i++) {
            finalQuery.append("subject=\"");
            finalQuery.append(mSubjectListChecked.get(i));
            finalQuery.append("\"");
            checked = true;

            if (i < size - 1)
                finalQuery.append(mOR);
        }
        //finalQuery.append(")");

        size = mDifficultyListChecked.size();

        if (checked && size > 0) {
            finalQuery.append(mAnd);
        }

        for (int i = 0; i < size; i++) {
            finalQuery.append(mDifficultyListChecked.get(i));

            if (i < size - 1)
                finalQuery.append(mOR);
        }

        Log.d(TAG, finalQuery.toString());

        clearLists();

        onButtonPressed(MainActivity.STATUS_PRACTICE_MORE, finalQuery.toString());
    }

    private void clearLists() {
        mCheckedList.clear();

        mSubjectListChecked.clear();
        mSubjectHash.clear();

        mExamListChecked.clear();
        mExamHash.clear();

        mDifficultyListChecked.clear();

        checked = false;
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

    private void setDynamicHeight(ListView view) {

        if (view == null) {
            return;
        }

        int totalHeight, rows;
        ListAdapter listViewAdapter = view.getAdapter();
        int items = listViewAdapter.getCount();

        if (items <= 0) {
            // looks like database is not ready, just return
            return;
        }

        View listItem = listViewAdapter.getView(0, null, view);
        listItem.measure(0, 0);
        totalHeight = (int)(listItem.getMeasuredHeight() * items * 1.1f);

/*        float x = 1;
        if (items > columns) {
            x = items / columns;
            rows = (int) (x + 2);
            totalHeight *= rows;
        } else if (items < columns && items > 0) {
            totalHeight *= 2;
        }*/

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = totalHeight;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);
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

    public HashMap<String, String> abbreviations = new HashMap<>();

    public void updateAbreviations() {
        abbreviations.put("IYBA", "BioTechnology - IYBA");
        abbreviations.put("ECON", "Economics - ECON");
        abbreviations.put("INTA", "TBD - INTA");
        abbreviations.put("GEOG", "Geography - GEOG");
        abbreviations.put("POLI", "Politics - POLI");
        abbreviations.put("ENVI", "Environment - ENVI");
        abbreviations.put("HIST", "History - HIST");
        abbreviations.put("CURR", "Current Affairs - CURR");
        abbreviations.put("SNTC", "TBD - SNTC");
        abbreviations.put("CSP", "TBD - CSP");
    }

    public class LocalAdapter extends BaseAdapter {

        private final Context mContext;
        private String[] mItems;
        public boolean isAll;

        private class ViewHolder {
            private final TextView itemText;
            private final CheckBox itemBox;

            public ViewHolder(TextView text, CheckBox box) {
                this.itemText = text;
                this.itemBox = box;
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
                convertView = layoutInflater.inflate(R.layout.practice_view_item, null);

                final CheckBox cb = convertView.findViewById(R.id.list_item_box);
                final TextView tv = convertView.findViewById(R.id.list_item_text);
                tv.setText(abbreviations.get(titleName));
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        mCheckedList.add(titleName);
                    } else {
                        mCheckedList.remove(titleName);
                    }
                });

                final PracticeFragment.LocalAdapter.ViewHolder viewHolder = new PracticeFragment.LocalAdapter.ViewHolder(tv, cb);
                convertView.setTag(viewHolder);
            }

            PracticeFragment.LocalAdapter.ViewHolder viewHolder = (PracticeFragment.LocalAdapter.ViewHolder) convertView.getTag();
            viewHolder.itemBox.setChecked(isAll);

            return convertView;
        }

    }
}
