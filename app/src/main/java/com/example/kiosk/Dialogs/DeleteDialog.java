package com.example.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Screens.OrderEntry;
import com.example.kiosk.R;

public class DeleteDialog extends Dialog implements android.view.View.OnClickListener {

    private String orderNumberStr;
    private View view;
    ProgressBar progressBar;

    public DeleteDialog(String orderNumberStr, Context context, View view) {
        super(context);
        this.orderNumberStr = orderNumberStr;
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
        // progressBar.setVisibility(View.GONE);
        if (Language.getCurrentLanguage() == 0) {
            deleteOrder.setText("Are you sure you want to delete order: " + orderNumberStr + "?");
            yes.setText(R.string.yes_eng);
            no.setText(R.string.no_eng);
        } else if (Language.getCurrentLanguage() == 1) {
            deleteOrder.setText("¿Está seguro de que quiere eliminar el pedido: " + orderNumberStr + "?");
            yes.setText(R.string.yes_sp);
            no.setText(R.string.no_sp);
        } else if (Language.getCurrentLanguage() == 2) {
            deleteOrder.setText("Voulez-vous vraiment supprimer la commande: " + orderNumberStr + "?");
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
                OrderEntry.removeItem(view);
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