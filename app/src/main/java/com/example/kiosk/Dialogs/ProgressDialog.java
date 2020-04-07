package com.example.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import com.example.kiosk.R;

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
