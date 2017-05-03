package com.example.android.draddest.countryinfo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.draddest.countryinfo.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // member variables for views
    EditText mCountrySearchBox;
    TextView mSearchResultsTextView;
    Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the views
        mCountrySearchBox = (EditText) findViewById(R.id.et_country_name);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_search_result);
        mSearchButton = (Button) findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCountryName = mCountrySearchBox.getText().toString();
                if (TextUtils.isEmpty(enteredCountryName))
                    enteredCountryName = "bosnia";
                URL builtUrl = NetworkUtils.buildUrl(enteredCountryName);
                new RestCountryTask().execute(builtUrl);
            }
        });

    }

    public class RestCountryTask extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String restSearchResults = null;
            try {
                restSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return restSearchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            List<Country> countries = NetworkUtils.extractFeatureFromJson(searchResults);
            int numberOfCountries = countries.size();
            Country firstCountry = countries.get(0);
            String countryName = firstCountry.getName();
            String currencyCode = firstCountry.getCurrencyCode();
            String currencySymbol = firstCountry.getCurrencySymbol();
            String languageName = firstCountry.getLanguage();

            String displayText = "Country name: " + countryName + "\n"
                    + "Currency code: " + currencyCode + "\n"
                    + "Currency symbol: " + currencySymbol + "\n"
                    + "Language: " + languageName;
            mSearchResultsTextView.setText("number of extracted countries: " + numberOfCountries
                    + "\n" + "First country in the list" + "\n" + displayText);
        }
    }
}
