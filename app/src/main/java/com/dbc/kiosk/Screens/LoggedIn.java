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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Dialogs.ListViewDialog;
import com.dbc.kiosk.Dialogs.LogoutDialog;
import com.dbc.kiosk.Helpers.KeyboardListener;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.Helpers.LicenseTransformationMethod;
import com.dbc.kiosk.Helpers.PhoneNumberFormat;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Report;
import com.dbc.kiosk.Settings;
import com.dbc.kiosk.Webservices.CheckForExistingAccount;
import com.dbc.kiosk.Webservices.GetServerTime;
import com.dbc.kiosk.Webservices.UpdateShippingTruckDriver;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.Collections;
import java.util.List;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;
/**
 * CreateAccount.java
 *
 * User has the opportunity to update any information that has changed
 * on their account since the last time they logged in
 *
 * This activity is started when the user selects "Next" in MainActivity.java
 * while there is no confirm fields and only "Email Address" and "Phone Number" fields
 *
 * Update account information, calls UpdateShippingTruckDriver.java when "Next" is pressed
 */
public class LoggedIn extends AppCompatActivity {

    private Button logoutBtn;
    private Button nextBtn;
    private TextView loggedInText;
    private EditText emailAddress, phoneNumber, truckName, truckNumber, trailerLicense, driverLicense, driverName, dispatcherPhoneNumber;
    private TextView verifyText, preferText, text, email, both, standardRatesApply, emailHint, phoneHint, driverNameHint, driverLicenseHint,
                        truckNameHint, truckNumberHint, trailerLicenseHint, dispatcherHint, phoneInUseWarning;
    private String emailStr, phoneStr, truckNameStr, truckNumberStr, trailerLicenseStr, driverLicenseStr, driverNameStr, dispatcherNumberStr;
    private CheckBox textCheckbox, emailCheckbox, bothCheckbox;
    private Button selectState1, selectState2;
    private ProgressBar progressBar;

    public static MutableLiveData<Boolean> checkboxListener;
    public static MutableLiveData<Integer> phoneListener;
    private static int PREFERRED_COMMUNICATION = -1;
    private Account CURRENT_ACCOUNT = Account.getCurrentAccount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Report report = new Report(this);
        report.setDriverTags();
        setContentView(R.layout.activity_logged_in);
        setup();
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "User logged in");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
        System.out.println("Kiosk number: " + Settings.getKioskNumber());
        System.out.println("Cooler location: " + Settings.getCoolerLocation());

        // Records time at which user logged in (used for comparing to
        // entered order appointment times to check for late/early/on-time)
        new GetServerTime().execute();

        emailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setStatus(-1, Collections.singletonList(emailAddress));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setStatus(-1, Collections.singletonList(phoneNumber));
                phoneInUseWarning.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        dispatcherPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        logoutBtn.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(LoggedIn.this, LoggedIn.this);
            dialog.show();
            dialog.setCancelable(false);
        });

        phoneListener = new MutableLiveData<>();

        phoneListener.observe(LoggedIn.this, integer -> {
            if (integer == 0) {
                // in use
                System.out.println("Phone in use...");
                setStatus(0, Collections.singletonList(phoneNumber));
                phoneInUseWarning.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                enableObjects(nextBtn, logoutBtn, phoneNumber, driverName, driverLicense, selectState1, truckName, truckNumber,
                        trailerLicense, selectState2, dispatcherPhoneNumber, textCheckbox, emailCheckbox, bothCheckbox);
            } else if (integer == 1) {
                // good phone
                System.out.println("Good phone...");
                setStatus(-1, Collections.singletonList(phoneNumber));
                phoneInUseWarning.setVisibility(View.GONE);
                System.out.println("Start next activity...");
                Account.getCurrentAccount().updateCurrentInfo(emailStr, driverNameStr, phoneStr, truckNameStr, truckNumberStr, driverLicenseStr, selectState1.getText().toString(),
                        trailerLicenseStr, selectState2.getText().toString(), dispatcherNumberStr, Integer.toString(Language.getCurrentLanguage()), Integer.toString(PREFERRED_COMMUNICATION));
                progressBar.setVisibility(View.VISIBLE);
                new UpdateShippingTruckDriver(Account.getCurrentAccount(), emailStr, LoggedIn.this).execute();
            }
        });

        nextBtn.setOnClickListener(v -> {
            disableObjects(nextBtn, logoutBtn, phoneNumber, driverName, driverLicense, selectState1, truckName, truckNumber,
                    trailerLicense, selectState2, dispatcherPhoneNumber, textCheckbox, emailCheckbox, bothCheckbox);
            emailStr = emailAddress.getText().toString();
            phoneStr = PhoneNumberFormat.extract(phoneNumber.getText().toString());
            truckNameStr = truckName.getText().toString();
            truckNumberStr = truckNumber.getText().toString();
            trailerLicenseStr = trailerLicense.getText().toString();
            driverLicenseStr = driverLicense.getText().toString();
            driverNameStr = driverName.getText().toString();
            dispatcherNumberStr = PhoneNumberFormat.extract(dispatcherPhoneNumber.getText().toString());
            System.out.println("Next button pressed");
            if (!Account.getCurrentAccount().getEmail().toLowerCase().equals(emailAddress.getText().toString().toLowerCase()) || !Account.getCurrentAccount().getPhoneNumber().equals(phoneStr)) {
                progressBar.setVisibility(View.VISIBLE);
                if (!Account.getCurrentAccount().getPhoneNumber().equals(phoneStr)) {
                    System.out.println("Phone changed...");
                    new CheckForExistingAccount(LoggedIn.this, phoneStr, 1, false).execute();
                } else {
                    phoneListener.setValue(1);
                }
            } else if (Account.getCurrentAccount().getEmail().toLowerCase().equals(emailAddress.getText().toString().toLowerCase()) && Account.getCurrentAccount().getPhoneNumber().equals(phoneStr)) {
                System.out.println("Phone was not changed...");
                String oldEmail = Account.getCurrentAccount().getEmail();
                Account.getCurrentAccount().updateCurrentInfo(emailStr, driverNameStr, phoneStr, truckNameStr, truckNumberStr, driverLicenseStr, selectState1.getText().toString(),
                        trailerLicenseStr, selectState2.getText().toString(), dispatcherNumberStr, Integer.toString(Language.getCurrentLanguage()), Integer.toString(PREFERRED_COMMUNICATION));
                progressBar.setVisibility(View.VISIBLE);
                new UpdateShippingTruckDriver(Account.getCurrentAccount(), oldEmail, LoggedIn.this).execute();
            }
        });

        textCheckbox.setOnClickListener(v -> handleChecks(textCheckbox));

        emailCheckbox.setOnClickListener(v -> handleChecks(emailCheckbox));

        bothCheckbox.setOnClickListener(v -> handleChecks(bothCheckbox));

        selectState1.setOnClickListener(v -> {
            ListViewDialog dialog = new ListViewDialog(LoggedIn.this, selectState1, 1);
            dialog.show();
            dialog.setCancelable(false);
        });

        selectState2.setOnClickListener(v -> {
            ListViewDialog dialog = new ListViewDialog(LoggedIn.this, selectState2, 1);
            dialog.show();
            dialog.setCancelable(false);
        });
    }

    int b = 0;
    public void handleChecks(CheckBox cb) {
        if ((Account.getCurrentAccount().getCommunicationPreference().equals("1")) && (cb.getId() == R.id.TextCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if ((Account.getCurrentAccount().getCommunicationPreference().equals("2")) && (cb.getId() == R.id.EmailCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if ((Account.getCurrentAccount().getCommunicationPreference().equals("3")) && (cb.getId() == R.id.BothCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        }
        if (cb.getId() == R.id.TextCheckbox) {
            textCheckbox.setClickable(false);
            Account.getCurrentAccount().setCommunicationPreference("1");
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
            Account.getCurrentAccount().setCommunicationPreference("2");
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
            Account.getCurrentAccount().setCommunicationPreference("3");
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

    // used to enable all of the EditTexts/Buttons/Checkboxes
    public void enableObjects(TextView... textViews) {
        for (TextView object : textViews) {
            object.setEnabled(true);
        }
    }
    // used to disable all of the EditTexts/Buttons/Checkboxes
    public void disableObjects(TextView ... textViews) {
        for (TextView object : textViews) {
            object.setEnabled(false);
        }
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
            case 1:
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
                verifyText.setText(R.string.verify_next_eng);
                preferText.setText(R.string.comm_preference_eng);
                text.setText(R.string.text_msg_eng);
                email.setText(R.string.email_eng);
                both.setText(R.string.text_and_email_eng);
                standardRatesApply.setText(R.string.standard_rates_apply_eng);
                selectState1.setText(R.string.state_eng);
                selectState2.setText(R.string.state_eng);
                emailHint.setText(R.string.hint_email_eng);
                phoneHint.setText(R.string.hint_phone_eng);
                driverNameHint.setText(R.string.hint_driver_name_eng);
                driverLicenseHint.setText(R.string.hint_driver_license_eng);
                truckNameHint.setText(R.string.hint_truck_name_eng);
                truckNumberHint.setText(R.string.hint_truck_number_eng);
                trailerLicenseHint.setText(R.string.hint_trailer_license_eng);
                dispatcherHint.setText(R.string.hint_dispatcher_eng);
                break;
            case 2:
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
                verifyText.setText(R.string.verify_next_sp);
                preferText.setText(R.string.comm_preference_sp);
                text.setText(R.string.text_msg_sp);
                email.setText(R.string.email_sp);
                both.setText(R.string.text_and_email_sp);
                standardRatesApply.setText(R.string.standard_rates_apply_sp);
                selectState1.setText(R.string.state_sp);
                selectState2.setText(R.string.state_sp);
                emailHint.setText("Dirección de email");
                phoneHint.setText("Número de teléfono");
                driverNameHint.setText("Nombre del conductor");
                driverLicenseHint.setText("Número de licencia de conducir");
                truckNameHint.setText("Nombre del camión");
                truckNumberHint.setText("Número  de camión");
                trailerLicenseHint.setText("Número de matrícula del tráiler");
                dispatcherHint.setText("Número de teléfono del despachador");
                break;
            case 3:
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
                verifyText.setText(R.string.verify_next_fr);
                preferText.setText(R.string.comm_preference_fr);
                text.setText(R.string.text_msg_fr);
                email.setText(R.string.email_fr);
                both.setText(R.string.text_and_email_fr);
                standardRatesApply.setText(R.string.standard_rates_apply_fr);
                selectState1.setText(R.string.state_fr);
                selectState2.setText(R.string.state_fr);
                emailHint.setText("Adresse électronique");
                phoneHint.setText("Numéro de téléphone");
                driverNameHint.setText("Nom du conducteur");
                driverLicenseHint.setText("Numéro de permis de conduire");
                truckNameHint.setText("Nom du camion");
                truckNumberHint.setText("Numéro de camion");
                trailerLicenseHint.setText("Numéro de licence de la remorque");
                dispatcherHint.setText("Numéro de téléphone du répartiteur");
                break;
        }
    }

    private void setup() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        logoutBtn = findViewById(R.id.LogoutBtn);
        nextBtn = findViewById(R.id.NextBtn);
        loggedInText = findViewById(R.id.LoggedInText);
        emailAddress = findViewById(R.id.EmailAddressBox);
        phoneNumber = findViewById(R.id.PhoneNumberBox);
        truckName = findViewById(R.id.TruckNameBox);
        truckNumber = findViewById(R.id.TruckNumberBox);
        trailerLicense = findViewById(R.id.TrailerLicenseBox);
        driverLicense = findViewById(R.id.DriverLicenseBox);
        driverLicense.setTransformationMethod(new LicenseTransformationMethod());
        driverName = findViewById(R.id.DriverNameBox);
        dispatcherPhoneNumber = findViewById(R.id.DispatcherPhoneNumberBox);
        verifyText = findViewById(R.id.VerifyText);
        text = findViewById(R.id.Text);
        email = findViewById(R.id.Email);
        both = findViewById(R.id.Both);
        textCheckbox = findViewById(R.id.TextCheckbox);
        emailCheckbox = findViewById(R.id.EmailCheckbox);
        bothCheckbox = findViewById(R.id.BothCheckbox);
        standardRatesApply = findViewById(R.id.SelectText);
        preferText = findViewById(R.id.PreferInfoText);
        selectState1 = findViewById(R.id.StateButton1);
        selectState2 = findViewById(R.id.StateButton2);
        // Field Hints
        emailHint = findViewById(R.id.EmailHint);
        phoneHint = findViewById(R.id.PhoneHint);
        driverNameHint = findViewById(R.id.DriverNameHint);
        driverLicenseHint = findViewById(R.id.DriverLicenseHint);
        truckNameHint = findViewById(R.id.TruckNameHint);
        truckNumberHint = findViewById(R.id.TruckNumberHint);
        trailerLicenseHint = findViewById(R.id.TrailerLicenseHint);
        dispatcherHint = findViewById(R.id.DispatcherHint);

        phoneInUseWarning = findViewById(R.id.PhoneInUseWarning);
        phoneInUseWarning.setVisibility(View.GONE);

        progressBar = findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.GONE);

        ListView driverStateListView = findViewById(R.id.DriverStateListView);
        ListView trailerStateListView = findViewById(R.id.TrailerStateListView);
        android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        driverStateListView.setMinimumHeight((int)(display.getHeight()*0.50));
        trailerStateListView.setMinimumHeight((int)(display.getHeight()*0.50));

        driverStateListView.setVisibility(View.GONE);
        trailerStateListView.setVisibility(View.GONE);

        // emailAddress.setEnabled(false);
        // phoneNumber.setEnabled(false);
        driverLicense.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(20)});
        trailerLicense.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(20)});

        String commPreference = Account.getCurrentAccount().getCommunicationPreference();
        System.out.println("commPreference: " + commPreference);

        TextView userEmail = findViewById(R.id.UserEmail);
        TextView userPhone = findViewById(R.id.UserPhone);
        TextView userTruck = findViewById(R.id.UserTruck);
        userEmail.setText(Account.getCurrentAccount().getEmail());
        userPhone.setText(PhoneNumberFormat.formatPhoneNumber(Account.getCurrentAccount().getPhoneNumber()));
        userTruck.setText(String.format("%s %s", Account.getCurrentAccount().getTruckName(), Account.getCurrentAccount().getTruckNumber()));

        showSoftKeyboard(truckName);
        dispatcherPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        emailAddress.setTextColor(getResources().getColor(R.color.black));
        phoneNumber.setTextColor(getResources().getColor(R.color.black));

        emailAddress.setText(CURRENT_ACCOUNT.getEmail());
        phoneNumber.setText(PhoneNumberFormat.formatPhoneNumber(CURRENT_ACCOUNT.getPhoneNumber()));
        truckName.setText(CURRENT_ACCOUNT.getTruckName());
        truckNumber.setText(CURRENT_ACCOUNT.getTruckNumber());
        trailerLicense.setText(CURRENT_ACCOUNT.getTrailerLicense());
        selectState1.setText(CURRENT_ACCOUNT.getDriverState());
        selectState2.setText(CURRENT_ACCOUNT.getTrailerState());
        driverLicense.setText(CURRENT_ACCOUNT.getDriverLicense());
        driverName.setText(CURRENT_ACCOUNT.getDriverName());
        dispatcherPhoneNumber.setText(PhoneNumberFormat.formatPhoneNumber(CURRENT_ACCOUNT.getDispatcherPhoneNumber()));

        emailAddress.setEnabled(false);

        changeLanguage(Language.getCurrentLanguage());
        selectState1.setText(CURRENT_ACCOUNT.getDriverState());
        selectState2.setText(CURRENT_ACCOUNT.getTrailerState());
        PREFERRED_COMMUNICATION = Integer.parseInt(Account.getCurrentAccount().getCommunicationPreference());
        Account.getCurrentAccount().setCommunicationPreference(Integer.toString(PREFERRED_COMMUNICATION));
        System.out.println("PREFERRED COMMUNICATION: " + PREFERRED_COMMUNICATION);
        System.out.println("Accounts contact preference: " + Account.getCurrentAccount().getCommunicationPreference());

        if (Account.getCurrentAccount().getCommunicationPreference().equals("1")) {
            textCheckbox.performClick();
        } else if (Account.getCurrentAccount().getCommunicationPreference().equals("2")) {
            emailCheckbox.performClick();
        } else if (Account.getCurrentAccount().getCommunicationPreference().equals("3")) {
            bothCheckbox.performClick();
        }
    }
}
