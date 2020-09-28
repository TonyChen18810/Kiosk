package com.dbc.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import com.dbc.kiosk.R;

/**
 * ProgressDialog.java
 *
 * @param String progressText, Context context
 *
 * Called from OrderSummary.java
 *
 * Displayed while processing DeleteOrderDetails.java
 * and UpdateMasterOrder.java web service calls
 *
 * Closes after DriverNotification.java web service has finished
 */
public class ProgressDialog extends Dialog{

    private String progressText;

    public ProgressDialog(String progressText, Context context) {
        super(context);
        this.progressText = progressText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_wait_dialog);
        TextView progressTextView = findViewById(R.id.HelpText);
        progressTextView.setText(progressText);
    }
}
