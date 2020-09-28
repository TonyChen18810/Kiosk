package com.dbc.kiosk.Helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
/**
 * HintTextWatcher.java
 *
 * Controls whether or not the hints are displayed in
 * CreateAccount.java. The hints will appear above
 * the text entry field only if the field is not empty,
 * if it IS empty then the hint is displayed inside the
 * field itself.
 */
public class HintTextWatcher implements TextWatcher {

    private EditText field;
    private TextView hint;

    public HintTextWatcher(EditText field, TextView hint) {
        this.field = field;
        this.hint = hint;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (field.length() == 0) {
            hint.animate().alpha(0.0f).setDuration(500).start();
        } else {
            hint.animate().alpha(1.0f).setDuration(500).start();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
