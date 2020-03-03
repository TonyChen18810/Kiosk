package com.example.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewHorizontalAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalAdapter.MyViewHolder> {

    private List<Order> orders;
    private LayoutInflater mInflater;
    private Context context;

    RecyclerViewHorizontalAdapter(Context context, List<Order> orders) {
        this.context = context;
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
        holder.appointment.setText(order.getAppointmentTime());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber;
        TextView buyerName;
        TextView destination;
        TextView appointment;
        Button deleteBtn;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.orderNumber = itemView.findViewById(R.id.OrderNum);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            this.destination = itemView.findViewById(R.id.Destination);
            this.appointment = itemView.findViewById(R.id.AppointmentTime);
            this.deleteBtn = itemView.findViewById(R.id.DeleteBtn);

            if (Language.getCurrentLanguage() == 0) {
                deleteBtn.setText(R.string.delete_eng);
            } else if (Language.getCurrentLanguage() == 1) {
                deleteBtn.setText(R.string.delete_sp);
            } else if (Language.getCurrentLanguage() == 2) {
                deleteBtn.setText(R.string.delete_fr);
            }

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderEntry.confirmMsg(itemView, context);
                }
            });
        }
    }
}
