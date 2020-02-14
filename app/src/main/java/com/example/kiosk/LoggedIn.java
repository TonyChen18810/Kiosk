package com.example.kiosk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class LoggedIn extends AppCompatActivity {

    private Button logoutBtn;
    private Button nextBtn;
    private TextView loggedInText;
    private EditText emailAddress;
    private EditText phoneNumber;
    private EditText truckName;
    private EditText truckNumber;
    private EditText trailerLicense;
    private Spinner trailerStateSpinner;
    private EditText driverLicense;
    private Spinner driverStateSpinner;
    private EditText driverName;
    private EditText dispatcherPhoneNumber;
    private TextView verifyText;
    private TextView selectRadioText;
    private RadioButton radioTextMsg;
    private RadioButton radioEmailMsg;
    private RadioButton radioBothMsg;
    private TextView preferText;
    private TextView selectIconText;
    private ImageButton truckNameHelp, truckNumberHelp, trailerLicenseHelp,
            driverLicenseHelp, driverNameHelp, dispatcherPhoneNumberHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        Objects.requireNonNull(getSupportActionBar()).hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        String[] states = getResources().getStringArray(R.array.states);
        setup();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                emailAddress = null;
                phoneNumber = null;
                truckName = null;
                truckNumber = null;
                trailerLicense = null;
                trailerStateSpinner = null;
                driverLicense = null;
                driverStateSpinner = null;
                driverName = null;
                dispatcherPhoneNumber = null;
            } else {
                emailAddress.setText(extras.getString("Email Address"));
                phoneNumber.setText(extras.getString("Phone Number"));
                truckName.setText(extras.getString("Truck Name"));
                truckNumber.setText(extras.getString("Truck Number"));
                trailerLicense.setText(extras.getString("Trailer License"));
                for (int i = 0; i < states.length; i++) {
                    if (states[i].toLowerCase().equals(extras.getString("Trailer State").toLowerCase())) {
                        trailerStateSpinner.setSelection(i);
                    }
                    if (states[i].equals(extras.getString("Driver State"))) {
                        driverStateSpinner.setSelection(i);
                    }
                }
                driverLicense.setText(extras.getString("Driver License"));
                driverName.setText(extras.getString("Driver Name"));
                dispatcherPhoneNumber.setText(extras.getString("Dispatcher's Phone Number"));
            }
        } else {
            // email = (String) savedInstanceState.getSerializable("Email Address");
            // phone = (String) savedInstanceState.getSerializable("Phone Number");
        }

        // phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        changeLanguage(MainActivity.getCurrentLanguage());

        ActivityCompat.requestPermissions(LoggedIn.this, new String[]{Manifest.permission.SEND_SMS}, 0);

        final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        // SpinnerAdapter stateAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.states));
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trailerStateSpinner.setAdapter(stateAdapter);
        // stateSpinner.setSelection(stateAdapter.getCount());
        trailerStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<CharSequence> stateAdapter2 = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        // SpinnerAdapter stateAdapter2 = new SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.states));
        stateAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverStateSpinner.setAdapter(stateAdapter2);
        // stateSpinner2.setSelection(stateAdapter.getCount() - 1);
        driverStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        truckNameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedIn.this);
                builder.setCancelable(true);
                builder.setTitle("Help information");
                builder.setMessage("Please enter the name of your truck/truck company");
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        truckNumberHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedIn.this);
                builder.setCancelable(true);
                builder.setTitle("Help information");
                builder.setMessage("Please enter the number of your truck (NOT your license plate number)");
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        trailerLicenseHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedIn.this);
                builder.setCancelable(true);
                builder.setTitle("Help information");
                builder.setMessage("Please enter the license plate number of your trailer");
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        driverLicenseHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedIn.this);
                builder.setCancelable(true);
                builder.setTitle("Help information");
                builder.setMessage("Please enter your driver's license number");
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        driverNameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedIn.this);
                builder.setCancelable(true);
                builder.setTitle("Help information");
                builder.setMessage("Please enter your name");
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        dispatcherPhoneNumberHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedIn.this);
                builder.setCancelable(true);
                builder.setTitle("Help information");
                builder.setMessage("Please enter your dispatcher's phone number");
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedIn.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr, phoneStr, truckNameStr, truckNumberStr, trailerLicenseStr, driverLicenseStr, driverNameStr, dispatcherNumberStr;
                emailStr = emailAddress.getText().toString();
                phoneStr = phoneNumber.getText().toString();
                truckNameStr = truckName.getText().toString();
                truckNumberStr = truckNumber.getText().toString();
                trailerLicenseStr = trailerLicense.getText().toString();
                String trailerStateStr = trailerStateSpinner.getSelectedItem().toString();
                String driverStateStr = driverStateSpinner.getSelectedItem().toString();
                driverLicenseStr = driverLicense.getText().toString();
                driverNameStr = driverName.getText().toString();
                dispatcherNumberStr = dispatcherPhoneNumber.getText().toString();
                Account account = new Account(emailStr, phoneStr, truckNameStr, truckNumberStr, trailerLicenseStr,
                        trailerStateStr, driverLicenseStr, driverStateStr, driverNameStr, dispatcherNumberStr);
                Intent intent = new Intent(LoggedIn.this, OrderInfo.class);
                startActivity(intent);

                if (radioTextMsg.isChecked()) {
                    // send text message
                    // start next activity
                } else if (radioEmailMsg.isChecked()) {
                    // send email message
                    // start next activity
                } else if (radioBothMsg.isChecked()) {
                    // send text message AND email message
                    // start next activity
                } else {
                    selectRadioText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, SHOW_IMPLICIT);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeLanguage(int val) {
        truckName.setHintTextColor(getResources().getColor(R.color.dark_gray));
        truckNumber.setHintTextColor(getResources().getColor(R.color.dark_gray));
        trailerLicense.setHintTextColor(getResources().getColor(R.color.dark_gray));
        driverLicense.setHintTextColor(getResources().getColor(R.color.dark_gray));
        driverName.setHintTextColor(getResources().getColor(R.color.dark_gray));
        dispatcherPhoneNumber.setHintTextColor(getResources().getColor(R.color.dark_gray));
        switch(val) {
            case 0:
                //English
                logoutBtn.setText("Logout");
                nextBtn.setText("Next");
                loggedInText.setText("Logged in as: " + emailAddress.getText().toString());
                // emailAddress.setHint("Email address");
                // phoneNumber.setHint("Phone number");
                truckName.setHint("Truck name");
                truckNumber.setHint("Truck number");
                trailerLicense.setHint("Trailer license number");
                driverLicense.setHint("Driver license number");
                driverName.setHint("Driver's name");
                dispatcherPhoneNumber.setHint("Dispatcher's phone number");
                verifyText.setText("*By clicking 'Submit' I verify that all information is correct and accurate");
                preferText.setText("How would you prefer communication?");
                radioTextMsg.setText("Text message");
                radioEmailMsg.setText("Email");
                radioBothMsg.setText("Text message and email");
                selectRadioText.setText("*Please select one");
                verifyText.setText("*By clicking 'Submit' I verify that all information is correct and accurate");
                selectIconText.setText("Select icon for help");
                break;
            case 1:
                //Spanish
                logoutBtn.setText("Cerrar sesión");
                nextBtn.setText("Próximo");
                loggedInText.setText("Conectado como: " + emailAddress.getText().toString());
                // emailAddress.setHint("Dirección de correo electrónico");
                // phoneNumber.setHint("Número de teléfono");
                truckName.setHint("Nombre del camión");
                truckNumber.setHint("Numero de camión");
                trailerLicense.setHint("Número de licencia de remolque");
                driverLicense.setHint("Número de licencia de conducir");
                driverName.setHint("Nombre del conductor");
                dispatcherPhoneNumber.setHint("Número de teléfono del despachador");
                verifyText.setText("*Al hacer clic en 'Enviar' verifico que toda la información es correcta y precisa");
                preferText.setText("¿Cómo preferirías la comunicación?");
                radioTextMsg.setText("Mensaje de texto");
                radioEmailMsg.setText("Correo electrónico");
                radioBothMsg.setText("Mensaje de texto y correo electrónico");
                selectRadioText.setText("*Por favor, seleccione uno");
                verifyText.setText("*Al hacer clic en 'Enviar' verifico que toda la información es correcta y precisa");
                selectIconText.setText("Seleccionar icono para ayuda");
                break;
            case 2:
                //French
                logoutBtn.setText("Se déconnecter");
                nextBtn.setText("Prochain");
                loggedInText.setText("Connecté en tant que: " + emailAddress.getText().toString());
                // emailAddress.setHint("Adresse électronique");
                // phoneNumber.setHint("Numéro de téléphone");
                truckName.setHint("Nom du camion");
                truckNumber.setHint("Numéro de camion");
                trailerLicense.setHint("Numéro de licence de la remorque");
                driverLicense.setHint("Numéro de permis de conduire");
                driverName.setHint("Nom du conducteur");
                dispatcherPhoneNumber.setHint("Numéro de téléphone du répartiteur");
                verifyText.setText("*En cliquant sur 'Soumettre', je vérifie que toutes les informations sont correctes et exactes");
                preferText.setText("Comment préférez-vous la communication?");
                radioTextMsg.setText("Message texte");
                radioEmailMsg.setText("email");
                radioBothMsg.setText("Message texte et email");
                selectRadioText.setText("*S'il vous plait sélectionner en un");
                verifyText.setText("En cliquant sur «Soumettre», je vérifie que toutes les informations sont correctes et exactes");
                selectIconText.setText("Sélectionnez l'icône pour obtenir de l'aide");
                break;
        }
    }

    private void setup() {

        logoutBtn = findViewById(R.id.LogoutBtn);
        nextBtn = findViewById(R.id.NextBtn);
        loggedInText = findViewById(R.id.LoggedInText);
        emailAddress = findViewById(R.id.EmailAddressBox);
        phoneNumber = findViewById(R.id.PhoneNumberBox);
        truckName = findViewById(R.id.TruckNameBox);
        truckNumber = findViewById(R.id.TruckNumberBox);
        trailerLicense = findViewById(R.id.TrailerLicenseBox);
        driverLicense = findViewById(R.id.DriverLicenseBox);
        driverName = findViewById(R.id.DriverNameBox);
        dispatcherPhoneNumber = findViewById(R.id.DispatcherPhoneNumberBox);
        verifyText = findViewById(R.id.VerifyText);
        radioTextMsg = findViewById(R.id.TextMsg);
        radioEmailMsg = findViewById(R.id.EmailMsg);
        radioBothMsg = findViewById(R.id.BothMsg);
        selectRadioText = findViewById(R.id.PleaseSelectOne);
        preferText = findViewById(R.id.PreferInfoText);
        truckNameHelp = findViewById(R.id.TruckNameHelp);
        truckNumberHelp = findViewById(R.id.TruckNumberHelp);
        trailerLicenseHelp = findViewById(R.id.TrailerLicenseHelp);
        driverLicenseHelp = findViewById(R.id.DriverLicenseHelp);
        driverNameHelp = findViewById(R.id.DriverNameHelp);
        dispatcherPhoneNumberHelp = findViewById(R.id.DispatcherPhoneNumberHelp);
        selectIconText = findViewById(R.id.helpText);

        trailerStateSpinner = findViewById(R.id.StateSpinner);
        driverStateSpinner = findViewById(R.id.StateSpinner2);

        selectRadioText.setVisibility(View.INVISIBLE);

        showSoftKeyboard(truckName);
        dispatcherPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        emailAddress.setText(emailAddress.getText().toString());
        phoneNumber.setText(emailAddress.getText().toString());
        emailAddress.setTextColor(getResources().getColor(R.color.black));
        phoneNumber.setTextColor(getResources().getColor(R.color.black));
    }
}
