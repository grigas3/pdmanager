package com.pdmanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdmanager.R;
import com.pdmanager.models.Observation;
import com.telerik.widget.list.ListViewDataSourceAdapter;
import com.telerik.widget.list.ListViewHolder;
import com.telerik.widget.list.ListViewTextHolder;

import java.util.List;

/**
 * Created by George on 1/29/2016.
 */
public class EventAdapter extends ListViewDataSourceAdapter {


    private Context parentContext;


    public EventAdapter(List items) {
        super(items);

    }

    @Override
    public ListViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        this.parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.event_list_item, parent, false);


        return new EventItemViewHolder(view);
    }

    private String getName(Observation obs) {
        if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("tremor"))
            return "Time spent with tremor ";//+Integer.toString((int)(obs.getValue()*100)) +"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("lid"))
            return "Time spent with dyskinesia ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("off"))
            return "Time spent with off ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("brad"))
            return "Average Brad. UPDRS ";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("gait"))
            return "Average Gait UPDRS ";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("fog"))
            return "Number of FOG events per day ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("act_stand"))
            return "Time standing ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("med_adh"))
            return "Patient medication adherence  ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("test_bis"))
            return "Patient performed BISS11 test ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("test_nmss"))
            return "Patient performed NMSS test ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("nutr_nrs"))
            return "NRS2002 ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("nutr_mop"))
            return "MOP ";//+Integer.toString((int)(obs.getValue()*100))+"%";
        else
            return obs.getCode();
    }


    private String getValue(Observation obs) {
        if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("tremor"))
            return Integer.toString((int) (obs.getValue() * 25)) + "%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("lid"))
            return Integer.toString((int) (obs.getValue() * 100)) + "%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("off"))
            return Integer.toString((int) (obs.getValue() * 100)) + "%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("fog"))
            return Integer.toString((int) (obs.getValue()));
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("act_stand"))
            return Integer.toString((int) (obs.getValue() * 100)) + "%";
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("med_adh"))
            return Integer.toString((int) (obs.getValue() * 100)) + "%";
        else
            return Integer.toString((int) (obs.getValue()));
    }

    private int getImage(Observation obs) {


        if (obs.getCode() != null && obs.getCategory().toLowerCase().equals("motor"))
                /*!=null&&obs.getCode().toLowerCase().startsWith("tremor")||obs.getCode().toLowerCase().startsWith("lid")
                ||obs.getCode().toLowerCase().startsWith("off")
                ||obs.getCode().toLowerCase().startsWith("fog")
                )
                */
            return R.drawable.ic_pd;

        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("med"))
            return R.drawable.ic_dataform_pill;
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("act"))
            return R.drawable.ic_pd;
        else if (obs.getCode() != null && obs.getCode().toLowerCase().startsWith("test"))
            return R.drawable.ic_dataform_guest;
        else
            return R.drawable.ic_pd;

    }


    private int getStatusColor(Observation obs) {
        if (obs.getCode() == "BRAD" || obs.getCode() == "GAIT") {

            if (obs.getValue() > 1)
                return Color.parseColor("#dd2222");
            else
                return Color.parseColor("#22dd22");

        } else if (obs.getCategoryCode() == "MOTOR") {

            if (obs.getValue() > 0.2)
                return Color.parseColor("#dd2222");
            else
                return Color.parseColor("#22dd22");

        } else if (obs.getCategoryCode() == "MED") {

            if (obs.getValue() > 0.8)
                return Color.parseColor("#22dd22");
            else
                return Color.parseColor("#dd2222");


        } else if (obs.getCategoryCode() == "TEST_BIS11") {

            //if(obs.getValue()<0.2)
            return Color.parseColor("#222222");


        } else if (obs.getCategoryCode() == "TEST_NMSS") {

            //if(obs.getValue()<0.2)
            return Color.parseColor("#222222");


        } else if (obs.getCategoryCode() == "ACT") {

            if (obs.getValue() < 0.2)
                return Color.parseColor("#dd2222");
            else
                return Color.parseColor("#22dd22");


        }
        return Color.parseColor("#22dd22");

    }

    private String getStatus(Observation obs) {
        if (obs.getCode() == "BRAD" || obs.getCode() == "GAIT") {

            if (obs.getValue() > 1)
                return "High";
            else
                return "Normal";

        } else if (obs.getCategoryCode() == "MOTOR") {

            if (obs.getValue() > 0.2)
                return "High";
            else
                return "Normal";

        } else if (obs.getCategoryCode() == "MED") {

            if (obs.getValue() > 0.8)
                return "Norma";
            else
                return "Low";


        } else if (obs.getCategoryCode() == "TEST_BIS11") {

            //if(obs.getValue()<0.2)
            return "";


        } else if (obs.getCategoryCode() == "TEST_NMSS") {

            //if(obs.getValue()<0.2)
            return "";


        } else if (obs.getCategoryCode() == "ACT") {

            if (obs.getValue() < 0.2)
                return "Low";
            else
                return "Normal";


        }
        return "Normal";


    }

    @Override
    public ListViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.event_group_item, parent, false);
        return new ListViewTextHolder(view, R.id.headerTextView);
    }

    @Override
    public void onBindGroupViewHolder(ListViewHolder holder, Object groupKey) {
        ((ListViewTextHolder) holder).textView.setText(String.valueOf(groupKey));
    }

    private String getDate(Observation obs) {
        return obs.getDate().toString();
    }

    @Override
    public void onBindItemViewHolder(ListViewHolder holder, Object entity) {


        EventItemViewHolder recipeViewHolder = (EventItemViewHolder) holder;
        //updateLayoutParams(recipeViewHolder.imageLayout);

        Observation recipe = (Observation) entity;


        if (recipeViewHolder.itemImage != null && parentContext != null) {

            recipeViewHolder.itemImage.setImageDrawable(ContextCompat.getDrawable(parentContext, getImage(recipe)));
        }
        //recipeViewHolder.itemImage.setImageResource(getImage(recipe));

        if (recipeViewHolder.itemDate != null)
            recipeViewHolder.itemDate.setText(getDate(recipe));


        //recipeViewHolder.itemStatus.setTextColor(Color.parseColor("#224422"));

        if (recipeViewHolder.itemEvent != null)
            recipeViewHolder.itemEvent.setText(getName(recipe));

        recipeViewHolder.itemValue.setText(getValue(recipe));

        if (recipeViewHolder.itemStatus != null) {
            recipeViewHolder.itemStatus.setText(getStatus(recipe));
            recipeViewHolder.itemValue.setTextColor(getStatusColor(recipe));
            recipeViewHolder.itemStatus.setTextColor(getStatusColor(recipe));
        }


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