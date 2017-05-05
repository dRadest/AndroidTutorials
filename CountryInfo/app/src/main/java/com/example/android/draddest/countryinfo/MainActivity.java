package com.example.android.draddest.countryinfo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.draddest.countryinfo.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CountryAdapter mCountryAdapter;
    private RecyclerView mCountryRecyclerView;

    // member variables for views
    EditText mCountrySearchBox;
    //TextView mSearchResultsTextView;
    Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the views
        mCountrySearchBox = (EditText) findViewById(R.id.et_country_name);
        //mSearchResultsTextView = (TextView) findViewById(R.id.tv_search_result);
        mSearchButton = (Button) findViewById(R.id.search_button);

        mCountryRecyclerView = (RecyclerView) findViewById(R.id.country_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mCountryRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mCountryRecyclerView.setLayoutManager(linearLayoutManager);

        // specify an adapter
        mCountryAdapter = new CountryAdapter();
        mCountryRecyclerView.setAdapter(mCountryAdapter);


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
            mCountryAdapter.setCountryData(countries);
        }
    }
}
