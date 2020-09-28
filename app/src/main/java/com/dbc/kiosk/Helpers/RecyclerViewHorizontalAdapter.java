package com.dbc.kiosk.Helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.OrderEntry;
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
    private static int lastPosition = -1;

    public RecyclerViewHorizontalAdapter(Context context, List<Order> orders, Activity activity) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.orders = orders;
        this.activity = activity;
    }

    public static void decrementLastPosition() {
        lastPosition--;
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
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        System.out.println("Position: " + position);
        System.out.println("Last position: " + lastPosition);

        if (position == 0 && lastPosition == 0 && getItemCount() == 1) {
            System.out.println("We're in the position == 0 && lastPosition == 0");
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.recycler_add);
            viewToAnimate.startAnimation(animation);
            lastPosition = 0;
        } else if (position > lastPosition) {
            System.out.println("We're in the position > lastPosition");
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.recycler_add);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
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

            if (Language.getCurrentLanguage() == 1) {
                deleteBtn.setText(R.string.delete_eng);
            } else if (Language.getCurrentLanguage() == 2) {
                deleteBtn.setText(R.string.delete_sp);
            } else if (Language.getCurrentLanguage() == 3) {
                deleteBtn.setText(R.string.delete_fr);
            }

            deleteBtn.setOnClickListener(v -> {
                OrderEntry.confirmMsg(itemView, context);
            });
        }
    }
}
