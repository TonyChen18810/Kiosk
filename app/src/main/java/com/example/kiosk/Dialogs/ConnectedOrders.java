package com.example.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.RecyclerViewAssociatedAdapter;
import com.example.kiosk.Helpers.RecyclerViewHorizontalAdapter;
import com.example.kiosk.Order;
import com.example.kiosk.R;
import com.example.kiosk.Screens.MainActivity;
import com.example.kiosk.Screens.OrderEntry;
import com.example.kiosk.Webservices.GetOrderDetails;
import java.util.ArrayList;
import java.util.List;

public class ConnectedOrders extends Dialog implements android.view.View.OnClickListener {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerViewHorizontalAdapter adapter;

    private ActionMode actionMode;
    private boolean isMultiSelect = false;
    //i created List of int type to store id of data, you can create custom class type data according to your need.
    private List<Integer> selectedIds = new ArrayList<>();

    public ConnectedOrders(@NonNull Context context, RecyclerView recyclerView, RecyclerViewHorizontalAdapter adapter) {
        super(context);
        this.context = context;
        this.recyclerView = recyclerView;
        this.adapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.connected_orders);

        Button addBtn = findViewById(R.id.addBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);
        TextView associatedOrdersText = findViewById(R.id.AssociatedOrdersText);

        if (Language.getCurrentLanguage() == 0) {
            addBtn.setText("Add Order(s)");
            cancelBtn.setText("Cancel");
            associatedOrdersText.setText(R.string.following_orders_eng);
        } else if (Language.getCurrentLanguage() == 1) {
            associatedOrdersText.setText(R.string.following_orders_sp);
            addBtn.setText("Agregar Pedido(s)");
            cancelBtn.setText("Cancelar");
        } else if (Language.getCurrentLanguage() == 2) {
            associatedOrdersText.setText(R.string.following_orders_fr);
            addBtn.setText("Ajouter Commande(s)");
            cancelBtn.setText("Annuler");
        }

        ArrayList<Order> connectedOrders = new ArrayList<>(Order.getAssociatedOrdersList());

        RecyclerView recyclerView2 = findViewById(R.id.AssociatedOrdersView);
        LinearLayoutManager verticalLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(verticalLayoutManager2);
        RecyclerViewAssociatedAdapter adapter2 = new RecyclerViewAssociatedAdapter(connectedOrders, addBtn);
        recyclerView2.setAdapter(adapter2);

        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                List<Order> selectedOrders = RecyclerViewAssociatedAdapter.getSelectedOrders();
                for (int i = 0; i < selectedOrders.size(); i++) {
                    Order.addMasterOrderToList(selectedOrders.get(i));
                }
                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                recyclerView.scheduleLayoutAnimation();
                for (int i = 0; i < Order.getAssociatedOrdersList().size(); i++) {
                    if (Order.getAssociatedOrdersList().get(i).getAppointment().equals("true") && GetOrderDetails.checkApppointmentTime(Order.getAssociatedOrdersList().get(i).getAppointmentTime()) == -1) {
                        OrderEntry.appointmentTimeListener.setValue(-2);
                    }
                }
                Order.clearAssociatedOrderList();
                dismiss();
                break;
            case R.id.cancelBtn:
                setContentView(R.layout.activity_order_entry);
                Order.clearAssociatedOrderList();
                dismiss();
                break;
            default:
                break;
        }
    }
}
