package com.example.kiosk.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kiosk.Helpers.Language;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderEntry;
import com.example.kiosk.Webservices.GetPossibleShipTos;

public class CustomerDialog extends Dialog implements android.view.View.OnClickListener {

    public Dialog d;
    private Activity a;
    private String customerNameStr;
    private EditText orderNumber;
    private TextView customer;
    private Button destination;
    private ImageButton checkOrderBtn;
    private Context context;

    public CustomerDialog(Activity a, EditText orderNumber, String customerName, TextView customer, Button destination, ImageButton checkOrderBtn, Context context) {
        super(a);
        this.a = a;
        this.customerNameStr = customerName;
        this.customer = customer;
        this.destination = destination;
        this.orderNumber = orderNumber;
        this.checkOrderBtn = checkOrderBtn;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_customer);
        Button yes = findViewById(R.id.btn_yes);
        Button no = findViewById(R.id.btn_no);
        TextView customerName = findViewById(R.id.CustomerName);
        TextView correctCustomer = findViewById(R.id.CorrectCustomer);
        customerName.setText(customerNameStr);
        if (Language.getCurrentLanguage() == 0) {
            correctCustomer.setText(R.string.correct_customer_eng);
            destination.setText(R.string.select_destination_eng);
            yes.setText(R.string.yes_eng);
            no.setText(R.string.no_eng);
        } else if (Language.getCurrentLanguage() == 1) {
            correctCustomer.setText(R.string.correct_customer_sp);
            destination.setText(R.string.select_destination_sp);
            yes.setText(R.string.yes_sp);
            no.setText(R.string.no_sp);
        } else if (Language.getCurrentLanguage() == 2) {
            correctCustomer.setText(R.string.correct_customer_fr);
            destination.setText(R.string.select_destination_fr);
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
                new GetPossibleShipTos(a, MasterOrder.getCurrentMasterOrder().getSOPNumber()).execute();
                customer.setVisibility(View.VISIBLE);
                customer.setText(customerNameStr);
                destination.setVisibility(View.VISIBLE);
                destination.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade));
                dismiss();
                break;
            case R.id.btn_no:
                checkOrderBtn.setEnabled(true);
                orderNumber.setText("");
                orderNumber.setFocusable(true);
                orderNumber.requestFocus();
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}