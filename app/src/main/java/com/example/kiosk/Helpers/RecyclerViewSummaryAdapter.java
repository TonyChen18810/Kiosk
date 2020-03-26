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

import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerViewSummaryAdapter extends RecyclerView.Adapter<RecyclerViewSummaryAdapter.MyViewHolder> {

    private List<MasterOrder> masterOrders;
    private final static int minHeight = 190;

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
        MasterOrder masterOrder = masterOrders.get(position);
        holder.orderNumber.setText(masterOrder.getSOPNumber());

        String buyerNameEdit, buyerStr = masterOrder.getCustomerName();
        String[] words = buyerStr.split(" ");
        if (buyerStr.length() > 13) {
            StringBuilder buyerNameStrBuilder = new StringBuilder();
            char[] buyerNameCharArray = buyerStr.toCharArray();
            for (int i = 0; i < buyerNameCharArray.length; i++) {
                buyerNameStrBuilder.append(buyerNameCharArray[i]);
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
        holder.buyerName.setText(buyerNameEdit);

        if (masterOrder.getDestination().length() > 11) {
            char[] destArray = masterOrder.getDestination().toCharArray();
            StringBuilder destStrBuilder = new StringBuilder();
            for (int i = 0; i < destArray.length; i++) {
                destStrBuilder.append(destArray[i]);
                if (destArray[i]== ',') {
                    destStrBuilder.append('\n');
                }
            }
            holder.destination.setText(destStrBuilder.toString());
        } else {
            holder.destination.setText(masterOrder.getDestination());
        }


        // holder.destination.setText(masterOrder.getDestination());
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
        CardView card;
        LinearLayout linearLayout;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.linearLayout = itemView.findViewById(R.id.LinearLayoutAboveCardView);
            this.card = itemView.findViewById(R.id.HeartOfTheCards);
            this.orderNumber = itemView.findViewById(R.id.OrderNumber);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            System.out.println("Buyer name line count: " + this.buyerName.getLineCount());
            if (this.buyerName.getLineCount() == 3) {
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
