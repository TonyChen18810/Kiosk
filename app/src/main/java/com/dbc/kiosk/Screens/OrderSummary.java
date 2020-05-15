package com.dbc.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Dialogs.LogoutDialog;
import com.dbc.kiosk.Dialogs.ProgressDialog;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.Helpers.RecyclerViewSummaryAdapter;
import com.dbc.kiosk.Helpers.Rounder;
import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Report;
import com.dbc.kiosk.Webservices.DeleteOrderDetails;
import com.dbc.kiosk.Webservices.GetOrderDetails;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;
/**
 * OrderSummary.java
 *
 * This activity is used for reviewing the entered orders and their information.
 *
 * The user can press "Back" to return to the Order Entry screen in the case of
 * an incorrect order entered, taking the user back to OrderEntry.java.
 *
 * The list of orders and their information is contained within a RecyclerView and
 * managed by RecyclerViewSummaryAdapter.java.
 *
 * If the user presses "Confirm", both the DeleteOrderDetails.java web service and
 * the UpdateMasterOrder.java web service will be called on each order, updating their
 * master numbers and setting their "isCheckedIn" value to true.
 */
public class OrderSummary extends AppCompatActivity {

    TextView confirmOrders, confirmationNumberText, orderNumber, buyerName, estPallets, aptTime, destination, estWeight,
            totalOrders, totalPallets, totalWeight, ordersCount, totalPalletsCount, totalWeightCount, characterCounterTextView,
            straight, blocked, sideways, noPreference, preferLoadingText, selectOne;
    Button confirmBtn, backBtn;

    private CheckBox straightCheckbox, sidewaysCheckbox, blockedCheckbox, noPreferenceCheckbox, otherCheckbox;
    private EditText otherEntry;

    private String loadingPreference = "";

    private Button logoutBtn;

    private int counter;
    public static CountDownTimer timer;

    public static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Report report = new Report(this);
        report.setDriverTags();
        report.setOrderTags();
        setContentView(R.layout.activity_order_summary);
        setup();

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
            LogoutDialog dialog = new LogoutDialog(OrderSummary.this, OrderSummary.this);
            dialog.show();
            dialog.setCancelable(false);
        });
        backBtn.setOnClickListener(v12 -> {
            Intent intent = new Intent(OrderSummary.this, OrderEntry.class);
            intent.putExtra("enable", "true");
            startActivity(new Intent(OrderSummary.this, OrderEntry.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        });

        straightCheckbox.setOnClickListener(v -> handleChecks(straightCheckbox));
        sidewaysCheckbox.setOnClickListener(v -> handleChecks(sidewaysCheckbox));
        blockedCheckbox.setOnClickListener(v -> handleChecks(blockedCheckbox));
        noPreferenceCheckbox.setOnClickListener(v -> handleChecks(noPreferenceCheckbox));
        otherCheckbox.setOnClickListener(v -> handleChecks(otherCheckbox));

        otherEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // counter!!
                characterCounterTextView.setText(otherEntry.length() + "/225");
            }
        });

        findViewById(R.id.ConfirmBtn).setOnClickListener(v -> {
            if (loadingPreference.equals("")) {
                selectOne.setVisibility(View.VISIBLE);
            } else {
                if (loadingPreference.equals("Other")) {
                    loadingPreference += "-" + otherEntry.getText().toString();
                }
                Account.setLoadingPreference(loadingPreference);
                System.out.println(Account.getLoadingPreference());
                List<Order> orderList = Order.getOrdersList();
                List<Order> outlierList = new ArrayList<>(Order.getOutlierOrders());

                Set<String> outlierSet = new HashSet<>();

                // doubly make sure there are no duplicate outlier orders, and also no outlier orders
                // in the order list to be checked-in/submitted
                for (int i = 0; i < outlierList.size(); i++) {
                    boolean contains = false;
                    for (int j = 0; j < orderList.size(); j++) {
                        if (orderList.get(j).getSOPNumber().equals(outlierList.get(i).getSOPNumber())) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        outlierSet.add(outlierList.get(i).getSOPNumber());
                    }
                }

                System.out.println("Here are the outlier orders, they will now be deleted and updated:");
                for (String SOP : outlierSet) {
                    new DeleteOrderDetails(SOP, OrderSummary.this, "false", false).execute(); // delete and update each outlier order
                    System.out.println(SOP);
                }
                System.out.println("Here are the orders to be checked in, they will now be deleted and updated");
                for (int i = 0; i < orderList.size(); i++) {
                    if (i == orderList.size()-1) {
                        new DeleteOrderDetails(orderList.get(i).getSOPNumber(), OrderSummary.this, "true",true).execute(); // last call, delete -> update -> send notification
                    } else {
                        new DeleteOrderDetails(orderList.get(i).getSOPNumber(), OrderSummary.this, "true", false).execute(); // delete and update each added order
                    }
                    System.out.println(orderList.get(i).getSOPNumber());
                }

                setContentView(R.layout.final_screen);
                String message = "";
                if (Language.getCurrentLanguage() == 1) {
                    message = "Submitting your orders...";
                } else if (Language.getCurrentLanguage() == 2) {
                    message = "Enviando sus pedidos...";
                } else if (Language.getCurrentLanguage() == 3) {
                    message = "Soumettre vos commandes...";
                }
                dialog = new ProgressDialog(message, OrderSummary.this);
                dialog.show();
                dialog.setCancelable(false);
                final Button logoutBtn = findViewById(R.id.LogoutBtn);
                final TextView textView = findViewById(R.id.textView);
                logoutBtn.setEnabled(false);
                textView.setVisibility(View.INVISIBLE);

                timer = new CountDownTimer(5000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        counter++;
                        textView.setText(String.valueOf(counter));
                    }
                    public void onFinish() {
                        textView.setText("done");
                        logoutBtn.performClick();
                    }
                };

                TextView tv1 = findViewById(R.id.textView1);
                TextView tv2 = findViewById(R.id.textView2);
                if (Language.getCurrentLanguage() == 1) {
                    if (Order.getOrdersList().size() > 1) {
                        tv1.setText(R.string.thanks_bye_eng);
                    } else {
                        tv1.setText(R.string.thanks_bye2_eng);
                    }
                    tv2.setText(R.string.please_logout_eng);
                    logoutBtn.setText(R.string.logout_eng);
                } else if (Language.getCurrentLanguage() == 2) {
                    if (Order.getOrdersList().size() > 1) {
                        tv1.setText(R.string.thanks_bye_sp);
                    } else {
                        tv1.setText(R.string.thanks_bye2_sp);
                    }
                    tv2.setText(R.string.please_logout_sp);
                    logoutBtn.setText(R.string.logout_sp);
                } else if (Language.getCurrentLanguage() == 3) {
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
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                });
            }
        });
    }

    int b = 0;
    public void handleChecks(CheckBox cb) {
        selectOne.setVisibility(View.GONE);
        if ((loadingPreference.equals("Straight")) && (cb.getId() == R.id.StraightCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if ((loadingPreference.equals("Sideways")) && (cb.getId() == R.id.SidewaysCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if ((loadingPreference.equals("Blocked")) && (cb.getId() == R.id.BlockedCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if ((loadingPreference.equals("No Preference")) && (cb.getId() == R.id.NoPreferenceCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if ((loadingPreference.equals("Other")) && (cb.getId() == R.id.OtherCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        }
        if (cb.getId() == R.id.StraightCheckbox) {
            straightCheckbox.setClickable(false);
            loadingPreference = "Straight";
            if (sidewaysCheckbox.isChecked()) {
                sidewaysCheckbox.toggle();
                sidewaysCheckbox.setClickable(true);
            }
            if (blockedCheckbox.isChecked()) {
                blockedCheckbox.toggle();
                blockedCheckbox.setClickable(true);
            }
            if (noPreferenceCheckbox.isChecked()) {
                noPreferenceCheckbox.toggle();
                noPreferenceCheckbox.setClickable(true);
            }
            if (otherCheckbox.isChecked()) {
                otherCheckbox.toggle();
                otherCheckbox.setClickable(true);
                otherEntry.setEnabled(false);
                otherEntry.setText("");
            }
        }
        if (cb.getId() == R.id.SidewaysCheckbox) {
            sidewaysCheckbox.setClickable(false);
            loadingPreference = "Sideways";
            if (straightCheckbox.isChecked()) {
                straightCheckbox.toggle();
                straightCheckbox.setClickable(true);
            }
            if (blockedCheckbox.isChecked()) {
                blockedCheckbox.toggle();
                blockedCheckbox.setClickable(true);
            }
            if (noPreferenceCheckbox.isChecked()) {
                noPreferenceCheckbox.toggle();
                noPreferenceCheckbox.setClickable(true);
            }
            if (otherCheckbox.isChecked()) {
                otherCheckbox.toggle();
                otherCheckbox.setClickable(true);
                otherEntry.setEnabled(false);
                otherEntry.setText("");
            }
        }
        if (cb.getId() == R.id.BlockedCheckbox) {
            blockedCheckbox.setClickable(false);
            loadingPreference = "Blocked";
            if (straightCheckbox.isChecked()) {
                straightCheckbox.toggle();
                straightCheckbox.setClickable(true);
            }
            if (sidewaysCheckbox.isChecked()) {
                sidewaysCheckbox.toggle();
                sidewaysCheckbox.setClickable(true);
            }
            if (noPreferenceCheckbox.isChecked()) {
                noPreferenceCheckbox.toggle();
                noPreferenceCheckbox.setClickable(true);
            }
            if (otherCheckbox.isChecked()) {
                otherCheckbox.toggle();
                otherCheckbox.setClickable(true);
                otherEntry.setEnabled(false);
                otherEntry.setText("");
            }
        }
        if (cb.getId() == R.id.NoPreferenceCheckbox) {
            noPreferenceCheckbox.setClickable(false);
            loadingPreference = "No Preference";
            if (straightCheckbox.isChecked()) {
                straightCheckbox.toggle();
                straightCheckbox.setClickable(true);
            }
            if (sidewaysCheckbox.isChecked()) {
                sidewaysCheckbox.toggle();
                sidewaysCheckbox.setClickable(true);
            }
            if (blockedCheckbox.isChecked()) {
                blockedCheckbox.toggle();
                blockedCheckbox.setClickable(true);
            }
            if (otherCheckbox.isChecked()) {
                otherCheckbox.toggle();
                otherCheckbox.setClickable(true);
                otherEntry.setEnabled(false);
                otherEntry.setText("");
            }
        }
        if (cb.getId() == R.id.OtherCheckbox) {
            otherCheckbox.setClickable(false);
            loadingPreference = "Other";
            otherEntry.setEnabled(true);
            showSoftKeyboard(otherEntry);
            if (straightCheckbox.isChecked()) {
                straightCheckbox.toggle();
                straightCheckbox.setClickable(true);
            }
            if (sidewaysCheckbox.isChecked()) {
                sidewaysCheckbox.toggle();
                sidewaysCheckbox.setClickable(true);
            }
            if (blockedCheckbox.isChecked()) {
                blockedCheckbox.toggle();
                blockedCheckbox.setClickable(true);
            }
            if (noPreferenceCheckbox.isChecked()) {
                noPreferenceCheckbox.toggle();
                noPreferenceCheckbox.setClickable(true);
            }
        }
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
        straightCheckbox = findViewById(R.id.StraightCheckbox);
        straight = findViewById(R.id.Straight);
        sidewaysCheckbox = findViewById(R.id.SidewaysCheckbox);
        sideways = findViewById(R.id.Sideways);
        blockedCheckbox = findViewById(R.id.BlockedCheckbox);
        blocked = findViewById(R.id.Blocked);
        noPreferenceCheckbox = findViewById(R.id.NoPreferenceCheckbox);
        noPreference = findViewById(R.id.NoPreference);
        otherCheckbox = findViewById(R.id.OtherCheckbox);
        otherEntry = findViewById(R.id.Other);
        otherEntry.setEnabled(false);
        preferLoadingText = findViewById(R.id.PreferLoadingText);
        selectOne = findViewById(R.id.SelectOne);
        selectOne.setVisibility(View.GONE);
        ordersCount = findViewById(R.id.OrdersCount);
        totalPalletsCount = findViewById(R.id.PalletCount);
        totalWeightCount = findViewById(R.id.TotalWeight);
        characterCounterTextView = findViewById(R.id.CharacterCounter);

        ordersCount.setText(Integer.toString(Order.getOrdersList().size()));
        DecimalFormat formatter = new DecimalFormat("#,###");
        totalPalletsCount.setText(Double.toString(Rounder.round(Order.getTotalPalletCount(), 2)));
        totalWeightCount.setText(formatter.format(Order.getTotalWeight()) + " lbs");

        if (Language.getCurrentLanguage() == 1) {
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
            orderNumber.setTextSize(40);
            buyerName.setTextSize(40);
            estPallets.setTextSize(40);
            aptTime.setTextSize(40);
            destination.setTextSize(40);
            estWeight.setTextSize(40);
            preferLoadingText.setText("Select your preferred loading method:");
            selectOne.setText("*You must select a loading method");
            straight.setText("Straight");
            sideways.setText("Sideways");
            blocked.setText("Blocked");
            noPreference.setText("No Preference");
            otherEntry.setHint("Other");
        } else if (Language.getCurrentLanguage() == 2) {
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
            orderNumber.setTextSize(36);
            buyerName.setTextSize(36);
            estPallets.setTextSize(36);
            aptTime.setTextSize(36);
            destination.setTextSize(36);
            estWeight.setTextSize(36);
            preferLoadingText.setText("Seleccione la configuracion de su carga");
            selectOne.setText("*Debes seleccionar un configuracion de carga");
            straight.setText("Paletas derechas");
            sideways.setText("Paletas de lado");
            blocked.setText("Paletas bloqueadass");
            noPreference.setText("Sin preferencias");
            otherEntry.setHint("Instucciones especificas");
        } else if (Language.getCurrentLanguage() == 3) {
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
            orderNumber.setTextSize(30);
            buyerName.setTextSize(30);
            estPallets.setTextSize(30);
            aptTime.setTextSize(30);
            destination.setTextSize(30);
            estWeight.setTextSize(30);
            preferLoadingText.setText("Sélectionnez votre méthode de chargement préférée:");
            selectOne.setText("*Vous devez sélectionner une méthode de chargement");
            straight.setText("Straight");
            sideways.setText("Sideways");
            blocked.setText("Blocked");
            noPreference.setText("No Preference");
            otherEntry.setHint("Other");
        }
    }
}
