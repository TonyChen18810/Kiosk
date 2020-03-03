package com.example.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class LoggedIn extends AppCompatActivity {

    private int currentLanguage = Language.getCurrentLanguage();

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
    private TextView preferText;
    private TextView selectIconText;
    private ImageButton truckNameHelp, truckNumberHelp, trailerLicenseHelp,
            driverLicenseHelp, driverNameHelp, dispatcherPhoneNumberHelp;
    private TextView text, email, both, select, userEmail, userPhone, userTruck;
    private View textCheckbox, emailCheckbox, bothCheckbox;

    private Button selectState1, selectState2;
    private String state1, state2;
    private boolean initialSelection1 = false;
    private boolean initialSelection2 = false;

    private int PREFERRED_COMMUNICATION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

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
                selectState1.setText(extras.getString("Trailer State"));
                selectState2.setText(extras.getString("Driver State"));
                driverLicense.setText(extras.getString("Driver License"));
                driverName.setText(extras.getString("Driver Name"));
                dispatcherPhoneNumber.setText(extras.getString("Dispatcher's Phone Number"));
                state1 = extras.getString("Trailer State");
                state2 = extras.getString("Driver State");
            }
        } else {
            // email = (String) savedInstanceState.getSerializable("Email Address");
            // phone = (String) savedInstanceState.getSerializable("Phone Number");
        }

        ActivityCompat.requestPermissions(LoggedIn.this, new String[]{Manifest.permission.SEND_SMS}, 0);

        final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.states, R.layout.spinner_layout);
        stateAdapter.setDropDownViewResource(R.layout.spinner_layout);
        trailerStateSpinner.setAdapter(stateAdapter);
        trailerStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (initialSelection1) {
                    selectState1.setText(getResources().getStringArray(R.array.states)[position]);
                } else {
                    initialSelection1 = true;
                    selectState1.setText(state1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<CharSequence> stateAdapter2 = ArrayAdapter.createFromResource(this, R.array.states, R.layout.spinner_layout);
        stateAdapter2.setDropDownViewResource(R.layout.spinner_layout);
        driverStateSpinner.setAdapter(stateAdapter2);
        driverStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (initialSelection2) {
                    selectState2.setText(getResources().getStringArray(R.array.states)[position]);
                } else {
                    initialSelection2 = true;
                    selectState2.setText(state2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        driverNameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = null;
                if (currentLanguage == 0) {
                    message = "Please enter your first and last name.";
                } else if (currentLanguage == 1) {
                    message = "Por favor introduce tu primer nombre y apellido.";
                } else if (currentLanguage == 2) {
                    message = "S'il-vous-plaît, entrer votre prénom et votre nom.";
                }
                HelpDialog dialog = new HelpDialog(message, LoggedIn.this);
                dialog.show();
            }
        });

        driverLicenseHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = null;
                if (currentLanguage == 0) {
                    message = "Please enter your driver license number";
                } else if (currentLanguage == 1) {
                    message = "Por favor ingrese su número de licencia de conducir";
                } else if (currentLanguage == 2) {
                    message = "Veuillez entrer votre numéro de permis de conduire";
                }
                HelpDialog dialog = new HelpDialog(message, LoggedIn.this);
                dialog.show();
            }
        });

        truckNameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = null;
                if (currentLanguage == 0) {
                    message = "Please enter the company name of your truck (NOT the make/model)";
                } else if (currentLanguage == 1) {
                    message = "Ingrese el nombre de la compañía de su camión (NO la marca / modelo)";
                } else if (currentLanguage == 2) {
                    message = "Veuillez saisir le nom de l'entreprise de votre camion (PAS la marque / le modèle)";
                }
                HelpDialog dialog = new HelpDialog(message, LoggedIn.this);
                dialog.show();
            }
        });

        truckNumberHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = null;
                if (currentLanguage == 0) {
                    message = "Please enter the number of your truck";
                } else if (currentLanguage == 1) {
                    message = "Por favor ingrese el número de su camión";
                } else if (currentLanguage == 2) {
                    message = "Veuillez entrer le numéro de votre camion";
                }
                HelpDialog dialog = new HelpDialog(message, LoggedIn.this);
                dialog.show();
            }
        });

        trailerLicenseHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = null;
                if (currentLanguage == 0) {
                    message = "Please enter the license plate number of your trailer";
                } else if (currentLanguage == 1) {
                    message = "Ingrese el número de placa de su remolque";
                } else if (currentLanguage == 2) {
                    message = "Veuillez entrer le numéro de plaque d'immatriculation de votre remorque";
                }
                HelpDialog dialog = new HelpDialog(message, LoggedIn.this);
                dialog.show();
            }
        });

        dispatcherPhoneNumberHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = null;
                if (currentLanguage == 0) {
                    message = "Please enter the phone number of your current dispatcher";
                } else if (currentLanguage == 1) {
                    message = "Ingrese el número de teléfono de su despachador actual";
                } else if (currentLanguage == 2) {
                    message = "Veuillez entrer le numéro de téléphone de votre répartiteur actuel";
                }
                HelpDialog dialog = new HelpDialog(message, LoggedIn.this);
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
                if (PREFERRED_COMMUNICATION == -1) {
                    select.setVisibility(View.VISIBLE);
                } else {
                    select.setVisibility(View.GONE);
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
                    Intent intent = new Intent(LoggedIn.this, OrderEntry.class);
                    startActivity(intent);
                }
            }
        });

        textCheckbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                emailCheckbox.setPressed(false);
                bothCheckbox.setPressed(false);
                textCheckbox.setPressed(true);
                PREFERRED_COMMUNICATION = 0;
                return true;
            }
        });

        emailCheckbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bothCheckbox.setPressed(false);
                textCheckbox.setPressed(false);
                emailCheckbox.setPressed(true);
                PREFERRED_COMMUNICATION = 1;
                return true;
            }
        });

        bothCheckbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textCheckbox.setPressed(false);
                emailCheckbox.setPressed(false);
                bothCheckbox.setPressed(true);
                PREFERRED_COMMUNICATION = 2;
                return true;
            }
        });

        findViewById(R.id.Text).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                emailCheckbox.setPressed(false);
                bothCheckbox.setPressed(false);
                textCheckbox.setPressed(true);
                PREFERRED_COMMUNICATION = 0;
                return true;
            }
        });

        findViewById(R.id.Email).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bothCheckbox.setPressed(false);
                textCheckbox.setPressed(false);
                emailCheckbox.setPressed(true);
                PREFERRED_COMMUNICATION = 1;
                return true;
            }
        });

        findViewById(R.id.Both).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textCheckbox.setPressed(false);
                emailCheckbox.setPressed(false);
                bothCheckbox.setPressed(true);
                PREFERRED_COMMUNICATION = 2;
                return true;
            }
        });

        selectState1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trailerStateSpinner.performClick();
            }
        });

        selectState2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverStateSpinner.performClick();
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
                logoutBtn.setText(R.string.logout_eng);
                nextBtn.setText(R.string.next_eng);
                loggedInText.setText(R.string.logged_in_as_eng);
                truckName.setHint("Truck name");
                truckNumber.setHint("Truck number");
                trailerLicense.setHint("Trailer license number");
                driverLicense.setHint("Driver license number");
                driverName.setHint("Driver's name");
                dispatcherPhoneNumber.setHint("Dispatcher's phone number");
                verifyText.setText(R.string.verify_submit_eng);
                preferText.setText(R.string.comm_preference_eng);
                text.setText(R.string.text_msg_eng);
                email.setText(R.string.email_eng);
                both.setText(R.string.text_and_email_eng);
                select.setText(R.string.select_one_eng);
                selectIconText.setText(R.string.select_help_icon_eng);
                selectState1.setText(R.string.state_eng);
                selectState2.setText(R.string.state_eng);
                break;
            case 1:
                //Spanish
                logoutBtn.setText(R.string.logout_sp);
                nextBtn.setText(R.string.next_sp);
                loggedInText.setText(R.string.logged_in_as_sp);
                truckName.setHint("Nombre del camión");
                truckNumber.setHint("Numero de camión");
                trailerLicense.setHint("Número de licencia de remolque");
                driverLicense.setHint("Número de licencia de conducir");
                driverName.setHint("Nombre del conductor");
                dispatcherPhoneNumber.setHint("Número de teléfono del despachador");
                verifyText.setText(R.string.verify_submit_sp);
                preferText.setText(R.string.comm_preference_sp);
                text.setText(R.string.text_msg_sp);
                email.setText(R.string.email_sp);
                both.setText(R.string.text_and_email_sp);
                select.setText(R.string.select_one_sp);
                selectIconText.setText(R.string.select_help_icon_sp);
                selectState1.setText(R.string.state_sp);
                selectState2.setText(R.string.state_sp);
                break;
            case 2:
                //French
                logoutBtn.setText(R.string.logout_fr);
                nextBtn.setText(R.string.next_fr);
                loggedInText.setText(R.string.logged_in_as_fr);
                truckName.setHint("Nom du camion");
                truckNumber.setHint("Numéro de camion");
                trailerLicense.setHint("Numéro de licence de la remorque");
                driverLicense.setHint("Numéro de permis de conduire");
                driverName.setHint("Nom du conducteur");
                dispatcherPhoneNumber.setHint("Numéro de téléphone du répartiteur");
                verifyText.setText(R.string.verify_submit_fr);
                preferText.setText(R.string.comm_preference_fr);
                text.setText(R.string.text_msg_fr);
                email.setText(R.string.email_fr);
                both.setText(R.string.text_and_email_fr);
                select.setText(R.string.select_one_fr);
                selectIconText.setText(R.string.select_help_icon_fr);
                selectState1.setText(R.string.state_fr);
                selectState2.setText(R.string.state_fr);
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
        text = findViewById(R.id.Text);
        email = findViewById(R.id.Email);
        both = findViewById(R.id.Both);
        textCheckbox = findViewById(R.id.TextCheckbox);
        emailCheckbox = findViewById(R.id.EmailCheckbox);
        bothCheckbox = findViewById(R.id.BothCheckbox);
        select = findViewById(R.id.SelectText);
        preferText = findViewById(R.id.PreferInfoText);
        truckNameHelp = findViewById(R.id.TruckNameHelp);
        truckNumberHelp = findViewById(R.id.TruckNumberHelp);
        trailerLicenseHelp = findViewById(R.id.TrailerLicenseHelp);
        driverLicenseHelp = findViewById(R.id.DriverLicenseHelp);
        driverNameHelp = findViewById(R.id.DriverNameHelp);
        dispatcherPhoneNumberHelp = findViewById(R.id.DispatcherPhoneNumberHelp);
        selectState1 = findViewById(R.id.StateButton1);
        selectState2 = findViewById(R.id.StateButton2);

        selectIconText = findViewById(R.id.HelpText);

        userEmail = findViewById(R.id.UserEmail);
        userPhone = findViewById(R.id.UserPhone);
        userTruck = findViewById(R.id.UserTruck);
        userEmail.setText(MainActivity.getCurrentAccount().getEmail());
        userPhone.setText(MainActivity.getCurrentAccount().getPhoneNumber());
        userTruck.setText(String.format("%s %s", MainActivity.getCurrentAccount().getTruckName(), MainActivity.getCurrentAccount().getTruckNumber()));

        select.setVisibility(View.GONE);

        trailerStateSpinner = findViewById(R.id.StateSpinner);
        driverStateSpinner = findViewById(R.id.StateSpinner2);

        trailerStateSpinner.setVisibility(View.INVISIBLE);
        driverStateSpinner.setVisibility(View.INVISIBLE);

        showSoftKeyboard(truckName);
        dispatcherPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        emailAddress.setText(emailAddress.getText().toString());
        phoneNumber.setText(emailAddress.getText().toString());
        emailAddress.setTextColor(getResources().getColor(R.color.black));
        phoneNumber.setTextColor(getResources().getColor(R.color.black));

        changeLanguage(currentLanguage);
    }
}
