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
import android.widget.TextView;
import com.dbc.kiosk.Helpers.CustomOrderKeyboard;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.R;
import java.lang.ref.WeakReference;

public class MoreOrdersDialog extends Dialog implements android.view.View.OnClickListener {

    private static WeakReference<Activity> mWeakActivity;

    public MoreOrdersDialog(Context context, Activity activity) {
        super(context);
        mWeakActivity = new WeakReference<>(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_delete);
        Button yes = findViewById(R.id.btn_yes);
        Button no = findViewById(R.id.btn_no);
        TextView text = findViewById(R.id.CorrectCustomer);
        if (Language.getCurrentLanguage() == 1) {
            text.setText("Do you have any more order numbers to enter?");
            yes.setText(R.string.yes_eng);
            no.setText(R.string.no_eng);
        } else if (Language.getCurrentLanguage() == 2) {
            text.setText("¿Tiene más números de pedido para ingresar?");
            yes.setText(R.string.yes_sp);
            no.setText(R.string.no_sp);
        } else if (Language.getCurrentLanguage() == 3) {
            text.setText("Avez-vous d'autres numéros de commande à saisir?");
            yes.setText(R.string.yes_fr);
            no.setText(R.string.no_fr);
        }
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Activity activity = mWeakActivity.get();
        switch (v.getId()) {
            case R.id.btn_yes:
                // order entry
                if (activity != null) {
                    EditText orderNumber = activity.findViewById(R.id.OrderNumberBox);
                    orderNumber.setText("");
                    orderNumber.setEnabled(true);
                    ImageButton checkOrderBtn = activity.findViewById(R.id.CheckOrderBtn);
                    checkOrderBtn.setEnabled(false);
                    CustomOrderKeyboard.disableEnterButton();
                }
                dismiss();
            case R.id.btn_no:
                if (activity != null) {
                    Button submitOrdersBtn = activity.findViewById(R.id.SubmitBtn2);
                    submitOrdersBtn.performClick();
                }
                dismiss();
        }
    }
}
