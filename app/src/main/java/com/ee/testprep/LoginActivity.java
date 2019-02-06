package com.ee.testprep;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ee.testprep.util.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    /**
     * shared preference for login activity;
     */
    private PreferenceUtils prefs;
    private static final String LOGIN_KEY = "LOGIN_KEY";

    // UI references.
    private TextInputEditText mRegisterEmailView, mSignInEmailView;
    private TextInputEditText mRegisterNameView;
    private Button mSignInView;
    private Button mRegisterView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView, logoLayout;
    private LinearLayout signInLayout, registerLayout;
    private TextView eeTextView;
    private ImageView eeImageView;
    private CheckBox mTermsView;

    private String email;
    private String name;

    private static final String PASSWORD = "Equality123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceUtils.getInstance(getApplicationContext());

        String action = getIntent().getStringExtra("ACTION");

        if (action != null && action.compareTo("SignOut") == 0) {
            prefs.savePrefs(LOGIN_KEY, false);
            signOutUser();
        }

        Boolean isUserLoggedin = prefs.readPrefs(LOGIN_KEY, false);

        if (isUserLoggedin) {
            // User loggedin already, start with MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Launch Login Activity
            setContentView(R.layout.login_layout);
            initViews();
        }
    }

    private void launchMainActivity() {
        prefs.savePrefs(LOGIN_KEY, true);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void initViews() {
        mRegisterNameView = findViewById(R.id.yourNameEditText);
        mRegisterEmailView = findViewById(R.id.emailEditText);
        mRegisterView = findViewById(R.id.registerButton);
        mSignInView = findViewById(R.id.signinButton);
        eeTextView = findViewById(R.id.eeTextView);
        eeImageView = findViewById(R.id.eeImageView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        logoLayout = findViewById(R.id.logo);
        afterAnimationView = findViewById(R.id.afterAnimationView);
        mSignInEmailView = findViewById(R.id.emailSignInEditText);
        signInLayout = findViewById(R.id.signinLayout);
        registerLayout = findViewById(R.id.registerLayout);
        mTermsView = findViewById(R.id.terms_conditions);

        setUpFadeInAnimation(logoLayout);

        mSignInView.setAlpha(0.4f);

        // configure register/signin buttons
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignInView.setAlpha(0.4f);
                mRegisterView.setAlpha(1f);

                signInLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);

                // Store values at the time of the login attempt.
                email = mRegisterEmailView.getText().toString();
                name = mRegisterNameView.getText().toString();

                if (validateRegisterForm())
                    registerNewUser();
            }
        });

        mSignInView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mRegisterView.setAlpha(0.4f);
                mSignInView.setAlpha(1f);

                registerLayout.setVisibility(View.GONE);
                signInLayout.setVisibility(View.VISIBLE);

                // Store values at the time of the login attempt.
                email = mSignInEmailView.getText().toString();
                name = null;

                if (validateSignInForm()) {
                    signInUser();
                }
            }
        });

        mTermsView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mTermsView.setError(null, null);
            }
        });
    }

    private void setUpFadeOutAnimation(final View logoLayout) {
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);
        fadeOut.setStartOffset(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                afterAnimationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        logoLayout.startAnimation(fadeOut);

    }

    private void setUpFadeInAnimation(final View logoLayout) {
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(3000);
        fadeIn.setStartOffset(100);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                afterAnimationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        logoLayout.startAnimation(fadeIn);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }

    /**
     * email must contain @ and .com
     *
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".com");
    }

    /**
     * userName should be at least 3 characters and it got to be only characters, space is OK
     *
     * @param userName
     * @return
     */
    private boolean isNameValid(String userName) {
        return userName.length() > 2 && isAlpha(userName);
    }

    private boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+") || name.matches(" ");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new androidx.loader.content.CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mName;

        UserLoginTask(String email, String name) {
            mEmail = email;
            mName = name;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            loadingProgressBar.setIndeterminate(true);
            return checkUser();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private boolean checkUser() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) { //success user found
            hideKeyBoard();
            return true;
        }

        return false;
    }

    private void signInUser() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email.trim(), PASSWORD)
                .addOnCompleteListener(this, onCompleteListener);
    }

    private void registerNewUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email.trim(), PASSWORD)
                .addOnCompleteListener(this, onCompleteListener);
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
    }

    private boolean validateRegisterForm() {
        if (email != null && name != null) {
            if (validRegisterFormHelper()) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                mAuthTask = new UserLoginTask(email, name);
                mAuthTask.execute((Void) null);
                return true;
            }
        }

        return false;
    }

    private boolean validateSignInForm() {
        if (email != null && !email.isEmpty()) {
            if (validSignInHelper()) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                mAuthTask = new UserLoginTask(email, "");
                mAuthTask.execute((Void) null);
                return true;
            }
        }

        return false;
    }

    private boolean validRegisterFormHelper() {
        // Reset errors.
        mRegisterEmailView.setError(null);
        mRegisterNameView.setError(null);
        mTermsView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid name, if the user entered one.
        if (name.compareTo("") == 0) {
            mRegisterNameView.setError(getString(R.string.error_field_required));
            focusView = mRegisterNameView;
            cancel = true;
        } else if (!isNameValid(name)) {
            mRegisterNameView.setError(getString(R.string.error_invalid_name));
            focusView = mRegisterNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (email.compareTo("") == 0) {
            mRegisterEmailView.setError(getString(R.string.error_field_required));
            focusView = mRegisterEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mRegisterEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mRegisterEmailView;
            cancel = true;
        }

        if (!mTermsView.isChecked()) {
            mTermsView.setError(getString(R.string.error_field_required));
            focusView = mTermsView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validSignInHelper() {
        // Reset errors.
        mSignInEmailView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mSignInEmailView.setError(getString(R.string.error_field_required));
            focusView = mSignInEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mSignInEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mSignInEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        }

        return true;
    }

    private void clearRegisterForm() {
        mRegisterNameView.setText("");
        mRegisterEmailView.setText("");
        mTermsView.setChecked(false);
    }

    private void clearSignInForm() {
        mSignInEmailView.setText("");
    }


    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSignInEmailView.getWindowToken(), 0);
    }

    private OnCompleteListener<AuthResult> onCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Toast toast = new Toast(getApplicationContext());
            if (task.isSuccessful()) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (name != null) {
                    UserProfileChangeRequest request =
                            new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    FirebaseUser user = auth.getCurrentUser();
                    user.updateProfile(request);
                }
                hideKeyBoard();
                launchMainActivity();
                showToast("Authentication Successful!");
            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                showToast("Already Registered!");
            } else {
                showToast("Authentication Failed!");
            }
        }
    };

    private void showToast(String str) {
        Toast toast = Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, 50);
        toast.show();
    }
}

