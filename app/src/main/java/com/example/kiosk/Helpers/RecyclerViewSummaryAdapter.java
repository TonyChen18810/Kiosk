package com.example.kiosk.Helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;

import java.util.List;

public class RecyclerViewSummaryAdapter extends RecyclerView.Adapter<RecyclerViewSummaryAdapter.MyViewHolder> {

    // private List<Order> orders;
    private List<MasterOrder> masterOrders;

    public RecyclerViewSummaryAdapter(List<MasterOrder> masterOrders) {
        this.masterOrders = masterOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Order order = orders.get(position);
        MasterOrder masterOrder = masterOrders.get(position);
        String buyerNameEdit, buyerStr = masterOrder.getCustomerName();
        char[] buyerChar = buyerStr.toCharArray();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < buyerStr.length(); i++) {
            str.append(buyerChar[i]);
            if (buyerChar[i] == '/') {
                str.append("\n");
            }
        }
        buyerNameEdit = str.toString();
        holder.orderNumber.setText(masterOrder.getSOPNumber());
        holder.buyerName.setText(buyerNameEdit);
        holder.destination.setText(masterOrder.getDestination());
        holder.aptTime.setText(masterOrder.getAppointmentTime());
        holder.estPallets.setText(Double.toString(masterOrder.getEstimatedPallets()));
        holder.estWeight.setText(Double.toString(masterOrder.getEstimatedWeight()));
    }

    @Override
    public int getItemCount() {
        return masterOrders.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber, buyerName, destination, aptTime, estPallets, estWeight;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.orderNumber = itemView.findViewById(R.id.OrderNumber);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            this.destination = itemView.findViewById(R.id.Destination);
            this.aptTime = itemView.findViewById(R.id.AptTime);
            this.estPallets = itemView.findViewById(R.id.EstPallets);
            this.estWeight = itemView.findViewById(R.id.EstWeight);
        }
    }
}
