package com.pdmanager.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdmanager.R;
import com.pdmanager.models.ClinicalInfo;
import com.pdmanager.models.DssInfo;
import com.telerik.widget.list.ListViewDataSourceAdapter;
import com.telerik.widget.list.ListViewHolder;
import com.telerik.widget.list.ListViewTextHolder;

import java.util.List;

/**
 * Created by IP on 11/17/2017.
 */
public class DssInfoAdapter extends ListViewDataSourceAdapter {


    public DssInfoAdapter(List items) {
        super(items);

    }

    @Override
    public ListViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.dssinfo_list_item, parent, false);
        return new DssInfoItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ListViewHolder holder, Object entity) {
        DssInfoItemViewHolder recipeViewHolder = (DssInfoItemViewHolder) holder;
        //updateLayoutParams(recipeViewHolder.imageLayout);

        DssInfo recipe = (DssInfo) entity;

        recipeViewHolder.itemSubscriber.setText(recipe.CreatedBy);
        recipeViewHolder.itemDate.setText(recipe.getDate().toString());

        if (recipe.Code != null && recipe.Code.toLowerCase().equals("high")) {

            recipeViewHolder.itemText.setTextColor(Color.parseColor("#ff2222"));
        } else if (recipe.Code != null && recipe.Code.toLowerCase().equals("normal")) {
            recipeViewHolder.itemText.setTextColor(Color.parseColor("#22ff22"));

        } else
            recipeViewHolder.itemText.setTextColor(Color.parseColor("#2222ff"));

        //    recipeViewHolder.itemPriority.setText(recipe.Priority);
        //recipeViewHolder.itemStatus.setTextColor(Color.parseColor("#224422"));


       recipeViewHolder.itemCode.setText(recipe.Code);
       if(recipe.Code.equals("Decision")) {
           recipeViewHolder.itemCode.setText("Suggestion regarding the medication");
       }
       else if (recipe.Code.equals("Rule")) {
           recipeViewHolder.itemCode.setText("Reasoning");
       }
       else if (recipe.Code.equals("Medication_change")){
           recipeViewHolder.itemCode.setText("The suggested modification is");
       }

       recipeViewHolder.itemText.setText(recipe.Value);
        // off for now  recipeViewHolder.itemCode.setText("CHANGE");
        // off for now recipeViewHolder.itemText.setText("The patient seems to have many issues and problems and");
    }

    @Override
    public ListViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.med_group_item, parent, false);
        return new ListViewTextHolder(view, R.id.headerTextView);
    }

    @Override
    public void onBindGroupViewHolder(ListViewHolder holder, Object groupKey) {
        ((ListViewTextHolder) holder).textView.setText(String.valueOf(groupKey));
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