package com.pdmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.pdmanager.R;
import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;

import java.util.List;

/**
 * Created by George on 1/29/2016.
 */
public class AllergyAdapter extends ListViewAdapter {


    private Context parentContext;


    public AllergyAdapter(List items) {
        super(items);

    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        this.parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_list_item, parent, false);
        return new AllergyItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {

        AllergyItemViewHolder recipeViewHolder = (AllergyItemViewHolder) holder;

        //updateLayoutParams(recipeViewHolder.imageLayout);

        String recipe = (String) getItem(position);

        (recipeViewHolder).itemValue.setText(recipe);


    }


}

/*
public class CityAdapter extends ListViewAdapter {
    public CityAdapter(List items) {
        super(items);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.city_list_item, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        CityViewHolder viewHolder = (CityViewHolder)holder;
        City city = (City)getItems().get(position);
        viewHolder.nameView.setText(city.getName());
        viewHolder.countryView.setText(city.getCountry());
    }

    public static class CityViewHolder extends ListViewHolder {

        TextView nameView;
        TextView countryView;

        public CityViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView)itemView.findViewById(R.id.nameView);
            countryView = (TextView)itemView.findViewById(R.id.countryView);
        }
    }
}
 */