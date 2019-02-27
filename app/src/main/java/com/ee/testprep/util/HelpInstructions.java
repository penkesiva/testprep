package com.ee.testprep.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
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

        sb.append("\u25CF Make a selection from the list of available filters\n");
        sb.append("\u25CF Click Start Button to practice questions\n");
        sb.append("\u25CF Click on star icon on top-right to review it later\n");
        sb.append("\u25CF Press back button to go home\n");
        sb.append("\n   Good Luck!");

        return sb;
    }

    private static StringBuilder quizInstructions() {
        StringBuilder sb = new StringBuilder();

        sb.append("\u25CF Choose your favorite subject/exam from the drop-down box\n");
        sb.append("\u25CF Every Quiz has 25 questions\n");
        sb.append("\u25CF Time to complete each quiz is about 15 min\n");
        sb.append("\u25CF Every Model Test has varied questions depends on the exam\n");
        sb.append("\u25CF You can check your results at the end of the exam\n");
        sb.append("\u25CF If you retake the exam, the previous exam results will be erased\n");
        sb.append("\u25CF You can submit the exam at any time\n");
        sb.append("\u25CF Make use of Pause/Play button for a quick break\n");
        sb.append("\n   Good Luck!");

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

            helpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            helpDialog.show();

        }, 500);
    }
}
