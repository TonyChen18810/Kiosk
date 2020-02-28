package com.example.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

public class OrderSummary extends AppCompatActivity {

    private final int CONFIRMATION_NUMBER = ThreadLocalRandom.current().nextInt(1000, 9999 + 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RecyclerView recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(OrderSummary.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        RecyclerViewSummaryAdapter adapter = new RecyclerViewSummaryAdapter(Order.getOrders());
        recyclerView.setAdapter(adapter);

        TextView confirmationNum = findViewById(R.id.confirm);
        confirmationNum.setText(String.valueOf(CONFIRMATION_NUMBER));

        findViewById(R.id.ConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.final_screen);
                findViewById(R.id.LoginBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        ConstraintLayout final_screen = findViewById(R.id.finalScreenLayout);
                        final_screen.setVisibility(View.GONE);
                        ConstraintLayout rules_regulations = findViewById(R.id.RulesRegulations);
                        rules_regulations.setVisibility(View.GONE);
                         */
                        Account.clearAccounts();
                        Order.clearOrders();
                        startActivity(new Intent(OrderSummary.this, MainActivity.class));
                    }
                });
            }
        });
    }
}
