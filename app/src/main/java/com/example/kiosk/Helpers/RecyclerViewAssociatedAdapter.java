package com.example.kiosk.Helpers;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAssociatedAdapter extends RecyclerView.Adapter<RecyclerViewAssociatedAdapter.MyViewHolder> {

    private List<MasterOrder> associatedOrders;
    private Button addBtn;

    private static List<MasterOrder> selectedOrders;

    public RecyclerViewAssociatedAdapter(List<MasterOrder> associatedOrders, Button addBtn) {
        this.associatedOrders = associatedOrders;
        selectedOrders = new ArrayList<>();
        this.addBtn = addBtn;
        this.addBtn.setEnabled(false);
    }

    public static List<MasterOrder> getSelectedOrders() {
        return selectedOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_associated, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MasterOrder masterOrder = associatedOrders.get(position);

        // format customer name
        String buyerNameEdit, buyerStr = masterOrder.getCustomerName();
        holder.orderNumber.setText(masterOrder.getSOPNumber());
        if (buyerStr.length() > 13) {
            StringBuilder buyerNameStrBuilder = new StringBuilder();
            char[] buyerNameCharArray = buyerStr.toCharArray();
            for (int i = 0; i < buyerNameCharArray.length; i++) {
                buyerNameStrBuilder.append(buyerNameCharArray[i]);
                if ((i + 1) != buyerNameCharArray.length) {
                    if (buyerNameCharArray[i] == ' ' || buyerNameCharArray[i+1] == '-') {
                        buyerNameStrBuilder.append('\n');
                    }
                }
            }
            buyerNameEdit = buyerNameStrBuilder.toString();
        } else {
            buyerNameEdit = buyerStr;
        }

        holder.orderNumber.setText(masterOrder.getSOPNumber());
        holder.buyerName.setText(buyerNameEdit);

        // format destination
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
    }

    @Override
    public int getItemCount() {
        return associatedOrders.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber, buyerName, destination;
        Boolean isSelected;
        LinearLayout layout;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.orderNumber = itemView.findViewById(R.id.OrderNumber);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            this.destination = itemView.findViewById(R.id.Destination);
            this.isSelected = false;
            this.layout = itemView.findViewById(R.id.LinearLayoutInCardView);

            itemView.setOnClickListener(v -> {
                if (this.isSelected) {
                    this.isSelected = false;
                    this.layout.setBackgroundColor(Color.parseColor("#1E04B486"));
                    selectedOrders.remove(associatedOrders.get(getAdapterPosition()));
                    if (selectedOrders.size() == 0) {
                        addBtn.setEnabled(false);
                        // ConnectedOrders.buttonListener.setValue(false);
                    } else {
                        // ConnectedOrders.buttonListener.setValue(true);
                        addBtn.setEnabled(true);
                    }
                    System.out.println("SELECTION REMOVED: ");
                    for (int i = 0; i < selectedOrders.size(); i++) {
                        System.out.println(selectedOrders.get(i));
                    }
                    notifyDataSetChanged();
                } else {
                    this.isSelected = true;
                    this.layout.setBackgroundColor(Color.parseColor("#04B486"));
                    selectedOrders.add(associatedOrders.get(getAdapterPosition()));
                    addBtn.setEnabled(true);
                    // ConnectedOrders.buttonListener.setValue(true);
                    System.out.println("SELECTION ADDED: ");
                    for (int i = 0; i < selectedOrders.size(); i++) {
                        System.out.println(selectedOrders.get(i));
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }
}
