package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Html;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.kiosk.Account;
import com.example.kiosk.Dialogs.HelpDialog;
import com.example.kiosk.Dialogs.ListViewDialog;
import com.example.kiosk.Dialogs.LogoutDialog;
import com.example.kiosk.Helpers.KeyboardListener;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.PhoneNumberFormat;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import com.example.kiosk.Webservices.UpdateShippingTruckDriver;

import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class CreateAccount extends AppCompatActivity {

    private String email, phone;
    private Button logoutBtn, nextBtn;
    private TextView createAccount, verifyText, preferText, helpText;
    private EditText emailAddress;
    private EditText phoneNumber;
    private EditText truckName;
    private EditText truckNumber;
    private EditText trailerLicense;
    private EditText driverLicense;
    private EditText driverName;
    private EditText dispatcherPhoneNumber;
    private Spinner trailerStateSpinner, driverStateSpinner;
    private ImageButton truckNameHelp, truckNumberHelp, trailerLicenseHelp,
            driverLicenseHelp, driverNameHelp, dispatcherPhoneNumberHelp;

    String emailStr, phoneStr, truckNameStr, truckNumberStr, trailerLicenseStr, driverLicenseStr, driverNameStr, dispatcherNumberStr;

    private TextView txtText, emailText, bothText, selectText;
    private View textCheckbox, emailCheckbox, bothCheckbox;

    private ProgressBar progressBar;

    private Button selectState1, selectState2;
    private boolean initialSelection1 = false;
    private boolean initialSelection2 = false;
    private boolean clicked1 = false, clicked2 = false;

    public static MutableLiveData<Boolean> checkboxListener;

    private int PREFERRED_COMMUNICATION = -1;

    private static int currentLanguage = Language.getCurrentLanguage();

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
        setup();

        checkboxListener = new MutableLiveData<>();
        checkboxListener.observe(CreateAccount.this, updateState -> {
            if (updateState) {
                setCommunication();
                clicked1 = true;
                selectState1.clearAnimation();
            } else if (!updateState) {
                setCommunication();
                clicked2 = true;
                selectState2.clearAnimation();
            }
        });

        final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.states, R.layout.spinner_layout);
        stateAdapter.setDropDownViewResource(R.layout.spinner_layout);
        trailerStateSpinner.setAdapter(stateAdapter);
        trailerStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (initialSelection1) {
                    selectState1.setText(getResources().getStringArray(R.array.states_abbreviated)[position]);
                    selectState1.clearAnimation();
                    clicked1 = true;
                } else {
                    initialSelection1 = true;
                    if (currentLanguage == 0) {
                        selectState1.setText(R.string.state_eng);
                    } else if (currentLanguage == 1) {
                        selectState1.setText(R.string.state_sp);
                    } else if (currentLanguage == 2) {
                        selectState1.setText(R.string.state_fr);
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
                    selectState2.setText(getResources().getStringArray(R.array.states_abbreviated)[position]);
                    selectState2.clearAnimation();
                    clicked2 = true;
                } else {
                    initialSelection2 = true;
                    if (currentLanguage == 0) {
                        selectState2.setText(R.string.state_eng);
                    } else if (currentLanguage == 1) {
                        selectState2.setText(R.string.state_sp);
                    } else if (currentLanguage == 2) {
                        selectState2.setText(R.string.state_fr);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        driverNameHelp.setOnClickListener(v -> {
            String message = null;
            if (currentLanguage == 0) {
                message = "Please enter your first and last name.";
            } else if (currentLanguage == 1) {
                message = "Por favor introduce tu primer nombre y apellido.";
            } else if (currentLanguage == 2) {
                message = "S'il-vous-plaît, entrer votre prénom et votre nom.";
            }
            HelpDialog dialog = new HelpDialog(message, CreateAccount.this);
            dialog.show();
        });

        driverLicenseHelp.setOnClickListener(v -> {
            String message = null;
            if (currentLanguage == 0) {
                message = "Please enter your driver license number";
            } else if (currentLanguage == 1) {
                message = "Por favor ingrese su número de licencia de conducir";
            } else if (currentLanguage == 2) {
                message = "Veuillez entrer votre numéro de permis de conduire";
            }
            HelpDialog dialog = new HelpDialog(message, CreateAccount.this);
            dialog.show();
        });

        truckNameHelp.setOnClickListener(v -> {
            String message = null;
            if (currentLanguage == 0) {
                message = "Please enter the company name of your truck (NOT the make/model)";
            } else if (currentLanguage == 1) {
                message = "Ingrese el nombre de la compañía de su camión (NO la marca / modelo)";
            } else if (currentLanguage == 2) {
                message = "Veuillez saisir le nom de l'entreprise de votre camion (PAS la marque / le modèle)";
            }
            HelpDialog dialog = new HelpDialog(message, CreateAccount.this);
            dialog.show();
        });

        truckNumberHelp.setOnClickListener(v -> {
            String message = null;
            if (currentLanguage == 0) {
                message = "Please enter the number of your truck";
            } else if (currentLanguage == 1) {
                message = "Por favor ingrese el número de su camión";
            } else if (currentLanguage == 2) {
                message = "Veuillez entrer le numéro de votre camion";
            }
            HelpDialog dialog = new HelpDialog(message, CreateAccount.this);
            dialog.show();
        });

        trailerLicenseHelp.setOnClickListener(v -> {
            String message = null;
            if (currentLanguage == 0) {
                message = "Please enter the license plate number of your trailer";
            } else if (currentLanguage == 1) {
                message = "Ingrese el número de placa de su remolque";
            } else if (currentLanguage == 2) {
                message = "Veuillez entrer le numéro de plaque d'immatriculation de votre remorque";
            }
            HelpDialog dialog = new HelpDialog(message, CreateAccount.this);
            dialog.show();
        });

        dispatcherPhoneNumberHelp.setOnClickListener(v -> {
            String message = null;
            if (currentLanguage == 0) {
                message = "Please enter the phone number of your current dispatcher";
            } else if (currentLanguage == 1) {
                message = "Ingrese el número de teléfono de su despachador actual";
            } else if (currentLanguage == 2) {
                message = "Veuillez entrer le numéro de téléphone de votre répartiteur actuel";
            }
            HelpDialog dialog = new HelpDialog(message, CreateAccount.this);
            dialog.show();
        });

        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        dispatcherPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        logoutBtn.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(CreateAccount.this, v);
            dialog.show();
            // Intent intent = new Intent(CreateAccount.this, MainActivity.class);
            // startActivity(intent);
        });

        nextBtn.setOnClickListener(v -> {
            ArrayList<EditText> fields = new ArrayList<>();
            fields.add(truckName);
            fields.add(truckNumber);
            fields.add(trailerLicense);
            fields.add(driverLicense);
            fields.add(driverName);
            fields.add(dispatcherPhoneNumber);
            List<EditText> errorFields = new ArrayList<>();
            if (truckName.length() == 0 || truckNumber.length() == 0 || trailerLicense.length() == 0 || driverLicense.length() == 0 || driverName.length() == 0 || dispatcherPhoneNumber.length() == 0 || !clicked1 || !clicked2) {
                for (int i = 0; i < fields.size(); i++) {
                    if (fields.get(i).length() == 0) {
                        errorFields.add(fields.get(i));
                    }
                }
                setStatus(0, errorFields);
                if (!clicked1) {
                    selectState1.startAnimation(AnimationUtils.loadAnimation(CreateAccount.this, R.anim.fade_error));
                }
                if (!clicked2) {
                    selectState2.startAnimation(AnimationUtils.loadAnimation(CreateAccount.this, R.anim.fade_error));
                }
                if (PREFERRED_COMMUNICATION == -1) {
                    selectText.setVisibility(View.VISIBLE);
                }
            } else {
                fields.clear();
                errorFields.clear();
                setStatus(1, errorFields);
                emailStr = emailAddress.getText().toString();
                phoneStr = phoneNumber.getText().toString();
                truckNameStr = truckName.getText().toString();
                truckNumberStr = truckNumber.getText().toString();
                trailerLicenseStr = trailerLicense.getText().toString();
                String trailerStateStr = selectState1.getText().toString();
                String driverStateStr = selectState2.getText().toString();
                driverLicenseStr = driverLicense.getText().toString();
                driverNameStr = driverName.getText().toString();
                dispatcherNumberStr = dispatcherPhoneNumber.getText().toString();

                selectText.setVisibility(View.INVISIBLE);
                if (PREFERRED_COMMUNICATION == 0) {
                    textCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                } else if (PREFERRED_COMMUNICATION == 1) {
                    emailCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                } else if (PREFERRED_COMMUNICATION == 2) {
                    bothCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                }
                selectText.setVisibility(View.GONE);
                setContentView(R.layout.account_created_msg);

                TextView userEmail = findViewById(R.id.emailAddress);
                TextView userNumber = findViewById(R.id.phoneNumber);
                TextView userTruckName = findViewById(R.id.truckName);
                TextView userTruckNumber = findViewById(R.id.truckNumber);
                TextView userTrailerLicense = findViewById(R.id.trailerLicense);
                TextView userDriverLicense = findViewById(R.id.driverLicense);
                TextView userDriverName = findViewById(R.id.driverName);
                TextView userDispatcherPhone = findViewById(R.id.dispatcherPhoneNumber);

                progressBar.setVisibility(View.VISIBLE);
                // pass a weak reference?
                new UpdateShippingTruckDriver(CreateAccount.this, emailStr, emailStr, driverNameStr, PhoneNumberFormat.extract(phoneStr), truckNameStr, truckNumberStr,
                        driverLicenseStr, driverStateStr, trailerLicenseStr, trailerStateStr, PhoneNumberFormat.extract(dispatcherNumberStr),"0", Integer.toString(PREFERRED_COMMUNICATION+1)).execute();

                userEmail.setText(Html.fromHtml("Email address: " + "<b>" + email + "<b>"));
                userNumber.setText(Html.fromHtml("Phone number: " + "<b>" + phone + "<b>"));
                userTruckName.setText(Html.fromHtml("Current truck name: " + "<b>" + truckNameStr + "<b>"));
                userTruckNumber.setText(Html.fromHtml("Current truck number: " + "<b>" + truckNumberStr + "<b>"));
                userTrailerLicense.setText(Html.fromHtml("Current trailer license: " + "<b>" + trailerLicenseStr + "<b>"));
                userDriverLicense.setText(Html.fromHtml("Driver license: " + "<b>" + driverLicenseStr + "<b>"));
                userDriverName.setText(Html.fromHtml("Driver name: " + "<b>" + driverNameStr + "<b>"));
                userDispatcherPhone.setText(Html.fromHtml("Current dispatcher's phone number: " + "<b>\n" + dispatcherNumberStr + "<b>"));

                findViewById(R.id.LogoutBtn).setOnClickListener(v1 -> {
                    Account.clearAccounts();
                    MasterOrder.reset();
                    startActivity(new Intent(CreateAccount.this, MainActivity.class));
                });
            }
        });

        textCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(emailCheckbox, bothCheckbox, textCheckbox);
            return true;
        });

        findViewById(R.id.Text).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(emailCheckbox, bothCheckbox, textCheckbox);
            return true;
        });

        emailCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(bothCheckbox, textCheckbox, emailCheckbox);
            return true;
        });

        findViewById(R.id.Email).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(bothCheckbox, textCheckbox, emailCheckbox);
            return true;
        });

        bothCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(textCheckbox, emailCheckbox, bothCheckbox);
            return true;
        });

        findViewById(R.id.Both).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(textCheckbox, emailCheckbox, bothCheckbox);
            return true;
        });

        selectState1.setOnClickListener(v -> {
            // trailerStateSpinner.performClick();
            ListViewDialog dialog = new ListViewDialog(CreateAccount.this, selectState1, 2);
            dialog.show();
        });

        selectState2.setOnClickListener(v -> {
            // driverStateSpinner.performClick();
            ListViewDialog dialog = new ListViewDialog(CreateAccount.this, selectState2, 2);
            dialog.show();
        });
    }

    public void setCommunication() {
        if (Account.getCurrentAccount().getCommunicationPreference().equals("0")) {
            setChecked(emailCheckbox, bothCheckbox, textCheckbox);
        } else if (Account.getCurrentAccount().getCommunicationPreference().equals("1")) {
            setChecked(textCheckbox, bothCheckbox, emailCheckbox);
        } else if (Account.getCurrentAccount().getCommunicationPreference().equals("2")) {
            setChecked(emailCheckbox, textCheckbox, bothCheckbox);
        }
    }

    private void setChecked(View... checkBox) {
        for (int i = 0; i < checkBox.length; i++) {
            if (i == checkBox.length-1) {
                checkBox[i].setPressed(true);
            } else {
                checkBox[i].setPressed(false);
            }
        }
        if (checkBox[checkBox.length-1] == textCheckbox) {
            Account.getCurrentAccount().setCommunicationPreference("0");
            PREFERRED_COMMUNICATION = 0;
        } else if (checkBox[checkBox.length-1] == emailCheckbox) {
            Account.getCurrentAccount().setCommunicationPreference("1");
            PREFERRED_COMMUNICATION = 1;
        } else if (checkBox[checkBox.length-1] == bothCheckbox) {
            Account.getCurrentAccount().setCommunicationPreference("2");
            PREFERRED_COMMUNICATION = 2;
        }
    }

    private void setStatus(int status, List<EditText> editTexts) {
        for (int i = 0; i < editTexts.size(); i++) {
            if (status == 1) {
                editTexts.get(i).getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
            } else if (status == 0){
                editTexts.get(i).getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
            } else if (status == -1){
                editTexts.get(i).getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                createAccount.setText(R.string.create_account_eng);
                helpText.setText(R.string.select_help_icon_eng);
                truckName.setHint("Truck name");
                truckNumber.setHint("Truck number");
                trailerLicense.setHint("Trailer license number");
                driverLicense.setHint("Driver license number");
                driverName.setHint("Driver's name");
                dispatcherPhoneNumber.setHint("Dispatcher's phone number");
                verifyText.setText(R.string.verify_next_eng);
                preferText.setText(R.string.comm_preference_eng);
                txtText.setText(R.string.text_msg_eng);
                emailText.setText(R.string.email_eng);
                bothText.setText(R.string.text_and_email_eng);
                selectText.setText(R.string.select_one_eng);
                selectState1.setText(R.string.state_eng);
                selectState2.setText(R.string.state_eng);
                break;
            case 1:
                //Spanish
                logoutBtn.setText(R.string.logout_sp);
                nextBtn.setText(R.string.next_sp);
                createAccount.setText(R.string.create_account_sp);
                helpText.setText(R.string.select_help_icon_sp);
                truckName.setHint("Nombre del camión");
                truckNumber.setHint("Numero de camión");
                trailerLicense.setHint("Número de licencia de remolque");
                driverLicense.setHint("Número de licencia de conducir");
                driverName.setHint("Nombre del conductor");
                dispatcherPhoneNumber.setHint("Número de teléfono del despachador");
                verifyText.setText(R.string.verify_next_sp);
                preferText.setText(R.string.comm_preference_sp);
                txtText.setText(R.string.text_msg_sp);
                emailText.setText(R.string.email_sp);
                bothText.setText(R.string.text_and_email_sp);
                selectText.setText(R.string.select_one_sp);
                selectState1.setText(R.string.state_sp);
                selectState2.setText(R.string.state_sp);
                break;
            case 2:
                //French
                logoutBtn.setText(R.string.logout_fr);
                nextBtn.setText(R.string.next_fr);
                createAccount.setText(R.string.create_account_fr);
                helpText.setText(R.string.select_help_icon_fr);
                truckName.setHint("Nom du camion");
                truckNumber.setHint("Numéro de camion");
                trailerLicense.setHint("Numéro de licence de la remorque");
                driverLicense.setHint("Numéro de permis de conduire");
                driverName.setHint("Nom du conducteur");
                dispatcherPhoneNumber.setHint("Numéro de téléphone du répartiteur");
                verifyText.setText(R.string.verify_next_fr);
                preferText.setText(R.string.comm_preference_fr);
                txtText.setText(R.string.text_msg_fr);
                emailText.setText(R.string.email_fr);
                bothText.setText(R.string.text_and_email_fr);
                selectText.setText(R.string.select_one_fr);
                selectState1.setText(R.string.state_fr);
                selectState2.setText(R.string.state_fr);
                break;
        }
    }

    private void setup() {

        logoutBtn = findViewById(R.id.LogoutBtn);
        nextBtn = findViewById(R.id.NextBtn);
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
        truckNameHelp = findViewById(R.id.TruckNameHelp);
        truckNumberHelp = findViewById(R.id.TruckNumberHelp);
        trailerLicenseHelp = findViewById(R.id.TrailerLicenseHelp);
        driverLicenseHelp = findViewById(R.id.DriverLicenseHelp);
        driverNameHelp = findViewById(R.id.DriverNameHelp);
        dispatcherPhoneNumberHelp = findViewById(R.id.DispatcherPhoneNumberHelp);
        trailerStateSpinner = findViewById(R.id.StateSpinner);
        driverStateSpinner = findViewById(R.id.StateSpinner2);
        helpText = findViewById(R.id.HelpText);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        trailerStateSpinner.setVisibility(View.INVISIBLE);
        driverStateSpinner.setVisibility(View.INVISIBLE);

        driverLicense.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        trailerLicense.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

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

        showSoftKeyboard(driverName);
        dispatcherPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        emailAddress.setText(email);
        phoneNumber.setText(PhoneNumberFormat.formatPhoneNumber(phone));
        emailAddress.setTextColor(getResources().getColor(R.color.black));
        phoneNumber.setTextColor(getResources().getColor(R.color.black));

        changeLanguage(currentLanguage);
    }
}
