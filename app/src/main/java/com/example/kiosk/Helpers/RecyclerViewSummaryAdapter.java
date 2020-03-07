package com.example.kiosk.Helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.Order;
import com.example.kiosk.R;

import java.util.List;

public class RecyclerViewSummaryAdapter extends RecyclerView.Adapter<RecyclerViewSummaryAdapter.MyViewHolder> {

    private List<Order> orders;

    public RecyclerViewSummaryAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orders.get(position);
        String buyerNameEdit, buyerStr = order.getBuyerName();
        char[] buyerChar = buyerStr.toCharArray();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < buyerStr.length(); i++) {
            str.append(buyerChar[i]);
            if (buyerChar[i] == '/') {
                str.append("\n");
            }
        }
        buyerNameEdit = str.toString();
        holder.orderNumber.setText(order.getOrderNumber());
        holder.buyerName.setText(buyerNameEdit);
        holder.destination.setText(order.getDestination());
        holder.aptTime.setText("5:30PM");
        holder.estPallets.setText("30");
        holder.estWeight.setText("15000 lbs");
    }

    @Override
    public int getItemCount() {
        return orders.size();
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
