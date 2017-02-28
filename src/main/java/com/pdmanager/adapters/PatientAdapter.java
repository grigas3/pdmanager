package com.pdmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdmanager.R;
import com.pdmanager.models.Patient;
import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;

import java.util.List;

/**
 * Created by George on 1/29/2016.
 */
public class PatientAdapter extends ListViewAdapter {


    public PatientAdapter(List items) {
        super(items);

    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.patient_list_item, parent, false);
        return new PatientItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        PatientItemViewHolder recipeViewHolder = (PatientItemViewHolder) holder;
        //updateLayoutParams(recipeViewHolder.imageLayout);

        Patient recipe = (Patient) getItem(position);
        recipe.fillFields();


        recipeViewHolder.itemName.setText(recipe.Family + " " + recipe.Given + "(" + recipe.Gender + ")");

        if (recipe.LastVisitDate != null)
            recipeViewHolder.itemLastVisit.setText("Last Visit: " + recipe.LastVisitDate);
        else
            recipeViewHolder.itemLastVisit.setText("Last Visit: ?");
        recipeViewHolder.itemStatus.setText(recipe.Status);
        //recipeViewHolder.itemStatus.setTextColor(Color.parseColor("#224422"));

        String phone = recipe.getPhone();
        if (phone != null)
            recipeViewHolder.itemPhone.setText("Phone: " + phone);
        else
            recipeViewHolder.itemPhone.setText("Phone: -");


        if (recipe.MRN != null)
            recipeViewHolder.itemMRN.setText("MRN: " + recipe.MRN);
        else
            recipeViewHolder.itemMRN.setText("MRN: -");

        if (recipe.YWP != null)
            recipeViewHolder.itemYWP.setText("YWP: " + recipe.YWP);
        else
            recipeViewHolder.itemYWP.setText("YWP: -");


        recipeViewHolder.itemDOB.setText("DOB: " + recipe.BirthDate);
        recipeViewHolder.itemLastVisit.setText("Last Visit: " + recipe.LastVisitDate);


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