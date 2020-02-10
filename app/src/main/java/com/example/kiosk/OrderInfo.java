package com.example.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class OrderInfo extends AppCompatActivity {

    private EditText orderNumber;
    private EditText buyerName;
    private EditText destination;
    private Button logoutBtn;
    private TextView emailStr;
    private TextView phoneNumberStr;
    private TextView truckNumberStr;
    private Account currentAccount;
    private TextView pleaseEnterText;
    private TextView appointmentText;
    private TextView loggedInAsText;
    private Button nextBtn;

    private RecyclerViewAdapter adapter;

    private List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_order_info);
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
        changeLanguage(MainActivity.getCurrentLanguage());

        orders.add(new Order("FF555", "Charlies", "Yuma, Arizona"));
        orders.add(new Order("ASDA4", "Whole Foods", "Santa Cruz, California"));
        orders.add(new Order("654FF", "Jonathon", "Denver, Colorado"));
        orders.add(new Order("BC333", "Brock", "Detroit, Michigan"));
        orders.add(new Order("JHGG5", "Safeway", "Los Angeles, California"));
        orders.add(new Order("GSSD2", "New Leaf", "San Francisco, California"));
        orders.add(new Order("HGFF3", "Target", "Orlando, Florida"));
        orders.add(new Order("XF2DX", "Costco", "Seattle, Washington"));
        orders.add(new Order("54VVC", "Johnnie's Farm", "Houston, Texas"));

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.OrdersView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(OrderInfo.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new RecyclerViewAdapter(this, orders);
        // adapter.setClickListener((RecyclerViewAdapter.ItemClickListener) this);
        recyclerView.setAdapter(adapter);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderNumberStr, buyerNameStr, destinationStr;
                orderNumberStr = orderNumber.getText().toString();
                buyerNameStr = buyerName.getText().toString();
                destinationStr = destination.getText().toString();
                Order order = new Order(orderNumberStr, buyerNameStr, destinationStr);
                orders.add(order);
            }
        });
/**
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printOrders();
            }
        });
 */
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

    public void printOrders() {
        for (int i = 0; i < orders.size(); i++) {
            System.out.println("Order " + i + ":\n" + "Order number: " + orders.get(i).getOrderNumber() + "\n" + "Buyer name: " + orders.get(i).getBuyerName() + "\n" + "Destination: " + orders.get(i).getDestination());
        }
    }

    public String formatPhoneNumber(String number) {
        String newNum = "";
        char charNum[] = number.toCharArray();
        newNum += "(";
        for (int i = 0; i < charNum.length; i++) {
            if (i == 2) {
                newNum += charNum[i];
                newNum += ")-";
            } else if (i == 5) {
                newNum += charNum[i];
                newNum += "-";
            } else {
                newNum += charNum[i];
            }
        }
        return newNum;
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
                pleaseEnterText.setText("Please enter order information");
                appointmentText.setText("If your order requires an appointment please call 831-455-4305 to schedule an appointment");
                loggedInAsText.setText("You are logged in as: ");
                nextBtn.setText("Next");
                logoutBtn.setText("Logout");
                break;
            case 1:
                // Spanish
                orderNumber.setHint("El número de pedido");
                buyerName.setHint("Nombre del comprador");
                destination.setHint("Destino");
                logoutBtn.setText("Logout");
                pleaseEnterText.setText("Por favor, introduzca la información del pedido");
                appointmentText.setText("Si su pedido requiere una cita, llame al 831-455-4305 para programar una cita");
                loggedInAsText.setText("Has iniciado sesión como: ");
                nextBtn.setText("Próximo");
                logoutBtn.setText("Cerrar sesión");
                break;

            case 2:
                // French
                orderNumber.setHint("Numéro de commande");
                buyerName.setHint("Nom de l'acheteur");
                destination.setHint("Destination");
                logoutBtn.setText("Logout");
                pleaseEnterText.setText("Veuillez saisir les informations de commande");
                appointmentText.setText("Si votre commande nécessite un rendez-vous, veuillez appeler le 831-455-4305 pour fixer un rendez-vous");
                loggedInAsText.setText("Vous êtes connecté en tant que: ");
                nextBtn.setText("Prochain");
                logoutBtn.setText("Se déconnecter");
                break;
        }
    }

    private void setup(){
        orderNumber = findViewById(R.id.OrderNumberBox);
        buyerName = findViewById(R.id.BuyerNameBox);
        destination = findViewById(R.id.DestinationBox);
        logoutBtn = findViewById(R.id.LogoutBtn);
        emailStr = findViewById(R.id.EmailStr);
        phoneNumberStr = findViewById(R.id.PhoneNumberStr);
        truckNumberStr = findViewById(R.id.TruckNumberStr);
        pleaseEnterText = findViewById(R.id.EnterInfoText);
        appointmentText = findViewById(R.id.AppointmentText);
        loggedInAsText = findViewById(R.id.LoggedInAsText);
        nextBtn = findViewById(R.id.NextBtn);
        showSoftKeyboard(orderNumber);
        currentAccount = MainActivity.getCurrentAccount();
        emailStr.setText(currentAccount.getEmail());
        phoneNumberStr.setText(formatPhoneNumber(currentAccount.getPhoneNumber()));
        truckNumberStr.setText(currentAccount.getTruckName() + " " + currentAccount.getTruckNumber());
    }
}
