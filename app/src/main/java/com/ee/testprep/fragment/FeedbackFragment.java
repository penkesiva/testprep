package com.ee.testprep.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ee.testprep.R;

public class FeedbackFragment extends Fragment {
    private static String FEEDBACK_EMAIL = "equalityempowerment@gmail.com";
    private EditText title;
    private EditText description;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.feedback_title);
        description = view.findViewById(R.id.feedback_description);
        Button send = view.findViewById(R.id.feedback_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = "";
                if (title.getText() != null) subject += title.getText().toString().trim();
                String body = "";
                if (description.getText() != null) body += description.getText().toString().trim();

                if (subject.trim().isEmpty() && body.trim().isEmpty()) return;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + FEEDBACK_EMAIL));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.trim());
                emailIntent.putExtra(Intent.EXTRA_TEXT, body.trim());
                startActivity(Intent.createChooser(emailIntent, "Feedback Email"));
                title.setText("");
                description.setText("");
            }
        });
    }
}
