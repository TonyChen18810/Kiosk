package com.example.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class OrderSubmitted extends AppCompatActivity {

    private TextView email, number, truckName, truckNumber, trailerLicense, driverLicense, driverName, dispatcherPhone;

    private List<Order> orders = new ArrayList<>();
    private static RecyclerViewAdapter adapter;
    private static Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_submitted);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setup();

        orders.addAll(OrderInfo.getOrders());

        final RecyclerView recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(OrderSubmitted.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new RecyclerViewAdapter(this, orders);
        recyclerView.setAdapter(adapter);

        email.setText(Html.fromHtml("Email address: " + "<b>" + "kyle@gmail.com" + "<b>"));
        number.setText(Html.fromHtml("Phone number: " + "<b>" + "8315885534" + "<b>"));
        truckName.setText(Html.fromHtml("Current truck name: " + "<b>" + currentAccount.getTruckName() + "<b>"));
        truckNumber.setText(Html.fromHtml("Current truck number: " + "<b>" + currentAccount.getTruckNumber() + "<b>"));
        trailerLicense.setText(Html.fromHtml("Current trailer license: " + "<b>" + currentAccount.getTrailerLicense() + "<b>"));
        driverLicense.setText(Html.fromHtml("Driver license: " + "<b>" + currentAccount.getTrailerLicense() + "<b>"));
        driverName.setText(Html.fromHtml("Driver name: " + "<b>" + currentAccount.getDriverName() + "<b>"));
        dispatcherPhone.setText(Html.fromHtml("Current dispatcher's phone number: " + "<b>" + currentAccount.getDispatcherPhoneNumber() + "<b>"));


        Toast.makeText(this, "Here's the order count: " + orders.size(), Toast.LENGTH_SHORT).show();
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, SHOW_IMPLICIT);
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

        currentAccount = MainActivity.getCurrentAccount();
    }
}
