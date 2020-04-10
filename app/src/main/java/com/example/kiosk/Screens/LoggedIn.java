package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.kiosk.Account;
import com.example.kiosk.Dialogs.ListViewDialog;
import com.example.kiosk.Dialogs.LogoutDialog;
import com.example.kiosk.Helpers.KeyboardListener;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.LicenseTransformationMethod;
import com.example.kiosk.Helpers.PhoneNumberFormat;
import com.example.kiosk.Helpers.Time;
import com.example.kiosk.R;
import com.example.kiosk.Webservices.UpdateShippingTruckDriver;
import java.text.ParseException;
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
    private TextView verifyText, preferText, text, email, both, select, emailHint, phoneHint, driverNameHint, driverLicenseHint,
                        truckNameHint, truckNumberHint, trailerLicenseHint, dispatcherHint;
    private View textCheckbox, emailCheckbox, bothCheckbox;
    private Button selectState1, selectState2;
    public static MutableLiveData<Boolean> checkboxListener;
    private static int PREFERRED_COMMUNICATION = -1;
    private Account CURRENT_ACCOUNT = Account.getCurrentAccount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        setup();

        // Time.setTestingTime("07:00:00");

        try {
            Time.setTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        checkboxListener = new MutableLiveData<>();
        checkboxListener.observe(LoggedIn.this, needsUpdated -> {
            if (needsUpdated) {
                setCommunication();
            }
        });

        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        dispatcherPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        logoutBtn.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(LoggedIn.this, v);
            dialog.show();
        });

        nextBtn.setOnClickListener(v -> {
            if (PREFERRED_COMMUNICATION == -1) {
                select.setVisibility(View.VISIBLE);
            } else {
                if (PREFERRED_COMMUNICATION == 0) {
                    textCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                } else if (PREFERRED_COMMUNICATION == 1) {
                    emailCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                } else if (PREFERRED_COMMUNICATION == 2) {
                    bothCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                }
                select.setVisibility(View.GONE);
                String emailStr, phoneStr, truckNameStr, truckNumberStr, trailerLicenseStr, driverLicenseStr, driverNameStr, dispatcherNumberStr;
                emailStr = emailAddress.getText().toString();
                phoneStr = PhoneNumberFormat.extract(phoneNumber.getText().toString());
                truckNameStr = truckName.getText().toString();
                truckNumberStr = truckNumber.getText().toString();
                trailerLicenseStr = trailerLicense.getText().toString();
                driverLicenseStr = driverLicense.getText().toString();
                driverNameStr = driverName.getText().toString();
                dispatcherNumberStr = PhoneNumberFormat.extract(dispatcherPhoneNumber.getText().toString());
                Account.getCurrentAccount().updateCurrentInfo(emailStr, driverNameStr, phoneStr, truckNameStr, truckNumberStr, driverLicenseStr, selectState1.getText().toString(),
                        trailerLicenseStr, selectState2.getText().toString(), dispatcherNumberStr, Integer.toString(Language.getCurrentLanguage()+1), Integer.toString(PREFERRED_COMMUNICATION+1));
                new UpdateShippingTruckDriver(Account.getCurrentAccount()).execute();
                System.out.println("SENDING LANGUAGE PREFERENCE: " + Language.getCurrentLanguage()+1);
                Account.getCurrentAccount().setTruckName(truckNameStr);
                Account.getCurrentAccount().setTruckNumber(truckNumberStr);
                nextBtn.setEnabled(false);
                Intent intent = new Intent(LoggedIn.this, OrderEntry.class);
                startActivity(intent);
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
            ListViewDialog dialog = new ListViewDialog(LoggedIn.this, selectState1, 1);
            dialog.show();
        });

        selectState2.setOnClickListener(v -> {
            ListViewDialog dialog = new ListViewDialog(LoggedIn.this, selectState2, 1);
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
    /**
     * @param checkBox
     * use this function to check the custom communication checkboxes
     * the last checkbox passed as a parameter is the one to be checked
     * all others are unchecked
     */
    private void setChecked(View... checkBox) {
        for (int i = 0; i < checkBox.length; i++) {
            if (i == checkBox.length-1) {
                checkBox[i].setPressed(true);
            } else {
                checkBox[i].setPressed(false);
            }
        }
        if (checkBox[checkBox.length-1] == textCheckbox) {
            PREFERRED_COMMUNICATION = 0;
            Account.getCurrentAccount().setCommunicationPreference("0");
        } else if (checkBox[checkBox.length-1] == emailCheckbox) {
            PREFERRED_COMMUNICATION = 1;
            Account.getCurrentAccount().setCommunicationPreference("1");
        } else if (checkBox[checkBox.length-1] == bothCheckbox) {
            PREFERRED_COMMUNICATION = 2;
            Account.getCurrentAccount().setCommunicationPreference("2");
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
                selectState1.setText(R.string.state_sp);
                selectState2.setText(R.string.state_sp);
                emailHint.setText("Dirección de correo electrónico");
                phoneHint.setText("Número de teléfono");
                driverNameHint.setText("Nombre del conductor");
                driverLicenseHint.setText("Número de licencia de conducir");
                truckNameHint.setText("Nombre del camión");
                truckNumberHint.setText("Numero de camion");
                trailerLicenseHint.setText("Número de licencia de remolque");
                dispatcherHint.setText("Número de teléfono del despachador");
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
        select = findViewById(R.id.SelectText);
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

        ListView driverStateListView = findViewById(R.id.DriverStateListView);
        ListView trailerStateListView = findViewById(R.id.TrailerStateListView);
        android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        driverStateListView.setMinimumHeight((int)(display.getHeight()*0.50));
        trailerStateListView.setMinimumHeight((int)(display.getHeight()*0.50));

        driverStateListView.setVisibility(View.GONE);
        trailerStateListView.setVisibility(View.GONE);

        emailAddress.setEnabled(false);
        phoneNumber.setEnabled(false);
        driverLicense.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        trailerLicense.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        String commPreference = Account.getCurrentAccount().getCommunicationPreference();
        System.out.println("commPreference: " + commPreference);

        TextView userEmail = findViewById(R.id.UserEmail);
        TextView userPhone = findViewById(R.id.UserPhone);
        TextView userTruck = findViewById(R.id.UserTruck);
        userEmail.setText(Account.getCurrentAccount().getEmail());
        userPhone.setText(PhoneNumberFormat.formatPhoneNumber(Account.getCurrentAccount().getPhoneNumber()));
        userTruck.setText(String.format("%s %s", Account.getCurrentAccount().getTruckName(), Account.getCurrentAccount().getTruckNumber()));

        select.setVisibility(View.GONE);

        Spinner trailerStateSpinner = findViewById(R.id.StateSpinner);
        Spinner driverStateSpinner = findViewById(R.id.StateSpinner2);

        trailerStateSpinner.setVisibility(View.INVISIBLE);
        driverStateSpinner.setVisibility(View.INVISIBLE);

        showSoftKeyboard(driverName);
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

        changeLanguage(Language.getCurrentLanguage());
        selectState1.setText(CURRENT_ACCOUNT.getDriverState());
        selectState2.setText(CURRENT_ACCOUNT.getTrailerState());
        PREFERRED_COMMUNICATION = Integer.parseInt(Account.getCurrentAccount().getCommunicationPreference()) - 1;
        Account.getCurrentAccount().setCommunicationPreference(Integer.toString(PREFERRED_COMMUNICATION));
        System.out.println("PREFERRED COMMUNICATION: " + PREFERRED_COMMUNICATION);
        System.out.println("Accounts contact preference: " + Account.getCurrentAccount().getCommunicationPreference());
        setCommunication();
    }
}
