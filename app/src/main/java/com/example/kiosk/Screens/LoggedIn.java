package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kiosk.Account;
import com.example.kiosk.Dialogs.LogoutDialog;
import com.example.kiosk.Helpers.KeyboardListener;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.LicenseTransformationMethod;
import com.example.kiosk.Helpers.Time;
import com.example.kiosk.R;

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
    private TextView text;
    private TextView email;
    private TextView both;
    private TextView select;
    private View textCheckbox, emailCheckbox, bothCheckbox;

    private Button selectState1, selectState2;
    private String state1, state2;
    private boolean initialSelection1 = false;
    private boolean initialSelection2 = false;

    private int PREFERRED_COMMUNICATION = -1;
    private Account CURRENT_ACCOUNT = Account.getCurrentAccount();

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

        setup();

        Time.setTime();

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
                        trailerStateStr, driverLicenseStr, driverStateStr, driverNameStr, dispatcherNumberStr, "0", Integer.toString(PREFERRED_COMMUNICATION+1));
                Account.setCurrentAccount(account);
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

        selectState1.setOnClickListener(v -> trailerStateSpinner.performClick());

        selectState2.setOnClickListener(v -> driverStateSpinner.performClick());
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
            PREFERRED_COMMUNICATION = 0;
        } else if (checkBox[checkBox.length-1] == emailCheckbox) {
            PREFERRED_COMMUNICATION = 1;
        } else if (checkBox[checkBox.length-1] == bothCheckbox) {
            PREFERRED_COMMUNICATION = 2;
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

        TextView userEmail = findViewById(R.id.UserEmail);
        TextView userPhone = findViewById(R.id.UserPhone);
        TextView userTruck = findViewById(R.id.UserTruck);
        userEmail.setText(Account.getCurrentAccount().getEmail());
        userPhone.setText(Account.getCurrentAccount().getPhoneNumber());
        userTruck.setText(String.format("%s %s", Account.getCurrentAccount().getTruckName(), Account.getCurrentAccount().getTruckNumber()));

        select.setVisibility(View.GONE);

        trailerStateSpinner = findViewById(R.id.StateSpinner);
        driverStateSpinner = findViewById(R.id.StateSpinner2);

        trailerStateSpinner.setVisibility(View.INVISIBLE);
        driverStateSpinner.setVisibility(View.INVISIBLE);

        showSoftKeyboard(truckName);
        dispatcherPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        emailAddress.setTextColor(getResources().getColor(R.color.black));
        phoneNumber.setTextColor(getResources().getColor(R.color.black));

        emailAddress.setText(CURRENT_ACCOUNT.getEmail());
        phoneNumber.setText(CURRENT_ACCOUNT.getPhoneNumber());
        truckName.setText(CURRENT_ACCOUNT.getTruckName());
        truckNumber.setText(CURRENT_ACCOUNT.getTruckNumber());
        trailerLicense.setText(CURRENT_ACCOUNT.getTrailerLicense());
        selectState1.setText(CURRENT_ACCOUNT.getDriverState());
        selectState2.setText(CURRENT_ACCOUNT.getTrailerState());
        driverLicense.setText(CURRENT_ACCOUNT.getDriverLicense());
        driverName.setText(CURRENT_ACCOUNT.getDriverName());
        dispatcherPhoneNumber.setText(CURRENT_ACCOUNT.getDispatcherPhoneNumber());
        state1 = CURRENT_ACCOUNT.getDriverState();
        state2 = CURRENT_ACCOUNT.getTrailerState();

        changeLanguage(currentLanguage);
    }
}
