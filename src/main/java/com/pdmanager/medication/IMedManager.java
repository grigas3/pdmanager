package com.pdmanager.medication;

import com.pdmanager.models.Alert;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.UserAlert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by george on 30/11/2015.
 */
public interface IMedManager {
    boolean addMedicationOrders(List<MedicationOrder> medOrder);
    boolean addMedicationOrder(MedicationOrder medOrder);
    boolean clearAll();

    int pendingMedication();
    UserAlert getPendingMedAlert(int medid);
    boolean setLastMessage(String id);

    List<UserAlert> getAlerts();
}
