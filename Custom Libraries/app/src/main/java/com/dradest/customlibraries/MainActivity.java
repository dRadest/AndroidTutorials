package com.dradest.customlibraries;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dradest.answerlibrary.AnswerLUE;

public class MainActivity extends AppCompatActivity {
    Button mLUEButton;
    TextView mAnswerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the answer
        AnswerLUE answerHolder = new AnswerLUE();
        final String theAnswer = answerHolder.giveMeTheAnswer();

        mAnswerView = findViewById(R.id.ans_tv);
        mLUEButton = findViewById(R.id.lue_btn);
        mLUEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnswerView.setText(theAnswer);
            }
        });
    }
}
