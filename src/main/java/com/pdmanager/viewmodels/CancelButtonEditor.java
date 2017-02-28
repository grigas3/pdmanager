package com.pdmanager.viewmodels;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.pdmanager.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

public class CancelButtonEditor extends EntityPropertyEditor implements View.OnClickListener {
    private TextView cancelButton;
    private boolean cancelled;

    private RadDataForm form;

    private String name;

    public CancelButtonEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm, dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_cancel_editor_button,
                R.id.data_form_cancel_editor_button,
                dataForm.getEditorsValidationLayout(), property);


        this.name = property.name();

        this.form = dataForm;


        //var groupName=property.getGroupName();
        cancelButton = (TextView) editorView;
        cancelButton.setOnClickListener(this);
    }


    private void disableProperties() {
        try {
            form.getExistingEditorForProperty(name.replace("AddSchedule", "ReservationTime")).getEditorView().setEnabled(false);
            form.getExistingEditorForProperty(name.replace("AddSchedule", "Dose")).getEditorView().setEnabled(false);
        } catch (Exception ex) {

        }

    }

    private void enableProperties() {


        try {
            form.getExistingEditorForProperty(name.replace("AddSchedule", "ReservationTime")).getEditorView().setEnabled(true);
            form.getExistingEditorForProperty(name.replace("AddSchedule", "Dose")).getEditorView().setEnabled(true);
        } catch (Exception ex) {

        }

    }

    @Override
    protected void initHeader(View headerView, EntityProperty property) {
        super.initHeader(headerView, property);

        headerView.setVisibility(View.GONE);
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        Boolean isCancelled = (Boolean) entityValue;


        if (!isCancelled) {
            disableProperties();
            cancelButton.setText("ADD DOSE");
            cancelButton.setTextColor(Color.parseColor("#22dd22"));
        } else {
            enableProperties();
            cancelButton.setText("REMOVE DOSE");
            cancelButton.setTextColor(Color.parseColor("#dd2222"));
        }

        cancelled = isCancelled;
    }

    @Override
    public Object value() {
        return cancelled;
    }

    @Override
    public void onClick(View v) {
        applyEntityValueToEditor(!cancelled);

        onEditorValueChanged(cancelled);
    }
}
