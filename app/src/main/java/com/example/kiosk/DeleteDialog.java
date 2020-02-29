package com.example.kiosk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DeleteDialog extends Dialog implements android.view.View.OnClickListener {

    private String orderNumberStr;
    private Context context;
    private View view;

    DeleteDialog(String orderNumberStr, Context context, View view) {
        super(context);
        this.orderNumberStr = orderNumberStr;
        this.context = context;
        this.view = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_delete);
        Button yes = findViewById(R.id.btn_yes);
        Button no = findViewById(R.id.btn_no);
        TextView deleteOrder = findViewById(R.id.DeleteOrder);
        switch(MainActivity.getCurrentLanguage()) {
            case 0:
                deleteOrder.setText("Are you sure you want to delete order " + orderNumberStr + "?");
            case 1:
                deleteOrder.setText("¿Estás seguro de que quieres eliminar el pedido: " + orderNumberStr + "?");
            case 2:
                deleteOrder.setText("Voulez-vous vraiment supprimer la ordre " + orderNumberStr + "?");
            default:
                deleteOrder.setText("Are you sure you want to delete order " + orderNumberStr + "?");
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