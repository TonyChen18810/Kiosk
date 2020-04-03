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

public class HelpDialog extends Dialog implements android.view.View.OnClickListener {

    private String helpText;

    public HelpDialog(String helpText, Context context) {
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
            confirm.setText(R.string.confirm_eng);
        } else if (Language.getCurrentLanguage() == 1) {
            confirm.setText(R.string.confirm_sp);
        } else if (Language.getCurrentLanguage() == 2) {
            confirm.setText(R.string.confirm_fr);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}