package com.ee.testprep.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ee.testprep.LoginActivity;
import com.ee.testprep.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    public static boolean nightMode = false;
    private static String className = SettingsFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private TextView displayName;
    private EditText displayNameEdit;
    private Button displayNameEditButton;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        displayNameEdit = view.findViewById(R.id.settings_display_name_edit);
        displayName = view.findViewById(R.id.settings_display_name);
        displayName.setText(user.getDisplayName());
        TextView email = view.findViewById(R.id.settings_email);
        email.setText(user.getEmail());

        Button signout = view.findViewById(R.id.settings_signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attach the key value pair using putExtra to this intent
                Intent intent = new Intent(getContext(), LoginActivity.class);
                String action = "SignOut";
                intent.putExtra("ACTION", action);
                startActivity(intent);
                getActivity().finish();
            }
        });

        displayNameEditButton = view.findViewById(R.id.settings_display_name_edit_button);
        disableDisplayNameEdit();
        displayNameEditButton.setOnClickListener(v -> {
            if (displayNameEditButton.getText().toString().toLowerCase().equals("save")) {
                CharSequence editedName = displayNameEdit.getText();

                if (editedName == null || editedName.toString().isEmpty()) return;

                FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
                String savedName = u.getDisplayName();
                String newName = editedName.toString();

                if (!newName.equals(savedName)) {
                    UserProfileChangeRequest request =
                            new UserProfileChangeRequest.Builder().setDisplayName(newName).build();
                    u.updateProfile(request);
                    displayName.setText(newName);
                }
                disableDisplayNameEdit();
            } else {
                enableDisplayNameEdit();
            }
        });

//        final Switch swNightMode = view.findViewById(R.id.settings_night_mode);
//
//        swNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                    nightMode = true;
//                } else {
//                    nightMode = false;
//                }
//            }
//        });
    }

    private void enableDisplayNameEdit() {
        displayNameEditButton.setText("SAVE");
        displayNameEdit.setVisibility(View.VISIBLE);
        displayNameEdit.setText(displayName.getText());
        displayName.setVisibility(View.GONE);
    }

    private void disableDisplayNameEdit() {
        displayNameEditButton.setText("EDIT");
        InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(displayNameEdit.getWindowToken(), 0);
        displayNameEdit.setVisibility(View.GONE);
        displayName.setVisibility(View.VISIBLE);
    }
}
