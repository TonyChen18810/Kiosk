package com.example.kiosk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderSubmitted extends AppCompatActivity {

    // private static ArrayList<Order> orders = new ArrayList<>();
    private static ArrayList<String> removedOrders = new ArrayList<>();

    private static RecyclerViewVerticalAdapter adapter;
    private static RecyclerView recyclerView;

    private static Context context;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        context = this;

        // orders.addAll(OrderInfo.getOrders());

        recyclerView = findViewById(R.id.OrdersView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerViewVerticalAdapter(Order.getOrders());
        recyclerView.setAdapter(adapter);

        findViewById(R.id.SubmitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderSubmitted.this, OrderSummary.class));
            }
        });
    }

    public static void confirmMsg(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Delete order");
        builder.setMessage("Are you sure you want to delete this order?");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(v);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void editOrder(View v) {
        Intent intent = new Intent(context, OrderInfo.class);

        int selectedItemPosition = recyclerView.getChildLayoutPosition(v);
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
        TextView orderNumber = viewHolder.itemView.findViewById(R.id.OrderNum);
        String orderNumberStr = orderNumber.getText().toString();
        TextView buyerName = viewHolder.itemView.findViewById(R.id.BuyerName);
        String buyerNameStr = buyerName.getText().toString();
        TextView destination = viewHolder.itemView.findViewById(R.id.Destination);
        String destinationStr = destination.getText().toString();

        intent.putExtra("Order Number", orderNumberStr);
        intent.putExtra("Buyer Name", buyerNameStr);
        intent.putExtra("Destination", destinationStr);
        intent.putExtra("Position", selectedItemPosition);
        context.startActivity(intent);
    }

    private static void removeItem(View v) {
        int selectedItemPosition = recyclerView.getChildLayoutPosition(v);
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
        TextView orderNum = viewHolder.itemView.findViewById(R.id.OrderNum);
        String selectedName = orderNum.getText().toString();
        String selectedOrderNumber = "-1";

        for (int i = 0; i < Order.getSize(); i++) {
            if (selectedName.equals(Order.getOrders().get(i).getOrderNumber())) {
                selectedOrderNumber = Order.getOrders().get(i).getOrderNumber();
            }
        }
        removedOrders.add(selectedOrderNumber);
        Order.getOrders().remove(selectedItemPosition);
        adapter.notifyItemRemoved(selectedItemPosition);
    }
}
