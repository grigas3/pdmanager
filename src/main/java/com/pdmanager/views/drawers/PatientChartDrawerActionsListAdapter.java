package com.pdmanager.views.drawers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pdmanager.core.PDApplicationContext;
import com.pdmanager.core.R;
import com.telerik.viewmodels.MenuAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ginev on 17/04/2014.
 */
public class PatientChartDrawerActionsListAdapter extends ArrayAdapter<MenuAction> {
    private List<MenuAction> controls;
    private PDApplicationContext app;
    private MenuAction selectedControl;

    public PatientChartDrawerActionsListAdapter(Context context, int resource) {
        super(context, resource);
        this.app = (PDApplicationContext) context.getApplicationContext();
        this.selectedControl = this.findControlById(((Activity) this.getContext()).getIntent().getStringExtra(PDApplicationContext.INTENT_CONTROL_ID));
        InitGroups();//
    }

    private void InitGroups() {


        this.controls = new ArrayList<MenuAction>();


        MenuAction chart = new MenuAction();
        MenuAction calendar = new MenuAction();
        MenuAction observations = new MenuAction();
        MenuAction assessment = new MenuAction();

        //     MenuAction distributions=new MenuAction();
        MenuAction list = new MenuAction();

        chart.setActionInfo("Chart");
        chart.setHeaderText("Chart");

        assessment.setActionInfo("Assessment");
        assessment.setHeaderText("Assessment");

        assessment.setFragmentName("AssessmentFragment");


        observations.setActionInfo("Motor Symptoms");
        observations.setHeaderText("Motor Symptoms");

        observations.setFragmentName("ObservationChartFragment");


        chart.setFragmentName("PatientChartFragment");
        calendar.setActionInfo("Calendar");
        calendar.setHeaderText("Calendar");

        calendar.setFragmentName("PatientCalendarFragment");


        //   distributions.setActionInfo("Distributions");
        //     distributions.setHeaderText("Distributions");

        //     distributions.setFragmentName("DistributionChartFragment");

        list.setActionInfo("Back to List");
        list.setHeaderText("Back to List");

        list.setFragmentName("ClinicianActivity");


        this.controls.add(chart);
        this.controls.add(assessment);
        this.controls.add(calendar);
        this.controls.add(observations);
        //    this.controls.add(distributions);
        this.controls.add(list);

    }


    public MenuAction findControlById(String id) {
        if (id == null) {
            return null;
        }
        for (MenuAction group : this.controls) {
            if (group.getFragmentName().equals(id)) {
                return group;
            }
        }

        return null;
    }


    @Override
    public int getCount() {
        return this.controls.size();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) this.app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView = inflater.inflate(R.layout.drawer_control_item_container, null);
        //  ImageView controlBadge = (ImageView) rootView.findViewById(R.id.controlBadge);
        TextView controlName = (TextView) rootView.findViewById(R.id.controlName);

        MenuAction control = this.controls.get(position);

        controlName.setText(control.getHeaderText());
        // controlBadge.setImageResource(app.getDrawableResource(control.getDrawerIcon()));

        if (this.selectedControl != null && this.selectedControl.equals(control)) {
            controlName.setTextColor(this.getContext().getResources().getColor(R.color.telerikGreen));
        } else {
            controlName.setTextColor(this.getContext().getResources().getColor(R.color.black));
        }

        return rootView;
    }

    @Override
    public long getItemId(int position) {
        return this.controls.get(position).hashCode();
    }

    @Override
    public MenuAction getItem(int position) {
        return this.controls.get(position);
    }
}
