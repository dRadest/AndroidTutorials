package com.example.android.draddest.countryinfo.utilities;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.draddest.countryinfo.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utilities used to communicate with the network
 */

public class NetworkUtils {

    final static String REST_BASE_URL =
            "https://restcountries.eu/rest/v2/name";

    public static URL buildUrl(String restSearchQuery) {
        Uri builtUri = Uri.parse(REST_BASE_URL).buildUpon()
                .appendEncodedPath(restSearchQuery)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Country> extractFeatureFromJson(String restCountryResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(restCountryResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Country> countries = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONArray from the JSON response string
            JSONArray baseJsonResponse = new JSONArray(restCountryResponse);

            // For each country in the baseJsonResponse, create a {@link Country} object
            for (int i = 0; i < baseJsonResponse.length(); i++) {

                // Get a single country at position i within the list of countries
                JSONObject currentCountry = baseJsonResponse.getJSONObject(i);

                // extract the name of the country
                String countryName = currentCountry.getString("name");

                // get the currencies array and the first object in it
                // extract currency code and symbol fromit
                JSONArray currenciesArray = currentCountry.getJSONArray("currencies");
                JSONObject currenciesObject = currenciesArray.getJSONObject(0);
                String currencyCode = currenciesObject.getString("code");
                String currencySymbol = currenciesObject.getString("symbol");

                // get the languages array and the first object in it
                // extract language name from it
                JSONArray languagesArray = currentCountry.getJSONArray("languages");
                JSONObject languagesObject = languagesArray.getJSONObject(0);
                String languageName = languagesObject.getString("name");

                // Create a new {@link Country} object with the name, currency code and symbol,
                // language name from the JSON response.
                Country country =
                        new Country(countryName, currencyCode, currencySymbol, languageName);

                // Add the new {@link Country} to the list of countries.
                countries.add(country);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of countries
        return countries;
    }
}
