package com.dbc.kiosk.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dbc.kiosk.Helpers.CustomOrderKeyboard;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.OrderEntry;
import java.lang.ref.WeakReference;
/**
 * CancelDialog.java
 *
 * @params Activity activity, Context context, TextView buyerName
 *
 * This dialog is called and shown whenever the "red x" button is pressed
 * on OrderEntry.java, letting the user confirm the order being cleared
 * from entry.
 *
 * Passes Activity to use for a WeakReference and enable/disable certain UI elements.
 */
public class CancelDialog extends Dialog implements android.view.View.OnClickListener {

    private WeakReference<Activity> mWeakActivity;
    private TextView buyerName;

    public CancelDialog(Activity activity, Context context, TextView buyerName) {
        super(context);
        mWeakActivity = new WeakReference<>(activity);
        this.buyerName = buyerName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_cancel);
        Button yes = findViewById(R.id.btn_yes);
        Button no = findViewById(R.id.btn_no);
        TextView cancelOrder = findViewById(R.id.CancelOrderText);
        if (Language.getCurrentLanguage() == 0) {
            cancelOrder.setText("Are you sure you want to cancel this order entry?");
            yes.setText(R.string.yes_eng);
            no.setText(R.string.no_eng);
        } else if (Language.getCurrentLanguage() == 1) {
            cancelOrder.setText("¿Está seguro de que quiere cancelar esta entrada de pedido?");
            yes.setText(R.string.yes_sp);
            no.setText(R.string.no_sp);
        } else if (Language.getCurrentLanguage() == 2) {
            cancelOrder.setText("Voulez-vous vraiment annuler cette entrée de commande?");
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
                Activity activity = mWeakActivity.get();
                if (activity != null) {
                    ImageButton cancelOrderBtn = activity.findViewById(R.id.CancelOrderBtn);
                    ImageButton checkOrderBtn = activity.findViewById(R.id.CheckOrderBtn);
                    Button addOrderBtn = activity.findViewById(R.id.AddOrderBtn);
                    ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                    EditText orderNumber = activity.findViewById(R.id.OrderNumberBox);
                    Button selectDestinationBtn = activity.findViewById(R.id.SelectDestinationBtn);
                    CustomOrderKeyboard keyboard = activity.findViewById(R.id.keyboard);
                    keyboard.setVisibility(View.VISIBLE);
                    cancelOrderBtn.setEnabled(false);
                    cancelOrderBtn.setVisibility(View.GONE);
                    checkOrderBtn.setVisibility(View.VISIBLE);
                    addOrderBtn.setEnabled(false);
                    progressBar.setVisibility(View.GONE);
                    addOrderBtn.clearAnimation();
                    orderNumber.setText("");
                    buyerName.setText("");
                    selectDestinationBtn.setText("");
                    buyerName.setVisibility(View.GONE);
                    selectDestinationBtn.setVisibility(View.GONE);
                    selectDestinationBtn.setEnabled(true);
                    checkOrderBtn.setEnabled(false);
                    addOrderBtn.setEnabled(false);
                    orderNumber.setEnabled(true);
                    orderNumber.requestFocus();
                    OrderEntry.possibleCustomerDestinations.clear();
                }
                dismiss();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}