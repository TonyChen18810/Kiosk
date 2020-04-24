package com.dbc.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.Helpers.RecyclerViewAssociatedAdapter;
import com.dbc.kiosk.Helpers.RecyclerViewHorizontalAdapter;
import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.OrderEntry;
import com.dbc.kiosk.Webservices.GetOrderDetails;
import java.util.ArrayList;
import java.util.List;
/**
 * ConnectedOrders.java
 *
 * @params Context context, RecyclerView recyclerView, RecyclerViewHorizontalAdapter adapter
 *
 * Called/shown if GetOrderDetailsByMasterNumber.java returns any orders that aren't already
 * checked-in. The returned orders are displayed in a RecyclerView managed by RecyclerViewAssociatedAdapter.java
 */
public class ConnectedOrders extends Dialog implements android.view.View.OnClickListener {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerViewHorizontalAdapter adapter;
    private List<Order> connectedOrders;

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
            associatedOrdersText.setText("The following order(s) are also connected with order #" + Order.getOrdersList().get(Order.getOrdersList().size()-1).getSOPNumber() + ". You can tap any order to add it to your orders.");
        } else if (Language.getCurrentLanguage() == 1) {
            associatedOrdersText.setText("Los siguientes pedidos también están relacionados con el pedido #" + Order.getOrdersList().get(Order.getOrdersList().size()-1).getSOPNumber() + ". Puede hacer clic en cualquier pedido para agregarlo a su lista de pedidos.");
            addBtn.setText("Agregar");
            cancelBtn.setText("Cancelar");
        } else if (Language.getCurrentLanguage() == 2) {
            associatedOrdersText.setText("Les commandes suivantes sont également liées à la commande #" + Order.getOrdersList().get(Order.getOrdersList().size()-1).getSOPNumber() + ". Vous pouvez appuyer sur n’importe quelle commande pour l’ajouter à vos commandes.");
            addBtn.setText("Ajouter");
            cancelBtn.setText("Annuler");
        }

        connectedOrders = new ArrayList<>(Order.getAssociatedOrdersList());
        System.out.println("Here's the associated orders:");
        for (int i = 0; i < connectedOrders.size(); i++) {
            System.out.println(connectedOrders.get(i).getSOPNumber());
        }

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
                List<Order> selectedOrders = new ArrayList<>(RecyclerViewAssociatedAdapter.getSelectedOrders());
                for (int i = 0; i < selectedOrders.size(); i++) {
                    Order.addOrderToList(selectedOrders.get(i));
                    if (Order.getOutlierOrders().contains(selectedOrders.get(i))) {
                        Order.removeOrderFromOutlierList(selectedOrders.get(i));
                    }
                }
                for (int i = 0; i < connectedOrders.size(); i++) {
                    if (!selectedOrders.contains(connectedOrders.get(i))) {
                        if (connectedOrders.get(i).getCheckedIn().equals("false") && !Order.getOutlierOrders().contains(connectedOrders.get(i)) && !Order.getOrdersList().contains(connectedOrders.get(i))) {
                            Order.addOrderToOutlierList(connectedOrders.get(i));
                        }
                    }
                }
                boolean showEarlyDialog = false;
                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                recyclerView.scheduleLayoutAnimation();
                for (int i = 0; i < selectedOrders.size(); i++) {
                    if ((selectedOrders.get(i).getAppointment().equals("true") && GetOrderDetails.checkApppointmentTime(selectedOrders.get(i).getAppointmentTime()) == -1)
                            || GetOrderDetails.checkApppointmentTime(Order.getOrdersList().get((Order.getOrdersList().size()-1) - selectedOrders.size()).getAppointmentTime()) == -1) {
                        showEarlyDialog = true;
                        System.out.println("SHOW EARLY DIALOG: " + showEarlyDialog);
                        break;
                    }
                }
                if (showEarlyDialog) {
                    OrderEntry.appointmentTimeListener.setValue(-2);
                    OrderEntry.appointmentTimeListener.setValue(-100); // reset value for next check if there is one
                }
                Order.clearAssociatedOrderList();
                dismiss();
                break;
            case R.id.cancelBtn:
                selectedOrders = new ArrayList<>(RecyclerViewAssociatedAdapter.getSelectedOrders());
                for (int i = 0; i < connectedOrders.size(); i++) {
                    if (!selectedOrders.contains(connectedOrders.get(i))) {
                        if (connectedOrders.get(i).getCheckedIn().equals("false") && !Order.getOutlierOrders().contains(connectedOrders.get(i)) && !Order.getOrdersList().contains(connectedOrders.get(i))) {
                            Order.addOrderToOutlierList(connectedOrders.get(i));
                        }
                    }
                }
                if (GetOrderDetails.checkApppointmentTime(Order.getOrdersList().get(Order.getOrdersList().size()-1).getAppointmentTime()) == -1) {
                    OrderEntry.appointmentTimeListener.setValue(-2);
                    OrderEntry.appointmentTimeListener.setValue(-100);
                }
                // setContentView(R.layout.activity_order_entry);
                Order.clearAssociatedOrderList();
                dismiss();
                break;
            default:
                break;
        }
    }
}
