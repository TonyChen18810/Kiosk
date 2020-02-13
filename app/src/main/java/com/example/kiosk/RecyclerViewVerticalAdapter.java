package com.example.kiosk;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecyclerViewVerticalAdapter extends RecyclerView.Adapter<RecyclerViewVerticalAdapter.MyViewHolder> {

    private ArrayList<Order> orders;

    public RecyclerViewVerticalAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_view_long, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView orderNumber = holder.orderNumber;
        TextView buyerName = holder.buyerName;
        TextView destination = holder.destination;

        orderNumber.setText(orders.get(position).getOrderNumber());
        buyerName.setText(orders.get(position).getBuyerName());
        destination.setText(orders.get(position).getDestination());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber;
        TextView buyerName;
        TextView destination;
        Button deleteBtn;
        Button editBtn;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.orderNumber = itemView.findViewById(R.id.OrderNum);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            this.destination = itemView.findViewById(R.id.Destination);
            this.deleteBtn = itemView.findViewById(R.id.deleteBtn);
            this.editBtn = itemView.findViewById(R.id.editBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderSubmitted.confirmMsg(itemView);
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderSubmitted.editOrder(itemView);
                }
            });
        }
    }
}