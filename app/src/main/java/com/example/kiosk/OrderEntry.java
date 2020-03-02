package com.example.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class OrderEntry extends AppCompatActivity {

    private int currentLanguage = Language.getCurrentLanguage();

    private EditText orderNumber;
    private TextView buyerName, errorMessage, appointmentText, loggedInAsText, currentlyEntered;
    private Button logoutBtn, submitBtn, addOrderBtn, selectDestinationBtn;
    private ImageButton checkOrderBtn;

    // Rules and Regulations page
    private TextView line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11, line12, line13;

    private static Order currentOrder;

    private Spinner destinationSpinner;
    private boolean initialSelection = false;

    private static RecyclerViewHorizontalAdapter adapter;
    private static RecyclerView recyclerView;

    private static ArrayList<Order> possibleOrders = new ArrayList<>();

    private static boolean empty;

    Context context;

    private static int DESTINATION_ATTEMPTS = 0;

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

        setup();

        context = this;

        if (Order.getSize() == 0) {
            Order.addOrder(new Order("","","", ""));
            empty = true;
        }

        recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(OrderEntry.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerViewHorizontalAdapter(context, Order.getOrders());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (empty) {
           recyclerView.setVisibility(View.INVISIBLE);
        }

        final ArrayAdapter<CharSequence> destinationAdapter = ArrayAdapter.createFromResource(this, R.array.states, R.layout.spinner_layout);
        destinationAdapter.setDropDownViewResource(R.layout.spinner_layout);
        destinationSpinner.setAdapter(destinationAdapter);
        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] statesArray = getResources().getStringArray(R.array.states);
                if (initialSelection) {
                    if (statesArray[position].equals(currentOrder.getDestination())) {
                        selectDestinationBtn.setText(getResources().getStringArray(R.array.states)[position]);
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
                            // initialSelection = false;

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
/**
        Order.addOrder(new Order("FF555", "Charlies", "Arizona"));
        Order.addOrder(new Order("ASDA4", "Whole Foods", "Santa Cruz, California"));
        Order.addOrder(new Order("654FF", "Jonathon", "Denver, Colorado"));
        Order.addOrder(new Order("BC333", "Brock", "Detroit, Michigan"));
        Order.addOrder(new Order("JHGG5", "Safeway", "Los Angeles, California"));
        Order.addOrder(new Order("GSSD2", "New Leaf", "San Francisco, California"));
        Order.addOrder(new Order("HGFF3", "Target", "Orlando, Florida"));
        Order.addOrder(new Order("XF2DX", "Costco", "Seattle, Washington"));
        Order.addOrder(new Order("54VVC", "Johnnie's Farm", "Houston, Texas"));
*/
        adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderEntry.this, MainActivity.class);
                startActivity(intent);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Number of orders: " + Order.getSize());
                if (Order.getOrders().get(0).getOrderNumber().length() > 1) {
                    setContentView(R.layout.rules_regulations);

                    findViewById(R.id.SubmitBtn2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OrderEntry.this, OrderSummary.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    String message = null;
                    if (currentLanguage == 0) {
                        message = "No orders have been added. Please add an order before submitting.";
                    } else if (currentLanguage == 1) {
                        message = "No se han agregado pedidos. Agregue un pedido antes de enviarlo.";
                    } else if (currentLanguage == 2) {
                        message = "Aucune commande n'a été ajoutée. Veuillez ajouter une ordre avant de la soumettre.";
                    }
                    HelpDialog dialog = new HelpDialog(message, OrderEntry.this);
                    dialog.show();
                }
            }
        });

        addOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrderBtn.clearAnimation();
                String orderNumberStr, buyerNameStr, destinationStr;
                orderNumberStr = orderNumber.getText().toString();
                buyerNameStr = buyerName.getText().toString();
                destinationStr = selectDestinationBtn.getText().toString();
                if (!recyclerView.isShown()) {
                    Order.getOrders().remove(0);
                    recyclerView.setVisibility(View.VISIBLE);
                    Order.addOrder(new Order(orderNumberStr, buyerNameStr, destinationStr, "5:00pm"));
                    empty = false;
                } else {
                    Order.addOrder(new Order(orderNumberStr, buyerNameStr, destinationStr, "5:00pm"));
                    recyclerView.setVisibility(View.VISIBLE);
                    empty = false;
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

                showSoftKeyboard(orderNumber);
                orderNumber.setFocusable(true);
                orderNumber.requestFocus();
                checkOrderBtn.setEnabled(true);
                addOrderBtn.setEnabled(false);
                submitBtn.setEnabled(true);
                currentlyEntered.setVisibility(View.VISIBLE);
            }
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

        checkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentOrder = null;
                boolean found = false;
                for (int i = 0; i < possibleOrders.size(); i++) {
                    if (possibleOrders.get(i).getOrderNumber().equals(orderNumber.getText().toString())) {
                        currentOrder = possibleOrders.get(i);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    errorMessage.setText("*Invalid order number, please try again");
                    errorMessage.setVisibility(View.VISIBLE);
                } else {
                    if (orderNumber.getText().toString().equals(currentOrder.getOrderNumber())) {
                        checkOrderBtn.setEnabled(false);
                        errorMessage.setVisibility(View.GONE);
                        checkOrderBtn.setBackgroundResource(R.drawable.arrow_down);
                        CustomerDialog dialog = new CustomerDialog(OrderEntry.this, orderNumber, currentOrder.getBuyerName(), buyerName, selectDestinationBtn, errorMessage, checkOrderBtn, OrderEntry.this);
                        dialog.show();
                    }
                }
            }
        });

        selectDestinationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationSpinner.performClick();
            }
        });
    }

    public static void confirmMsg(final View v, Context context) {
        int selectedItemPosition = recyclerView.getChildLayoutPosition(v);
        DeleteDialog dialog = new DeleteDialog(Order.getOrders().get(selectedItemPosition).getOrderNumber(), context, v);
        dialog.show();
    }

    public static void removeItem(View v) {
        int selectedItemPosition = recyclerView.getChildLayoutPosition(v);

        Order.removeOrder(selectedItemPosition);
        adapter.notifyItemRemoved(selectedItemPosition);

        if (Order.getSize() == 0) {
            Order.addOrder(new Order("","","", ""));
            empty = true;
            recyclerView.setVisibility(View.INVISIBLE);
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

    @SuppressLint("SetTextI18n")
    private void changeLanguage(int val) {
        System.out.println("val: " + val);
        switch(val) {
            case 0:
                // English
                orderNumber.setHint("Order number");
                logoutBtn.setText("Logout");
                appointmentText.setText("If your order requires an appointment please call 831-455-4305 to schedule an appointment");
                loggedInAsText.setText("You are logged in as: ");
                submitBtn.setText("Submit Orders");
                logoutBtn.setText("Logout");
                addOrderBtn.setText("Add Order");
                currentlyEntered.setText("You have entered information for the following orders:");
/*
                line1.setText("&#8226; Your trailer temperature must be set to 34&#176;F continuously and pre-cooled prior to loading.");
                line2.setText("&#8226; Your trailer must be in sanitary condition to transport human food.");
                line3.setText("&#8226; Trailers are to be cleaned prior to loading, no cleaning on the premises.");
                line4.setText("&#8226; You must slide your tandems to the back prior to loading.");
                line5.setText("&#8226; You are responsible for your own load locks. You are expected to remove your load locks prior to loading and secure your load after loading.");
                line6.setText("&#8226; All drivers are expected to be present while being loaded.");
                line7.setText("&#8226; All drivers are to stand in designated areas at all times.");
                line8.setText("&#8226; Absolutely no minors allowed on the dock. No exceptions!");
                line9.setText("&#8226; No food or drinks allowed inside the cold box.");
                line10.setText("&#8226; Do not operate doors or dock plates.");
                line11.setText("&#8226; Be extremely cautious of wet, slippery floors as well as moving forklifts.");
                line12.setText("&#8226; Cash transactions and/or gratuities of any kind are strictly prohibited.");
                line13.setText("&#8226; Be advised that video surveillance is capturing your actions 24 hours a day.");

 */
                break;
            case 1:
                // Spanish
                orderNumber.setHint("Número de pedido");
                logoutBtn.setText("Logout");
                appointmentText.setText("Si su pedido requiere una cita, llame al 831-455-4305 para programar una cita");
                loggedInAsText.setText("Conectado como: ");
                submitBtn.setText("Enviar pedidos");
                logoutBtn.setText("Cerrar sesión");
                addOrderBtn.setText("Añadir pedido");
                currentlyEntered.setText("Ha ingresado información para los siguientes pedidos:");
                break;

            case 2:
                // French
                orderNumber.setHint("Numero de ordre");
                logoutBtn.setText("Logout");
                appointmentText.setText("Si votre commande nécessite un rendez-vous, veuillez appeler le 831-455-4305 pour fixer un rendez-vous");
                loggedInAsText.setText("Connecté en tant que: ");
                submitBtn.setText("Soumettre ordres");
                logoutBtn.setText("Se déconnecter");
                addOrderBtn.setText("Ajouter ordre");
                currentlyEntered.setText("Vous avez entré des informations pour les commandes suivantes:");
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
        errorMessage = findViewById(R.id.ErrorMessage);
        errorMessage.setVisibility(View.GONE);
        currentlyEntered = findViewById(R.id.CurrentlyEntered);
        currentlyEntered.setVisibility(View.INVISIBLE);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);
        line5 = findViewById(R.id.line5);
        line6 = findViewById(R.id.line6);
        line7 = findViewById(R.id.line7);
        line8 = findViewById(R.id.line8);
        line9 = findViewById(R.id.line9);
        line10 = findViewById(R.id.line10);
        line11 = findViewById(R.id.line11);
        line12 = findViewById(R.id.line12);
        line13 = findViewById(R.id.line13);

        possibleOrders.add(new Order("FF555", "Charlies", "Arizona", "5:00pm"));
        possibleOrders.add(new Order("BB222", "John","California","6:30pm"));

        showSoftKeyboard(orderNumber);
        Account currentAccount = MainActivity.getCurrentAccount();
        emailStr.setText(currentAccount.getEmail());
        phoneNumberStr.setText(formatPhoneNumber(currentAccount.getPhoneNumber()));
        truckNumberStr.setText(String.format("%s %s", currentAccount.getTruckName(), currentAccount.getTruckNumber()));
        addOrderBtn.setEnabled(false);
        submitBtn.setEnabled(false);
        changeLanguage(currentLanguage);
    }
}
