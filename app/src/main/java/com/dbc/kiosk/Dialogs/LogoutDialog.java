package com.dbc.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.FirstScreen;

public class LogoutDialog extends Dialog implements android.view.View.OnClickListener {

    private Context context;
    private View view;

    public LogoutDialog(Context context, View view) {
        super(context);
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
        TextView logoutText = findViewById(R.id.CorrectCustomer);
        if (Language.getCurrentLanguage() == 0) {
            logoutText.setText(R.string.logout_confirm_eng);
            yes.setText(R.string.yes_eng);
            no.setText(R.string.no_eng);
        } else if (Language.getCurrentLanguage() == 1) {
            logoutText.setText(R.string.logout_confirm_sp);
            yes.setText(R.string.yes_sp);
            no.setText(R.string.no_sp);
        } else if (Language.getCurrentLanguage() == 2) {
            logoutText.setText(R.string.logout_confirm_fr);
            yes.setText(R.string.yes_fr);
            no.setText(R.string.yes_fr);
        }
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                Intent intent = new Intent(context, FirstScreen.class);
                context.startActivity(intent);
                dismiss();
                break;
            case R.id.btn_no:
                dismiss();
                break;
        }
        dismiss();
    }
}