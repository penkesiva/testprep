package com.ee.testprep;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.db.MetaData;
import com.ee.testprep.db.PracticeViewModel;
import com.ee.testprep.db.UserDataViewModel;
import com.ee.testprep.fragment.DonateFragment;
import com.ee.testprep.fragment.FeedbackFragment;
import com.ee.testprep.fragment.HomeFragment;
import com.ee.testprep.fragment.OnFragmentInteractionListener;
import com.ee.testprep.fragment.PracticeFragment;
import com.ee.testprep.fragment.RateUsFragment;
import com.ee.testprep.fragment.ResultsFragment;
import com.ee.testprep.fragment.SettingsFragment;
import com.ee.testprep.fragment.StatsFragment;
import com.ee.testprep.fragment.TestPracticeFragment;
import com.ee.testprep.fragment.TestQuizFragment;
import com.ee.testprep.fragment.TestsListFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    // tags used to attach the fragments
    public static final String TAG_HOME = "nav_home";
    public static final String TAG_PRACTICE = "nav_practice";
    public static final String TAG_QUIZ = "quiz";
    public static final String TAG_MODELTEST = "model_test";
    public static final String TAG_STATS = "nav_stats";
    public static final String TAG_SETTINGS = "nav_settings";
    public static final String TAG_FEEDBACK = "nav_feedback";
    public static final String TAG_RATEUS = "nav_rateus";
    public static final String TAG_DONATE = "nav_donate";
    public static final String TAG_QUIZ_QUESTION = "quizQ";
    public static final String TAG_PRACTICE_QUESTION = "quizP";
    public static final int STATUS_QUIZ_MODELTEST_END = 1001;
    public static final int STATUS_QUIZ_MODELTEST_START = 1002;
    public static final int STATUS_PRACTICE_MORE = 1003;
    private static final int INDEX_HOME = 0;
    private static final int INDEX_LEARN = 1;
    private static final int INDEX_QUIZ = 2;
    private static final int INDEX_MODELTEST = 3;
    private static final int INDEX_STATS = 4;
    private static final int INDEX_SETTINGS = 5;
    private static final int INDEX_FEEDBACK = 6;
    private static final int INDEX_RATEUS = 7;
    private static final int INDEX_DONATE = 8;
    //permissions
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static int navItemIndex = INDEX_HOME;
    public static String CURRENT_TAG = TAG_HOME;
    public static Handler mUIHandler;
    private static Dialog statusDialog;
    private static ArrayList<String> examList;
    private static ArrayList<String> subjectList;
    private static ArrayList<String> yearList;
    private String className = getClass().getSimpleName();
    private Context mContext;
    private DataBaseHelper dbHelper;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtWebsite;
    private Toolbar toolbar;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private String savedPracticeQuery;

    public static ArrayList<String> getYears() {
        DataBaseHelper helper = DataBaseHelper.getInstance();
        if (helper != null) {
            yearList = helper.queryYears();
            return yearList;
        }

        return new ArrayList<>();
    }

    public static ArrayList<String> getSubjects() {
        DataBaseHelper helper = DataBaseHelper.getInstance();
        if (helper != null) {
            subjectList = helper.querySubjects();
            return subjectList;
        }

        return new ArrayList<>();
    }

    public static ArrayList<String> getExams() {
        DataBaseHelper helper = DataBaseHelper.getInstance();
        if (helper != null) {
            examList = helper.queryExams();
            return examList;
        }

        return new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProviders.of(this).get(PracticeViewModel.class);
        ViewModelProviders.of(this).get(UserDataViewModel.class);

        View rootView = findViewById(android.R.id.content);
        //rootView.setBackgroundResource(R.drawable.cover);
        //rootView.setBackgroundColor(getResources().getColor(R.color.colorBackground2));

        //this.setTheme(android.R.style.ThemeOverlay_Material_Dark);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUIHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtWebsite = navHeader.findViewById(R.id.website);
        imgProfile = navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        hideStatusBar();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        showLoadingDialog();
        dbHelper = DataBaseHelper.getInstance(this);
        dbHelper.dummyDBCall();
        cancelLoadingDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelLoadingDialog();
    }

    private void hideStatusBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setUpNavigationView() {

        //remove the icon tint; this makes the icon look colored
        navigationView.setItemIconTintList(null);

        //Setting Navigation View Item Selected Listener to handle the item click of the
        // navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(getBaseContext(), "PLEASE REGISTER!", Toast.LENGTH_LONG).show();
                    return true;
                }

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;a
                    case R.id.nav_home:
                        navItemIndex = INDEX_HOME;
                        CURRENT_TAG = TAG_HOME;
                        break;

                    case R.id.nav_practice:
                        navItemIndex = INDEX_LEARN;
                        CURRENT_TAG = TAG_PRACTICE;
                        break;

                    case R.id.nav_quiz:
                        navItemIndex = INDEX_QUIZ;
                        CURRENT_TAG = TAG_QUIZ;
                        break;

                    case R.id.nav_modeltest:
                        navItemIndex = INDEX_MODELTEST;
                        CURRENT_TAG = TAG_MODELTEST;
                        break;

                    case R.id.nav_stats:
                        navItemIndex = INDEX_STATS;
                        CURRENT_TAG = TAG_STATS;
                        break;

                    case R.id.nav_settings:
                        navItemIndex = INDEX_SETTINGS;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;

                    case R.id.nav_feedback:
                        navItemIndex = INDEX_FEEDBACK;
                        CURRENT_TAG = TAG_FEEDBACK;
                        break;

                    case R.id.nav_rateus:
                        // Uncomment after testing
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + getPackageName())));
                        return true;
                    //navItemIndex = INDEX_RATEUS;
                    //CURRENT_TAG = TAG_RATEUS;
                    //break;

//                    case R.id.nav_donate:
//                        navItemIndex = INDEX_DONATE;
//                        CURRENT_TAG = TAG_DONATE;
//                        break;

                    default:
                        navItemIndex = INDEX_HOME;
                        CURRENT_TAG = TAG_HOME;
                        break;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want anything to
                // happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to
                // happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    protected void showLoadingDialog() {
        statusDialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        statusDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        statusDialog.setCancelable(true);
        statusDialog.setContentView(R.layout.loading_dialog);

        final ImageView myImage = statusDialog.findViewById(R.id.loader);
        myImage.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate));
        statusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x7f000000));
        statusDialog.show();
    }

    protected void cancelDialog() {
        if (statusDialog != null && statusDialog.isShowing())
            statusDialog.cancel();
    }

    private void cancelLoadingDialog() {
        new Thread(() -> {
            while (!dbHelper.isDataBaseReady()) {
            }
            cancelDialog();
        }).start();
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
//        txtName.setText("Test Prep");
//        txtWebsite.setText("equality.org");

/*
        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
*/

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case INDEX_HOME:
                return new HomeFragment();
            case INDEX_LEARN:
                return PracticeFragment.newInstance();
            case INDEX_QUIZ:
                ArrayList<MetaData> quizzes = (ArrayList<MetaData>) dbHelper.queryAllQuizzes();
                return TestsListFragment.newInstance(quizzes);
            case INDEX_MODELTEST:
                ArrayList<MetaData> modelTests =
                        (ArrayList<MetaData>) dbHelper.queryAllModelTests();
                return TestsListFragment.newInstance(modelTests);
            case INDEX_STATS:
                return new StatsFragment();
            case INDEX_SETTINGS:
                return new SettingsFragment();
            case INDEX_FEEDBACK:
                return new FeedbackFragment();
            case INDEX_RATEUS:
                RateUsFragment.openAppRating(mContext);
                return null;
            case INDEX_DONATE:
                return new DonateFragment();
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() :
                mContext.getString(stringId);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        //Closing drawer on item click
        drawer.closeDrawers();

//        // if user select the current navigation menu again, don't do anything
//        // just close the navigation drawer
//        if (CURRENT_TAG != TAG_HOME && getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
//            return;
//        }

        FragmentManager fm = getSupportFragmentManager();
        int backStackEntryCount = fm.getBackStackEntryCount();
        for (int i = 0; i < backStackEntryCount; i++) {
            fm.popBackStack();
        }

        if (CURRENT_TAG == TAG_HOME && getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            return;
        }

        // update the main content by replacing fragments
        Fragment fragment = getHomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        if (CURRENT_TAG == TAG_HOME) {
            fragmentTransaction.add(R.id.frame, fragment, TAG_HOME);
        } else {
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG).addToBackStack(null);
        }
        fragmentTransaction.commitAllowingStateLoss();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.e("MainActivity", "MainActivity.onBackPressed: current tag = drawer");
            drawer.closeDrawers();
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 1) {
            navItemIndex = INDEX_HOME;
            selectNavMenu();
            setToolbarTitle();
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when nav_home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast
            .LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast
            .LENGTH_LONG).show();
        }*/

        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Storage permissions are required to store database." +
                    "Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    L.e(className, "Permission Granted!");
                } else {
                    L.e(className, "Permission Denied!");
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(int status, String param) {
        switch (status) {
            case STATUS_QUIZ_MODELTEST_END:
                showQuizResult(param);
                break;
            case STATUS_QUIZ_MODELTEST_START:
                startQuiz(param);
                break;
            case STATUS_PRACTICE_MORE:
                showPracticeQuestions(param);
                break;
            default:
                break;
        }
    }

    /***************************** START OF QUIZ **************************************************/

    private void startQuiz(String quizName) {
        if (dbHelper == null) return;

        Fragment fragment = TestQuizFragment.newInstance(quizName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, TAG_QUIZ_QUESTION).addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showQuizResult(String quizName) {
        if (dbHelper == null) return;

        getSupportFragmentManager().popBackStack();
        Fragment fragment = ResultsFragment.newInstance(quizName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, TAG_QUIZ_QUESTION).addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /***************************** START OF PRACTICE **********************************************/

    private void showPracticeQuestions(String query) {
        if (dbHelper == null) return;

        boolean parcticeMore = query == null;
        if (parcticeMore) {
            query = savedPracticeQuery;
            getSupportFragmentManager().popBackStack();
        } else {
            savedPracticeQuery = query;
        }

        Fragment fragment = TestPracticeFragment.newInstance(query);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, TAG_PRACTICE_QUESTION)
                .addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }
}
