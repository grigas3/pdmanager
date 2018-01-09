package com.pdmanager.viewmodels;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pdmanager.R;
import com.pdmanager.models.MedTiming;
import com.pdmanager.models.MedicationOrder;
import com.telerik.widget.dataform.engine.NonEmptyValidator;
import com.telerik.widget.dataform.engine.NotifyPropertyChangedBase;
import com.telerik.widget.dataform.visualization.annotations.DataFormProperty;
import com.telerik.widget.dataform.visualization.editors.DataFormSpinnerEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormTimeEditor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by George on 6/14/2016.
 */
public class MedicationOrderVM extends NotifyPropertyChangedBase {
    private String MedicationName;
    private String Instructions;
    private long time1 = Calendar.getInstance().getTimeInMillis();
    private String dose1 = "50mg";
    private String dose2 = "50mg";
    private String dose3 = "50mg";
    private String dose4 = "50mg";
    private String dose5 = "50mg";
    private boolean included1 = true;
    private boolean included2 = true;
    private boolean included3 = true;
    private boolean included4 = true;
    private boolean included5 = true;
    private long reservationDate = Calendar.getInstance().getTimeInMillis();
    private long reservationTime1 = Calendar.getInstance().getTimeInMillis();
    private long reservationTime2 = Calendar.getInstance().getTimeInMillis();
    private long reservationTime3 = Calendar.getInstance().getTimeInMillis();
    private long reservationTime4 = Calendar.getInstance().getTimeInMillis();
    private long reservationTime5 = Calendar.getInstance().getTimeInMillis();
    private String dose6 = "50mg";
    private String dose7 = "50mg";
    private String dose8 = "50mg";
    private String dose9 = "50mg";
    private String dose10 = "50mg";
    private boolean included6 = true;
    private boolean included7 = true;
    private boolean included8 = true;
    private boolean included9 = true;
    private boolean included10 = true;
    private long reservationDate6 = Calendar.getInstance().getTimeInMillis();
    private long reservationTime7 = Calendar.getInstance().getTimeInMillis();
    private long reservationTime8 = Calendar.getInstance().getTimeInMillis();
    private long reservationTime9 = Calendar.getInstance().getTimeInMillis();
    private long reservationTime10 = Calendar.getInstance().getTimeInMillis();


    public MedicationOrderVM() {

    }

    public static MedicationOrderVM getMedicationOrder(MedicationOrder order) {

        MedicationOrderVM vm = new MedicationOrderVM();
        vm.MedicationName = order.MedicationId;
        vm.Instructions = order.Instructions;


        List<MedTiming> timing = new ArrayList<>();
        int count = 1;
        for (MedTiming time : order.getTimings()
                ) {


            switch (count) {
                case 1: {
                    vm.included1 = true;
                    vm.reservationTime1 = time.Time;
                    break;

                }
                case 2: {
                    vm.included2 = true;
                    vm.reservationTime2 = time.Time;
                    break;

                }

                case 3: {
                    vm.included3 = true;
                    vm.reservationTime3 = time.Time;
                    break;

                }

                case 4: {
                    vm.included4 = true;
                    vm.reservationTime4 = time.Time;
                    break;

                }


                case 5: {
                    vm.included5 = true;
                    vm.reservationTime5 = time.Time;
                    break;

                }
            }
        }

        return vm;


    }

    public static MedicationOrder getMedicationOrder(MedicationOrderVM vm, String patientid) {

        MedicationOrder order = new MedicationOrder();
        order.MedicationId = vm.MedicationName;
        order.PatientId = ((patientid));

        order.Id = "newid";
        order.Instructions = vm.Instructions;
        order.Status = "active";
        //      order.PrescribedBy="doctor";
        order.Timestamp = Calendar.getInstance().getTimeInMillis();
//        order.DateWriten=Calendar.getInstance().getTime().toString();

        List<MedTiming> timing = new ArrayList<>();
        if (vm.included1) {

            timing.add(new MedTiming("1", vm.dose1, vm.reservationTime1));

        }
        if (vm.included2) {

            timing.add(new MedTiming("2", vm.dose2, vm.reservationTime2));

        }

        if (vm.included3) {

            timing.add(new MedTiming("3", vm.dose3, vm.reservationTime3));

        }
        if (vm.included4) {

            timing.add(new MedTiming("4", vm.dose4, vm.reservationTime4));

        }
        Gson json = new Gson();
        order.Timing = json.toJson(timing, new TypeToken<List<MedTiming>>() {
        }.getType());

        return order;


    }

    @DataFormProperty(label = " ",
            index = 0,
            editor = DataFormSpinnerEditor.class,
            editorLayout = R.layout.reservation_editor_layout,
            coreEditorLayout = R.layout.reservation_spinner_editor,
            headerLayout = R.layout.reservation_editor_collapsed_header,
            validator = NonEmptyValidator.class,
            group = "group1")
    public String getMedicationName() {
        return MedicationName;
    }

    public void setMedicationName(String MedicationName) {
        this.MedicationName = MedicationName;
        notifyListeners("MedicationName", MedicationName);
    }

    @DataFormProperty(label = " ",
            index = 1,
            validator = NonEmptyValidator.class,
            editorLayout = R.layout.reservation_editor_layout,
            coreEditorLayout = R.layout.reservation_text_editor,
            headerLayout = R.layout.reservation_editor_collapsed_header,
            group = "group1")
    public String getInstructions() {
        return Instructions;
    }

    public void setInstructions(String Instructions) {
        this.Instructions = Instructions;
        notifyListeners("Instructions", Instructions);
    }

    @DataFormProperty(
            editor = CancelButtonEditor.class,
            editorLayout = R.layout.reservation_editor_layout_no_image,
            index = 0,
            columnIndex = 1,
            group = "Reservation Date1")
    public boolean getAddSchedule1() {
        return included1;
    }

    public void setAddSchedule1(boolean cancelled) {
        this.included1 = cancelled;
        notifyListeners("Included1", cancelled);
    }


    @DataFormProperty(label = "Dose",
            index = 1,
            columnIndex = 2,
            validator = NonEmptyValidator.class,
            editorLayout = R.layout.reservation_editor_layout,
            coreEditorLayout = R.layout.reservation_pill_editor,
            group = "Reservation Date1"
    )
    public String getDose1() {
        return dose1;
    }

    public void setDose1(String dose) {
        this.dose1 = dose;
        notifyListeners("Dose1", dose);
    }


    @DataFormProperty(label = "Time",
            coreEditorLayout = R.layout.reservation_time_editor,
            editorLayout = R.layout.reservation_editor_layout,
            index = 2,
            columnIndex = 3,
            editor = DataFormTimeEditor.class,
            group = "Reservation Date1")
    public long getReservationTime1() {
        return reservationTime1;
    }

    public void setReservationTime1(long reservationTime) {
        this.reservationTime1 = reservationTime;
        notifyListeners("ReservationTime", reservationTime);
    }


    @DataFormProperty(
            editor = CancelButtonEditor.class,
            editorLayout = R.layout.reservation_editor_layout_no_image,
            index = 0,
            columnIndex = 1,
            group = "Reservation Date2")
    public boolean getAddSchedule2() {
        return included2;
    }

    public void setAddSchedule2(boolean cancelled) {
        this.included2 = cancelled;
        notifyListeners("Included2", cancelled);
    }

    @DataFormProperty(label = "Dose",
            index = 1,
            columnIndex = 2,
            validator = NonEmptyValidator.class,
            editorLayout = R.layout.reservation_editor_layout,
            coreEditorLayout = R.layout.reservation_pill_editor,
            group = "Reservation Date2"
    )
    public String getDose2() {
        return dose2;
    }

    public void setDose2(String dose) {
        this.dose2 = dose;
        notifyListeners("Dose2", dose);
    }


    @DataFormProperty(label = "Time",
            coreEditorLayout = R.layout.reservation_time_editor,
            editorLayout = R.layout.reservation_editor_layout,
            index = 2,
            columnIndex = 3,
            editor = DataFormTimeEditor.class,
            group = "Reservation Date2")
    public long getReservationTime2() {
        return reservationTime2;
    }

    public void setReservationTime2(long reservationTime) {
        this.reservationTime2 = reservationTime;
        notifyListeners("ReservationTime", reservationTime);
    }


    @DataFormProperty(
            editor = CancelButtonEditor.class,
            editorLayout = R.layout.reservation_editor_layout_no_image,
            index = 0,
            columnIndex = 1,
            group = "Reservation Date3")
    public boolean getAddSchedule3() {
        return included3;
    }

    public void setAddSchedule3(boolean cancelled) {
        this.included3 = cancelled;
        notifyListeners("Included3", cancelled);
    }

    @DataFormProperty(label = "Dose",
            index = 1,
            columnIndex = 2,
            validator = NonEmptyValidator.class,
            editorLayout = R.layout.reservation_editor_layout,
            coreEditorLayout = R.layout.reservation_pill_editor,
            group = "Reservation Date3"
    )
    public String getDose3() {
        return dose3;
    }

    public void setDose3(String dose) {
        this.dose3 = dose;
        notifyListeners("Dose3", dose);
    }


    @DataFormProperty(label = "Time",
            coreEditorLayout = R.layout.reservation_time_editor,
            editorLayout = R.layout.reservation_editor_layout,
            index = 2,
            columnIndex = 3,
            editor = DataFormTimeEditor.class,
            group = "Reservation Date3")
    public long getReservationTime3() {
        return reservationTime3;
    }

    public void setReservationTime3(long reservationTime) {
        this.reservationTime3 = reservationTime;
        notifyListeners("ReservationTime3", reservationTime);
    }


    @DataFormProperty(
            editor = CancelButtonEditor.class,
            editorLayout = R.layout.reservation_editor_layout_no_image,
            index = 0,
            columnIndex = 1,
            group = "Reservation Date4")
    public boolean getAddSchedule4() {
        return included4;
    }

    public void setAddSchedule4(boolean cancelled) {
        this.included4 = cancelled;
        notifyListeners("Included4", cancelled);
    }

    @DataFormProperty(label = "Dose",
            index = 1,
            columnIndex = 2,
            validator = NonEmptyValidator.class,
            editorLayout = R.layout.reservation_editor_layout,
            coreEditorLayout = R.layout.reservation_pill_editor,
            group = "Reservation Date4"
    )
    public String getDose4() {
        return dose4;
    }

    public void setDose4(String dose) {
        this.dose4 = dose;
        notifyListeners("Dose4", dose);
    }


    @DataFormProperty(label = "Time",
            coreEditorLayout = R.layout.reservation_time_editor,
            editorLayout = R.layout.reservation_editor_layout,
            index = 2,
            columnIndex = 3,
            editor = DataFormTimeEditor.class,
            group = "Reservation Date4")
    public long getReservationTime4() {
        return reservationTime3;
    }

    public void setReservationTime4(long reservationTime) {
        this.reservationTime4 = reservationTime;
        notifyListeners("ReservationTime4", reservationTime);
    }


}
