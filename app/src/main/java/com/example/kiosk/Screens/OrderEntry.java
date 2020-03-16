package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.kiosk.Account;
import com.example.kiosk.Dialogs.CustomerDialog;
import com.example.kiosk.Dialogs.DeleteDialog;
import com.example.kiosk.Dialogs.HelpDialog;
import com.example.kiosk.Dialogs.LogoutDialog;
import com.example.kiosk.Dialogs.SubmitDialog;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.RecyclerViewAssociatedAdapter;
import com.example.kiosk.Helpers.RecyclerViewHorizontalAdapter;
import com.example.kiosk.Helpers.RecyclerViewSummaryAdapter;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import com.example.kiosk.Webservices.GetMasterOrderDetails;
import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class OrderEntry extends AppCompatActivity {

    private int currentLanguage = Language.getCurrentLanguage();

    private EditText orderNumber;
    private TextView buyerName, appointmentText, loggedInAsText, currentlyEntered;
    private Button logoutBtn, submitBtn, addOrderBtn, selectDestinationBtn;
    private ImageButton checkOrderBtn;
    private ProgressBar progressBar;

    private Spinner destinationSpinner;
    private boolean initialSelection = false;

    private static RecyclerViewHorizontalAdapter adapter;
    private static RecyclerView recyclerView;

    public static List<String> possibleCustomerDestinations;

    private static MutableLiveData<Boolean> listener = null;
    private static MutableLiveData<Boolean> dialogListener = null;
    public static MutableLiveData<Boolean> validOrderNumber = null;
    public static MutableLiveData<Boolean> listListener = null;

    private int DESTINATION_ATTEMPTS = 0;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_entry);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        System.out.println("Total weight: " + MasterOrder.getTotalWeight());

        listener = new MutableLiveData<>();
        listener.setValue(true);

        dialogListener = new MutableLiveData<>();
        dialogListener.setValue(false);

        validOrderNumber = new MutableLiveData<>();

        listListener = new MutableLiveData<>();

        listener.observe(OrderEntry.this, empty -> {
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

        dialogListener.observe(OrderEntry.this, dialogChoice -> {
            if (dialogChoice) {
                setContentView(R.layout.rules_regulations);
                rulesRegulationsSetup();
                findViewById(R.id.SubmitBtn2).setOnClickListener(v -> {
                    Intent intent = new Intent(OrderEntry.this, OrderSummary.class);
                    startActivity(intent);
                });
            }
        });

        validOrderNumber.observe(OrderEntry.this, valid -> {
            if (!valid) {
                checkOrderBtn.setEnabled(true);
                String message = null;
                if (Language.getCurrentLanguage() == 0) {
                    message = "Invalid order number, please try again";
                } else if (Language.getCurrentLanguage() == 1) {
                    message = "Número de pedido no válido, intente nuevamente";
                } else if (Language.getCurrentLanguage() == 2) {
                    message = "Numéro de ordre non valide, veuillez réessayer";
                }
                HelpDialog dialog = new HelpDialog(message, OrderEntry.this);
                dialog.show();
            } else {
                orderNumber.setEnabled(false);
                checkOrderBtn.setEnabled(false);
                checkOrderBtn.setBackgroundResource(R.drawable.arrow_down);
                CustomerDialog dialog = new CustomerDialog(OrderEntry.this, orderNumber, MasterOrder.getCurrentMasterOrder().getCustomerName(),
                        buyerName, selectDestinationBtn, checkOrderBtn, OrderEntry.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        listListener.observe(OrderEntry.this, empty -> {
            if (!empty) {
                ArrayAdapter<String> destinationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, possibleCustomerDestinations);
                destinationAdapter.setDropDownViewResource(R.layout.spinner_layout);
                destinationSpinner.setAdapter(destinationAdapter);
            }
        });

        setup();

        if (MasterOrder.getMasterOrdersList().size() == 0) {
            MasterOrder.addMasterOrderToList(new MasterOrder("","","",
                    "", "","","","","",
                    "","","0","0"));
            listener.setValue(true);
        }

        recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(OrderEntry.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerViewHorizontalAdapter(OrderEntry.this, MasterOrder.getMasterOrdersList());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // String[] statesArray = getResources().getStringArray(R.array.states);
                String[] destinationsArray = possibleCustomerDestinations.toArray(new String[possibleCustomerDestinations.size()]);
                if (initialSelection) {
                    if (destinationsArray[position].equals(MasterOrder.getCurrentMasterOrder().getDestination())) {
                        selectDestinationBtn.setText(destinationsArray[position]);
                        addOrderBtn.setEnabled(true);
                        selectDestinationBtn.clearAnimation();
                        addOrderBtn.startAnimation(AnimationUtils.loadAnimation(OrderEntry.this, R.anim.fade));
                        DESTINATION_ATTEMPTS = 0;
                        selectDestinationBtn.setEnabled(false);
                    } else {
                        DESTINATION_ATTEMPTS++;
                        System.out.println("Destination Attempts: " + DESTINATION_ATTEMPTS);
                        if (DESTINATION_ATTEMPTS >= 2) {
                            String message = null;
                            if (currentLanguage == 0) {
                                message = "Maximum destination attempts exceeded, please try another order number or contact your dispatcher.";
                            } else if (currentLanguage == 1) {
                                message = "Se excedieron los intentos de destino máximos, intente con otro número de pedido o comuníquese con su despachador.";
                            } else if (currentLanguage == 2) {
                                message = "Nombre maximal de tentatives de destination dépassé, veuillez essayer un autre numéro de ordre ou contacter votre répartiteur.";
                            }
                            HelpDialog dialog = new HelpDialog(message, OrderEntry.this);
                            dialog.show();
                            orderNumber.setText("");
                            buyerName.setVisibility(View.GONE);
                            selectDestinationBtn.setVisibility(View.GONE);

                            showSoftKeyboard(orderNumber);
                            orderNumber.setFocusable(true);
                            orderNumber.requestFocus();
                            checkOrderBtn.setEnabled(true);
                            addOrderBtn.setEnabled(false);
                            DESTINATION_ATTEMPTS = 0;
                        } else {
                            String message = null;
                            if (currentLanguage == 0) {
                                message = "Incorrect destination for the entered order number, you have one attempt remaining.";
                            } else if (currentLanguage == 1) {
                                message = "Destino incorrecto para el número de pedido ingresado, le queda un intento.";
                            } else if (currentLanguage == 2) {
                                message = "Destination incorrecte pour le numéro de ordre saisi, il vous reste un tentative";
                            }
                            HelpDialog dialog = new HelpDialog(message, OrderEntry.this);
                            dialog.show();
                        }
                    }
                } else {
                    initialSelection = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

        logoutBtn.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(OrderEntry.this, v);
            dialog.show();
            // Intent intent = new Intent(OrderEntry.this, MainActivity.class);
            // startActivity(intent);
        });

        submitBtn.setOnClickListener(v -> {
            SubmitDialog dialog = new SubmitDialog(OrderEntry.this, v);
            dialog.show();
        });

        addOrderBtn.setOnClickListener(v -> {
            addOrderBtn.clearAnimation();
            if (!recyclerView.isShown()) {
                MasterOrder.getMasterOrdersList().remove(0);
                MasterOrder.addMasterOrderToList(MasterOrder.getCurrentMasterOrder());
                listener.setValue(false);
            } else {
                MasterOrder.addMasterOrderToList(MasterOrder.getCurrentMasterOrder());
                // listener.setValue(false);
            }

            destinationSpinner.setSelection(0);
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
            initialSelection = false;

            setContentView(R.layout.connected_orders);
            MasterOrder test = new MasterOrder("F0WADA","OWAW99W0","01","San Jose, California","Driscolls","Fulfilled",
                    "Loblaw Companies Inc.","true","true","03/13/2020","16:00:00", "3600.000","1.67000");
            MasterOrder test2 = new MasterOrder("LOPS0L","XCW245R","01","Santa Cruz, California","Charlies","Fulfilled",
                    "Johnson Farms Inc.","true","true","03/13/2020","16:00:00", "3600.000","1.67000");
            MasterOrder test3 = new MasterOrder("30I34I","AWD0IOS","01","Yuma, Arizona","Johnnies","Fulfilled",
                    "Gabriel Companies Inc.","true","true","03/13/2020","16:00:00", "3600.000","1.67000");

            ArrayList<MasterOrder> connectedOrders = new ArrayList<>();
            connectedOrders.add(test);
            connectedOrders.add(test2);
            connectedOrders.add(test3);

            RecyclerView recyclerView = findViewById(R.id.AssociatedOrdersView);
            LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(OrderEntry.this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(verticalLayoutManager);
            RecyclerViewAssociatedAdapter adapter = new RecyclerViewAssociatedAdapter(connectedOrders);
            recyclerView.setAdapter(adapter);

            findViewById(R.id.btn_add).setOnClickListener(v1 -> {
                List<MasterOrder> selectedOrders = RecyclerViewAssociatedAdapter.getSelectedOrders();
                for (int i = 0; i < selectedOrders.size(); i++) {
                    MasterOrder.addMasterOrderToList(selectedOrders.get(i));
                }
                setContentView(R.layout.activity_order_entry);
            });

            findViewById(R.id.btn_cancel).setOnClickListener(v12 -> setContentView(R.layout.activity_order_entry));

            orderNumber.setEnabled(true);
            showSoftKeyboard(orderNumber);
            orderNumber.setFocusable(true);
            orderNumber.requestFocus();
            checkOrderBtn.setEnabled(true);
            addOrderBtn.setEnabled(false);
        });

        orderNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (orderNumber.length() == 0) {
                    checkOrderBtn.setBackgroundResource(R.drawable.arrow_right);
                    selectDestinationBtn.setVisibility(View.GONE);
                    checkOrderBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkOrderBtn.setOnClickListener(v -> {
            checkOrderBtn.setEnabled(false);
            boolean added = false;
            for (int i = 0; i < MasterOrder.getMasterOrdersList().size(); i++) {
                if (MasterOrder.getMasterOrdersList().get(i).getSOPNumber().equals(orderNumber.getText().toString())) {
                    added = true;
                    break;
                }
            }
            if (added) {
                String helpText = "";
                if (currentLanguage == 0) {
                    helpText = "The order has already been added";
                } else if (currentLanguage == 1) {
                    helpText = "El pedido ya ha sido agregado";
                } else if (currentLanguage == 2) {
                    helpText = "La ordre a déjà été ajoutée";
                }
                HelpDialog dialog = new HelpDialog(helpText, OrderEntry.this);
                dialog.show();
                orderNumber.setText("");
                // showSoftKeyboard(orderNumber);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                new GetMasterOrderDetails(OrderEntry.this, orderNumber.getText().toString()).execute();
            }
        });

        selectDestinationBtn.setOnClickListener(v -> destinationSpinner.performClick());
    }

    public static void confirmMsg(final View v, Context context) {
        int selectedItemPosition = recyclerView.getChildLayoutPosition(v);
        DeleteDialog dialog = new DeleteDialog(MasterOrder.getMasterOrdersList().get(selectedItemPosition).getSOPNumber(), context, v);
        dialog.show();
    }

    public static void removeItem(View v) {
        int selectedItemPosition = recyclerView.getChildLayoutPosition(v);

        MasterOrder.removeMasterOrderFromList(selectedItemPosition);
        adapter.notifyItemRemoved(selectedItemPosition);

        if (MasterOrder.getMasterOrdersList().size() == 0) {
            // Order.addOrder(new Order("","","", "", 0, 0));
            MasterOrder.addMasterOrderToList(new MasterOrder("","","",
                    "","","","","","",
                    "","","0","0"));
            listener.setValue(true);
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, SHOW_IMPLICIT);
            }
        }
    }

    public static void setDialogListener(Boolean b) {
        dialogListener.setValue(b);
    }

    public String formatPhoneNumber(String number) {
        StringBuilder newNum = new StringBuilder();
        char[] charNum = number.toCharArray();
        newNum.append("(");
        for (int i = 0; i < charNum.length; i++) {
            if (i == 2) {
                newNum.append(charNum[i]);
                newNum.append(")-");
            } else if (i == 5) {
                newNum.append(charNum[i]);
                newNum.append("-");
            } else {
                newNum.append(charNum[i]);
            }
        }
        return newNum.toString();
    }

    private void changeLanguage(int val) {
        System.out.println("val: " + val);
        switch(val) {
            case 0:
                // English
                orderNumber.setHint("Order number");
                logoutBtn.setText(R.string.logout_eng);
                appointmentText.setText(R.string.appt_required_eng);
                loggedInAsText.setText(R.string.logged_in_as_eng);
                submitBtn.setText(R.string.submit_orders_eng);
                addOrderBtn.setText(R.string.add_order_eng);
                currentlyEntered.setText(R.string.entered_orders_eng);
                break;
            case 1:
                // Spanish
                orderNumber.setHint("Número de pedido");
                logoutBtn.setText(R.string.logout_sp);
                appointmentText.setText(R.string.appt_required_sp);
                loggedInAsText.setText(R.string.logged_in_as_sp);
                submitBtn.setText(R.string.submit_orders_sp);
                addOrderBtn.setText(R.string.add_order_sp);
                currentlyEntered.setText(R.string.entered_orders_sp);
                break;

            case 2:
                // French
                orderNumber.setHint("Numero de ordre");
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
        selectDestinationBtn = findViewById(R.id.SelectDestinationBtn);
        selectDestinationBtn.setVisibility(View.GONE);
        destinationSpinner = findViewById(R.id.DestinationSpinner);
        destinationSpinner.setVisibility(View.INVISIBLE);
        buyerName = findViewById(R.id.BuyerName);
        buyerName.setVisibility(View.GONE);
        currentlyEntered = findViewById(R.id.CurrentlyEntered);
        currentlyEntered.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        possibleCustomerDestinations = new ArrayList<>();

        // possibleOrders.add(new Order("FF555", "Charlies" + "/" + "Consignee Co.", "San Jose, California", "5:00pm", 1350, 5));
        // possibleOrders.add(new Order("BB222", "John" + "/" + "Jonathon","Santa Cruz, California","6:30pm", 500, 1));
        // possibleOrders.add(new Order("00000", "Starbucks" + "/" + "Bob's","Seattle, Washington","1:00pm", 1200, 5));
        // possibleOrders.add(new Order("11111", "Safeway" + "/" + "Vans","Yuma, Arizona","9:00am", 6000, 8));

        showSoftKeyboard(orderNumber);
        emailStr.setText(Account.getCurrentAccount().getEmail());
        phoneNumberStr.setText(formatPhoneNumber(Account.getCurrentAccount().getPhoneNumber()));
        truckNumberStr.setText(String.format("%s %s", Account.getCurrentAccount().getTruckName(), Account.getCurrentAccount().getTruckNumber()));
        addOrderBtn.setEnabled(false);
        submitBtn.setEnabled(false);
        changeLanguage(currentLanguage);
        findViewById(R.id.cardView3).setVisibility(View.INVISIBLE);
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
        Button submitBtn2 = findViewById(R.id.SubmitBtn2);

        if (currentLanguage == 0) {
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
            submitBtn2.setText(R.string.submit_eng);
        } else if (currentLanguage == 1) {
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
            submitBtn2.setText(R.string.submit_sp);
        } else if (currentLanguage == 2) {
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
            submitBtn2.setText(R.string.submit_fr);
        }
    }
}
