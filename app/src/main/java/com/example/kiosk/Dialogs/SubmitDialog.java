package com.example.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.kiosk.Helpers.Language;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderEntry;

public class SubmitDialog extends Dialog implements android.view.View.OnClickListener {

    private String orderNumberStr;
    private View view;

    public SubmitDialog(Context context, View view) {
        super(context);
        this.view = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_delete);
        Button yes = findViewById(R.id.btn_yes);
        Button no = findViewById(R.id.btn_no);
        TextView deleteOrder = findViewById(R.id.CorrectCustomer);
        if (Language.getCurrentLanguage() == 0) {
            deleteOrder.setText("Are you sure you want to submit these orders?");
            yes.setText(R.string.yes_eng);
            no.setText(R.string.no_eng);
        } else if (Language.getCurrentLanguage() == 1) {
            deleteOrder.setText("¿Estás seguro de que quieres enviar pedidos?");
            yes.setText(R.string.yes_sp);
            no.setText(R.string.no_sp);
        } else if (Language.getCurrentLanguage() == 2) {
            deleteOrder.setText("Voulez-vous vraiment soumettre des ordres?");
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
                OrderEntry.setDialogListener(true);
                dismiss();
                break;
            case R.id.btn_no:
                OrderEntry.setDialogListener(false);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}