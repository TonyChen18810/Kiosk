package com.dbc.kiosk.Helpers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.dbc.kiosk.Dialogs.HelpDialog;
import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.dbc.kiosk.Webservices.GetOrderDetails.checkApppointmentTime;
/**
 * RecyclerViewAssociatedAdapter.java
 *
 * Adapter used for managing the RecyclerView in ConnectedOrders.java,
 * the pop-up that is called if GetOrderDetailsByMasterNumber.java returns
 * any orders connected to the entered order number in OrderEntry.java
 *
 * holder.itemView.setOnClickListener() checks order status (early, late, on-time, etc.)
 * and will change color depending on the status. If the order is on-time or early,
 * it will be highlighted in green. Any error and the order turns gray,
 * displaying an error message and cannot be selected again.
 *
 * Any valid order that has been selected can be unselected (toggleable)
 *
 * Any order that is already checked-in will not be shown on this list.
 */
public class RecyclerViewAssociatedAdapter extends RecyclerView.Adapter<RecyclerViewAssociatedAdapter.MyViewHolder> {

    private List<Order> associatedOrders;
    private static Set<Order> selectedOrders;
    private static Set<Order> errorOrders;

    private Button addBtn;

    public RecyclerViewAssociatedAdapter(List<Order> associatedOrders, Button addBtn) {
        this.associatedOrders = associatedOrders;
        selectedOrders = new HashSet<>();
        errorOrders = new HashSet<>();
        this.addBtn = addBtn;
        addBtn.setEnabled(false);
    }

    public static Set<Order> getSelectedOrders() {
        return selectedOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_associated, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order currentOrder = associatedOrders.get(position);
        holder.orderNumber.setText(currentOrder.getSOPNumber());
        holder.buyerName.setText(currentOrder.getCustomerName());

        if (!errorOrders.contains(currentOrder)) {
            if (selectedOrders.contains(currentOrder)) {
                holder.errorMsg.setVisibility(View.GONE);
                highlightView(holder);
            } else {
                holder.errorMsg.setVisibility(View.GONE);
                unhighlightView(holder);
            }
        } else {
            if (currentOrder.getAppointment().equals("true") && currentOrder.getAppointmentTime().equals("00:00:00")) {
                scheduleAptText(holder);
                errorView(holder);
                errorOrders.add(currentOrder);
            } else if (currentOrder.getAppointment().equals("true")) {
                if (checkApppointmentTime(currentOrder.getAppointmentTime()) == 1) {
                    lateText(holder);
                    errorView(holder);
                    errorOrders.add(currentOrder);
                } else if (Order.getOrdersList().get(Order.getOrdersList().size() - 1).getAppointment().equals("true") && !currentOrder.getAppointmentTime().equals(Order.getCurrentAppointmentTime())) {
                    differentAptTimeText(holder);
                    errorView(holder);
                    errorOrders.add(currentOrder);
                }
            }
            holder.errorMsg.setVisibility(View.VISIBLE);
            errorView(holder);
        }

        // format destination
        if (currentOrder.getDestination().length() > 11) {
            char[] destArray = currentOrder.getDestination().toCharArray();
            StringBuilder destStrBuilder = new StringBuilder();
            for (char c : destArray) {
                destStrBuilder.append(c);
                if (c == ',') {
                    destStrBuilder.append('\n');
                }
            }
            holder.destination.setText(destStrBuilder.toString());
        } else {
            holder.destination.setText(currentOrder.getDestination());
        }

        // click
        holder.itemView.setOnClickListener(view -> {
            System.out.println("Clicked order: " + currentOrder.getSOPNumber());
            if (!errorOrders.contains(currentOrder)) {
                if (currentOrder.getAppointment().equals("true") && currentOrder.getAppointmentTime().equals("00:00:00")) {
                    scheduleApt(holder);
                    errorView(holder);
                    errorOrders.add(currentOrder);
                } else if (currentOrder.getAppointment().equals("true")) {
                    if (checkApppointmentTime(currentOrder.getAppointmentTime()) == 1) {
                        late(holder);
                        errorView(holder);
                        errorOrders.add(currentOrder);
                    } else if (checkApppointmentTime(currentOrder.getAppointmentTime()) == 0) {
                        // on time
                    }
                }
            } else {
                errorOrders.add(currentOrder);
            }

            if (!errorOrders.contains(currentOrder)) {
                if (selectedOrders.contains(currentOrder)) {
                    selectedOrders.remove(currentOrder);
                    unhighlightView(holder);
                    if (selectedOrders.size() == 0) {
                        addBtn.setEnabled(false);
                    } else {
                        addBtn.setEnabled(true);
                    }
                    notifyDataSetChanged();
                } else if (!selectedOrders.contains(currentOrder)) {
                    selectedOrders.add(currentOrder);
                    highlightView(holder);
                    addBtn.setEnabled(true);
                    notifyDataSetChanged();
                }
            } else {
                errorView(holder);
                errorOrders.add(currentOrder);
            }
        });
    }

    private void highlightView(MyViewHolder holder) {
        holder.errorMsg.setVisibility(View.GONE);
        holder.linearLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.andy_accent));
    }

    private void unhighlightView(MyViewHolder holder) {
        holder.errorMsg.setVisibility(View.GONE);
        holder.linearLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));
    }

    private void errorView(MyViewHolder holder) {
        holder.linearLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_gray));
        holder.errorMsg.setVisibility(View.VISIBLE);
    }

    private void scheduleAptText(MyViewHolder holder) {
        if (Language.getCurrentLanguage() == 0) {
            holder.errorMsg.setText("Appointment has not been scheduled. Please call 831-455-4305 to schedule appointment.");
        } else if (Language.getCurrentLanguage() == 1) {
            holder.errorMsg.setText("La cita no ha sido programada. Llame al 831-455-4305 para programar una cita.");
        } else if (Language.getCurrentLanguage() == 2) {
            holder.errorMsg.setText("La commande nécessite un rendez-vous mais n'a pas eu de rendez-vous, veuillez appeler le 831-455-4305 pour planifier un rendez-vous.");
        }
    }

    private void scheduleApt(MyViewHolder holder) {
        String message = null;
        if (Language.getCurrentLanguage() == 0) {
            message = "Order #" + holder.orderNumber.getText().toString() + " requires an appointment but hasn't had one scheduled, please call 831-455-4305 to schedule an appointment.";
            holder.errorMsg.setText("Appointment has not been scheduled. Please call 831-455-4305 to schedule appointment.");
        } else if (Language.getCurrentLanguage() == 1) {
            message = "El pedido #" + holder.orderNumber.getText().toString() + " requiere una cita pero no ha programado una, llame al 831-455-4305 para programar una cita.";
            holder.errorMsg.setText("La cita no ha sido programada. Llame al 831-455-4305 para programar una cita.");
        } else if (Language.getCurrentLanguage() == 2) {
            message = "La commande #" + holder.orderNumber.getText().toString() + " nécessite un rendez-vous, mais aucun n’a été pris. Veuillez appeler le 831-455-4305 pour prendre rendez-vous.";
            holder.errorMsg.setText("Le rendez-vous n'a pas été prévu. Veuillez appeler le 831-455-4305 pour fixer un rendez-vous.");
        }
        HelpDialog dialog = new HelpDialog(message, holder.itemView.getContext());
        dialog.show();
        dialog.setCancelable(false);
    }

    private void lateText(MyViewHolder holder) {
        if (Language.getCurrentLanguage() == 0) {
            holder.errorMsg.setText("Appointment time has been missed. Please call 831-455-4305 to re-schedule an appointment.");
        } else if (Language.getCurrentLanguage() == 1) {
            holder.errorMsg.setText("Nadie fue a la cita. Llame al 831-455-4305 para reprogramar una cita.");
        } else if (Language.getCurrentLanguage() == 2) {
            holder.errorMsg.setText("L'heure du rendez-vous a été manquée. Veuillez appeler le 831-455-4305 pour reprogrammer un rendez-vous.");
        }
    }

    private void late(MyViewHolder holder) {
        String helpText = "";
        if (Language.getCurrentLanguage() == 0) {
            helpText = "Appointment time has been missed. Please call 831-455-4305 to re-schedule an appointment.";
            holder.errorMsg.setText("Appointment time has been missed. Please call 831-455-4305 to re-schedule an appointment.");
        } else if (Language.getCurrentLanguage() == 1) {
            helpText = "Se ha perdido el tiempo de la cita. Llame al 831-455-4305 para reprogramar una cita.";
            holder.errorMsg.setText("Nadie fue a la cita. Llame al 831-455-4305 para reprogramar una cita.");
        } else if (Language.getCurrentLanguage() == 2) {
            helpText = "L'heure du rendez-vous a été manquée. Veuillez appeler le 831-455-4305 pour reprogrammer un rendez-vous.";
            holder.errorMsg.setText("L'heure du rendez-vous a été manquée. Veuillez appeler le 831-455-4305 pour reprogrammer un rendez-vous.");
        }
        HelpDialog dialog = new HelpDialog(helpText, holder.itemView.getContext());
        dialog.show();
        dialog.setCancelable(false);
    }

    private void differentAptTimeText(MyViewHolder holder) {
        if (Language.getCurrentLanguage() == 0) {
            holder.errorMsg.setText("The appointment time of this order differs from the appointment time of the entered order making it ineligible for pick-up.");
        } else if (Language.getCurrentLanguage() == 1) {
            holder.errorMsg.setText("La hora de la cita de este pedido difiere de la hora de la cita del pedido ingresado, por lo que no es elegible para ser recogido.");
        } else if (Language.getCurrentLanguage() == 2) {
            holder.errorMsg.setText("L'heure de rendez-vous de cette commande diffère de l'heure de rendez-vous de la commande saisie, ce qui la rend inéligible au retrait.");
        }
    }

    private void differentAptTime(MyViewHolder holder) {
        String helpText = "";
        if (Language.getCurrentLanguage() == 0) {
            helpText = "The appointment time of this order differs from the appointment time of the entered order making it ineligible for pick-up.";
            holder.errorMsg.setText("The appointment time of this order differs from the appointment time of the entered order making it ineligible for pick-up.");
        } else if (Language.getCurrentLanguage() == 1) {
            helpText = "La hora de la cita de este pedido difiere de la hora de la cita del pedido ingresado, por lo que no es elegible para ser recogido.";
            holder.errorMsg.setText("La hora de la cita de este pedido difiere de la hora de la cita del pedido ingresado, por lo que no es elegible para ser recogido.");
        } else if (Language.getCurrentLanguage() == 2) {
            helpText = "L'heure de rendez-vous de cette commande diffère de l'heure de rendez-vous de la commande saisie, ce qui la rend inéligible au retrait.";
            holder.errorMsg.setText("L'heure de rendez-vous de cette commande diffère de l'heure de rendez-vous de la commande saisie, ce qui la rend inéligible au retrait.");
        }
        HelpDialog dialog = new HelpDialog(helpText, holder.itemView.getContext());
        dialog.show();
        dialog.setCancelable(false);
    }

    @Override
    public int getItemCount() {
        return associatedOrders.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber, buyerName, destination, errorMsg;
        Boolean isSelected;
        LinearLayout linearLayout;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            itemView.setLongClickable(true);

            this.orderNumber = itemView.findViewById(R.id.OrderNumber);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            this.destination = itemView.findViewById(R.id.Destination);
            this.errorMsg = itemView.findViewById(R.id.ErrorMessage);
            this.isSelected = false;
            this.linearLayout = itemView.findViewById(R.id.LinearLayoutInCardView);
        }
    }
}
