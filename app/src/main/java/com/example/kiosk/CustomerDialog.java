package com.example.kiosk;

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

public class CustomerDialog extends Dialog implements android.view.View.OnClickListener {

    public Dialog d;
    private String customerNameStr;
    private EditText orderNumber;
    private TextView customer;
    private Button destination;
    private TextView errorMessage;
    private ImageButton checkOrderBtn;
    private Context context;

    CustomerDialog(Activity a, EditText orderNumber, String customerName, TextView customer, Button destination, TextView errorMessage, ImageButton checkOrderBtn, Context context) {
        super(a);
        this.customerNameStr = customerName;
        this.customer = customer;
        this.destination = destination;
        this.orderNumber = orderNumber;
        this.errorMessage = errorMessage;
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
            correctCustomer.setText("Is this the correct customer for this order number?");
            destination.setText("Select destination");
            yes.setText("Yes");
            no.setText("No");
        } else if (Language.getCurrentLanguage() == 1) {
            correctCustomer.setText("¿Es este el cliente correcto para este número de pedido?");
            destination.setText("Seleccione destino");
            yes.setText("Sí");
            no.setText("No");
        } else if (Language.getCurrentLanguage() == 2) {
            correctCustomer.setText("Est-ce le bon client pour ce numéro de commande?");
            destination.setText("Sélectionner la destination");
            yes.setText("Oui");
            no.setText("Non");
        }
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                errorMessage.setVisibility(View.GONE);
                customer.setVisibility(View.VISIBLE);
                customer.setText(customerNameStr);
                destination.setVisibility(View.VISIBLE);
                destination.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade));
                dismiss();
                break;
            case R.id.btn_no:
                // errorMessage.setText();
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