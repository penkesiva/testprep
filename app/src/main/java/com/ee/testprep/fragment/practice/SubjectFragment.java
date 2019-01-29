package com.ee.testprep.fragment.practice;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;
import com.ee.testprep.fragment.OnFragmentInteractionListener;

public class SubjectFragment extends Fragment {

    private static String className = SubjectFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private String[] mSubjects;
    private OnFragmentInteractionListener mListener;
    private HashMap<String, String> nameHash;

    public SubjectFragment() {
    }

    public static SubjectFragment newInstance(ArrayList<String> subjects) {
        SubjectFragment fragment = new SubjectFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, subjects);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSubjects = ((ArrayList<String>) getArguments().getSerializable(ARG_PARAM1)).toArray(new String[0]);
            updateNameHash();
        }
    }

    private void updateNameHash() {
        nameHash = new HashMap<>();
        for (int i = 0; i < mSubjects.length; i++) {
            if(mSubjects[i].equals("IYBA")) {
                nameHash.put(mSubjects[i], "TBD");
            } else if(mSubjects[i].equals("ECON")) {
                nameHash.put(mSubjects[i], "Economics");
            } else if(mSubjects[i].equals("INTA")) {
                nameHash.put(mSubjects[i], "TBD");
            } else if(mSubjects[i].equals("GEOG")) {
                nameHash.put(mSubjects[i], "Geography");
            } else if(mSubjects[i].equals("POLI")) {
                nameHash.put(mSubjects[i], "Politics");
            } else if(mSubjects[i].equals("ENVI")) {
                nameHash.put(mSubjects[i], "Environment");
            } else if(mSubjects[i].equals("HIST")) {
                nameHash.put(mSubjects[i], "History");
            } else if(mSubjects[i].equals("CURR")) {
                nameHash.put(mSubjects[i], "Current Affairs");
            } else if(mSubjects[i].equals("SNTC")) {
                nameHash.put(mSubjects[i], "TBD");
            } else {
                nameHash.put(mSubjects[i], "");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_subject, container, false);

        GridView gridView = view.findViewById(R.id.subject_gridview);
        final SubjectFragment.FilterAdapter filterAdapter = new SubjectFragment.FilterAdapter(getActivity(), mSubjects);
        gridView.setAdapter(filterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                onButtonPressed(MainActivity.STATUS_PRACTICE_SUBJECT_XX, item);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int status, String subject) {
        if (mListener != null) {
            mListener.onFragmentInteraction(status, subject);
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

    public class FilterAdapter extends BaseAdapter {

        private final Context mContext;
        private String mFilter[];

        public FilterAdapter(Context context, String[] filter) {
            this.mContext = context;
            this.mFilter = filter;
        }

        @Override
        public int getCount() {
            return mFilter.length;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mFilter[position];
        }

        private boolean getIsLocked() {
            return false; //TODO
        }

        private String getFullName(String subject) {
            if(nameHash.containsKey(subject)) {
                return nameHash.get(subject);
            }

            return "";
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String filterName = mFilter[position];
            String fullName = getFullName(filterName);

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_subject_item, null);

                final TextView nameTextView = convertView.findViewById(R.id.textview_filter_name);
                final TextView fullNameTextView = convertView.findViewById(R.id.tv_fullname);
                final ImageView lockImageView = convertView.findViewById(R.id.imageview_unlock);

                final SubjectFragment.FilterAdapter.ViewHolder viewHolder = new SubjectFragment.FilterAdapter.ViewHolder(nameTextView, fullNameTextView, lockImageView);
                convertView.setTag(viewHolder);
            }

            final SubjectFragment.FilterAdapter.ViewHolder viewHolder = (SubjectFragment.FilterAdapter.ViewHolder)convertView.getTag();
            viewHolder.nameTextView.setText(filterName);
            viewHolder.fullNameTextView.setText(fullName);
            viewHolder.lockImageView.setImageResource(getIsLocked() ? R.drawable.lock : R.drawable.unlock);

            return convertView;
        }

        private class ViewHolder {
            private final TextView nameTextView;
            private final TextView fullNameTextView;
            private final ImageView lockImageView;

            public ViewHolder(TextView nameTextView, TextView fullName, ImageView lockImageView) {
                this.nameTextView = nameTextView;
                this.fullNameTextView = fullName;
                this.lockImageView = lockImageView;
            }
        }
    }

}
