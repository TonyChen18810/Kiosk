package com.example.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewSummaryAdapter extends RecyclerView.Adapter<RecyclerViewSummaryAdapter.MyViewHolder> {

    private List<Order> orders;
    private LayoutInflater mInflater;

    RecyclerViewSummaryAdapter(Context context, List<Order> orders) {
        this.mInflater = LayoutInflater.from(context);
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderNumber.setText(order.getOrderNumber());
        holder.buyerName.setText(order.getBuyerName());
        holder.destination.setText(order.getDestination());
        holder.aptTime.setText("---");
        holder.estPallets.setText("---");
        holder.estWeight.setText("---" + " lbs");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber, buyerName, destination, aptTime, estPallets, estWeight;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.orderNumber = itemView.findViewById(R.id.OrderNum);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            this.destination = itemView.findViewById(R.id.Destination);
            this.aptTime = itemView.findViewById(R.id.AptTime);
            this.estPallets = itemView.findViewById(R.id.EstPallets);
            this.estWeight = itemView.findViewById(R.id.EstWeight);
        }
    }
}
