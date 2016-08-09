package com.pdmanager.core.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdmanager.core.R;
import com.pdmanager.core.models.MedicationOrder;
import com.telerik.widget.list.ListViewDataSourceAdapter;
import com.telerik.widget.list.ListViewHolder;
import com.telerik.widget.list.ListViewTextHolder;

import java.util.List;

/**
 * Created by George on 1/29/2016.
 */
public class MedOrderAdapter extends ListViewDataSourceAdapter {


    public MedOrderAdapter(List items) {
        super(items);

    }

/*
    @Override
    public void onBindSwipeContentHolder(ListViewHolder holder, int position) {
        RelativeLayout mainLayout = (RelativeLayout)holder.itemView;
        LinearLayout leftLayout = (LinearLayout)mainLayout.getChildAt(0);
        LinearLayout rightLayout = (LinearLayout)mainLayout.getChildAt(1);

        MedicationOrder recipe = (MedicationOrder) getItem(position);

        if(recipe.Status.toLowerCase().equals("active")) {


            super.onBindSwipeContentHolder(holder,position);
            Button leftButton = new Button(mainLayout.getContext());
            leftButton.setText("replace");

            Button rightButton = new Button(mainLayout.getContext());
            rightButton.setText("cancel");

            leftLayout.removeAllViews();
            leftLayout.addView(leftButton);

            rightLayout.removeAllViews();
            rightLayout.addView(rightButton);

        }
    }
*/

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

    @Override
    public ListViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.medorder_list_item, parent, false);
        return new MedOrderItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ListViewHolder holder, Object entity) {
        MedOrderItemViewHolder recipeViewHolder = (MedOrderItemViewHolder) holder;
        //updateLayoutParams(recipeViewHolder.imageLayout);

        MedicationOrder recipe = (MedicationOrder) entity;

        recipeViewHolder.itemPrescriber.setText(recipe.PrescribedBy);
        recipeViewHolder.itemDate.setText(recipe.DateWriten);
        recipeViewHolder.itemStatus.setText(recipe.Status);

        if (recipe.Status != null) {
            if (recipe.Status.toLowerCase().equals("active")) {
                recipeViewHolder.itemStatus.setTextColor(Color.parseColor("#22ff22"));
            } else {
                recipeViewHolder.itemStatus.setTextColor(Color.parseColor("#ff2222"));
            }
        }

        recipeViewHolder.itemDoses.setText(recipe.getTimingsAsString());
        recipeViewHolder.itemProduct.setText(recipe.MedicationId);
        recipeViewHolder.itemInstructions.setText(recipe.Instructions);


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