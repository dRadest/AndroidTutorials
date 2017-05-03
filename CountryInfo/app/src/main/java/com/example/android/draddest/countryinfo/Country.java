package com.example.android.draddest.countryinfo;

/**
 * Class to hold information about the country
 */

public class Country {
        // member variables to store country information
        private String mName;
        private String mCurrencyCode;
        private String mCurrencySymbol;
        private String mLanguage;

        // constructor to construct a country object with the information
        public Country(String countryName, String currencyCode, String currencySymbol, String language){
            mName = countryName;
            mCurrencyCode = currencyCode;
            mCurrencySymbol = currencySymbol;
            mLanguage = language;
        }

        // getter methods
        public  String getName() {
            return mName;
        }public String getCurrencyCode() {
            return mCurrencyCode;
        }public String getCurrencySymbol() {
            return mCurrencySymbol;
        }public String getLanguage() {
            return mLanguage;
        }
}
