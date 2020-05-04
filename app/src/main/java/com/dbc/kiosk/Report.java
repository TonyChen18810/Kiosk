package com.dbc.kiosk;

import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.dbc.kiosk.Helpers.Time;

import java.util.Set;

import io.fabric.sdk.android.Fabric;

/**
 * Report.java
 *
 * Used to set tags for Crashlytics in the case
 * of an application crash
 */
public class Report {

    public Report(Context context) {
        Fabric.with(context, new Crashlytics());
    }

    public void setDriverTags() {
        if (Account.getCurrentAccount() != null) {
            Account CURRENT_ACCOUNT = Account.getCurrentAccount();
            Crashlytics.setString("Email", CURRENT_ACCOUNT.getEmail());
            Crashlytics.setString("Phone", CURRENT_ACCOUNT.getPhoneNumber());
            Crashlytics.setString("Name", CURRENT_ACCOUNT.getDriverName());
            Crashlytics.setString("Driver License", CURRENT_ACCOUNT.getDriverLicense() + " " + CURRENT_ACCOUNT.getDriverState());
            Crashlytics.setString("Trailer License", CURRENT_ACCOUNT.getTrailerLicense() + " " + CURRENT_ACCOUNT.getTrailerState());
            Crashlytics.setString("Truck Name", CURRENT_ACCOUNT.getTruckName());
            Crashlytics.setString("Truck Number", CURRENT_ACCOUNT.getTruckNumber());
            Crashlytics.setString("Dispatcher Phone", CURRENT_ACCOUNT.getDispatcherPhoneNumber());

            Crashlytics.setString("Current Time", Time.getCurrentTime());
            Crashlytics.setString("Current Date", Time.getCurrentDate());

            Crashlytics.setString("Kiosk Number", Settings.getKioskNumber());
            Crashlytics.setString("Cooler location", Settings.getCoolerLocation());
        }
    }

    public void setOrderTags() {
        if (Order.getCurrentOrder() != null) {
            Order current = Order.getCurrentOrder();
            Crashlytics.setString("Master Number", current.getMasterNumber());
            Crashlytics.setString("SOP Number", current.getSOPNumber());
            Crashlytics.setString("Destination", current.getDestination());
            Crashlytics.setString("Customer", current.getCustomerName());
            Crashlytics.setString("Consignee", current.getConsignee());
            Crashlytics.setString("Truck Status", current.getTruckStatus());
            Crashlytics.setString("Checked In", current.getCheckedIn());
            Crashlytics.setString("Is Appointment", current.getAppointment());
            Crashlytics.setString("Appointment Time", current.getAppointmentTime());
            Crashlytics.setDouble("Estimated Weight", current.getEstimatedWeight());
            Crashlytics.setDouble("Estimated Pallets", current.getEstimatedPallets());

            Crashlytics.setDouble("Total Weight", Order.getTotalWeight());
            Crashlytics.setDouble("Total Pallets", Order.getTotalPalletCount());
        }
    }
}
