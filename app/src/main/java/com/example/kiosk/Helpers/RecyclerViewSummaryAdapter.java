package com.example.kiosk.Helpers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kiosk.Order;
import com.example.kiosk.R;
import java.text.DecimalFormat;
import java.util.List;

/**
 * RecyclerViewSummaryAdapter.java
 *
 * Adapter used for managing the RecyclerView in OrderSummary.java.
 *
 * Each view displays the Order Number, Customer Name, Appointment Time,
 * Destination, Estimated Pallet Count and Estimated Weight.
 */
public class RecyclerViewSummaryAdapter extends RecyclerView.Adapter<RecyclerViewSummaryAdapter.MyViewHolder> {

    private List<Order> orders;
    private final static int minHeight = 190;

    public RecyclerViewSummaryAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderNumber.setText(order.getSOPNumber());

        String buyerNameEdit, buyerStr = order.getCustomerName();
        String[] words = buyerStr.split(" ");
        if (buyerStr.length() > 13) {
            StringBuilder buyerNameStrBuilder = new StringBuilder();
            char[] buyerNameCharArray = buyerStr.toCharArray();
            for (int i = 0; i < buyerNameCharArray.length; i++) {
                if (buyerNameCharArray[i] != '&') {
                    buyerNameStrBuilder.append(buyerNameCharArray[i]);
                }
                if ((i + 1) != buyerNameCharArray.length && words.length < 4) {
                    if (buyerNameCharArray[i] == ' ' || buyerNameCharArray[i+1] == '-') {
                        buyerNameStrBuilder.append('\n');
                    }
                } else if (words.length > 3) {
                    if (buyerNameCharArray[i] == ' ') {
                        buyerNameStrBuilder.append('\n');
                    }
                }
            }
            buyerNameEdit = buyerNameStrBuilder.toString();
        } else {
            buyerNameEdit = buyerStr;
        }
        String customerName = buyerNameEdit.replaceAll("\\s+","\n");
        holder.buyerName.setText(order.getCustomerName());

        if (order.getDestination().length() > 11) {
            char[] destArray = order.getDestination().toCharArray();
            StringBuilder destStrBuilder = new StringBuilder();
            for (int i = 0; i < destArray.length; i++) {
                destStrBuilder.append(destArray[i]);
                if (destArray[i]== ',') {
                    destStrBuilder.append('\n');
                }
            }
            holder.destination.setText(destStrBuilder.toString());
        } else {
            holder.destination.setText(order.getDestination());
        }


        // holder.destination.setText(order.getDestination());
        if (order.getAppointmentTime().equals("00:00:00")) {
            holder.aptTime.setText("N/A");
        } else {
            holder.aptTime.setText(order.getAppointmentTime());
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.estPallets.setText(Double.toString(Rounder.round(order.getEstimatedPallets(), 1)));
        holder.estWeight.setText(formatter.format(order.getEstimatedWeight()) + " lbs");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber, buyerName, destination, aptTime, estPallets, estWeight;
        CardView card;
        LinearLayout linearLayout;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.linearLayout = itemView.findViewById(R.id.LinearLayoutAboveCardView);
            this.card = itemView.findViewById(R.id.HeartOfTheCards);
            this.orderNumber = itemView.findViewById(R.id.OrderNumber);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            if (this.buyerName.getLineCount() == 1) {
                this.card.getLayoutParams().height = 130;
            } else if (this.buyerName.getLineCount() == 2) {
                this.card.getLayoutParams().height = 150;
            }else if (this.buyerName.getLineCount() == 3) {
                this.card.getLayoutParams().height = 170;
            } else if (this.buyerName.getLineCount() == 4) {
                this.card.getLayoutParams().height = 200;
            } else if (this.buyerName.getLineCount() == 5) {
                this.card.getLayoutParams().height = 240;
            }
            this.destination = itemView.findViewById(R.id.Destination);
            this.aptTime = itemView.findViewById(R.id.AptTime);
            this.estPallets = itemView.findViewById(R.id.EstPallets);
            this.estWeight = itemView.findViewById(R.id.EstWeight);
        }
    }
}
