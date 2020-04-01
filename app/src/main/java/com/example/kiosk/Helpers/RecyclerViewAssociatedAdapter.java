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

import com.example.kiosk.Dialogs.HelpDialog;
import com.example.kiosk.Order;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderEntry;
import com.example.kiosk.Webservices.GetNextMasterOrderNumber;
import com.example.kiosk.Webservices.GetOrderDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.kiosk.Webservices.GetOrderDetails.checkApppointmentTime;

public class RecyclerViewAssociatedAdapter extends RecyclerView.Adapter<RecyclerViewAssociatedAdapter.MyViewHolder> {

    private List<Order> associatedOrders;
    private Button addBtn;

    private static List<Order> selectedOrders;

    public RecyclerViewAssociatedAdapter(List<Order> associatedOrders, Button addBtn) {
        this.associatedOrders = associatedOrders;
        selectedOrders = new ArrayList<>();
        this.addBtn = addBtn;
        this.addBtn.setEnabled(false);
    }

    public static List<Order> getSelectedOrders() {
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
        Order order = associatedOrders.get(position);
/*
        // if (order.getAppointment().equals("true") && !order.getAppointmentTime().equals("00:00:00") && GetOrderDetails.checkApppointmentTime(order.getAppointmentTime()) == 1) {
        if (GetOrderDetails.checkApppointmentTime(order.getAppointmentTime()) == 1) {
            // this.setBackgroundColor(Color.parseColor("#BE424242"));
            holder.isLate = true;
            holder.lateWarning.setVisibility(View.VISIBLE);
        } else {
            holder.isLate = false;
            holder.lateWarning.setVisibility(View.GONE);
        }
        */

        holder.orderNumber.setText(order.getSOPNumber());

        // format customer name
        String buyerNameEdit, buyerStr = order.getCustomerName();
        String[] words = buyerStr.split(" ");
        List<String> filteredWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (!words[i].equals("&") && !words[i].equals("and")) {
                filteredWords.add(words[i]);
            }
        }

        if (buyerStr.length() > 13) {
            StringBuilder buyerNameStrBuilder = new StringBuilder();
            char[] buyerNameCharArray = filteredWords.toString().toCharArray();
            for (int i = 0; i < buyerNameCharArray.length; i++) {
                if (buyerNameCharArray[i] != '&') {
                    buyerNameStrBuilder.append(buyerNameCharArray[i]);
                }
                if ((i + 1) != buyerNameCharArray.length  && filteredWords.size() < 4) {
                    if (buyerNameCharArray[i] == ' ' || buyerNameCharArray[i+1] == '-') {
                        buyerNameStrBuilder.append('\n');
                    }
                } else if (filteredWords.size() > 3) {
                    if (buyerNameCharArray[i] == ' ') {
                        buyerNameStrBuilder.append('\n');
                    }
                }
            }
            buyerNameEdit = buyerNameStrBuilder.toString();
        } else {
            buyerNameEdit = buyerStr;
        }

        holder.orderNumber.setText(order.getSOPNumber());
        holder.buyerName.setText(order.getCustomerName());

        // format destination
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
    }

    @Override
    public int getItemCount() {
        return associatedOrders.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumber, buyerName, destination;
        Boolean isSelected, error;
        LinearLayout layout;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.orderNumber = itemView.findViewById(R.id.OrderNumber);
            this.buyerName = itemView.findViewById(R.id.BuyerName);
            this.destination = itemView.findViewById(R.id.Destination);
            this.isSelected = false;
            this.error = false;
            this.layout = itemView.findViewById(R.id.LinearLayoutInCardView);

            itemView.setOnClickListener(v -> {

                boolean isGoodOrder = false;

                Order clickedOrder = associatedOrders.get(getAdapterPosition());
                if (!this.error) {
                    if (clickedOrder.getTruckStatus().equals("Outstanding")) {
                        if (clickedOrder.getCheckedIn().equals("false")) {
                            if (clickedOrder.getAppointment().equals("true") && clickedOrder.getAppointmentTime().equals("00:00:00")) {
                                System.out.println("Need to make appointment");
                                isGoodOrder = false;
                                String message = null;
                                this.layout.setBackgroundColor(Color.parseColor("#b3b3b3"));
                                this.error = true;
                                if (Language.getCurrentLanguage() == 0) {
                                    message = "Order #" + orderNumber.getText().toString() + " requires an appointment but has not had one scheduled, please call 831-455-4305 to schedule an appointment.";
                                } else if (Language.getCurrentLanguage() == 1) {
                                    message = "Pedido #" + orderNumber.getText().toString() + " requiere una cita pero no ha programado una, llame al 831-455-4305 para programar una cita.";
                                } else if (Language.getCurrentLanguage() == 2) {
                                    message = "Ordre #" + orderNumber.getText().toString() + " nécessite un rendez-vous mais n'a pas eu de rendez-vous, veuillez appeler le 831-455-4305 pour fixer un rendez-vous.";
                                }
                                HelpDialog dialog = new HelpDialog(message, itemView.getContext());
                                dialog.show();
                                // OrderEntry.validOrderNumber.setValue(2);
                            } else if (clickedOrder.getAppointment().equals("true")) {
                                System.out.println("Has an appointment, now check for late/early/on-time");
                                System.out.println("Clicked apt. time: " + clickedOrder.getSOPNumber() + " " + clickedOrder.getAppointmentTime());
                                System.out.println("Entered order apt. time: " + Order.getCurrentOrder().getSOPNumber() + " " + Order.getCurrentOrder().getAppointmentTime());
                                if (!clickedOrder.getAppointmentTime().equals(Order.getCurrentAppointmentTime())) {
                                    isGoodOrder = false;
                                    this.layout.setBackgroundColor(Color.parseColor("#b3b3b3"));
                                    this.error = true;
                                    String helpText = "";
                                    if (Language.getCurrentLanguage() == 0) {
                                        helpText = "The appointment time of this order differs from the appointment time of the entered order making it ineligible for pick-up.";
                                    } else if (Language.getCurrentLanguage() == 1) {
                                        helpText = "La hora de la cita de este pedido difiere de la hora de la cita del pedido ingresado, por lo que no es elegible para ser recogido.";
                                    } else if (Language.getCurrentLanguage() == 2) {
                                        helpText = "L'heure de rendez-vous de cette commande diffère de l'heure de rendez-vous de la commande saisie, ce qui la rend inéligible au retrait.";
                                    }
                                    HelpDialog dialog = new HelpDialog(helpText, itemView.getContext());
                                    dialog.show();
                                } else if (checkApppointmentTime(clickedOrder.getAppointmentTime()) == -1) {
                                    System.out.println("You're early");
                                    isGoodOrder = true;
                                    String helpText = "";
                                    if (Language.getCurrentLanguage() == 0) {
                                        helpText = "This order has a later appointment time. This submitted order will not be checked in until 1 hour prior to appointment time.";
                                    } else if (Language.getCurrentLanguage() == 1) {
                                        helpText = "Este pedido tiene una hora de cita posterior. Este pedido enviado no se registrará hasta 1 hora antes de la hora de la cita.";
                                    } else if (Language.getCurrentLanguage() == 2) {
                                        helpText = "Cette commande a une heure de rendez-vous ultérieure. Cette commande soumise ne sera enregistrée que 1 heure avant l'heure du rendez-vous.";
                                    }
                                    HelpDialog dialog = new HelpDialog(helpText, itemView.getContext());
                                    dialog.show();
                                    // OrderEntry.appointmentTimeListener.setValue(0);
                                    // HelpDialog dialog = new HelpDialog("This order is early", itemView.getContext());
                                    // dialog.show();
                                } else if (checkApppointmentTime(clickedOrder.getAppointmentTime()) == 1) {
                                    System.out.println("You're late");
                                    isGoodOrder = false;
                                    // OrderEntry.appointmentTimeListener.setValue(1);
                                    this.layout.setBackgroundColor(Color.parseColor("#b3b3b3"));
                                    this.error = true;
                                    String helpText = "";
                                    if (Language.getCurrentLanguage() == 0) {
                                        helpText = "Appointment time has been missed. Please call 831-455-4305 to re-schedule an appointment.";
                                    } else if (Language.getCurrentLanguage() == 1) {
                                        helpText = "Se ha perdido el tiempo de la cita. Llame al 831-455-4305 para reprogramar una cita.";
                                    } else if (Language.getCurrentLanguage() == 2) {
                                        helpText = "L'heure du rendez-vous a été manquée. Veuillez appeler le 831-455-4305 pour reprogrammer un rendez-vous.";
                                    }
                                    HelpDialog dialog = new HelpDialog(helpText, itemView.getContext());
                                    dialog.show();
                                } else if (checkApppointmentTime(clickedOrder.getAppointmentTime()) == 0) {
                                    System.out.println("On time");
                                    isGoodOrder = true;
                                }
                            } else {
                                System.out.println("No appointment - continue");
                                isGoodOrder = true;
                            }
                        } else {
                            System.out.println("hi");
                            this.layout.setBackgroundColor(Color.parseColor("#b3b3b3"));
                            this.error = true;
                            String helpText = "";
                            if (Language.getCurrentLanguage() == 0) {
                                helpText = "The order has already been checked in";
                            } else if (Language.getCurrentLanguage() == 1) {
                                helpText = "El pedido ya ha sido facturado";
                            } else if (Language.getCurrentLanguage() == 2) {
                                helpText = "La ordre a déjà été enregistrée";
                            }
                            HelpDialog dialog = new HelpDialog(helpText, itemView.getContext());
                            dialog.show();
                            isGoodOrder = false;
                            // OrderEntry.validOrderNumber.setValue(0);
                        }
                    } else {
                        System.out.println("hello");
                        String helpText = "";
                        if (Language.getCurrentLanguage() == 0) {
                            helpText = "The order has already been checked in";
                        } else if (Language.getCurrentLanguage() == 1) {
                            helpText = "El pedido ya ha sido facturado";
                        } else if (Language.getCurrentLanguage() == 2) {
                            helpText = "La ordre a déjà été enregistrée";
                        }
                        HelpDialog dialog = new HelpDialog(helpText, itemView.getContext());
                        dialog.show();
                        isGoodOrder = false;
                    }

                    if (isGoodOrder) {
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
                    } else {
                        // bad order, turn card grey? pop-up should be handled above (isGoodOrder)
                    }
                }
            });
        }
    }
}
