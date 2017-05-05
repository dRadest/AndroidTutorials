package com.example.android.draddest.countryinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * CountryAdapter exposes a list of countries to the RecycleView
 */

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private List<Country> mCountryList;

    public CountryAdapter(){}

    class CountryViewHolder extends RecyclerView.ViewHolder{
        TextView countryNameView;
        TextView currencyView;
        TextView languageView;

        public CountryViewHolder(View itemView) {
            super(itemView);
            countryNameView = (TextView) itemView.findViewById(R.id.tv_country_name);
            currencyView = (TextView) itemView.findViewById(R.id.tv_currency);
            languageView = (TextView) itemView.findViewById(R.id.tv_language);
        }
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.country_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        CountryViewHolder viewHolder = new CountryViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        Country currentCountry = mCountryList.get(position);
        String countryName = currentCountry.getName();
        String currencyCode = currentCountry.getCurrencyCode();
        String currencySymbol = currentCountry.getCurrencySymbol();
        String languageName = currentCountry.getLanguage();
        String currencyInfo = currencyCode + " " + currencySymbol;
        holder.countryNameView.setText(countryName);
        holder.currencyView.setText(currencyInfo);
        holder.languageView.setText(languageName);
    }

    @Override
    public int getItemCount() {
        if (mCountryList == null){
            return 0;
        }
        return mCountryList.size();
    }

    public void setCountryData (List<Country> countryData){
        mCountryList = countryData;
        notifyDataSetChanged();
    }

}
