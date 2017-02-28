package com.pdmanager.viewmodels;

import com.pdmanager.R;
import com.telerik.widget.dataform.engine.NonEmptyValidator;
import com.telerik.widget.dataform.engine.NotifyPropertyChangedBase;
import com.telerik.widget.dataform.visualization.annotations.DataFormProperty;

/**
 * Created by george on 15/6/2016.
 */
public class MedicationTimingVM extends NotifyPropertyChangedBase {

    private String dose = "50mg";
    private String time = "18:00";

    public MedicationTimingVM() {

    }

    @DataFormProperty(label = " ",
            index = 0,
            editorLayout = R.layout.reservation_editor_layout,
            coreEditorLayout = R.layout.reservation_pill_editor,
            headerLayout = R.layout.reservation_editor_collapsed_header,
            validator = NonEmptyValidator.class,
            group = "group1")
    public String getDose() {
        return dose;
    }

    public void setDose(String Dose) {
        this.dose = Dose;
        notifyListeners("Dose", Dose);
    }

    @DataFormProperty(label = " ",
            index = 1,
            validator = NonEmptyValidator.class,
            editorLayout = R.layout.reservation_editor_layout,
            coreEditorLayout = R.layout.reservation_text_editor,
            headerLayout = R.layout.reservation_editor_collapsed_header,
            group = "group1")
    public String getTime() {
        return time;
    }

    public void setTime(String Instructions) {
        this.time = Instructions;
        notifyListeners("Time", Instructions);
    }

}
