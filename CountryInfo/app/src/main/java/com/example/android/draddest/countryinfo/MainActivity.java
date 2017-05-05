package com.example.android.draddest.countryinfo;

import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>{

    /* A constant to save and restore the URL that is being  */
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    // unique Loader ID
    private static final int COUNTRY_LOADER_ID = 1337;

    // LoaderManager member variable
    private LoaderManager mLoaderManager;

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

        // initialize the loader
        mLoaderManager = getSupportLoaderManager();
        mLoaderManager.initLoader(COUNTRY_LOADER_ID, null, this);


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCountryName = mCountrySearchBox.getText().toString();
                if (TextUtils.isEmpty(enteredCountryName))
                    enteredCountryName = "bosnia";
                URL builtUrl = NetworkUtils.buildUrl(enteredCountryName);
                Bundle queryBundle = new Bundle();
                queryBundle.putString(SEARCH_QUERY_URL_EXTRA, builtUrl.toString());
                Loader<String> countryLoader = mLoaderManager.getLoader(COUNTRY_LOADER_ID);
                if (countryLoader == null) {
                    mLoaderManager.initLoader(COUNTRY_LOADER_ID, queryBundle, MainActivity.this);
                } else {
                    mLoaderManager.restartLoader(COUNTRY_LOADER_ID, queryBundle, MainActivity.this);
                }
            }
        });
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mCountryJson;
            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                if (mCountryJson != null) {
                    deliverResult(mCountryJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String searchUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                String restSearchResults = null;
                try {
                    URL searchUrl = new URL(searchUrlString);
                    restSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                    return restSearchResults;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                mCountryJson = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        List<Country> countries = NetworkUtils.extractFeatureFromJson(data);
        mCountryAdapter.setCountryData(countries);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String queryUrl = mCountrySearchBox.getText().toString();
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);
    }
}
