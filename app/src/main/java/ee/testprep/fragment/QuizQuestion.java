package ee.testprep.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import ee.testprep.DBRow;
import ee.testprep.MainActivity;
import ee.testprep.R;

public class QuizQuestion extends Fragment{

    private static String className = QuizQuestion.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private DBRow mQuestion;
    private OnFragmentInteractionListener mListener;

    public QuizQuestion() {
    }

    public static QuizQuestion newInstance(DBRow question) {
        QuizQuestion fragment = new QuizQuestion();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1, question);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestion = (DBRow) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.quiz_question, container, false);

        TextView tvQuestion = view.findViewById(R.id.question);
        tvQuestion.setText(mQuestion.question);
        tvQuestion.setMovementMethod(new ScrollingMovementMethod());

        RadioButton tvOptA = view.findViewById(R.id.rb_optA);
        tvOptA.setText(mQuestion.optionA);
        RadioButton tvOptB = view.findViewById(R.id.rb_optB);
        tvOptB.setText(mQuestion.optionB);
        RadioButton tvOptC = view.findViewById(R.id.rb_optC);
        tvOptC.setText(mQuestion.optionC);
        RadioButton tvOptD = view.findViewById(R.id.rb_optD);
        tvOptD.setText(mQuestion.optionD);

        Button btnNext = view.findViewById(R.id.nextButton);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(MainActivity.STATUS_QUIZ_NEXT);
            }
        });

        Button btnPrev = view.findViewById(R.id.previousButton);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(MainActivity.STATUS_QUIZ_PREVIOUS);
            }
        });

        // populate the question
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
}
