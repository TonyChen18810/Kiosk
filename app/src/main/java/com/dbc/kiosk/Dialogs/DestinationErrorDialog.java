package com.dbc.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.OrderEntry;

/**
 * DestinationErriorDialog.java
 *
 * Displays a dialog to the user when an incorrect destination is selected
 * after entering an order number and confirming the correct customer
 */
public class DestinationErrorDialog extends Dialog implements android.view.View.OnClickListener {

    public DestinationErrorDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_help);
        TextView helpTextView = findViewById(R.id.HelpText);
        Button confirm = findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(this);

        if (Language.getCurrentLanguage() == 1) {
            helpTextView.setText("Incorrect destination for the entered order number, you have one attempt remaining.");
            confirm.setText("OK");
        } else if (Language.getCurrentLanguage() == 2) {
            helpTextView.setText("Destino incorrecto para el número de pedido ingresado, le queda un intento.");
            confirm.setText("OK");
        } else if (Language.getCurrentLanguage() == 3) {
            helpTextView.setText("Destination incorrecte pour le numéro de commande saisi, il vous reste un tentative");
            confirm.setText("OK");
        }
    }

    @Override
    public void onClick(View v) {
        OrderEntry.destinationListener.setValue("show_dialog");
        dismiss();
    }
}
