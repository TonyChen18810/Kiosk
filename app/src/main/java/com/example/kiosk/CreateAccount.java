package com.example.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.text.Html;
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

import java.util.ArrayList;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class CreateAccount extends AppCompatActivity {

    private String email, phone;
    private Button logoutBtn, nextBtn;
    private TextView createAccount;
    private EditText truckName;
    private EditText truckNumber;
    private EditText trailerLicense;
    private EditText driverLicense;
    private EditText driverName;
    private EditText dispatcherPhoneNumber;
    private Spinner trailerStateSpinner, driverStateSpinner;
    private TextView verifyText;
    private TextView preferText;
    private ImageButton truckNameHelp, truckNumberHelp, trailerLicenseHelp,
            driverLicenseHelp, driverNameHelp, dispatcherPhoneNumberHelp;

    private TextView txtText, emailText, bothText, selectText;
    private View textCheckbox, emailCheckbox, bothCheckbox;

    private Button selectState1, selectState2;
    private boolean initialSelection1 = false;
    private boolean initialSelection2 = false;

    private int PREFERRED_COMMUNICATION = -1;

    private static Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

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
                    switch(MainActivity.getCurrentLanguage()) {
                        case 0:
                            selectState1.setText("State");
                        case 1:
                            selectState1.setText("Estado");
                        case 2:
                            selectState1.setText("État");
                        default:
                            selectState1.setText("State");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<CharSequence> stateAdapter2 = ArrayAdapter.createFromResource(this, R.array.states, R.layout.spinner_layout);
        stateAdapter.setDropDownViewResource(R.layout.spinner_layout);
        driverStateSpinner.setAdapter(stateAdapter2);
        driverStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (initialSelection2) {
                    selectState2.setText(getResources().getStringArray(R.array.states)[position]);
                } else {
                    initialSelection2 = true;
                    switch(MainActivity.getCurrentLanguage()) {
                        case 0:
                            selectState2.setText("State");
                        case 1:
                            selectState2.setText("Estado");
                        case 2:
                            selectState2.setText("État");
                        default:
                            selectState2.setText("State");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*
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
*/
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccount.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (truckName.length() == 0 || truckNumber.length() == 0 || trailerLicense.length() == 0 || driverLicense.length() == 0 || driverName.length() == 0 || dispatcherPhoneNumber.length() == 0) {

                } else if (PREFERRED_COMMUNICATION == -1) {
                    selectText.setVisibility(View.VISIBLE);
                } else {
                    String truckNameStr, truckNumberStr, trailerLicenseStr, driverLicenseStr, driverNameStr, dispatcherNumberStr;
                    truckNameStr = truckName.getText().toString();
                    truckNumberStr = truckNumber.getText().toString();
                    trailerLicenseStr = trailerLicense.getText().toString();
                    String trailerStateStr = selectState1.getText().toString();
                    String driverStateStr = selectState2.getText().toString();
                    driverLicenseStr = driverLicense.getText().toString();
                    driverNameStr = driverName.getText().toString();
                    dispatcherNumberStr = dispatcherPhoneNumber.getText().toString();
                    Account account = new Account(email, phone, truckNameStr, truckNumberStr, trailerLicenseStr,
                            trailerStateStr, driverLicenseStr, driverStateStr, driverNameStr, dispatcherNumberStr);
                    currentAccount = account;
                    Account.addAccount(account);
                    System.out.println("Currently registered accounts: ");
                    ArrayList<Account> temp = Account.getAccounts();
                    for (int i = 0; i < temp.size(); i++) {
                        System.out.println("Email: " + temp.get(i).getEmail() + " | Phone number: " + temp.get(i).getPhoneNumber());
                    }
                    selectText.setVisibility(View.INVISIBLE);
                    if (PREFERRED_COMMUNICATION == 0) {

                    } else if (PREFERRED_COMMUNICATION == 1) {

                    } else if (PREFERRED_COMMUNICATION == 2) {

                    }
                    selectText.setVisibility(View.GONE);
                    // such a waste - change to a view
                    setContentView(R.layout.account_created_msg);

                    TextView userEmail = findViewById(R.id.emailAddress);
                    TextView userNumber = findViewById(R.id.phoneNumber);
                    TextView userTruckName = findViewById(R.id.truckName);
                    TextView userTruckNumber = findViewById(R.id.truckNumber);
                    TextView userTrailerLicense = findViewById(R.id.trailerLicense);
                    TextView userDriverLicense = findViewById(R.id.driverLicense);
                    TextView userDriverName = findViewById(R.id.driverName);
                    TextView userDispatcherPhone = findViewById(R.id.dispatcherPhoneNumber);

                    userEmail.setText(Html.fromHtml("Email address: " + "<b>" + currentAccount.getEmail() + "<b>"));
                    userNumber.setText(Html.fromHtml("Phone number: " + "<b>" + currentAccount.getPhoneNumber() + "<b>"));
                    userTruckName.setText(Html.fromHtml("Current truck name: " + "<b>" + currentAccount.getTruckName() + "<b>"));
                    userTruckNumber.setText(Html.fromHtml("Current truck number: " + "<b>" + currentAccount.getTruckNumber() + "<b>"));
                    userTrailerLicense.setText(Html.fromHtml("Current trailer license: " + "<b>" + currentAccount.getTrailerLicense() + "<b>"));
                    userDriverLicense.setText(Html.fromHtml("Driver license: " + "<b>" + currentAccount.getTrailerLicense() + "<b>"));
                    userDriverName.setText(Html.fromHtml("Driver name: " + "<b>" + currentAccount.getDriverName() + "<b>"));
                    userDispatcherPhone.setText(Html.fromHtml("Current dispatcher's phone number: " + "<b>" + currentAccount.getDispatcherPhoneNumber() + "<b>"));

                    findViewById(R.id.LogoutBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Account.clearAccounts();
                            Order.clearOrders();
                            startActivity(new Intent(CreateAccount.this, MainActivity.class));
                        }
                    });
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
                logoutBtn.setText("Logout");
                nextBtn.setText("Next");
                createAccount.setText("Create Account");
                // emailAddress.setHint("Email address");
                // phoneNumber.setHint("Phone number");
                truckName.setHint("Truck name");
                truckNumber.setHint("Truck number");
                trailerLicense.setHint("Trailer license number");
                driverLicense.setHint("Driver license number");
                driverName.setHint("Driver's name");
                dispatcherPhoneNumber.setHint("Dispatcher's phone number");
                verifyText.setText("*By clicking 'Next' I verify that all\ninformation is correct and accurate");
                preferText.setText("How would you prefer communication?");
                txtText.setText("Text message");
                emailText.setText("Email");
                bothText.setText("Text message and email");
                selectText.setText("*Please select one");
                selectState1.setText("State");
                selectState2.setText("State");
                break;
            case 1:
                //Spanish
                logoutBtn.setText("Cerrar sesión");
                nextBtn.setText("Próximo");
                createAccount.setText("Crear una cuenta");
                // emailAddress.setHint("Dirección de correo electrónico");
                // phoneNumber.setHint("Número de teléfono");
                truckName.setHint("Nombre del camión");
                truckNumber.setHint("Numero de camión");
                trailerLicense.setHint("Número de licencia de remolque");
                driverLicense.setHint("Número de licencia de conducir");
                driverName.setHint("Nombre del conductor");
                dispatcherPhoneNumber.setHint("Número de teléfono del despachador");
                verifyText.setText("*Al hacer clic en 'Próximo' verifico que\ntoda la información es correcta y precisa");
                preferText.setText("¿Cómo preferirías la comunicación?");
                txtText.setText("Mensaje de texto");
                emailText.setText("Correo electrónico");
                bothText.setText("Mensaje de texto y correo electrónico");
                selectText.setText("*Por favor, seleccione uno");
                selectState1.setText("Estado");
                selectState2.setText("Estado");
                break;
            case 2:
                //French
                logoutBtn.setText("Se déconnecter");
                nextBtn.setText("Prochain");
                createAccount.setText("Créer un compte");
                // emailAddress.setHint("Adresse électronique");
                // phoneNumber.setHint("Numéro de téléphone");
                truckName.setHint("Nom du camion");
                truckNumber.setHint("Numéro de camion");
                trailerLicense.setHint("Numéro de licence de la remorque");
                driverLicense.setHint("Numéro de permis de conduire");
                driverName.setHint("Nom du conducteur");
                dispatcherPhoneNumber.setHint("Numéro de téléphone du répartiteur");
                verifyText.setText("*En cliquant sur 'Prochain', je vérifie que\ntoutes les informations sont correctes et exactes");
                preferText.setText("Comment préférez-vous la communication?");
                txtText.setText("Message texte");
                emailText.setText("email");
                bothText.setText("Message texte et email");
                selectText.setText("*S'il vous plait sélectionner en un");
                selectState1.setText("État");
                selectState2.setText("État");
                break;
        }
    }

    private void setup() {

        logoutBtn = findViewById(R.id.LogoutBtn);
        nextBtn = findViewById(R.id.NextBtn);
        createAccount = findViewById(R.id.CreateAccountText);
        EditText emailAddress = findViewById(R.id.EmailAddressBox);
        EditText phoneNumber = findViewById(R.id.PhoneNumberBox);
        truckName = findViewById(R.id.TruckNameBox);
        truckNumber = findViewById(R.id.TruckNumberBox);
        trailerLicense = findViewById(R.id.TrailerLicenseBox);
        driverLicense = findViewById(R.id.DriverLicenseBox);
        driverName = findViewById(R.id.DriverNameBox);
        dispatcherPhoneNumber = findViewById(R.id.DispatcherPhoneNumberBox);
        verifyText = findViewById(R.id.VerifyText);
        truckNameHelp = findViewById(R.id.TruckNameHelp);
        truckNumberHelp = findViewById(R.id.TruckNumberHelp);
        trailerLicenseHelp = findViewById(R.id.TrailerLicenseHelp);
        driverLicenseHelp = findViewById(R.id.DriverLicenseHelp);
        driverNameHelp = findViewById(R.id.DriverNameHelp);
        dispatcherPhoneNumberHelp = findViewById(R.id.DispatcherPhoneNumberHelp);
        trailerStateSpinner = findViewById(R.id.StateSpinner);
        driverStateSpinner = findViewById(R.id.StateSpinner2);

        trailerStateSpinner.setVisibility(View.INVISIBLE);
        driverStateSpinner.setVisibility(View.INVISIBLE);

        txtText = findViewById(R.id.Text);
        emailText = findViewById(R.id.Email);
        bothText = findViewById(R.id.Both);
        textCheckbox = findViewById(R.id.TextCheckbox);
        emailCheckbox = findViewById(R.id.EmailCheckbox);
        bothCheckbox = findViewById(R.id.BothCheckbox);
        selectText = findViewById(R.id.SelectText);
        preferText = findViewById(R.id.PreferInfoText);

        selectText.setVisibility(View.GONE);

        selectState1 = findViewById(R.id.StateButton1);
        selectState2 = findViewById(R.id.StateButton2);
        selectState1.setText("Select state");
        selectState2.setText("Select state");

        changeLanguage(MainActivity.getCurrentLanguage());

        showSoftKeyboard(truckName);
        dispatcherPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        emailAddress.setText(email);
        phoneNumber.setText(phone);
        emailAddress.setTextColor(getResources().getColor(R.color.black));
        phoneNumber.setTextColor(getResources().getColor(R.color.black));
    }
}
