package com.example.kiosk;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class AccountCreatedMsg extends AppCompatActivity {

    private TextView email, number, truckName, truckNumber, trailerLicense, driverLicense, driverName, dispatcherPhone;

    private Account currentAccount;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_created_msg);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        Objects.requireNonNull(getSupportActionBar()).hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setup();
        currentAccount = CreateAccount.getCurrentAccount();

        // email.setText("Email address: " + currentAccount.getEmail());
        // String str = "Email address: " + "<b>" + currentAccount.getEmail() + "<b>";
        email.setText(Html.fromHtml("Email address: " + "<b>" + currentAccount.getEmail() + "<b>"));
        number.setText(Html.fromHtml("Phone number: " + "<b>" + currentAccount.getPhoneNumber() + "<b>"));
        truckName.setText(Html.fromHtml("Current truck name: " + "<b>" + currentAccount.getTruckName() + "<b>"));
        truckNumber.setText(Html.fromHtml("Current truck number: " + "<b>" + currentAccount.getTruckNumber() + "<b>"));
        trailerLicense.setText(Html.fromHtml("Current trailer license: " + "<b>" + currentAccount.getTrailerLicense() + "<b>"));
        driverLicense.setText(Html.fromHtml("Driver license: " + "<b>" + currentAccount.getTrailerLicense() + "<b>"));
        driverName.setText(Html.fromHtml("Driver name: " + "<b>" + currentAccount.getDriverName() + "<b>"));
        dispatcherPhone.setText(Html.fromHtml("Current dispatcher's phone number: " + "<b>" + currentAccount.getDispatcherPhoneNumber() + "<b>"));

        EditText blank = findViewById(R.id.editText);
        showSoftKeyboard(blank);
        blank.setVisibility(View.INVISIBLE);

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountCreatedMsg.this, MainActivity.class));
            }
        });
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, SHOW_IMPLICIT);
            }
        }
    }

    private void setup() {
        email = findViewById(R.id.emailAddress);
        number = findViewById(R.id.phoneNumber);
        truckName = findViewById(R.id.truckName);
        truckNumber = findViewById(R.id.truckNumber);
        trailerLicense = findViewById(R.id.trailerLicense);
        driverLicense = findViewById(R.id.driverLicense);
        driverName = findViewById(R.id.driverName);
        dispatcherPhone = findViewById(R.id.dispatcherPhoneNumber);
    }
}
