package com.example.android.draddest.alertdialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView displayTextView = (TextView) findViewById(R.id.display_textview);

        Button alertDialogButton = (Button) findViewById(R.id.alertdialog_button);
        alertDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Alert Dialog");
                builder.setMessage("alert dialog message");

                // programmatically add edit text view
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setHint("Enter text");
                builder.setView(input);

                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredText = input.getText().toString().trim();
                        if (TextUtils.isEmpty(enteredText)){
                            enteredText = "blank";
                        }
                        displayTextView.setText("Text entered: " + enteredText);
                        dialog.dismiss();
                    }
                });
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        Button cadButton = (Button) findViewById(R.id.custom_alertdialog_button);
        cadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Custom Alert Dialog title");
                builder.setMessage("custom alert dialog message");
                LayoutInflater inflater = getLayoutInflater();
                final View dialogLayout = inflater.inflate(R.layout.custom_alertdialog, null);
                builder.setView(dialogLayout);
                builder.setPositiveButton("Positive", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editTextView = (EditText) dialogLayout.findViewById(R.id.cad_edittext);
                        String textEntered = editTextView.getText().toString().trim();
                        if (TextUtils.isEmpty(textEntered)){
                            textEntered = "blank";
                        }
                        displayTextView.setText("Text entered: " + textEntered);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        Button cdButton = (Button) findViewById(R.id.customdialog_button);
        cdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create custom dialog
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Custom Dialog Title");

                // find and set dialog components
                Button discardButton = (Button) dialog.findViewById(R.id.discard_button);
                discardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button submitButton = (Button) dialog.findViewById(R.id.submit_button);
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) dialog.findViewById(R.id.custom_dialog_edittext);
                        String textEntered = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(textEntered)){
                            textEntered = "blank";
                        }
                        displayTextView.setText("Number entered: " + textEntered);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        Button adButton = (Button) findViewById(R.id.activity_dialog_button);
        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialogIntent = new Intent(MainActivity.this, DialogActivity.class);
                startActivityForResult(dialogIntent, 187);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 187 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String text = extras.getString("Entered Text");
            if (TextUtils.isEmpty(text)){
                text = "blank";
            }
            TextView displayTextView = (TextView) findViewById(R.id.display_textview);
            displayTextView.setText("Text entered: " + text);
        }
    }
}
