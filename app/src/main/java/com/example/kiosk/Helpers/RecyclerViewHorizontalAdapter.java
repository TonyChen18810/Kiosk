package com.example.kiosk.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.MasterOrder;
import com.example.kiosk.Screens.OrderEntry;
import com.example.kiosk.R;
import java.util.List;

public class RecyclerViewHorizontalAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalAdapter.MyViewHolder> {

    private List<MasterOrder> masterOrders;
    private LayoutInflater mInflater;
    private Context context;

    public RecyclerViewHorizontalAdapter(Context context, List<MasterOrder> masterOrders) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.masterOrders = masterOrders;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.order_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MasterOrder masterOrder = masterOrders.get(position);
        holder.orderNumber.setText(masterOrder.getSOPNumber());
        holder.buyerName.setText(masterOrder.getCustomerName());
        holder.destination.setText(masterOrder.getDestination());
        if (masterOrder.getAppointmentTime().equals("00:00:00")) {
            holder.appointment.setText("N/A");
        } else {
            holder.appointment.setText(masterOrder.getAppointmentTime());
        }
    }

    @Override
    public int getItemCount() {
        return masterOrders.size();
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
