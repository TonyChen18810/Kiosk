package com.example.kiosk.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kiosk.Helpers.Language;
import com.example.kiosk.R;
import com.example.kiosk.Screens.CreateAccount;
import com.example.kiosk.Screens.LoggedIn;
import com.example.kiosk.Screens.OrderEntry;
import java.util.List;

public class ListViewDialog extends Dialog {

    private Activity activity;
    private Button button;
    private int listCode;

    public ListViewDialog(Activity activity, Button button, int listCode) {
        super(activity);
        this.activity = activity;
        this.button = button;
        this.listCode = listCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_listview);

        TextView listViewTitle = findViewById(R.id.ListViewTitle);

        if (listCode == 0) { // OrderEntry customer destination selector
            listViewTitle.setVisibility(View.GONE);
            List<String> possibleCustomerDestinations = OrderEntry.possibleCustomerDestinations;
            ArrayAdapter<String> destinationAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_layout, possibleCustomerDestinations);
            destinationAdapter.setDropDownViewResource(R.layout.spinner_layout);
            ListView listView = findViewById(R.id.ListView);
            listView.setAdapter(destinationAdapter);
            String[] destinationsArray = possibleCustomerDestinations.toArray(new String[0]);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                OrderEntry.destinationListener.setValue(destinationsArray[position]);
                dismiss();
            });
        } else if (listCode == 1) { // LoggedIn state selector
            listViewTitle.setVisibility(View.VISIBLE);
            if (button.getId() == R.id.StateButton1) {
                if(Language.getCurrentLanguage() == 0) {
                    listViewTitle.setText("Select Driver License State");
                } else if (Language.getCurrentLanguage() == 1) {
                    listViewTitle.setText("Seleccione el estado de la licencia de conducir");
                } else if (Language.getCurrentLanguage() == 2) {
                    listViewTitle.setText("Sélectionnez l'état du permis de conduire");
                }
            } else if (button.getId() == R.id.StateButton2) {
                if(Language.getCurrentLanguage() == 0) {
                    listViewTitle.setText("Select Trailer License State");
                } else if (Language.getCurrentLanguage() == 1) {
                    listViewTitle.setText("Seleccionar estado de licencia de remolque");
                } else if (Language.getCurrentLanguage() == 2) {
                    listViewTitle.setText("Sélectionnez l'état de la licence de la remorque");
                }
            }
            final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(activity, R.array.states, R.layout.spinner_layout);
            stateAdapter.setDropDownViewResource(R.layout.spinner_layout);
            ListView listview = findViewById(R.id.ListView);
            listview.setAdapter(stateAdapter);
            listview.setOnItemClickListener((parent, view, position, id) -> {
                String state = activity.getResources().getStringArray(R.array.states_abbreviated)[position];
                button.setText(state);
                dismiss();
                LoggedIn.checkboxListener.setValue(true);
            });
        } else if (listCode == 2) { // CreateAccount state selector
            listViewTitle.setVisibility(View.VISIBLE);
            if (button.getId() == R.id.StateButton1) {
                if(Language.getCurrentLanguage() == 0) {
                    listViewTitle.setText("Select Driver License State");
                } else if (Language.getCurrentLanguage() == 1) {
                    listViewTitle.setText("Seleccione el estado de la licencia de conducir");
                } else if (Language.getCurrentLanguage() == 2) {
                    listViewTitle.setText("Sélectionnez l'état du permis de conduire");
                }
            } else if (button.getId() == R.id.StateButton2) {
                if(Language.getCurrentLanguage() == 0) {
                    listViewTitle.setText("Select Trailer License State");
                } else if (Language.getCurrentLanguage() == 1) {
                    listViewTitle.setText("Seleccionar estado de licencia de remolque");
                } else if (Language.getCurrentLanguage() == 2) {
                    listViewTitle.setText("Sélectionnez l'état de la licence de la remorque");
                }
            }
            final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(activity, R.array.states, R.layout.spinner_layout);
            stateAdapter.setDropDownViewResource(R.layout.spinner_layout);
            ListView listview = findViewById(R.id.ListView);
            listview.setAdapter(stateAdapter);
            listview.setOnItemClickListener((parent, view, position, id) -> {
                String state = activity.getResources().getStringArray(R.array.states_abbreviated)[position];
                button.setText(state);
                dismiss();
                if (button.getId() == R.id.StateButton1) {
                    CreateAccount.checkboxListener.setValue(true);
                } else if (button.getId() == R.id.StateButton2) {
                    CreateAccount.checkboxListener.setValue(false);
                }
            });
        }

    }
}
