package com.example.android.draddest.alertdialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity to display as dialog
 */

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);

        // receive intent that started this activity
        final Intent dialogIntent = getIntent();

        // find views
        final EditText editText = (EditText) findViewById(R.id.dialog_activity_edittext);
        Button cancelButton = (Button) findViewById(R.id.dialog_cancel_button);
        Button okButton = (Button) findViewById(R.id.dialog_ok_button);

        // clicking cancel button finishes the activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, dialogIntent);
                // finish();
            }
        });

        // clicking on ok button saves the text from edit text view
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEntered = editText.getText().toString().trim();
                dialogIntent.putExtra("Entered Text", textEntered);
                setResult(RESULT_OK, dialogIntent);
                finish();
            }
        });
    }
}
