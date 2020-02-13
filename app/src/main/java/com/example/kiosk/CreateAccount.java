package com.example.kiosk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;

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

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class CreateAccount extends AppCompatActivity {

    private String email, phone;
    private Button homeBtn;
    private Button submitBtn;
    private TextView createAccount;
    private EditText emailAddress;
    private EditText phoneNumber;
    private EditText truckName;
    private EditText truckNumber;
    private EditText trailerLicense;
    private EditText driverLicense;
    private EditText driverName;
    private EditText dispatcherPhoneNumber;
    private TextView verifyText;
    private TextView selectRadioText;
    private RadioButton radioTextMsg;
    private RadioButton radioEmailMsg;
    private RadioButton radioBothMsg;
    private TextView preferText;
    private ImageButton truckNameHelp, truckNumberHelp, trailerLicenseHelp,
            driverLicenseHelp, driverNameHelp, dispatcherPhoneNumberHelp;

    private static Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_create_account);
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                email = null;
                phone = null;
            } else {
                email = extras.getString("Email Address");
                phone = extras.getString("Phone Number");
            }
        } else {
            email = (String) savedInstanceState.getSerializable("Email Address");
            phone = (String) savedInstanceState.getSerializable("Phone Number");
        }

        ActivityCompat.requestPermissions(CreateAccount.this, new String[]{Manifest.permission.SEND_SMS}, 0);

        setup();

        final Spinner stateSpinner = findViewById(R.id.StateSpinner);
        final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        // SpinnerAdapter stateAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.states));
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        // stateSpinner.setSelection(stateAdapter.getCount());
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner stateSpinner2 = findViewById(R.id.StateSpinner2);
        final ArrayAdapter<CharSequence> stateAdapter2 = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        // SpinnerAdapter stateAdapter2 = new SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.states));
        stateAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner2.setAdapter(stateAdapter2);
        // stateSpinner2.setSelection(stateAdapter.getCount() - 1);
        stateSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
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
                // findViewById(R.id.TruckNumberHelpBubble).setVisibility(View.VISIBLE);
                // findViewById(R.id.TruckNumberHelpText).setVisibility(View.VISIBLE);
            }
        });

        trailerLicenseHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
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

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccount.this, MainActivity.class);
                startActivity(intent);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String truckNameStr, truckNumberStr, trailerLicenseStr, driverLicenseStr, driverNameStr, dispatcherNumberStr;
                truckNameStr = truckName.getText().toString();
                truckNumberStr = truckNumber.getText().toString();
                trailerLicenseStr = trailerLicense.getText().toString();
                String trailerStateStr = stateSpinner.getSelectedItem().toString();
                String driverStateStr = stateSpinner2.getSelectedItem().toString();
                driverLicenseStr = driverLicense.getText().toString();
                driverNameStr = driverName.getText().toString();
                dispatcherNumberStr = dispatcherPhoneNumber.getText().toString();
                Account account = new Account(email, phone, truckNameStr, truckNumberStr, trailerLicenseStr,
                        trailerStateStr, driverLicenseStr, driverStateStr, driverNameStr, dispatcherNumberStr);
                currentAccount = account;

                // Fragment fragment = new AccountCreatedMsgFragment();
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
                /*
                emailAddress.setVisibility(View.INVISIBLE);
                phoneNumber.setVisibility(View.INVISIBLE);
                truckName.setVisibility(View.INVISIBLE);
                truckNumber.setVisibility(View.INVISIBLE);
                trailerLicense.setVisibility(View.INVISIBLE);
                driverLicense.setVisibility(View.INVISIBLE);
                driverName.setVisibility(View.INVISIBLE);
                dispatcherPhoneNumber.setVisibility(View.INVISIBLE);
                dispatcherPhoneNumberHelp.setVisibility(View.INVISIBLE);
                driverLicenseHelp.setVisibility(View.INVISIBLE);
                driverNameHelp.setVisibility(View.INVISIBLE);
                trailerLicenseHelp.setVisibility(View.INVISIBLE);
                truckNameHelp.setVisibility(View.INVISIBLE);
                truckNumberHelp.setVisibility(View.INVISIBLE);
                 */
                // such a waste
                startActivity(new Intent(CreateAccount.this, AccountCreatedMsg.class));

                if (radioTextMsg.isChecked()) {
                    try {
                        MessageSender sendTxtMsg = new MessageSender();
                        sendTxtMsg.sendSMS(getApplicationContext());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                    }

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

    public static Account getCurrentAccount() {
        return currentAccount;
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
                homeBtn.setText("Return to login");
                submitBtn.setText("Submit");
                createAccount.setText("Create Account");
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
                submitBtn.setText("Submit");
                break;
            case 1:
                //Spanish
                homeBtn.setText("Regresar al inicio de sesión");
                submitBtn.setText("Enviar");
                createAccount.setText("Crear una cuenta");
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
                submitBtn.setText("Enviar");
                break;
            case 2:
                //French
                homeBtn.setText("Retour à la connexion");
                submitBtn.setText("Soumettre");
                createAccount.setText("Créer un compte");
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
                submitBtn.setText("Soumettre");
                break;
        }
    }

    private void setup() {

        homeBtn = findViewById(R.id.HomeBtn);
        submitBtn = findViewById(R.id.SubmitBtn);
        createAccount = findViewById(R.id.CreateAccountText);
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

        selectRadioText.setVisibility(View.INVISIBLE);

        changeLanguage(MainActivity.getCurrentLanguage());

        showSoftKeyboard(truckName);
        dispatcherPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        emailAddress.setText(email);
        phoneNumber.setText(phone);
        emailAddress.setTextColor(getResources().getColor(R.color.black));
        phoneNumber.setTextColor(getResources().getColor(R.color.black));
    }
}
