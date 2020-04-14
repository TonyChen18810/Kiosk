package com.example.kiosk.Helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.Order;
import com.example.kiosk.Screens.OrderEntry;
import com.example.kiosk.R;
import java.util.List;
/**
 * RecyclerViewHorizontalAdapter.java
 *
 * Adapter used for managing the RecyclerView in OrderEntry.java.
 *
 * Each view displays the Order Number, Customer Name, and Destination
 * with a Delete button at the bottom
 *
 * The delete button removes that order from the list. If the user wants to
 * add it back to the list, the order number must be typed in again.
 *
 */
public class RecyclerViewHorizontalAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalAdapter.MyViewHolder> {

    private List<Order> orders;
    private LayoutInflater mInflater;
    private Context context;
    private Activity activity;

    public RecyclerViewHorizontalAdapter(Context context, List<Order> orders, Activity activity) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.orders = orders;
        this.activity = activity;
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
        holder.orderNumber.setText(order.getSOPNumber());
        holder.buyerName.setText(order.getCustomerName());
        holder.destination.setText(order.getDestination());
        if (order.getAppointmentTime().equals("00:00:00")) {
            holder.appointment.setText("N/A");
        } else {
            holder.appointment.setText(order.getAppointmentTime());
        }
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

            deleteBtn.setOnClickListener(v -> {
                OrderEntry.confirmMsg(itemView, context);
            });
        }
    }
}
