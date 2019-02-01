package com.ee.testprep;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ee.testprep.PracticeMetrics.PracticeType;
import com.ee.testprep.db.DataBaseHelper;
import com.ee.testprep.fragment.DonateFragment;
import com.ee.testprep.fragment.FeedbackFragment;
import com.ee.testprep.fragment.HomeFragment;
import com.ee.testprep.fragment.ModelTestFragment;
import com.ee.testprep.fragment.OnFragmentInteractionListener;
import com.ee.testprep.fragment.PracticeFragment;
import com.ee.testprep.fragment.QuizFragment;
import com.ee.testprep.fragment.RateUsFragment;
import com.ee.testprep.fragment.ResultsFragment;
import com.ee.testprep.fragment.SettingsFragment;
import com.ee.testprep.fragment.StatsFragment;
import com.ee.testprep.fragment.TestPracticeFragment;
import com.ee.testprep.fragment.TestQuizFragment;
import com.ee.testprep.fragment.practice.ExamFragment;
import com.ee.testprep.fragment.practice.SubjectFragment;
import com.ee.testprep.fragment.practice.YearFragment;
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
import androidx.fragment.app.FragmentTransaction;

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
    public static final String TAG_YEAR = "year";
    public static final String TAG_SUBJECT = "subject";
    public static final String TAG_EXAM = "exam";
    public static final String TAG_EASY = "easy";
    public static final String TAG_MEDIUM = "medium";
    public static final String TAG_HARD = "hard";
    public static final String TAG_RANDOM = "random";
    public static final String TAG_NOTHING_TO_SHOW = "nothing";
    public static final String TAG_USERSTATUS = "userstatus";
    public static final String TAG_ALL = "all";
    public static final String TAG_YEAR_XX = "yearxx";
    public static final String TAG_SUBJECT_XX = "subjectxx";
    public static final String TAG_EXAM_XX = "examxx";
    public static final String TAG_QUIZ_QUESTION = "quizQ";
    public static final String TAG_PRACTICE_QUESTION = "quizP";
    public static final int STATUS_QUIZ_END = 1004;
    public static final int STATUS_QUIZ_XX = 1005;
    public static final int STATUS_PRACTICE = 2001;
    public static final int STATUS_PRACTICE_YEAR = 2002;
    public static final int STATUS_PRACTICE_SUBJECT = 2003;
    public static final int STATUS_PRACTICE_EXAM = 2004;
    public static final int STATUS_PRACTICE_EASY = 2005;
    public static final int STATUS_PRACTICE_MEDIUM = 2006;
    public static final int STATUS_PRACTICE_HARD = 2007;
    public static final int STATUS_PRACTICE_RANDOM = 2008;
    public static final int STATUS_PRACTICE_USERSTATUS = 2009;
    public static final int STATUS_PRACTICE_ALL = 2012;
    public static final int STATUS_PRACTICE_END = 2013;
    public static final int STATUS_PRACTICE_YEAR_XX = 3002;
    public static final int STATUS_PRACTICE_SUBJECT_XX = 3003;
    public static final int STATUS_PRACTICE_EXAM_XX = 3004;
    public static final int STATUS_MODELTEST_XX = 4001;
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
    private String className = getClass().getSimpleName();
    private Context mContext;
    private DataBaseHelper dbHelper;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtWebsite;
    private Toolbar toolbar;
    private YearFragment yearFragment;
    private SubjectFragment subjectFragment;
    private ExamFragment examFragment;
    private QuizFragment quizFragment;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    // flag to load nav_home fragment when user presses back key
    private boolean loadHomeOnBackPress = true;
    private TestQuizFragment questionQuizFragment;
    private TestPracticeFragment questionPracticeFragment;

    private static ArrayList<String> examList;
    private static ArrayList<String> subjectList;
    private static ArrayList<String> yearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        /*
        if (!checkPermission()) {
            requestPermission();
        } else {
            L.v(className, "Permissions already granted");
        }
        */

        showCustomDialog();

        //if there is no database already created, create one from .xlsx TODO
        dbHelper = DataBaseHelper.getInstance(this);

        cancelCustomDialog();
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
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        // ("market://details?id=PackageName")));
                        //return true;
                        navItemIndex = INDEX_RATEUS;
                        CURRENT_TAG = TAG_RATEUS;
                        break;

                    case R.id.nav_donate:
                        navItemIndex = INDEX_DONATE;
                        CURRENT_TAG = TAG_DONATE;
                        break;

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
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    protected void showCustomDialog() {

        statusDialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        statusDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        statusDialog.setCancelable(true);
        statusDialog.setContentView(R.layout.loading_dialog);

        final ImageView myImage = statusDialog.findViewById(R.id.loader);
        myImage.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate));
        statusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x7f000000)); //TODO
        statusDialog.show();
    }

    protected void cancelCustomDialog() {
        if (statusDialog != null || statusDialog.isShowing())
            statusDialog.cancel();
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
                ArrayList<String> quizzes = (ArrayList<String>) dbHelper.queryAllQuizzes();
                return QuizFragment.newInstance(quizzes);
            case INDEX_MODELTEST:
                ArrayList<String> modelTests = (ArrayList<String>) dbHelper.queryAllModelTests();
                return ModelTestFragment.newInstance(modelTests);
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

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mUIHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads nav_home fragment when back key is pressed
        // when user is in other fragment than nav_home
        if (loadHomeOnBackPress) {
            // checking if user is on other navigation menu
            // rather than nav_home
            if (navItemIndex != INDEX_HOME) {
                navItemIndex = INDEX_HOME;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
/*        // Inflate the menu; this adds items to the action bar if it is present.

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

/*        //noinspection SimplifiableIfStatement
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

    //TODO
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
    public void onFragmentInteraction(int status) {

        switch (status) {
            case STATUS_PRACTICE_END:
                break;
            case STATUS_PRACTICE:
                showFilters();
                break;
            case STATUS_PRACTICE_YEAR:
                getYears();
                break;
            case STATUS_PRACTICE_SUBJECT:
                getSubjects();
                break;
            case STATUS_PRACTICE_EXAM:
                getExams();
                break;
            case STATUS_PRACTICE_EASY:
                showEasyQuestions();
                break;
            case STATUS_PRACTICE_MEDIUM:
                showMediumQuestions();
                break;
            case STATUS_PRACTICE_HARD:
                showHardQuestions();
                break;
            case STATUS_PRACTICE_RANDOM:
                showRandomQuestions();
                break;
            case STATUS_PRACTICE_USERSTATUS:
                showUserStatus();
                break;
            case STATUS_PRACTICE_ALL:
                showAllQuestions();
                break;

            default:
                break;
        }

    }

    @Override
    public void onFragmentInteraction(int status, String param) {

        switch (status) {
            case STATUS_QUIZ_END:
                showQuizResult(param);
                break;
            case STATUS_PRACTICE_YEAR_XX:
                showYearXX(param);
                break;
            case STATUS_PRACTICE_SUBJECT_XX:
                showSubjectXX(param);
                break;
            case STATUS_PRACTICE_EXAM_XX:
                showExamXX(param);
                break;
            case STATUS_QUIZ_XX:
                startQuiz(param);
                break;
            case STATUS_MODELTEST_XX:
                startQuiz(param);
                break;
            default:
                break;
        }

    }

    /*
    @Override
    public void onSignOut() {
        if (navItemIndex != INDEX_HOME) {
            navItemIndex = INDEX_HOME;
            CURRENT_TAG = TAG_HOME;
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        }
    }*/

    /***************************** START OF QUIZ **************************************************/

    private void startQuiz(String quizName) {
        if (questionQuizFragment == null) {
            questionQuizFragment = TestQuizFragment.newInstance(quizName);
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = questionQuizFragment;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, TAG_QUIZ_QUESTION).addToBackStack(TAG_HOME);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        mUIHandler.post(mPendingRunnable);
    }

    private void showQuizResult(String quizName) {
        if (dbHelper != null) {
            final ResultsFragment resultsFragment = ResultsFragment.newInstance(quizName);

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = resultsFragment;
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_QUIZ_QUESTION)
                            .addToBackStack(TAG_QUIZ);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            mUIHandler.post(mPendingRunnable);
        }
    }

    private void showQuizzes() {
        if (dbHelper != null) {
            ArrayList<String> quizzes = (ArrayList<String>) dbHelper.queryAllQuizzes();
            quizFragment = QuizFragment.newInstance(quizzes);

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = quizFragment;
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_QUIZ).addToBackStack(TAG_HOME);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    /***************************** START OF PRACTICE **********************************************/

    private void showFilters() {

    }

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

    private void showUserStatus() {
        showPracticeQuestions(PracticeType.STARRED, null);
    }

    private void showEasyQuestions() {
        showPracticeQuestions(PracticeType.EASY, null);
    }

    private void showMediumQuestions() {
        showPracticeQuestions(PracticeType.MEDIUM, null);
    }

    private void showHardQuestions() {
        showPracticeQuestions(PracticeType.HARD, null);
    }

    private void showRandomQuestions() {
        showPracticeQuestions(PracticeType.RANDOM, null);
    }

    private void showAllQuestions() {
        showPracticeQuestions(PracticeType.ALL, null);
    }

    private void showYearXX(String year) {
        showPracticeQuestions(PracticeType.YEAR, year);
    }

    private void showSubjectXX(String subject) {
        showPracticeQuestions(PracticeType.SUBJECT, subject);
    }

    private void showExamXX(String exam) {
        showPracticeQuestions(PracticeType.EXAM, exam);
    }

    private void showPracticeQuestions(PracticeType category, String subCategory) {

        if (dbHelper != null) {
            questionPracticeFragment = TestPracticeFragment.newInstance(category, subCategory);

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_PRACTICE_QUESTION)
                            .addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            mUIHandler.post(mPendingRunnable);
        }
    }

    /******************************* END OF PRACTICE **********************************************/
}
