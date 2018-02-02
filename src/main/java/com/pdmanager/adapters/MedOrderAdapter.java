package com.pdmanager.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdmanager.R;
import com.pdmanager.models.MedicationOrder;
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
