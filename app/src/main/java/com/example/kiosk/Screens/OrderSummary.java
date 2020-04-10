package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.example.kiosk.Account;
import com.example.kiosk.Dialogs.LogoutDialog;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.RecyclerViewSummaryAdapter;
import com.example.kiosk.Helpers.Rounder;
import com.example.kiosk.Order;
import com.example.kiosk.R;
import com.example.kiosk.Webservices.DeleteOrderDetails;
import com.example.kiosk.Webservices.GetOrderDetails;
import com.example.kiosk.Webservices.UpdateMasterOrder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderSummary extends AppCompatActivity {

    TextView confirmOrders, confirmationNumberText, orderNumber, buyerName, estPallets, aptTime, destination, estWeight,
            totalOrders, totalPallets, totalWeight, ordersCount, totalPalletsCount, totalWeightCount;
    Button confirmBtn, backBtn;

    private int currentLanguage = Language.getCurrentLanguage();

    private Button logoutBtn;

    private int counter;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        setup();

        System.out.println("Here are the outlier orders:");
        for (int i = 0; i < Order.getOutlierOrders().size(); i++) {
            System.out.println(Order.getOutlierOrders().get(i).getSOPNumber());
        }

        System.out.println("Here are the orders to be checked in:");
        for (int i = 0; i < Order.getOrdersList().size(); i++) {
            System.out.println(Order.getOrdersList().get(i).getSOPNumber());
        }

        RecyclerView recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(OrderSummary.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        verticalLayoutManager.setAutoMeasureEnabled(false);
        RecyclerViewSummaryAdapter adapter = new RecyclerViewSummaryAdapter(Order.getOrdersList());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

        TextView confirmationNum = findViewById(R.id.confirm);
        confirmationNum.setText(GetOrderDetails.getMasterNumber());

        logoutBtn.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(OrderSummary.this, v);
            dialog.show();
        });
        backBtn.setOnClickListener(v12 -> {
            System.out.println("Back button clicked!");
            // finish();
            startActivity(new Intent(OrderSummary.this, OrderEntry.class));
        });

        findViewById(R.id.ConfirmBtn).setOnClickListener(v -> {
            List<Order> orderList = Order.getOrdersList();
            List<Order> outlierList = new ArrayList<>(Order.getOutlierOrders());
            /*
            for (int i = 0; i < outlierList.size(); i++) {
                if (outlierList.get(i).getMasterNumber().equals(GetOrderDetails.getMasterNumber())) {
                    System.out.println(outlierList.get(i).getSOPNumber() + "'s master number equals current master number: " + outlierList.get(i).getMasterNumber() + " and " + GetOrderDetails.getMasterNumber());
                    outlierList.remove(outlierList.get(i));
                }
            }*/

            System.out.println("Here are the outlier orders, they will now be updated:");
            for (int i = 0; i < outlierList.size(); i++) {
                new DeleteOrderDetails(outlierList.get(i).getSOPNumber()).execute();
                System.out.println(outlierList.get(i).getSOPNumber());
            }
            for (int i = 0; i < outlierList.size(); i++) {
                // update outlier orders
                new UpdateMasterOrder(GetOrderDetails.getMasterNumber(), Account.getCurrentAccount().getEmail(), outlierList.get(i).getSOPNumber(), "false",false).execute();
            }

            System.out.println("Here are the orders to be checked in, they will now be updated:");
            for (int i = 0; i < orderList.size(); i++) {
                new DeleteOrderDetails(orderList.get(i).getSOPNumber()).execute();
                System.out.println(orderList.get(i).getSOPNumber());
            }
            for (int i = 0; i < orderList.size(); i++) {
                if (i == orderList.size()-1) {
                    new UpdateMasterOrder(GetOrderDetails.getMasterNumber(), Account.getCurrentAccount().getEmail(), orderList.get(i).getSOPNumber(), "true",true).execute();
                } else {
                    new UpdateMasterOrder(GetOrderDetails.getMasterNumber(), Account.getCurrentAccount().getEmail(), orderList.get(i).getSOPNumber(), "true", false).execute();
                }
            }
            setContentView(R.layout.final_screen);

            final Button logoutBtn = findViewById(R.id.LogoutBtn);
            final TextView textView = findViewById(R.id.textView);
            textView.setVisibility(View.INVISIBLE);

            timer = new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    counter++;
                    textView.setText(String.valueOf(counter));
                }
                public void onFinish() {
                    textView.setText("done");
                    logoutBtn.performClick();
                }
            }.start();

            TextView tv1 = findViewById(R.id.textView1);
            TextView tv2 = findViewById(R.id.textView2);
            if (currentLanguage == 0) {
                if (Order.getOrdersList().size() > 1) {
                    tv1.setText(R.string.thanks_bye_eng);
                } else {
                    tv1.setText(R.string.thanks_bye2_eng);
                }
                tv2.setText(R.string.please_logout_eng);
                logoutBtn.setText(R.string.logout_eng);
            } else if (currentLanguage == 1) {
                if (Order.getOrdersList().size() > 1) {
                    tv1.setText(R.string.thanks_bye_sp);
                } else {
                    tv1.setText(R.string.thanks_bye2_sp);
                }
                tv2.setText(R.string.please_logout_sp);
                logoutBtn.setText(R.string.logout_sp);
            } else if (currentLanguage == 2) {
                if (Order.getOrdersList().size() > 1) {
                    tv1.setText(R.string.thanks_bye_fr);
                } else {
                    tv1.setText(R.string.thanks_bye2_fr);
                }
                tv2.setText(R.string.please_logout_fr);
                logoutBtn.setText(R.string.logout_fr);
            }
            logoutBtn.setOnClickListener(v1 -> {
                timer.cancel();
                System.out.println("done");
                startActivity(new Intent(OrderSummary.this, FirstScreen.class));
            });
        });
    }

    @SuppressLint("SetTextI18n")
    public void setup() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        confirmOrders = findViewById(R.id.ConfirmOrders);
        confirmationNumberText = findViewById(R.id.ConfirmationNumberText);
        orderNumber = findViewById(R.id.OrderNum);
        buyerName = findViewById(R.id.BuyerName);
        estPallets = findViewById(R.id.EstPallets);
        aptTime = findViewById(R.id.AptTime);
        destination = findViewById(R.id.Destination);
        estWeight = findViewById(R.id.EstWeight);
        totalOrders = findViewById(R.id.TotalOrderCount);
        totalPallets = findViewById(R.id.TotalPalletText);
        totalWeight = findViewById(R.id.TotalWeightText);
        confirmBtn = findViewById(R.id.ConfirmBtn);
        logoutBtn = findViewById(R.id.LogoutBtn);
        backBtn = findViewById(R.id.BackBtn);
        ordersCount = findViewById(R.id.OrdersCount);
        totalPalletsCount = findViewById(R.id.PalletCount);
        totalWeightCount = findViewById(R.id.TotalWeight);

        ordersCount.setText(Integer.toString(Order.getOrdersList().size()));
        DecimalFormat formatter = new DecimalFormat("#,###");
        totalPalletsCount.setText(Double.toString(Rounder.round(Order.getTotalPalletCount(), 1)));
        totalWeightCount.setText(formatter.format(Order.getTotalWeight()) + " lbs");

        if (currentLanguage == 0) {
            confirmOrders.setText(R.string.confirm_orders_eng);
            confirmationNumberText.setText(R.string.confirmation_num_eng);
            orderNumber.setText(R.string.order_number_eng);
            buyerName.setText(R.string.buyer_name_eng);
            estPallets.setText(R.string.est_pallets_eng);
            aptTime.setText(R.string.apt_time_eng);
            destination.setText(R.string.destination_eng);
            estWeight.setText(R.string.est_weight_eng);
            totalOrders.setText(R.string.total_orders_eng);
            totalPallets.setText(R.string.pallet_count_eng);
            totalWeight.setText(R.string.total_weight_eng);
            confirmBtn.setText(R.string.confirm_eng);
            logoutBtn.setText(R.string.logout_eng);
            backBtn.setText(R.string.back_eng);
        } else if (currentLanguage == 1) {
            confirmOrders.setText(R.string.confirm_orders_sp);
            confirmationNumberText.setText(R.string.confirmation_num_sp);
            orderNumber.setText(R.string.order_number_sp);
            buyerName.setText(R.string.buyer_name_sp);
            estPallets.setText(R.string.est_pallets_sp);
            aptTime.setText(R.string.apt_time_sp);
            destination.setText(R.string.destination_sp);
            estWeight.setText(R.string.est_weight_sp);
            totalOrders.setText(R.string.total_orders_sp);
            totalPallets.setText(R.string.pallet_count_sp);
            totalWeight.setText(R.string.total_weight_sp);
            confirmBtn.setText(R.string.confirm_sp);
            logoutBtn.setText(R.string.logout_sp);
            backBtn.setText(R.string.back_sp);
        } else if (currentLanguage == 2) {
            confirmOrders.setText(R.string.confirm_orders_fr);
            confirmationNumberText.setText(R.string.confirmation_num_fr);
            orderNumber.setText(R.string.order_number_fr);
            buyerName.setText(R.string.buyer_name_fr);
            estPallets.setText(R.string.est_pallets_fr);
            aptTime.setText(R.string.apt_time_fr);
            destination.setText(R.string.destination_fr);
            estWeight.setText(R.string.est_weight_fr);
            totalOrders.setText(R.string.total_orders_fr);
            totalPallets.setText(R.string.pallet_count_fr);
            totalWeight.setText(R.string.total_weight_fr);
            confirmBtn.setText(R.string.confirm_fr);
            logoutBtn.setText(R.string.logout_fr);
            backBtn.setText(R.string.back_fr);
        }
    }
}
