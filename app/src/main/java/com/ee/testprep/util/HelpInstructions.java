package com.ee.testprep.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ee.testprep.R;

import java.util.HashMap;

public class HelpInstructions {

    private Context mContext;
    private Dialog helpDialog;
    private static HashMap<Integer, String> helpPrefsMap = new HashMap<>();
    private static StringBuilder mPracticeHelp = new StringBuilder(practiceInstructions());
    private static StringBuilder mQuizzesHelp = new StringBuilder(quizInstructions());
    private static StringBuilder mModelTestHelp = new StringBuilder(quizInstructions());

    public HelpInstructions(Context context) {
        mContext = context;

        if(helpPrefsMap.isEmpty()) {
            helpPrefsMap.put(0, "HELP_PRACTICE_PREF");
            helpPrefsMap.put(1, "HELP_QUIZ_PREF");
            helpPrefsMap.put(2, "HELP_MODELTEST_PREF");
        }
    }

    private static StringBuilder practiceInstructions() {
        StringBuilder sb = new StringBuilder();

        sb.append("\u2022 Make a selection from the list of available filters\n");
        sb.append("\u2022 Click Start Button to practice questions\n");
        sb.append("\u2022 Click on star icon on top-right to review it later\n");
        sb.append("\u2022 Press back button to go home\n");

        return sb;
    }

    private static StringBuilder quizInstructions() {
        StringBuilder sb = new StringBuilder();

        sb.append("\u2022 Swipe left for next and right for previous questions\n");
        sb.append("\u2022 Every question in the quiz has the same weightage\n");
        sb.append("\u2022 Negative marking is applicable for incorrect answers\n");
        sb.append("\u2022 Make use of Pause/Play button for a quick break\n");

        return sb;
    }

    // id0 : Practice; id1: Quiz; id2: ModelTest
    public void showHelpDialog(int id) {

        //dont show dialog of if "Dont show this message" checkbox is enabled
        if(PreferenceUtils.readPrefs(mContext, helpPrefsMap.get(id), false))
            return;

        final Handler handler = new Handler();
            handler.postDelayed(() -> {
            helpDialog = new Dialog(mContext);
            helpDialog.setCancelable(true);
            helpDialog.setContentView(R.layout.help_dialog);

            WindowManager.LayoutParams params = helpDialog.getWindow().getAttributes();
            params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

            TextView tvInstructions = helpDialog.findViewById(R.id.help_instructions);
            ImageView ivClose = helpDialog.findViewById(R.id.help_close);
            CheckBox cbDontShow = helpDialog.findViewById(R.id.cb_help_dont_show);
            TextView tvDontShowMsg = helpDialog.findViewById(R.id.tv_help_dont_show);

            ivClose.setOnClickListener(v -> helpDialog.dismiss());
            cbDontShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked) {
                    PreferenceUtils.savePrefs(mContext, helpPrefsMap.get(id), true);
                }
            });

            tvDontShowMsg.setOnClickListener(v -> {
                if(cbDontShow.isChecked()) {
                    cbDontShow.setChecked(false);
                    return;
                }
                cbDontShow.setChecked(true);
            });

            switch (id) {
                case 0:
                    tvInstructions.setText(mPracticeHelp.toString());
                    break;
                case 1:
                    tvInstructions.setText(mQuizzesHelp);
                    break;
                case 2:
                    tvInstructions.setText(mModelTestHelp);
                    break;
            }

            //helpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            helpDialog.show();

        }, 500);
    }
}
