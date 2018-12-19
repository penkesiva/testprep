package ee.testprep.fragment.practice;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ee.testprep.MainActivity;
import ee.testprep.R;
import ee.testprep.fragment.OnFragmentInteractionListener;

public class YearFragment extends Fragment {

    private static String className = YearFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private String[] mYears;
    private OnFragmentInteractionListener mListener;

    public YearFragment() {
        // Required empty public constructor
    }

    public static YearFragment newInstance(ArrayList<String> years) {
        YearFragment fragment = new YearFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, years);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYears = ((ArrayList<String>) getArguments().getSerializable(ARG_PARAM1)).toArray(new String[0]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_year, container, false);

        GridView gridView = view.findViewById(R.id.year_gridview);
        final YearFragment.FilterAdapter filterAdapter = new YearFragment.FilterAdapter(getActivity(), mYears);
        gridView.setAdapter(filterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                onButtonPressed(MainActivity.STATUS_PRACTICE_YEAR_XX, item);
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
    public void onButtonPressed(int status, String year) {
        if (mListener != null) {
            mListener.onFragmentInteraction(status, year);
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String filterName = mFilter[position];

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.gridview_year_item, null);

                final TextView nameTextView = convertView.findViewById(R.id.textview_filter_name);
                final ImageView lockImageView = convertView.findViewById(R.id.imageview_unlock);

                final YearFragment.FilterAdapter.ViewHolder viewHolder = new YearFragment.FilterAdapter.ViewHolder(nameTextView, lockImageView);
                convertView.setTag(viewHolder);
            }

            final YearFragment.FilterAdapter.ViewHolder viewHolder = (YearFragment.FilterAdapter.ViewHolder)convertView.getTag();
            viewHolder.nameTextView.setText(filterName);
            viewHolder.lockImageView.setImageResource(getIsLocked() ? R.drawable.lock : R.drawable.unlock);

            return convertView;
        }

        private class ViewHolder {
            private final TextView nameTextView;
            private final ImageView lockImageView;

            public ViewHolder(TextView nameTextView, ImageView lockImageView) {
                this.nameTextView = nameTextView;
                this.lockImageView = lockImageView;
            }
        }
    }

}
