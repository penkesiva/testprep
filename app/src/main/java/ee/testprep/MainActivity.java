package ee.testprep;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ee.testprep.db.DBRow;
import ee.testprep.db.DataBaseHelper;
import ee.testprep.fragment.DonateFragment;
import ee.testprep.fragment.FeedbackFragment;
import ee.testprep.fragment.HomeFragment;
import ee.testprep.fragment.NothingToShowFragment;
import ee.testprep.fragment.PracticeFragment;
import ee.testprep.fragment.ModelTestFragment;
import ee.testprep.fragment.OnFragmentInteractionListener;
import ee.testprep.fragment.QuestionPracticeFragment;
import ee.testprep.fragment.QuizFragment;
import ee.testprep.fragment.QuestionQuizFragment;
import ee.testprep.fragment.RateUsFragment;
import ee.testprep.fragment.SettingsFragment;
import ee.testprep.fragment.StatsFragment;
import ee.testprep.fragment.practice.ExamFragment;
import ee.testprep.fragment.practice.SubjectFragment;
import ee.testprep.fragment.practice.UserStatusFragment;
import ee.testprep.fragment.practice.YearFragment;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private String className = getClass().getSimpleName();
    private Context mContext;

    private DataBaseHelper dbHelper;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtWebsite;
    private Toolbar toolbar;

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

    private static final int INDEX_HOME = 0;
    private static final int INDEX_LEARN = 1;
    private static final int INDEX_QUIZ = 2;
    private static final int INDEX_MODELTEST = 3;
    private static final int INDEX_STATS = 4;
    private static final int INDEX_SETTINGS = 5;
    private static final int INDEX_FEEDBACK = 6;
    private static final int INDEX_RATEUS = 7;
    private static final int INDEX_DONATE = 8;

    public static int navItemIndex = INDEX_HOME;
    public static String CURRENT_TAG = TAG_HOME;

    public static final int STATUS_QUIZ_START = 1001;
    public static final int STATUS_QUIZ_NEXT = 1002;
    public static final int STATUS_QUIZ_PREVIOUS = 1003;
    public static final int STATUS_QUIZ_END = 1004;

    public static final int STATUS_PRACTICE = 2001;
    public static final int STATUS_PRACTICE_YEAR = 2002;
    public static final int STATUS_PRACTICE_SUBJECT = 2003;
    public static final int STATUS_PRACTICE_EXAM = 2004;
    public static final int STATUS_PRACTICE_EASY = 2005;
    public static final int STATUS_PRACTICE_MEDIUM = 2006;
    public static final int STATUS_PRACTICE_HARD = 2007;
    public static final int STATUS_PRACTICE_RANDOM = 2008;
    public static final int STATUS_PRACTICE_USERSTATUS = 2009;
    public static final int STATUS_PRACTICE_NEXT = 2010;
    public static final int STATUS_PRACTICE_PREVIOUS = 2011;
    public static final int STATUS_PRACTICE_ALL = 2012;

    public static final int STATUS_PRACTICE_YEAR_XX = 3002;
    public static final int STATUS_PRACTICE_SUBJECT_XX = 3003;
    public static final int STATUS_PRACTICE_EXAM_XX = 3004;
    public static final int STATUS_PRACTICE_USERSTATUS_XX = 3005;

    public static final int TIME_INSEC_PER_QUESTION = 30; //30s/question

    private YearFragment yearFragment;
    private SubjectFragment subjectFragment;
    private ExamFragment examFragment;
    private UserStatusFragment userStatusFragment;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load nav_home fragment when user presses back key
    private boolean loadHomeOnBackPress = true;
    public static Handler mUIHandler;

    //permissions
    private static final int PERMISSION_REQUEST_CODE = 1;

    private ArrayList<DBRow> quizList;
    private QuizMetrics quiz;

    private ArrayList<DBRow> practiceQuestions;
    private PracticeMetrics practice;

    private QuestionQuizFragment questionQuizFragment;
    private Fragment questionPracticeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundResource(R.drawable.cover);

        //this.setTheme(android.R.style.ThemeOverlay_Material_Dark);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
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

        //set permissions if not already set
        //TODO check multiple permissions and less than 23 API
        if (!checkPermission()) {
            requestPermission();
        } else {
            L.v(className, "Permissions already granted");
        }

        //if there is no database already created, create one from .xlsx TODO
        dbHelper = DataBaseHelper.getInstance(this);
    }

    private void hideStatusBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setUpNavigationView() {

        //remove the icon tint; this makes the icon look colored
        navigationView.setItemIconTintList(null);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

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


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
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
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case INDEX_LEARN:
                PracticeFragment practiceFragment = PracticeFragment.newInstance();
                return practiceFragment;
            case INDEX_QUIZ:
                QuizFragment quizFragment = QuizFragment.newInstance();
                return quizFragment;
            case INDEX_MODELTEST:
                ModelTestFragment modelTestFragment = new ModelTestFragment();
                return modelTestFragment;
            case INDEX_STATS:
                StatsFragment statsFragment = new StatsFragment();
                return statsFragment;
            case INDEX_SETTINGS:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            case INDEX_FEEDBACK:
                FeedbackFragment feedbackFragment = new FeedbackFragment();
                return feedbackFragment;
            case INDEX_RATEUS:
                RateUsFragment.openAppRating(mContext);
                return null;
            case INDEX_DONATE:
                DonateFragment donateFragment = new DonateFragment();
                return donateFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(getApplicationName() + ": " + activityTitles[navItemIndex]);
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : mContext.getString(stringId);
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
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
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
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }*/

        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
            case STATUS_QUIZ_START:
                startQuiz();
                break;
            case STATUS_QUIZ_NEXT:
                nextQuizQuestion();
                break;
            case STATUS_QUIZ_PREVIOUS:
                prevQuizQuestion();
                break;
            case STATUS_QUIZ_END:
                break;
            case STATUS_PRACTICE:
                showFilters();
                break;
            case STATUS_PRACTICE_YEAR:
                showYears();
                break;
            case STATUS_PRACTICE_SUBJECT:
                showSubjects();
                break;
            case STATUS_PRACTICE_EXAM:
                showExams();
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
            case STATUS_PRACTICE_NEXT:
                nextPracticeQuestion();
                break;
            case STATUS_PRACTICE_PREVIOUS:
                prevPracticeQuestion();
                break;

            default:
                break;
        }

    }

    @Override
    public void onFragmentInteraction(int status, String filter) {

        switch (status) {
            case STATUS_PRACTICE_YEAR_XX:
                showYearXX(filter);
                break;
            case STATUS_PRACTICE_SUBJECT_XX:
                showSubjectXX(filter);
                break;
            case STATUS_PRACTICE_EXAM_XX:
                showExamXX(filter);
                break;
            default:
                break;
        }

    }

    /***************************** START OF QUIZ **************************************************/

    private void uiRefresh() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (quiz != null) {
                    sendDataToFragment(quiz.getRemainingTimeInSec());
                }
            }
        }, 0, 1000);
    }

    private void sendDataToFragment(int time) {
        if (questionQuizFragment != null) {
            (questionQuizFragment).uiRefresh(time, quiz.getProgress());
        }
    }

    private void startQuiz() {

        if (dbHelper != null) {
            quizList = (ArrayList<DBRow>) dbHelper.queryQuestionsQuiz();

            quiz = new QuizMetrics(quizList, quizList.size() * TIME_INSEC_PER_QUESTION);
            quiz.startQuiz();
            uiRefresh();
            questionQuizFragment = QuestionQuizFragment.newInstance(quiz.getNextQuestion(), quizList.size());

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionQuizFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_QUIZ_QUESTION)
                            .addToBackStack(TAG_HOME);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            mUIHandler.post(mPendingRunnable);
        }
    }

    private void nextQuizQuestion() {

        if (dbHelper != null) {
            DBRow question = quiz.getNextQuestion();
            if (question != null) {
                questionQuizFragment = QuestionQuizFragment.newInstance(question, quizList.size());

                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        Fragment fragment = questionQuizFragment;
                        FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
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
    }

    private void prevQuizQuestion() {

        if (dbHelper != null) {
            //questionFragment = QuestionFragment.newInstance((quizList.get(--questionIndex)));
            DBRow question = quiz.getPrevQuestion();
            if (question != null) {
                questionQuizFragment = QuestionQuizFragment.newInstance(question, quizList.size());
                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        Fragment fragment = questionQuizFragment;
                        FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
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
    }

    /***************************** START OF PRACTICE **********************************************/

    private void showFilters() {

    }

    private void showYears() {
        if (dbHelper != null) {
            ArrayList<String> years = dbHelper.queryYear();
            yearFragment = YearFragment.newInstance(years);

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = yearFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_YEAR).
                            addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showSubjects() {
        if (dbHelper != null) {
            ArrayList<String> subjects = dbHelper.querySubject();
            subjectFragment = SubjectFragment.newInstance(subjects);

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = subjectFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_SUBJECT).addToBackStack(TAG_PRACTICE);
                    ;
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showExams() {
        if (dbHelper != null) {
            ArrayList<String> exams = dbHelper.queryExam();
            examFragment = ExamFragment.newInstance(exams);

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = examFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_EXAM).addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showUserStatus() {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.queryQuestionsUserStatus();

            if (practiceQuestions.isEmpty()) {
                //no questions in this category, show empty fragment
                questionPracticeFragment = NothingToShowFragment.newInstance();
            } else {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);
                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_NOTHING_TO_SHOW).
                            addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showEasyQuestions() {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.queryQuestionsDifficulty("0 AND 3");

            if(practiceQuestions.size() > 0) {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);

                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            } else {
                questionPracticeFragment = new NothingToShowFragment();
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_EASY).
                            addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showMediumQuestions() {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.queryQuestionsDifficulty("4 AND 6");

            if(practiceQuestions.size() > 0) {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);

                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            } else {
                questionPracticeFragment = new NothingToShowFragment();
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_MEDIUM).
                            addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showHardQuestions() {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.queryQuestionsDifficulty("7 AND 9");

            if (practiceQuestions.size() > 0) {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);
                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            } else {
                questionPracticeFragment = new NothingToShowFragment();
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_HARD).
                            addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showRandomQuestions() {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.queryQuestionsRandom();

            if (practiceQuestions.size() > 0) {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);
                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            } else {
                questionPracticeFragment = new NothingToShowFragment();
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_RANDOM).
                            addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showAllQuestions() {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.queryAllQuestions();

            if (practiceQuestions.size() > 0) {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);
                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            } else {
                questionPracticeFragment = new NothingToShowFragment();
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_ALL).
                            addToBackStack(TAG_PRACTICE);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showYearXX(String year) {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.queryYearExt(year);

            if(practiceQuestions.size() > 0) {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);

                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            } else {
                questionPracticeFragment = new NothingToShowFragment();
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_YEAR_XX).
                            addToBackStack(TAG_YEAR);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showSubjectXX(String subject) {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.querySubjectExt(subject);

            if(practiceQuestions.size() > 0) {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);

                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            } else {
                questionPracticeFragment = new NothingToShowFragment();
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_SUBJECT_XX).
                            addToBackStack(TAG_SUBJECT);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void showExamXX(String exam) {

        if (dbHelper != null) {
            //query questions
            practiceQuestions = (ArrayList<DBRow>) dbHelper.queryExamExt(exam);

            if(practiceQuestions.size() > 0) {
                //start a nav_practice session
                practice = new PracticeMetrics(practiceQuestions);

                //get a question fragment
                questionPracticeFragment = QuestionPracticeFragment.newInstance(practiceQuestions.get(0));
            } else {
                questionPracticeFragment = new NothingToShowFragment();
            }

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = questionPracticeFragment;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, TAG_EXAM_XX).
                            addToBackStack(TAG_EXAM);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            MainActivity.mUIHandler.post(mPendingRunnable);
        }
    }

    private void nextPracticeQuestion() {

        if (dbHelper != null) {
            DBRow question = practice.getNextQuestion();
            if (question != null) {
                questionPracticeFragment = QuestionPracticeFragment.newInstance(question);

                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        Fragment fragment = questionPracticeFragment;
                        FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, TAG_PRACTICE_QUESTION)
                                .addToBackStack(TAG_PRACTICE);
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                };

                // If mPendingRunnable is not null, then add to the message queue
                mUIHandler.post(mPendingRunnable);
            } else {
                L.e(className, "No more questions available");
            }
        }
    }

    private void prevPracticeQuestion() {

        if (dbHelper != null) {
            DBRow question = practice.getPrevQuestion();
            if (question != null) {
                questionPracticeFragment = QuestionPracticeFragment.newInstance(question);

                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        Fragment fragment = questionPracticeFragment;
                        FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, TAG_PRACTICE_QUESTION)
                                .addToBackStack(TAG_PRACTICE);
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                };

                // If mPendingRunnable is not null, then add to the message queue
                mUIHandler.post(mPendingRunnable);
            } else {
                L.e(className, "No previous questions available");
            }
        }
    }

    /******************************* END OF PRACTICE **********************************************/
}
