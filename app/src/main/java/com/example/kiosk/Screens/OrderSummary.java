package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.example.kiosk.Dialogs.LogoutDialog;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.RecyclerViewSummaryAdapter;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import java.util.concurrent.ThreadLocalRandom;

public class OrderSummary extends AppCompatActivity {

    TextView confirmOrders, confirmationNumberText, orderNumber, buyerName, estPallets, aptTime, destination, estWeight,
            totalOrders, totalPallets, totalWeight, ordersCount, totalPalletsCount, totalWeightCount;
    Button confirmBtn;

    private int currentLanguage = Language.getCurrentLanguage();

    private final int CONFIRMATION_NUMBER = ThreadLocalRandom.current().nextInt(1000, 9999 + 1);

    private Button logoutBtn;

    private int counter;
    CountDownTimer timer;

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

        setup();

        RecyclerView recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(OrderSummary.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        RecyclerViewSummaryAdapter adapter = new RecyclerViewSummaryAdapter(MasterOrder.getMasterOrdersList());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

        TextView confirmationNum = findViewById(R.id.confirm);
        confirmationNum.setText(String.valueOf(CONFIRMATION_NUMBER));

        logoutBtn.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(OrderSummary.this, v);
            dialog.show();
        });

        findViewById(R.id.ConfirmBtn).setOnClickListener(v -> {
            setContentView(R.layout.final_screen);

            final Button logoutBtn = findViewById(R.id.LogoutBtn);
            final TextView textView = findViewById(R.id.textView);

            timer = new CountDownTimer(60000, 1000){
                public void onTick(long millisUntilFinished){
                    counter++;
                    textView.setText(String.valueOf(counter));
                }
                public void onFinish(){
                    textView.setText("done");
                    logoutBtn.performClick();
                }
            }.start();

            TextView tv1 = findViewById(R.id.textView1);
            TextView tv2 = findViewById(R.id.textView2);
            if (currentLanguage == 0) {
                if (MasterOrder.getMasterOrdersList().size() > 1) {
                    tv1.setText(R.string.thanks_bye_eng);
                } else {
                    tv1.setText(R.string.thanks_bye2_eng);
                }
                tv2.setText(R.string.please_logout_eng);
                logoutBtn.setText(R.string.logout_eng);
            } else if (currentLanguage == 1) {
                if (MasterOrder.getMasterOrdersList().size() > 1) {
                    tv1.setText(R.string.thanks_bye_sp);
                } else {
                    tv1.setText(R.string.thanks_bye2_sp);
                }
                tv2.setText(R.string.please_logout_sp);
                logoutBtn.setText(R.string.logout_sp);
            } else if (currentLanguage == 2) {
                if (MasterOrder.getMasterOrdersList().size() > 1) {
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
                startActivity(new Intent(OrderSummary.this, MainActivity.class));
            });
        });
    }

    public void setup() {
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
        ordersCount = findViewById(R.id.OrdersCount);
        totalPalletsCount = findViewById(R.id.PalletCount);
        totalWeightCount = findViewById(R.id.TotalWeight);

        ordersCount.setText(Integer.toString(MasterOrder.getMasterOrdersList().size()));
        totalPalletsCount.setText(Integer.toString(MasterOrder.getMasterOrdersList().size()));
        totalWeightCount.setText(Integer.toString(MasterOrder.getMasterOrdersList().size()));

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
        }
    }
}
