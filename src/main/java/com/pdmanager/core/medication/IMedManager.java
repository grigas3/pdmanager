package com.pdmanager.core.medication;

import com.pdmanager.core.models.Alert;
import com.pdmanager.core.models.MedicationOrder;
import com.pdmanager.core.models.UserAlert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by george on 30/11/2015.
 */
public interface IMedManager {
    boolean addMedicationOrders(List<MedicationOrder> medOrder);
    boolean addMedicationOrder(MedicationOrder medOrder);
    boolean clearMedOrders();

    int pendingMedication();
    UserAlert getPendingMedAlert(int medid);
    boolean setLastMessage(String id);

}
