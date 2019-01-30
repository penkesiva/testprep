package com.ee.testprep.fragment;

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

import androidx.fragment.app.Fragment;

public class PracticeFragment extends Fragment {

    private static String TAG = PracticeFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;

    private final static String mFilter[][] = {
            {"Year", "ex: 2016"},
            {"Subject", "ex: Politics"},
            {"Exam", "ex: CSP"},
            {"Easy", "scale: 0-3"},
            {"Medium", "scale 4-6"},
            {"Hard", "scale: 7-9"},
            {"Random", "random"},
            {"Starred", "to review later"},
            {"All", "no filters"}
    };

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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_practice, container, false);

        GridView gridView = view.findViewById(R.id.practice_gridview);
        final FilterAdapter filterAdapter = new FilterAdapter(getActivity(), mFilter);
        gridView.setAdapter(filterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String filter = mFilter[position][0];
                //filterAdapter.notifyDataSetChanged();
                switch (position) {
                    case 0:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_YEAR);
                        break;
                    case 1:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_SUBJECT);
                        break;
                    case 2:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_EXAM);
                        break;
                    case 3:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_EASY);
                        break;
                    case 4:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_MEDIUM);
                        break;
                    case 5:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_HARD);
                        break;
                    case 6:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_RANDOM);
                        break;
                    case 7:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_USERSTATUS);
                        break;
                    case 8:
                        onButtonPressed(MainActivity.STATUS_PRACTICE_ALL);
                        break;
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

    public class FilterAdapter extends BaseAdapter {

        private final Context mContext;
        private String mFilter[][];

        public FilterAdapter(Context context, String[][] filter) {
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
            return null;
        }

        private boolean getIsLocked() {
            return false; //TODO
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String filterName = mFilter[position][0];
            final String filterExample = mFilter[position][1];

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_practice_item, null);

                final ImageView lockImageView = convertView.findViewById(R.id.imageview_unlock);
                final TextView nameTextView = convertView.findViewById(R.id.textview_filter_name);
                final TextView exampleTextView = convertView.findViewById(R.id.textview_example);

                final ViewHolder viewHolder = new ViewHolder(nameTextView, exampleTextView, lockImageView);
                convertView.setTag(viewHolder);
            }

            final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
            viewHolder.nameTextView.setText(filterName);
            viewHolder.exampleTextView.setText(filterExample);
            viewHolder.lockImageview.setImageResource(getIsLocked() ? R.drawable.lock : R.drawable.unlock);

            return convertView;
        }

        private class ViewHolder {
            private final TextView nameTextView;
            private final TextView exampleTextView;
            private final ImageView lockImageview;

            public ViewHolder(TextView nameTextView, TextView exampleTextView, ImageView lockImageview) {
                this.nameTextView = nameTextView;
                this.exampleTextView = exampleTextView;
                this.lockImageview = lockImageview;
            }
        }
    }

}
