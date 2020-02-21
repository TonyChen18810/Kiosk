package com.example.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewHorizontalAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalAdapter.MyViewHolder> {

    private List<Order> orders;
    private LayoutInflater mInflater;

    RecyclerViewHorizontalAdapter(Context context, List<Order> orders) {
        this.mInflater = LayoutInflater.from(context);
        this.orders = orders;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.order_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.orderNumber.setText(order.getOrderNumber());
            holder.buyerName.setText(order.getBuyerName());
            holder.destination.setText(order.getDestination());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber;
        TextView buyerName;
        TextView destination;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.orderNumber = itemView.findViewById(R.id.OrderNum);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            this.destination = itemView.findViewById(R.id.Destination);
        }
    }
}
