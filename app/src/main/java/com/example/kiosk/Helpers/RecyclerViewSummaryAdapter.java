package com.example.kiosk.Helpers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerViewSummaryAdapter extends RecyclerView.Adapter<RecyclerViewSummaryAdapter.MyViewHolder> {

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

    @SuppressLint("SetTextI18n")
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
        if (buyerNameEdit.length() > 10) {
            StringBuilder buyerNameStrBuilder = new StringBuilder();
            for (char c: buyerNameEdit.toCharArray()) {
                buyerNameStrBuilder.append(c);
                if (c == ' ') {
                    buyerNameStrBuilder.append('\n');
                }
            }
            buyerNameEdit = buyerNameStrBuilder.toString();
        }
        holder.buyerName.setText(buyerNameEdit);
        holder.destination.setText(masterOrder.getDestination());
        if (masterOrder.getAppointmentTime().equals("00:00:00")) {
            holder.aptTime.setText("N/A");
        } else {
            holder.aptTime.setText(masterOrder.getAppointmentTime());
        }

        DecimalFormat formatter = new DecimalFormat("#,###");

        if (masterOrder.getEstimatedPallets() < 1) {
            holder.estPallets.setText("1");
        } else {
            holder.estPallets.setText(Integer.toString((int) Math.round(masterOrder.getEstimatedPallets())));
        }
        holder.estWeight.setText(formatter.format(masterOrder.getEstimatedWeight()) + " lbs");
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
