package com.example.kiosk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class HelpDialog extends Dialog implements android.view.View.OnClickListener {

    private String helpText;

    HelpDialog(String helpText, Context context) {
        super(context);
        this.helpText = helpText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_help);
        TextView helpTextView = findViewById(R.id.HelpText);
        Button confirm = findViewById(R.id.btn_confirm);
        helpTextView.setText(helpText);
        confirm.setOnClickListener(this);

        if (Language.getCurrentLanguage() == 0) {
            confirm.setText("Confirm");
        } else if (Language.getCurrentLanguage() == 1) {
            confirm.setText("Confirmar");
        } else if (Language.getCurrentLanguage() == 2) {
            confirm.setText("Confirmer");
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}