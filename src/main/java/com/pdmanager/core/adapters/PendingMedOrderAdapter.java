package com.pdmanager.core.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdmanager.core.R;
import com.pdmanager.core.models.PendingMedication;
import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;

import java.util.List;

/**
 * Created by George on 1/29/2016.
 */
public class PendingMedOrderAdapter extends ListViewAdapter {


    public PendingMedOrderAdapter(List items) {
        super(items);

    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.pedmed_list_item, parent, false);
        return new PedMedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        PedMedItemViewHolder recipeViewHolder = (PedMedItemViewHolder) holder;
        //updateLayoutParams(recipeViewHolder.imageLayout);

        PendingMedication recipe = (PendingMedication) getItem(position);


        recipeViewHolder.itemMedication.setText(recipe.Medication);
//        recipeViewHolder.itemStatus.setText(recipe.Status);
//        recipeViewHolder.itemStatus.setText(recipe.Status);
        //recipeViewHolder.itemStatus.setTextColor(Color.parseColor("#224422"));

        recipeViewHolder.itemHour.setText(recipe.getTime());
        recipeViewHolder.itemInstructions.setText(recipe.Instructions);

        if (recipe.getStatus().equals("delay")) {

            recipeViewHolder.itemBackground.setBackgroundColor(Color.parseColor("#ff2222"));
        } else if (recipe.getStatus().equals("get"))
            recipeViewHolder.itemBackground.setBackgroundColor(Color.parseColor("#22ff22"));
        else
            recipeViewHolder.itemBackground.setBackgroundColor(Color.parseColor("#888888"));


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