package com.dbc.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.*;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Dialogs.*;
import com.dbc.kiosk.Helpers.*;
import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Report;
import com.dbc.kiosk.Settings;
import com.dbc.kiosk.Webservices.GetOrderDetails;
import com.dbc.kiosk.Webservices.GetOrderDetailsByMasterNumber;
import java.util.ArrayList;
import java.util.List;
/**
 * OrderEntry.java
 *
 * User enters order information to check-in orders.
 *
 * This activity is started when the user selects "Next" in
 * LoggedIn.java
 *
 * After entering an order number and pressing the green arrow (checkOrderBtn),
 * calls GetOrderDetails.java. If this returns order information, GetPossibleShipTos.java
 * will be called, returning 6 destinations (5 incorrect, 1 correct). If the correct destination
 * is chosen, the order may be added. When the user presses "Add Order", GetOrderDetailsByMasterNumber.java
 * will be called and will return a list of orders that share a master number (an empty list if no others share it).
 * ConnectedOrders.java and it's RecyclerView will be populated by the returned list of orders, those of which
 * can be selected to be added as well.
 *
 * Each entered order is shown at the bottom of the screen with a "Delete" button, allowing
 * the user to remove it. This list of orders is contained within a RecyclerView and managed
 * with RecyclerViewHorizontalAdapter.java
 *
 * If the user presses "Submit Orders" and none of the entered orders have a master number,
 * GetNextMasterOrderNumber.java will be called and return a master number. If any of the orders
 * have a master number, this won't be called.
 */
public class OrderEntry extends AppCompatActivity {

    private int currentLanguage = Language.getCurrentLanguage();

    private EditText orderNumber;
    private TextView buyerName, appointmentText, loggedInAsText, currentlyEntered;
    private Button logoutBtn, submitBtn, addOrderBtn, selectDestinationBtn;
    private ImageButton checkOrderBtn, cancelOrderBtn;
    private ProgressBar progressBar;

    private static RecyclerViewHorizontalAdapter adapter;
    private static RecyclerView recyclerView;

    public static List<String> possibleCustomerDestinations;

    private static MutableLiveData<Boolean> addOrderListener = null;
    public static MutableLiveData<Boolean> submitDialogListener = null;
    public static MutableLiveData<Integer> validOrderNumber = null;
    public static MutableLiveData<String> destinationListener = null;

    public static int DESTINATION_ATTEMPTS = 0;

    public static RecyclerViewHorizontalAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_entry);
        Report report = new Report(this);
        report.setDriverTags();
        setup();

        System.out.println("Kiosk number: " + Settings.getKioskNumber());
        System.out.println("Cooler location: " + Settings.getCoolerLocation());

        CustomOrderKeyboard keyboard = findViewById(R.id.keyboard);
        orderNumber.setRawInputType(InputType.TYPE_CLASS_TEXT);
        orderNumber.setTextIsSelectable(true);
        InputConnection ic = orderNumber.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic, checkOrderBtn);

        System.out.println("MASTER NUMBER AT ORDER ENTRY: " + GetOrderDetails.getMasterNumber());

        addOrderListener = new MutableLiveData<>();
        submitDialogListener = new MutableLiveData<>();
        validOrderNumber = new MutableLiveData<>();
        destinationListener = new MutableLiveData<>();

        addOrderListener.observe(OrderEntry.this, empty -> {
            if (empty) {
                recyclerView.setVisibility(View.INVISIBLE);
                currentlyEntered.setVisibility(View.INVISIBLE);
                submitBtn.setEnabled(false);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                currentlyEntered.setVisibility(View.VISIBLE);
                submitBtn.setEnabled(true);
            }
        });

        submitDialogListener.observe(OrderEntry.this, dialogChoice -> {
            if (dialogChoice) {
                progressBar.setVisibility(View.INVISIBLE);
                setContentView(R.layout.rules_regulations);
                Button rulesAcceptBtn = findViewById(R.id.AcceptBtn);
                rulesAcceptBtn.setEnabled(true);
                rulesRegulationsSetup();
                rulesAcceptBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(OrderEntry.this, OrderSummary.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                });
            }
        });

        validOrderNumber.observe(OrderEntry.this, valid -> {
            // non-existing order
            if (valid == 0) {
                checkOrderBtn.setEnabled(true);
                // CustomOrderKeyboard.enableEnterButton();
                orderNumber.setText("");
                String message = null;
                if (Language.getCurrentLanguage() == 1) {
                    message = "Invalid order number, please try again.";
                } else if (Language.getCurrentLanguage() == 2) {
                    message = "El número de pedido no es válido. Intente otra vez.";
                } else if (Language.getCurrentLanguage() == 3) {
                    message = "Numéro de commande invalide, veuillez réessayer.";
                }
                HelpDialog dialog = new HelpDialog(message, OrderEntry.this);
                dialog.show();
                dialog.setCancelable(false);
                orderNumber.setEnabled(true);
                checkOrderBtn.setEnabled(false);
                CustomOrderKeyboard.disableEnterButton();
                // good order
            } else if (valid == 1) {
                orderNumber.setEnabled(false);
                checkOrderBtn.setEnabled(false);
                checkOrderBtn.setBackgroundResource(R.drawable.arrow_down_disabled);
                CustomerDialog dialog = new CustomerDialog(OrderEntry.this, orderNumber, Order.getCurrentOrder().getCustomerName(),
                        buyerName, selectDestinationBtn, checkOrderBtn, OrderEntry.this, progressBar, cancelOrderBtn, keyboard);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
                progressBar.setVisibility(View.GONE);
                CustomOrderKeyboard.disableEnterButton();
                // order needs to schedule appointment
            } else if (valid == 2) {
                checkOrderBtn.setEnabled(true);
                // CustomOrderKeyboard.enableEnterButton();
                String message = null;
                if (Language.getCurrentLanguage() == 1) {
                    message = "Order #" + orderNumber.getText().toString() + " requires an appointment but hasn't had one scheduled, please call 831-455-4305 to schedule an appointment.";
                } else if (Language.getCurrentLanguage() == 2) {
                    message = "Pedido #" + orderNumber.getText().toString() + " requiere una cita pero no ha programado una, llame al 831-455-4305 para programar una cita.";
                } else if (Language.getCurrentLanguage() == 3) {
                    message = "Commande #" + orderNumber.getText().toString() + " nécessite un rendez-vous, mais aucun rendez-vous n’a été prévu. Veuillez appeler le 831 455-4305 pour en programmer un.";
                }
                HelpDialog dialog = new HelpDialog(message, OrderEntry.this);
                dialog.show();
                dialog.setCancelable(false);
                checkOrderBtn.setEnabled(true);
                CustomOrderKeyboard.disableEnterButton();
                orderNumber.setEnabled(true);
                orderNumber.setText("");
            } else if (valid == 3) {
                String helpText = "";
                if (Language.getCurrentLanguage() == 1) {
                    helpText = "The order has already been checked in.";
                } else if (Language.getCurrentLanguage() == 2) {
                    helpText = "El pedido ya se ha registrado.";
                } else if (Language.getCurrentLanguage() == 3) {
                    helpText = "Cette commande a déjà été validée.";
                }
                HelpDialog dialog = new HelpDialog(helpText, OrderEntry.this);
                dialog.show();
                dialog.setCancelable(false);
                orderNumber.setText("");
                checkOrderBtn.setEnabled(true);
                CustomOrderKeyboard.disableEnterButton();
                orderNumber.setEnabled(true);
            } else if (valid == 4) {
                String helpText = "";
                if (Language.getCurrentLanguage() == 1) {
                    helpText = "Order number is for a different date, you can only submit orders for today.";
                } else if (Language.getCurrentLanguage() == 2) {
                    helpText = "El número de pedido es para una fecha diferente, solo puede enviar pedidos para hoy.";
                } else if (Language.getCurrentLanguage() == 3) {
                    helpText = "Le numéro de commande est pour une date différente, vous ne pouvez soumettre des commandes que pour aujourd'hui.";
                }
                HelpDialog dialog = new HelpDialog(helpText, OrderEntry.this);
                dialog.show();
                dialog.setCancelable(false);
                orderNumber.setText("");
                checkOrderBtn.setEnabled(true);
                CustomOrderKeyboard.disableEnterButton();
                orderNumber.setEnabled(true);
            } else if (valid == 5) {
                String helpText = "";
                if (Language.getCurrentLanguage() == 1) {
                    helpText = "A different driver has already checked in with this confirmation number.";
                } else if (Language.getCurrentLanguage() == 2) {
                    helpText = "Un controlador diferente ya se ha registrado con este número de confirmación";
                } else if (Language.getCurrentLanguage() == 3) {
                    helpText = "Un autre conducteur s'est déjà enregistré avec ce numéro de confirmation";
                }
                HelpDialog dialog = new HelpDialog(helpText, OrderEntry.this);
                dialog.show();
                dialog.setCancelable(false);
                orderNumber.setText("");
                checkOrderBtn.setEnabled(true);
                CustomOrderKeyboard.disableEnterButton();
                orderNumber.setEnabled(true);
            } else if (valid == 6) {
                String helpText = "";
                if (Language.getCurrentLanguage() == 1) {
                    helpText = "Appointment time has been missed. Please call 831-455-4305 to re-schedule an appointment.";
                } else if (Language.getCurrentLanguage() == 2) {
                    helpText = "Ha pasado el horario de la cita. Llame al 831-455-4305 para reprogramar la cita.";
                } else if (Language.getCurrentLanguage() == 3) {
                    helpText = "L’heure du rendez-vous est passée. Veuillez appeler le 831 455-4305 pour reprendre rendez-vous.";
                }
                HelpDialog dialog = new HelpDialog(helpText, OrderEntry.this);
                dialog.show();
                dialog.setCancelable(false);
                progressBar.setVisibility(View.GONE);
                orderNumber.setText("");
                orderNumber.setEnabled(true);
            } else if (valid == 7) {
                String helpText = "";
                if (Language.getCurrentLanguage() == 1) {
                    helpText = "One or more of the added orders has a later appointment time. These submitted orders will not be checked in until 1 hour prior to appointment time.";
                } else if (Language.getCurrentLanguage() == 2) {
                    helpText = "Ha pasado el horario de la cita para uno o más de los pedidos agregados. Estos pedidos enviados no se registrarán hasta 1 hora antes del horario de la cita.";
                } else if (Language.getCurrentLanguage() == 3) {
                    helpText = "Une ou plusieurs des commandes ajoutées ont un rendez-vous plus tard. Les commandes soumises ne seront pas validées jusqu’à 1 heure avant l’heure du rendez-vous.";
                }
                HelpDialog dialog = new HelpDialog(helpText, OrderEntry.this);
                dialog.show();
                dialog.setCancelable(false);
                progressBar.setVisibility(View.GONE);
                orderNumber.setText("");
                orderNumber.setEnabled(true);
            }
            progressBar.setVisibility(View.GONE);
        });
        // Used to initialize recycler view with data, empty order won't be visible.
        // If recycler view is empty (an actual order gets removed, causing it to be empty),
        // an empty order will be added until an actual order is added to the list
        if (Order.getOrdersList().size() == 0) {
            Order.addOrderToList(new Order("","","",
                    "", "","","","","",
                    "","","0","0"));
            addOrderListener.setValue(true);
        } else {
            addOrderListener.setValue(false);
        }

        recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(OrderEntry.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        horizontalLayoutManager.setAutoMeasureEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerViewHorizontalAdapter(OrderEntry.this, Order.getOrdersList(), OrderEntry.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        destinationListener.observe(OrderEntry.this, selectedDestination -> {
            if (selectedDestination.toLowerCase().equals(Order.getCurrentOrder().getDestination().toLowerCase())) {
                // correct
                selectDestinationBtn.setText(selectedDestination);
                selectDestinationBtn.clearAnimation();
                selectDestinationBtn.setEnabled(false);
                addOrderBtn.setEnabled(true);
                addOrderBtn.startAnimation(AnimationUtils.loadAnimation(OrderEntry.this, R.anim.fade));
                addOrderBtn.performClick();
                DESTINATION_ATTEMPTS = 0;
            } else if (selectedDestination.equals("show_dialog")) {
                selectDestinationBtn.performClick();
            } else {
                // incorrect
                DESTINATION_ATTEMPTS++;
                if (DESTINATION_ATTEMPTS >= 2) {
                    String message = null;
                    if (currentLanguage == 1) {
                        message = "Maximum destination attempts exceeded, please try another order number or contact your dispatcher.";
                    } else if (currentLanguage == 2) {
                        message = "Se excedieron los intentos de destino máximos, intente con otro número de pedido o comuníquese con su despachador.";
                    } else if (currentLanguage == 3) {
                        message = "Nombre maximal de tentatives de destination dépassé, veuillez essayer un autre numéro de commande ou contacter votre répartiteur.";
                    }
                    HelpDialog dialog = new HelpDialog(message, OrderEntry.this);
                    dialog.show();
                    dialog.setCancelable(false);
                    orderNumber.setText("");
                    buyerName.setVisibility(View.GONE);
                    selectDestinationBtn.setVisibility(View.GONE);
                    orderNumber.setEnabled(true);
                    checkOrderBtn.setEnabled(false);
                    CustomOrderKeyboard.disableEnterButton();
                    addOrderBtn.setEnabled(false);
                    checkOrderBtn.setBackgroundResource(R.drawable.arrow_right_disabled);
                    cancelOrderBtn.setVisibility(View.GONE);
                    checkOrderBtn.setVisibility(View.VISIBLE);
                    keyboard.setVisibility(View.VISIBLE);
                    DESTINATION_ATTEMPTS = 0;
                } else {
                    DestinationErrorDialog dialog = new DestinationErrorDialog(OrderEntry.this);
                    // selectDestinationBtn.performClick();
                    dialog.show();
                    dialog.setCancelable(false);
                }
            }
        });

        adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

        logoutBtn.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(OrderEntry.this, OrderEntry.this);
            dialog.show();
            dialog.setCancelable(false);
        });

        submitBtn.setOnClickListener(v -> {
            SubmitDialog dialog = new SubmitDialog(OrderEntry.this, OrderEntry.this);
            dialog.show();
            dialog.setCancelable(false);
        });

        addOrderBtn.setOnClickListener(v -> {
            keyboard.setVisibility(View.VISIBLE);
            addOrderBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            addOrderBtn.clearAnimation();
            if (!recyclerView.isShown()) {
                Order.getOrdersList().remove(0);
                Order.addOrderToList(Order.getCurrentOrder());
                if (Order.getOutlierOrders().contains(Order.getCurrentOrder())) {
                    Order.removeOrderFromOutlierList(Order.getCurrentOrder());
                }
                addOrderListener.setValue(false);
            } else {
                Order.addOrderToList(Order.getCurrentOrder());
                if (Order.getOutlierOrders().contains(Order.getCurrentOrder())) {
                    Order.removeOrderFromOutlierList(Order.getCurrentOrder());
                }
                addOrderListener.setValue(false);
            }

            if (Order.getCurrentOrder().getAppointment().equals("true")) {
                Order.setCurrentAppointmentTime(Order.getCurrentOrder().getAppointmentTime());
            } else {
                Order.setCurrentAppointmentTime(null);
            }

            adapter.notifyDataSetChanged();
            adapter.notifyItemInserted(adapter.getItemCount() - 1);
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            recyclerView.scheduleLayoutAnimation();

            orderNumber.setText("");
            buyerName.setText("");
            selectDestinationBtn.setText("");
            buyerName.setVisibility(View.GONE);
            selectDestinationBtn.setVisibility(View.GONE);
            selectDestinationBtn.setEnabled(true);

            possibleCustomerDestinations.clear();
            checkOrderBtn.setEnabled(false);
            CustomOrderKeyboard.disableEnterButton();
            addOrderBtn.setEnabled(false);
            cancelOrderBtn.setEnabled(false);
            cancelOrderBtn.setVisibility(View.GONE);
            checkOrderBtn.setVisibility(View.VISIBLE);

            // check if entered order has any connected orders
            try {
                new GetOrderDetailsByMasterNumber(Order.getCurrentOrder().getMasterNumber(), OrderEntry.this).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        orderNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (orderNumber.length() == 0) {
                    checkOrderBtn.setBackgroundResource(R.drawable.arrow_right_disabled);
                    selectDestinationBtn.setVisibility(View.GONE);
                    checkOrderBtn.setEnabled(false);
                    CustomOrderKeyboard.disableEnterButton();
                    if (recyclerView.isShown()) {
                        submitBtn.setEnabled(true);
                    } else {
                        submitBtn.setEnabled(false);
                    }
                } else {
                    checkOrderBtn.setBackgroundResource(R.drawable.arrow_right);
                    checkOrderBtn.setEnabled(true);
                    CustomOrderKeyboard.enableEnterButton();
                    submitBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        checkOrderBtn.setOnClickListener(v -> {
            checkOrderBtn.setEnabled(false);
            checkOrderBtn.setBackgroundResource(R.drawable.arrow_right_disabled);
            CustomOrderKeyboard.disableEnterButton();
            orderNumber.setEnabled(false);
            boolean added = false;
            for (int i = 0; i < Order.getOrdersList().size(); i++) {
                if (Order.getOrdersList().get(i).getSOPNumber().equals(orderNumber.getText().toString())) {
                    added = true;
                    break;
                }
            }
            if (added) {
                String helpText = "";
                if (currentLanguage == 1) {
                    helpText = "The order has already been added";
                } else if (currentLanguage == 2) {
                    helpText = "El pedido ya se ha agregado";
                } else if (currentLanguage == 3) {
                    helpText = "La commande a déjà été ajoutée";
                }
                HelpDialog dialog = new HelpDialog(helpText, OrderEntry.this);
                dialog.show();
                dialog.setCancelable(false);
                orderNumber.setText("");
                checkOrderBtn.setEnabled(true);
                CustomOrderKeyboard.enableEnterButton();
                orderNumber.setEnabled(true);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                try {
                    new GetOrderDetails(OrderEntry.this, orderNumber.getText().toString()).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelOrderBtn.setOnClickListener(v -> {
            CancelDialog dialog = new CancelDialog(OrderEntry.this, OrderEntry.this, buyerName);
            dialog.show();
            dialog.setCancelable(false);
        });

        selectDestinationBtn.setOnClickListener(v -> {
            ListViewDialog dialog = new ListViewDialog(OrderEntry.this, selectDestinationBtn, 0);
            dialog.show();
            dialog.setCancelable(false);
        });
    }

    public static void confirmMsg(final View v, Context context) {
        int selectedItemPosition = recyclerView.getChildLayoutPosition(v);
        DeleteDialog dialog = new DeleteDialog(Order.getOrdersList().get(selectedItemPosition).getSOPNumber(), context, v);
        dialog.show();
        dialog.setCancelable(false);
    }

    public static void removeItem(View v) {
        int selectedItemPosition = recyclerView.getChildLayoutPosition(v);

        Order.removeOrderFromList(selectedItemPosition);
        RecyclerViewHorizontalAdapter.decrementLastPosition();
        adapter.notifyItemRemoved(selectedItemPosition);

        if (Order.getOrdersList().size() == 0) {
            Order.addOrderToList(new Order("","","",
                    "","","","","","",
                    "","","0","0"));
            addOrderListener.setValue(true);
        }

        System.out.println("Order List: ");
        for (int i = 0; i < Order.getOrdersList().size(); i++) {
            System.out.println(Order.getOrdersList().get(i).getSOPNumber());
        }

        System.out.println("Outlier List: ");
        for (int i = 0; i < Order.getOutlierOrders().size(); i++) {
            System.out.println(Order.getOutlierOrders().get(i).getSOPNumber());
        }
    }

    private void changeLanguage(int val) {
        System.out.println("val: " + val);
        switch(val) {
            case 1:
                // English
                orderNumber.setHint("Order number");
                logoutBtn.setText(R.string.logout_eng);
                appointmentText.setText(R.string.appt_required_eng);
                loggedInAsText.setText(R.string.logged_in_as_eng);
                submitBtn.setText(R.string.submit_orders_eng);
                addOrderBtn.setText(R.string.add_order_eng);
                currentlyEntered.setText(R.string.entered_orders_eng);
                break;
            case 2:
                // Spanish
                orderNumber.setHint("Número de pedido");
                logoutBtn.setText(R.string.logout_sp);
                appointmentText.setText(R.string.appt_required_sp);
                loggedInAsText.setText(R.string.logged_in_as_sp);
                submitBtn.setText(R.string.submit_orders_sp);
                addOrderBtn.setText(R.string.add_order_sp);
                currentlyEntered.setText(R.string.entered_orders_sp);
                break;

            case 3:
                // French
                orderNumber.setHint("Numero de commande");
                logoutBtn.setText(R.string.logout_fr);
                appointmentText.setText(R.string.appt_required_fr);
                loggedInAsText.setText(R.string.logged_in_as_fr);
                submitBtn.setText(R.string.submit_orders_fr);
                addOrderBtn.setText(R.string.add_orders_fr);
                currentlyEntered.setText(R.string.entered_orders_fr);
                break;
        }
    }

    private void setup(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        orderNumber = findViewById(R.id.OrderNumberBox);
        logoutBtn = findViewById(R.id.LogoutBtn);
        TextView emailStr = findViewById(R.id.EmailStr);
        TextView phoneNumberStr = findViewById(R.id.PhoneNumberStr);
        TextView truckNumberStr = findViewById(R.id.TruckNumberStr);
        appointmentText = findViewById(R.id.AppointmentText);
        loggedInAsText = findViewById(R.id.LoggedInAsText);
        submitBtn = findViewById(R.id.SubmitBtn2);
        addOrderBtn = findViewById(R.id.AddOrderBtn);
        checkOrderBtn = findViewById(R.id.CheckOrderBtn);
        cancelOrderBtn = findViewById(R.id.CancelOrderBtn);
        cancelOrderBtn.setVisibility(View.GONE);
        selectDestinationBtn = findViewById(R.id.SelectDestinationBtn);
        selectDestinationBtn.setVisibility(View.GONE);

        buyerName = findViewById(R.id.BuyerName);
        buyerName.setVisibility(View.GONE);
        currentlyEntered = findViewById(R.id.CurrentlyEntered);
        currentlyEntered.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        possibleCustomerDestinations = new ArrayList<>();

        emailStr.setText(Account.getCurrentAccount().getEmail());
        phoneNumberStr.setText(PhoneNumberFormat.formatPhoneNumber(Account.getCurrentAccount().getPhoneNumber()));
        truckNumberStr.setText(String.format("%s %s", Account.getCurrentAccount().getTruckName(), Account.getCurrentAccount().getTruckNumber()));
        addOrderBtn.setEnabled(false);
        submitBtn.setEnabled(false);
        checkOrderBtn.setEnabled(false);
        CustomOrderKeyboard.disableEnterButton();
        checkOrderBtn.setBackgroundResource(R.drawable.arrow_right_disabled);
        changeLanguage(currentLanguage);
        if (currentLanguage == 3) {
            addOrderBtn.setTextSize(50);
            submitBtn.setTextSize(50);
        } else {
            addOrderBtn.setTextSize(60);
            submitBtn.setTextSize(60);
        }
        orderNumber.setShowSoftInputOnFocus(false);

        addOrderBtn.setVisibility(View.GONE);
    }

    public void rulesRegulationsSetup() {
        // Rules and Regulations page
        TextView title = findViewById(R.id.Title);
        TextView line1 = findViewById(R.id.line1);
        TextView line2 = findViewById(R.id.line2);
        TextView line3 = findViewById(R.id.line3);
        TextView line4 = findViewById(R.id.line4);
        TextView line5 = findViewById(R.id.line5);
        TextView line6 = findViewById(R.id.line6);
        TextView line7 = findViewById(R.id.line7);
        TextView line8 = findViewById(R.id.line8);
        TextView line9 = findViewById(R.id.line9);
        TextView line10 = findViewById(R.id.line10);
        TextView line11 = findViewById(R.id.line11);
        TextView line12 = findViewById(R.id.line12);
        TextView line13 = findViewById(R.id.line13);
        TextView bottomText = findViewById(R.id.BottomText);
        TextView selectText = findViewById(R.id.SelectText);
        Button rulesAcceptBtn = findViewById(R.id.AcceptBtn);

        if (currentLanguage == 1) {
            title.setText(R.string.regulations_eng);
            line1.setText(R.string.line1_eng);
            line2.setText(R.string.line2_eng);
            line3.setText(R.string.line3_eng);
            line4.setText(R.string.line4_eng);
            line5.setText(R.string.line5_eng);
            line6.setText(R.string.line6_eng);
            line7.setText(R.string.line7_eng);
            line8.setText(R.string.line8_eng);
            line9.setText(R.string.line9_eng);
            line10.setText(R.string.line10_eng);
            line11.setText(R.string.line11_eng);
            line12.setText(R.string.line12_eng);
            line13.setText(R.string.line13_eng);
            bottomText.setText(R.string.cooperation_eng);
            selectText.setText(R.string.verify_read_eng);
            rulesAcceptBtn.setText(R.string.accept_eng);
        } else if (currentLanguage == 2) {
            title.setText(R.string.regulations_sp);
            line1.setText(R.string.line1_sp);
            line2.setText(R.string.line2_sp);
            line3.setText(R.string.line3_sp);
            line4.setText(R.string.line4_sp);
            line5.setText(R.string.line5_sp);
            line6.setText(R.string.line6_sp);
            line7.setText(R.string.line7_sp);
            line8.setText(R.string.line8_sp);
            line9.setText(R.string.line9_sp);
            line10.setText(R.string.line10_sp);
            line11.setText(R.string.line11_sp);
            line12.setText(R.string.line12_sp);
            line13.setText(R.string.line13_sp);
            bottomText.setText(R.string.cooperation_sp);
            selectText.setText(R.string.verify_read_sp);
            rulesAcceptBtn.setText(R.string.accept_sp);
        } else if (currentLanguage == 3) {
            title.setText(R.string.regulations_fr);
            line1.setText(R.string.line_fr);
            line2.setText(R.string.line2_fr);
            line3.setText(R.string.line3_fr);
            line4.setText(R.string.line4_fr);
            line5.setText(R.string.line5_fr);
            line6.setText(R.string.line6_fr);
            line7.setText(R.string.line7_fr);
            line8.setText(R.string.line8_fr);
            line9.setText(R.string.line9_fr);
            line10.setText(R.string.line10_fr);
            line11.setText(R.string.line11_fr);
            line12.setText(R.string.line12_fr);
            line13.setText(R.string.line13_fr);
            bottomText.setText(R.string.cooperation_fr);
            selectText.setText(R.string.verify_read_fr);
            rulesAcceptBtn.setText(R.string.accept_fr);
        }
    }
}