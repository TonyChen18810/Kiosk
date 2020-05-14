package com.dbc.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Dialogs.ListViewDialog;
import com.dbc.kiosk.Dialogs.LogoutDialog;
import com.dbc.kiosk.Helpers.HintTextWatcher;
import com.dbc.kiosk.Helpers.KeyboardListener;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.Helpers.PhoneNumberFormat;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Webservices.GetServerTime;
import com.dbc.kiosk.Webservices.UpdateShippingTruckDriver;
import java.util.ArrayList;
import java.util.List;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;
/**
 * CreateAccount.java
 *
 * User enters information for the first time to create a check-in
 * account with D'Arrigo
 *
 * This activity is started when the user selects "No" in FirstScreen.java
 * and enters/confirms an email address and phone number in MainActivity.java
 *
 * Enter information to create an account, calls UpdateShippingTruckDriver.java
 * when "Next" button is pressed
 */
public class CreateAccount extends AppCompatActivity {
    private String email, phone;
    private Button logoutBtn, nextBtn;
    private TextView createAccount, verifyText, preferText, emailHint, phoneHint, driverNameHint,
            driverLicenseHint, truckNameHint, truckNumberHint, trailerLicenseHint, dispatcherPhoneHint;
    private EditText emailAddress, phoneNumber, truckName, truckNumber, trailerLicense, driverLicense,
            driverName, dispatcherPhoneNumber;
    private Spinner trailerStateSpinner, driverStateSpinner;

    ProgressBar progressBar;

    String emailStr, phoneStr, truckNameStr, truckNumberStr, trailerLicenseStr, driverLicenseStr,
            driverNameStr, dispatcherNumberStr;

    private TextView txtText, emailText, bothText, selectText, standardRatesApply;
    private CheckBox textCheckbox, emailCheckbox, bothCheckbox;

    private Button selectState1, selectState2;
    private boolean initialSelection1 = false, initialSelection2 = false;
    private boolean clicked1 = false, clicked2 = false;

    public static MutableLiveData<Boolean> checkboxListener;

    private int PREFERRED_COMMUNICATION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        new GetServerTime().execute();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                email = null;
                phone = null;
            } else {
                email = extras.getString("Email Address").toLowerCase();
                phone = PhoneNumberFormat.extract(extras.getString("Phone Number"));
            }
        } else {
            email = (String) savedInstanceState.getSerializable("Email Address");
            phone = (String) savedInstanceState.getSerializable("Phone Number");
        }
        setup();

        checkboxListener = new MutableLiveData<>();
        checkboxListener.observe(CreateAccount.this, updateState -> {
            if (updateState) {
                clicked1 = true;
                selectState1.clearAnimation();
                truckName.requestFocus();
            } else if (!updateState) {
                clicked2 = true;
                selectState2.clearAnimation();
                dispatcherPhoneNumber.requestFocus();
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
                    if (Language.getCurrentLanguage() == 1) {
                        selectState1.setText(R.string.state_eng);
                    } else if (Language.getCurrentLanguage() == 2) {
                        selectState1.setText(R.string.state_sp);
                    } else if (Language.getCurrentLanguage() == 3) {
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
                    if (Language.getCurrentLanguage() == 1) {
                        selectState2.setText(R.string.state_eng);
                    } else if (Language.getCurrentLanguage() == 2) {
                        selectState2.setText(R.string.state_sp);
                    } else if (Language.getCurrentLanguage() == 3) {
                        selectState2.setText(R.string.state_fr);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        dispatcherPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        driverLicense.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (selectState1.getText().equals("State") || selectState1.getText().equals("Estado") || selectState1.getText().equals("État")) {
                    selectState1.performClick();
                }
            }
        });

        trailerLicense.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (selectState2.getText().equals("State") || selectState2.getText().equals("Estado") || selectState2.getText().equals("État")) {
                    selectState2.performClick();
                }
            }
        });

        driverName.addTextChangedListener(new HintTextWatcher(driverName, driverNameHint));

        driverLicense.addTextChangedListener(new HintTextWatcher(driverLicense, driverLicenseHint));

        truckName.addTextChangedListener(new HintTextWatcher(truckName, truckNameHint));

        truckNumber.addTextChangedListener(new HintTextWatcher(truckNumber, truckNumberHint));

        trailerLicense.addTextChangedListener(new HintTextWatcher(trailerLicense, trailerLicenseHint));

        dispatcherPhoneNumber.addTextChangedListener(new HintTextWatcher(dispatcherPhoneNumber, dispatcherPhoneHint));

        logoutBtn.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(CreateAccount.this, CreateAccount.this);
            dialog.show();
            dialog.setCancelable(false);
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
            if (truckName.length() == 0 || truckNumber.length() == 0 || trailerLicense.length() == 0 || driverLicense.length() == 0 || driverName.length() == 0 || dispatcherPhoneNumber.length() == 0 || !clicked1 || !clicked2 || PREFERRED_COMMUNICATION == -1) {
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
                disableButtons(nextBtn, selectState1, selectState2, logoutBtn);
                disableEditTexts(emailAddress, phoneNumber, truckName, truckNumber, trailerLicense, driverLicense, driverName, dispatcherPhoneNumber);
                textCheckbox.setEnabled(false);
                emailCheckbox.setEnabled(false);
                bothCheckbox.setEnabled(false);
                selectText.setVisibility(View.INVISIBLE);
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
                // pass a weak reference?
                Account account = new Account(emailStr, driverNameStr, PhoneNumberFormat.extract(phoneStr), truckNameStr, truckNumberStr,
                        driverLicenseStr, driverStateStr, trailerLicenseStr, trailerStateStr, PhoneNumberFormat.extract(dispatcherNumberStr), Integer.toString(Language.getCurrentLanguage()), Integer.toString(PREFERRED_COMMUNICATION));
                Account.setCurrentAccount(account);
                progressBar.setVisibility(View.VISIBLE);
                new UpdateShippingTruckDriver(account, emailStr,CreateAccount.this).execute();
                selectText.setVisibility(View.INVISIBLE);
                selectText.setVisibility(View.GONE);
            }
        });

        textCheckbox.setOnClickListener(v -> handleChecks(textCheckbox));

        emailCheckbox.setOnClickListener(v -> handleChecks(emailCheckbox));

        bothCheckbox.setOnClickListener(v -> handleChecks(bothCheckbox));

        selectState1.setOnClickListener(v -> {
            ListViewDialog dialog = new ListViewDialog(CreateAccount.this, selectState1, 2);
            dialog.show();
            dialog.setCancelable(false);
        });

        selectState2.setOnClickListener(v -> {
            ListViewDialog dialog = new ListViewDialog(CreateAccount.this, selectState2, 2);
            dialog.show();
            dialog.setCancelable(false);
        });
    }

    public void disableEditTexts(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setEnabled(false);
        }
    }
    public void disableButtons(Button... buttons) {
        for (Button button : buttons) {
            button.setEnabled(false);
        }
    }

    int b = 0;
    public void handleChecks(CheckBox cb) {
        if ((PREFERRED_COMMUNICATION == 1) && (cb.getId() == R.id.TextCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if (PREFERRED_COMMUNICATION == 2 && (cb.getId() == R.id.EmailCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if (PREFERRED_COMMUNICATION == 3 && (cb.getId() == R.id.BothCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        }
        if (cb.getId() == R.id.TextCheckbox) {
            textCheckbox.setClickable(false);
            PREFERRED_COMMUNICATION = 1;
            if (emailCheckbox.isChecked()) {
                emailCheckbox.toggle();
                emailCheckbox.setClickable(true);
            }
            if (bothCheckbox.isChecked()) {
                bothCheckbox.toggle();
                bothCheckbox.setClickable(true);
            }
        }
        if (cb.getId() == R.id.EmailCheckbox) {
            emailCheckbox.setClickable(false);
            PREFERRED_COMMUNICATION = 2;
            if (textCheckbox.isChecked()) {
                textCheckbox.toggle();
                textCheckbox.setClickable(true);
            }
            if (bothCheckbox.isChecked()) {
                bothCheckbox.toggle();
                bothCheckbox.setClickable(true);
            }
        }
        if (cb.getId() == R.id.BothCheckbox) {
            bothCheckbox.setClickable(false);
            PREFERRED_COMMUNICATION = 3;
            if (textCheckbox.isChecked()) {
                textCheckbox.toggle();
                textCheckbox.setClickable(true);
            }
            if (emailCheckbox.isChecked()) {
                emailCheckbox.toggle();
                emailCheckbox.setClickable(true);
            }
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

    private void changeLanguage() {
        truckName.setHintTextColor(getResources().getColor(R.color.dark_gray));
        truckNumber.setHintTextColor(getResources().getColor(R.color.dark_gray));
        trailerLicense.setHintTextColor(getResources().getColor(R.color.dark_gray));
        driverLicense.setHintTextColor(getResources().getColor(R.color.dark_gray));
        driverName.setHintTextColor(getResources().getColor(R.color.dark_gray));
        dispatcherPhoneNumber.setHintTextColor(getResources().getColor(R.color.dark_gray));
        System.out.println("Current Language: " + Language.getCurrentLanguage());
        switch (Language.getCurrentLanguage()) {
            case 1:
                //English
                logoutBtn.setText(R.string.logout_eng);
                nextBtn.setText(R.string.next_eng);
                createAccount.setText(R.string.create_account_eng);
                emailHint.setText(R.string.hint_email_eng);
                phoneHint.setText(R.string.hint_phone_eng);
                truckName.setHint("Truck name");
                truckNameHint.setText(R.string.hint_truck_name_eng);
                truckNumber.setHint("Truck number");
                truckNumberHint.setText(R.string.hint_truck_number_eng);
                trailerLicense.setHint("Trailer license number");
                trailerLicenseHint.setText(R.string.hint_trailer_license_eng);
                driverLicense.setHint("Driver license number");
                driverLicenseHint.setText(R.string.hint_driver_license_eng);
                driverName.setHint("Driver's name");
                driverNameHint.setText(R.string.hint_driver_name_eng);
                dispatcherPhoneNumber.setHint("Dispatcher's phone number");
                dispatcherPhoneHint.setText(R.string.hint_dispatcher_eng);
                verifyText.setText(R.string.verify_next_eng);
                preferText.setText(R.string.comm_preference_eng);
                txtText.setText(R.string.text_msg_eng);
                emailText.setText(R.string.email_eng);
                bothText.setText(R.string.text_and_email_eng);
                selectText.setText(R.string.select_one_eng);
                selectState1.setText(R.string.state_eng);
                selectState2.setText(R.string.state_eng);
                standardRatesApply.setText(R.string.standard_rates_apply_eng);
                break;
            case 2:
                //Spanish
                logoutBtn.setText(R.string.logout_sp);
                nextBtn.setText(R.string.next_sp);
                createAccount.setText(R.string.create_account_sp);
                emailHint.setText(R.string.hint_email_sp);
                phoneHint.setText(R.string.hint_phone_sp);
                truckName.setHint("Nombre del camión");
                truckNameHint.setText(R.string.hint_truck_name_sp);
                truckNumber.setHint("N.º del camión");
                truckNumberHint.setText(R.string.hint_truck_number_sp);
                trailerLicense.setHint("N.º de la matrícula del tráiler");
                trailerLicenseHint.setText(R.string.hint_trailer_license_sp);
                driverLicense.setHint("N.º de licencia de conducir");
                driverLicenseHint.setText(R.string.hint_driver_license_sp);
                driverName.setHint("Nombre del conductor");
                driverNameHint.setText(R.string.hint_driver_name_sp);
                dispatcherPhoneNumber.setHint("N.º de teléfono del despachante");
                dispatcherPhoneHint.setText(R.string.hint_dispatcher_sp);
                verifyText.setText(R.string.verify_next_sp);
                preferText.setText(R.string.comm_preference_sp);
                txtText.setText(R.string.text_msg_sp);
                emailText.setText(R.string.email_sp);
                bothText.setText(R.string.text_and_email_sp);
                selectText.setText(R.string.select_one_sp);
                selectState1.setText(R.string.state_sp);
                selectState2.setText(R.string.state_sp);
                standardRatesApply.setText(R.string.standard_rates_apply_sp);
                break;
            case 3:
                //French
                logoutBtn.setText(R.string.logout_fr);
                nextBtn.setText(R.string.next_fr);
                createAccount.setText(R.string.create_account_fr);
                emailHint.setText(R.string.hint_email_fr);
                phoneHint.setText(R.string.hint_phone_fr);
                truckName.setHint("Nom du camion");
                truckNameHint.setText(R.string.hint_truck_name_fr);
                truckNumber.setHint("Numéro du camion");
                truckNumberHint.setText(R.string.hint_truck_number_fr);
                trailerLicense.setHint("Numéro du permis de la remorque");
                trailerLicenseHint.setText(R.string.hint_trailer_license_fr);
                driverLicense.setHint("Numéro du permis de conduire");
                driverLicenseHint.setText(R.string.hint_driver_license_fr);
                driverName.setHint("Nom du conducteur");
                driverNameHint.setText(R.string.hint_driver_name_fr);
                dispatcherPhoneNumber.setHint("Numéro de téléphone du répartiteur");
                dispatcherPhoneHint.setText(R.string.hint_dispatcher_fr);
                verifyText.setText(R.string.verify_next_fr);
                preferText.setText(R.string.comm_preference_fr);
                txtText.setText(R.string.text_msg_fr);
                emailText.setText(R.string.email_fr);
                bothText.setText(R.string.text_and_email_fr);
                selectText.setText(R.string.select_one_fr);
                selectState1.setText(R.string.state_fr);
                selectState2.setText(R.string.state_fr);
                standardRatesApply.setText(R.string.standard_rates_apply_fr);
                break;
        }
    }

    private void setup() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
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
        trailerStateSpinner = findViewById(R.id.StateSpinner);
        driverStateSpinner = findViewById(R.id.StateSpinner2);
        standardRatesApply = findViewById(R.id.StandardRatesApply);
        progressBar = findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        emailHint = findViewById(R.id.EmailHint);
        phoneHint = findViewById(R.id.PhoneHint);
        driverNameHint = findViewById(R.id.DriverNameHint);
        driverLicenseHint = findViewById(R.id.DriverLicenseHint);
        truckNameHint = findViewById(R.id.TruckNameHint);
        truckNumberHint = findViewById(R.id.TruckNumberHint);
        trailerLicenseHint = findViewById(R.id.TrailerLicenseHint);
        dispatcherPhoneHint = findViewById(R.id.DispatcherHint);
        driverNameHint.setAlpha(0.0f);
        driverLicenseHint.setAlpha(0.0f);
        truckNameHint.setAlpha(0.0f);
        truckNumberHint.setAlpha(0.0f);
        trailerLicenseHint.setAlpha(0.0f);
        dispatcherPhoneHint.setAlpha(0.0f);

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

        changeLanguage();
    }
}