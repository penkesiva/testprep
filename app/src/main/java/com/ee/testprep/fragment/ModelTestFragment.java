package com.ee.testprep.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ee.testprep.MainActivity;
import com.ee.testprep.R;

import java.util.ArrayList;

public class ModelTestFragment extends Fragment {

    private static String TAG = ModelTestFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private OnFragmentInteractionListener mListener;
    private String[] mModelTestList;

    public ModelTestFragment() {
    }

    public static ModelTestFragment newInstance(ArrayList<String> List) {
        ModelTestFragment fragment = new ModelTestFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1, List);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mModelTestList = ((ArrayList<String>) getArguments().getSerializable(ARG_PARAM1)).toArray(new String[0]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modeltest, container, false);

        GridView gridView = view.findViewById(R.id.modeltest_gridview);
        final ModelTestAdapter modelTestAdapter = new ModelTestAdapter(getActivity(), mModelTestList);
        gridView.setAdapter(modelTestAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                onButtonPressed(MainActivity.STATUS_MODELTEST_XX, item);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int status, String item) {
        if (mListener != null) {
            mListener.onFragmentInteraction(status, item);
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


    public class ModelTestAdapter extends BaseAdapter {

        private final Context mContext;
        private String mFilter[];

        public ModelTestAdapter(Context context, String[] filter) {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String filterName = mModelTestList[position];

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_modeltest_item, null);

                final TextView nameTextView = convertView.findViewById(R.id.textview_modeltest_name);
                final TextView fullNameTextView = convertView.findViewById(R.id.textview_exam);
                final ImageView lockImageView = convertView.findViewById(R.id.imageview_unlock);

                final ModelTestAdapter.ViewHolder viewHolder = new ModelTestAdapter.ViewHolder(nameTextView, fullNameTextView, lockImageView);
                convertView.setTag(viewHolder);
            }

            final ModelTestAdapter.ViewHolder viewHolder = (ModelTestAdapter.ViewHolder) convertView.getTag();
            viewHolder.nameTextView.setText(filterName.toUpperCase());
            viewHolder.fullNameTextView.setText("TBD");
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
