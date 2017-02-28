package com.pdmanager.views;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.pdmanager.R;
import com.pdmanager.viewmodels.MedicationOrderVM;
import com.telerik.android.common.Function2;
import com.telerik.widget.dataform.visualization.DataFormGroupLayoutManager;
import com.telerik.widget.dataform.visualization.DataFormLinearLayoutManager;
import com.telerik.widget.dataform.visualization.EditorGroup;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.CommitMode;


public class TestMedicationOrderActivity extends Activity {


    private RadDataForm dataForm;

    public static void initSpinnerData(RadDataForm dataForm) {


//        ((DataFormSpinnerEditor) dataForm.getExistingEditorForProperty("Dose1")).setAdapter(new EditorSpinnerAdapter(dataForm.getContext(), R.layout.reservation_table_spinner_item, new String[]{"50mg", "100mg", "150mg", "250mg"}));

        //   DataFormSpinnerEditor origin = (DataFormSpinnerEditor) dataForm.getExistingEditorForProperty("Origin");
        // origin.setAdapter(new EditorSpinnerAdapter(dataForm.getContext(), new String[]{"phone", "in-person", "online", "other"}));
        //((DataFormSpinnerEditor) dataForm.getExistingEditorForProperty("TableNumber")).setAdapter(new EditorSpinnerAdapter(dataForm.getContext(), R.layout.reservation_table_spinner_item, createTableNumbers()));

        //DataFormSpinnerEditor origin = (DataFormSpinnerEditor) dataForm.getExistingEditorForProperty("Origin");
        //origin.setAdapter(new EditorSpinnerAdapter(dataForm.getContext(), new String[]{"phone", "in-person", "online", "other"}));
        //((DataFormSpinnerEditor) dataForm.getExistingEditorForProperty("TableNumber")).setAdapter(new EditorSpinnerAdapter(dataForm.getContext(), R.layout.reservation_table_spinner_item, createTableNumbers()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testmedordercreate);


        dataForm = (RadDataForm) this.findViewById(R.id.medorder_data_form);
        final Activity activity = this;
        DataFormGroupLayoutManager groupLayoutManager = new DataFormGroupLayoutManager(this);

        groupLayoutManager.setCreateGroup(new Function2<Context, String, EditorGroup>() {
            @Override
            public EditorGroup apply(Context context, String groupName) {
                if (groupName.startsWith("Reservation")) {
                    EditorGroup group = new EditorGroup(context, groupName, R.layout.data_form_editor_group_reservation_date);
                    DataFormLinearLayoutManager linearManager = new DataFormLinearLayoutManager(activity, R.layout.data_form_editor_group_horizontal);
                    linearManager.setOrientation(LinearLayout.HORIZONTAL);
                    group.setLayoutManager(linearManager);
                    return group;
                } else {
                    return new EditorGroup(context, groupName, R.layout.data_form_editor_group_no_header);
                }
            }
        });
        dataForm.setLayoutManager(groupLayoutManager);

        dataForm.setEntity(new MedicationOrderVM());
        if (dataForm.getEntity() != null) {
            initSpinnerData(dataForm);
        }


        dataForm.setCommitMode(CommitMode.MANUAL);


    }

    public RadDataForm dataForm() {
        return dataForm;
    }

    public void setReservation(MedicationOrderVM value) {
        if (value == null) {
            return;
        }

        this.dataForm.setEntity(value);
        initSpinnerData(dataForm);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
    }


}

