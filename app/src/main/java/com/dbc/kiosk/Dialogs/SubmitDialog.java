package com.dbc.kiosk.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.OrderEntry;
import com.dbc.kiosk.Webservices.GetNextMasterOrderNumber;
import com.dbc.kiosk.Webservices.GetOrderDetails;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * SubmitDialog.java
 *
 * @params Context context, Activity activity (used for WeakReference)
 *
 * Called from OrderEntry.java after pressing the "Submit" button
 *
 * OnClick ("No"): Dismiss
 * OnClick ("Yes"): Checks if any orders in list has a master number, the first one
 * it finds with one will use that for UpdateMasterOrder.java in OrderSummary.java
 * If no order in the list has a master number, call GetNextMasterOrderNumber.java
 * to return and set new master number to use.
 */
public class SubmitDialog extends Dialog implements android.view.View.OnClickListener {

    private static WeakReference<Activity> mWeakActivity;

    public SubmitDialog(Context context, Activity activity) {
        super(context);
        mWeakActivity = new WeakReference<>(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_delete);
        Button yes = findViewById(R.id.btn_yes);
        Button no = findViewById(R.id.btn_no);
        TextView deleteOrder = findViewById(R.id.CorrectCustomer);
        if (Language.getCurrentLanguage() == 1) {
            deleteOrder.setText(R.string.submit_confirm_eng);
            yes.setText(R.string.yes_eng);
            no.setText(R.string.no_eng);
        } else if (Language.getCurrentLanguage() == 2) {
            deleteOrder.setText(R.string.submit_confirm_sp);
            yes.setText(R.string.yes_sp);
            no.setText(R.string.no_sp);
        } else if (Language.getCurrentLanguage() == 3) {
            deleteOrder.setText(R.string.submit_confirm_fr);
            yes.setText(R.string.yes_fr);
            no.setText(R.string.no_fr);
        }
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                AtomicBoolean masterNull = new AtomicBoolean(true);
                System.out.println("Here's current master number: " + GetOrderDetails.getMasterNumber());
                if (GetOrderDetails.getMasterNumber() == null) {
                    for (int i = 0; i < Order.getOrdersList().size(); i++) {
                        if (Order.getOrdersList().get(i).getMasterNumber() == null || Order.getOrdersList().get(i).getMasterNumber().equals("anyType{}") || Order.getOrdersList().get(i).getMasterNumber().equals("")) {
                            System.out.println("MasterNull set to true... will eventually get a new one");
                            masterNull.set(true);
                        } else {
                            System.out.println("MasterNull set to false... break,");
                            masterNull.set(false);
                            GetOrderDetails.setNewMasterNumber(Order.getOrdersList().get(i).getMasterNumber());
                            System.out.println(Order.getOrdersList().get(i).getSOPNumber());
                            break;
                        }
                    }
                } else {
                    masterNull.set(false);
                }
                if (masterNull.get()) {
                    // Thread thread = new Thread(() -> new GetNextMasterOrderNumber(OrderEntry.this).execute());
                    // thread.start();
                    Activity activity = mWeakActivity.get();
                    if (activity != null) {
                        activity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.OrderNumberBox).setEnabled(false);
                        activity.findViewById(R.id.CheckOrderBtn).setEnabled(false);
                        activity.findViewById(R.id.SubmitBtn2).setEnabled(false);
                        activity.findViewById(R.id.OrdersView).setEnabled(false);
                        // activity.findViewById(R.id.LogoutBtn).setEnabled(false);
                    }
                    new GetNextMasterOrderNumber(activity).execute();
                } else {
                    OrderEntry.submitDialogListener.setValue(true);
                }
                dismiss();
                break;
            case R.id.btn_no:
                // OrderEntry.setDialogListener(false);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}