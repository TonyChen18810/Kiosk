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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class OrderInfo extends AppCompatActivity {

    private EditText orderNumber;
    private EditText buyerName;
    private EditText destination;
    private Button logoutBtn;
    private TextView pleaseEnterText;
    private TextView appointmentText;
    private TextView loggedInAsText;
    private Button submitBtn;
    private Button addOrderBtn;

    private static RecyclerViewHorizontalAdapter adapter;
    private static RecyclerView recyclerView;

    boolean empty;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setup();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderNumber.setText(extras.getString("Order Number"));
            buyerName.setText(extras.getString("Buyer Name"));
            destination.setText(extras.getString("Destination"));
        }

        changeLanguage(MainActivity.getCurrentLanguage());
        context = this;

        if (Order.getSize() == 0) {
            Order.addOrder(new Order("","",""));
            empty = true;
        }

        recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(OrderInfo.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerViewHorizontalAdapter(context, Order.getOrders());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (empty) {
           recyclerView.setVisibility(View.INVISIBLE);
        }
        /*
        Order.addOrder(new Order("FF555", "Charlies", "Yuma, Arizona"));
        Order.addOrder(new Order("ASDA4", "Whole Foods", "Santa Cruz, California"));
        Order.addOrder(new Order("654FF", "Jonathon", "Denver, Colorado"));
        Order.addOrder(new Order("BC333", "Brock", "Detroit, Michigan"));
        Order.addOrder(new Order("JHGG5", "Safeway", "Los Angeles, California"));
        Order.addOrder(new Order("GSSD2", "New Leaf", "San Francisco, California"));
        Order.addOrder(new Order("HGFF3", "Target", "Orlando, Florida"));
        Order.addOrder(new Order("XF2DX", "Costco", "Seattle, Washington"));
        Order.addOrder(new Order("54VVC", "Johnnie's Farm", "Houston, Texas"));

        adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        */

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });

        orderNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && buyerName.length() != 0 && destination.length() != 0) {
                    addOrderBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buyerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && orderNumber.length() != 0 && destination.length() != 0) {
                    addOrderBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && buyerName.length() != 0 && orderNumber.length() != 0) {
                    addOrderBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderInfo.this, OrderSubmitted.class);
                startActivity(intent);
            }
        });

        addOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderNumberStr, buyerNameStr, destinationStr;
                orderNumberStr = orderNumber.getText().toString();
                buyerNameStr = buyerName.getText().toString();
                destinationStr = destination.getText().toString();
                if (!recyclerView.isShown()) {
                    Order.getOrders().remove(0);
                    recyclerView.setVisibility(View.VISIBLE);
                    Order.addOrder(new Order(orderNumberStr, buyerNameStr, destinationStr));
                    empty = false;
                } else {
                    Order.addOrder(new Order(orderNumberStr, buyerNameStr, destinationStr));
                    recyclerView.setVisibility(View.VISIBLE);
                    empty = false;
                }

                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                recyclerView.scheduleLayoutAnimation();

                orderNumber.setText("");
                buyerName.setText("");
                destination.setText("");
/*
                Animation animation = new AlphaAnimation(1, 0.5f);
                animation.setDuration(500);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(3);
                animation.setRepeatMode(Animation.REVERSE);
                View lastItemView = recyclerView.getLayoutManager().findViewByPosition(adapter.getItemCount() - 1);
                if (lastItemView != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.requestLayout();
                    recyclerView.startAnimation(animation);
                }
 */
                orderNumber.requestFocus();
                addOrderBtn.setEnabled(false);
            }
        });
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
                buyerName.setHint("Buyer name");
                destination.setHint("Destination");
                logoutBtn.setText("Logout");
                pleaseEnterText.setText("Enter order information: ");
                appointmentText.setText("If your order requires an appointment please call 831-455-4305 to schedule an appointment");
                loggedInAsText.setText("You are logged in as: ");
                submitBtn.setText("Submit");
                logoutBtn.setText("Logout");
                addOrderBtn.setText("Add Order");
                break;
            case 1:
                // Spanish
                orderNumber.setHint("Número de pedido");
                buyerName.setHint("Nombre del comprador");
                destination.setHint("Destino");
                logoutBtn.setText("Logout");
                pleaseEnterText.setText("Ingrese la información del pedido");
                appointmentText.setText("Si su pedido requiere una cita, llame al 831-455-4305 para programar una cita");
                loggedInAsText.setText("Conectado como: ");
                submitBtn.setText("Enviar");
                logoutBtn.setText("Cerrar sesión");
                addOrderBtn.setText("Añadir pedido");
                break;

            case 2:
                // French
                orderNumber.setHint("Numero de ordre");
                buyerName.setHint("Nom de l'acheteur");
                destination.setHint("Destination");
                logoutBtn.setText("Logout");
                pleaseEnterText.setText("Entrez les informations de la ordre");
                appointmentText.setText("Si votre commande nécessite un rendez-vous, veuillez appeler le 831-455-4305 pour fixer un rendez-vous");
                loggedInAsText.setText("Connecté en tant que: ");
                submitBtn.setText("Soumettre");
                logoutBtn.setText("Se déconnecter");
                addOrderBtn.setText("Ajouter ordre");
                break;
        }
    }

    private void setup(){
        orderNumber = findViewById(R.id.OrderNumberBox);
        buyerName = findViewById(R.id.BuyerNameBox);
        destination = findViewById(R.id.DestinationBox);
        logoutBtn = findViewById(R.id.LoginBtn);
        TextView emailStr = findViewById(R.id.EmailStr);
        TextView phoneNumberStr = findViewById(R.id.PhoneNumberStr);
        TextView truckNumberStr = findViewById(R.id.TruckNumberStr);
        pleaseEnterText = findViewById(R.id.EnterInfoText);
        appointmentText = findViewById(R.id.AppointmentText);
        loggedInAsText = findViewById(R.id.LoggedInAsText);
        submitBtn = findViewById(R.id.SubmitBtn);
        addOrderBtn = findViewById(R.id.AddOrderBtn);
        showSoftKeyboard(orderNumber);
        Account currentAccount = MainActivity.getCurrentAccount();
        emailStr.setText(currentAccount.getEmail());
        phoneNumberStr.setText(formatPhoneNumber(currentAccount.getPhoneNumber()));
        truckNumberStr.setText(String.format("%s %s", currentAccount.getTruckName(), currentAccount.getTruckNumber()));
        addOrderBtn.setEnabled(false);
    }
}
