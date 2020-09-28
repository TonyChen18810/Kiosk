package com.dbc.kiosk.Helpers;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.dbc.kiosk.R;
/**
 * CustomOrderKeyboard.java
 *
 * A custom keyboard used for entering order numbers in OrderEntry.java
 * Contains 0-9 numeric values and A, B, C, E for alpha characters with a
 * backspace button for deleting.
 */
public class CustomOrderKeyboard extends LinearLayout implements View.OnClickListener {

    private ImageButton checkOrderBtn;
    private static Button mButtonEnter;

    public CustomOrderKeyboard(Context context) {
        this(context, null, 0);
    }

    public CustomOrderKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomOrderKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public static void disableEnterButton() {
        mButtonEnter.setEnabled(false);
    }

    public static void enableEnterButton() {
        mButtonEnter.setEnabled(true);
    }

    SparseArray<String> keyValues = new SparseArray<>();
    InputConnection inputConnection;

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);
        Button mButtonA = findViewById(R.id.button_A);
        Button mButtonB = findViewById(R.id.button_B);
        Button mButtonC = findViewById(R.id.button_C);
        Button mButtonE = findViewById(R.id.button_E);
        Button mButton1 = findViewById(R.id.button_1);
        Button mButton2 = findViewById(R.id.button_2);
        Button mButton3 = findViewById(R.id.button_3);
        Button mButton4 = findViewById(R.id.button_4);
        Button mButton5 = findViewById(R.id.button_5);
        Button mButton6 = findViewById(R.id.button_6);
        Button mButton7 = findViewById(R.id.button_7);
        Button mButton8 = findViewById(R.id.button_8);
        Button mButton9 = findViewById(R.id.button_9);
        Button mButton0 = findViewById(R.id.button_0);
        LinearLayout mButtonBack = findViewById(R.id.button_back_layout);
        mButtonEnter = findViewById(R.id.button_enter);

        mButtonA.setOnClickListener(this);
        mButtonB.setOnClickListener(this);
        mButtonC.setOnClickListener(this);
        mButtonE.setOnClickListener(this);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonBack.setOnClickListener(this);
        mButtonEnter.setOnClickListener(this);

        keyValues.put(R.id.button_A, "A");
        keyValues.put(R.id.button_B, "B");
        keyValues.put(R.id.button_C, "C");
        keyValues.put(R.id.button_E, "E");
        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");
    }

    @Override
    public void onClick(View v) {
        // do nothing if the InputConnection has not been set yet
        if (inputConnection == null) return;

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.getId() == R.id.button_back_layout) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                // delete the selection
                inputConnection.commitText("", 1);
            }
        } else if (v.getId() == R.id.button_enter) {
            System.out.println("Enter button clicked, this is inside of CustomOrderKeyboard.java");
            checkOrderBtn.performClick();
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
            // mButtonEnter.setEnabled(true);
            // buttonEnterImage.setBackgroundResource(R.drawable.arrow_right);
        }
    }

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    public void setInputConnection(InputConnection ic, ImageButton button) {
        this.inputConnection = ic;
        checkOrderBtn = button;
        mButtonEnter.setEnabled(false);
    }
}
