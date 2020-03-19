package com.example.kiosk.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.Helpers.RecyclerViewAssociatedAdapter;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;

import java.util.ArrayList;
import java.util.List;

public class ConnectedOrders extends Dialog implements android.view.View.OnClickListener {

    private Context context;

    public static MutableLiveData<Boolean> buttonListener;

    public ConnectedOrders(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.connected_orders);

        Button addBtn = findViewById(R.id.btn_add);
        Button cancelBtn = findViewById(R.id.btn_cancel);

        addBtn.setEnabled(false);

        setContentView(R.layout.connected_orders);
        MasterOrder test = new MasterOrder("F0WADA","OWAW99W0","01","San Jose, California","Driscolls","Fulfilled",
                "Loblaw Companies Inc.","true","true","03/13/2020","16:00:00", "3600.000","1.67000");
        MasterOrder test2 = new MasterOrder("LOPS0L","XCW245R","01","Santa Cruz, California","Charlies","Fulfilled",
                "Johnson Farms Inc.","true","true","03/13/2020","16:00:00", "3600.000","1.67000");
        MasterOrder test3 = new MasterOrder("30I34I","AWD0IOS","01","Yuma, Arizona","Johnnies","Fulfilled",
                "Gabriel Companies Inc.","true","true","03/13/2020","16:00:00", "3600.000","1.67000");
        MasterOrder test4 = new MasterOrder("LOPS0L","XCW245R","01","Santa Cruz, California","Charlies","Fulfilled",
                "John Culligan Farms Inc.","true","true","03/13/2020","16:00:00", "3600.000","1.67000");

        ArrayList<MasterOrder> connectedOrders = new ArrayList<>();
        connectedOrders.add(test);
        connectedOrders.add(test2);
        connectedOrders.add(test3);
        connectedOrders.add(test4);

        RecyclerView recyclerView2 = findViewById(R.id.AssociatedOrdersView);
        LinearLayoutManager verticalLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(verticalLayoutManager2);
        RecyclerViewAssociatedAdapter adapter2 = new RecyclerViewAssociatedAdapter(connectedOrders);
        recyclerView2.setAdapter(adapter2);

        addBtn.setOnClickListener(v1 -> {
            List<MasterOrder> selectedOrders = RecyclerViewAssociatedAdapter.getSelectedOrders();
            for (int i = 0; i < selectedOrders.size(); i++) {
                MasterOrder.addMasterOrderToList(selectedOrders.get(i));
            }
            setContentView(R.layout.activity_order_entry);
        });

        cancelBtn.setOnClickListener(v12 -> setContentView(R.layout.activity_order_entry));
    }

    @Override
    public void onClick(View v) {

    }
}
