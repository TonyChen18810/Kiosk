package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.kiosk.Account;
import com.example.kiosk.Helpers.KeyboardListener;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.PhoneNumberFormat;
import com.example.kiosk.Helpers.States;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import com.example.kiosk.Webservices.GetShippingTruckDriver;
import java.util.Collections;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;
import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    public String version;

    private EditText emailAddressBox;
    private EditText phoneNumberBox;
    private EditText confirmEmailAddress;
    private EditText confirmPhoneNumber;
    private TextView appointmentText;
    private Button nextBtn;
    private TextView noEmailWarning;
    private TextView noPhoneNumberWarning;
    private TextView unmatchingEmail;
    private TextView unmatchingPhone;
    private boolean expanded = false;
    public ProgressBar progressBar;

    public static MutableLiveData<Boolean> accountExists;

    private View englishCheckbox, spanishCheckbox, frenchCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Account.clearAccounts();
        Account.setCurrentAccount(null);
        MasterOrder.reset();

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        accountExists = new MutableLiveData<>();

        accountExists.observe(MainActivity.this, accountExists -> {
            if (!accountExists) {
                confirmEmailAddress.requestFocus();
                newAccountExpand();
                confirmEmailAddress.requestFocus();
                if (emailAddressBox.getText().toString().equals(confirmEmailAddress.getText().toString())) {
                    setStatus(1, asList(emailAddressBox, confirmEmailAddress), asList(noEmailWarning, unmatchingEmail));
                }
            } else {
                if (expanded) {
                    animation(phoneNumberBox, "translationY", -10f);
                    animation(noPhoneNumberWarning, "translationY", -10f);
                    animation(confirmPhoneNumber, "translationY", -20f);
                    animation(nextBtn, "translationY", -20f);
                    expanded = false;
                    setStatus(-1, asList(emailAddressBox, phoneNumberBox), asList(confirmPhoneNumber, confirmEmailAddress, confirmPhoneNumber,
                            confirmEmailAddress, noEmailWarning, noPhoneNumberWarning, unmatchingEmail, unmatchingPhone));
                    setStatus(1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                } else {
                    setStatus(1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                }
            }
        });

        setup();

        confirmPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        Spinner languageSpinner = findViewById(R.id.LanguageSpinner);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.language_spinner_text);
        languageSpinner.setAdapter(languageAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int CURRENT_LANGUAGE = parent.getSelectedItemPosition();
                Language.setCurrentLanguage(CURRENT_LANGUAGE);
                changeLanguage(CURRENT_LANGUAGE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        languageSpinner.setVisibility(View.INVISIBLE);

        emailAddressBox.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (validEmail()) {
                    progressBar.setVisibility(View.VISIBLE);
                    new GetShippingTruckDriver(MainActivity.this, emailAddressBox.getText().toString().toLowerCase()).execute();
                } else {
                    setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                }
            }
        });

        phoneNumberBox.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!validNumber() && !phoneNumberBox.getText().toString().equals("")) {
                    setStatus(0, asList(phoneNumberBox, confirmPhoneNumber), Collections.singletonList(noPhoneNumberWarning));
                } else if (!phoneNumberBox.getText().toString().equals("") && validNumber() && doesPhoneMatch()) {
                    setStatus(1, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                }
            }
        });

        emailAddressBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setStatus(-1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumberBox.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        confirmPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        phoneNumberBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Account.getCurrentAccount() != null) {
                    if (doesPhoneMatch() && validNumber()) {
                        setStatus(1, asList(phoneNumberBox, confirmPhoneNumber), asList(unmatchingPhone, noPhoneNumberWarning));
                    } else if (count == 13) {
                        if (PhoneNumberFormat.extract(phoneNumberBox.getText().toString()).equals(Account.getCurrentAccount().getPhoneNumber())) {
                            setStatus(1, asList(phoneNumberBox, emailAddressBox), Collections.singletonList(noPhoneNumberWarning));
                        } else {
                            setStatus(0, asList(phoneNumberBox, emailAddressBox), Collections.singletonList(noPhoneNumberWarning));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (doesEmailMatch()) {
                    setStatus(1, asList(emailAddressBox, confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmEmailAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!doesEmailMatch() && emailAddressBox.length() != 0 && confirmEmailAddress.length() != 0) {
                    setStatus(0, asList(emailAddressBox, confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                } else if (confirmEmailAddress.length() == 0) {
                    setStatus(-1, Collections.singletonList(confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                }
            }
        });

        confirmPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!phoneNumberBox.getText().toString().equals(confirmPhoneNumber.getText().toString()) && confirmPhoneNumber.length() != 0 && phoneNumberBox.length() != 0) {
                    setStatus(0, asList(phoneNumberBox, confirmPhoneNumber), Collections.singletonList(unmatchingPhone));
                }
            }
        });

        confirmPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (PhoneNumberFormat.extract(confirmPhoneNumber.getText().toString()).equals(PhoneNumberFormat.extract(phoneNumberBox.getText().toString()))
                        && PhoneNumberFormat.extract(phoneNumberBox.getText().toString()).length() == 10) {
                    setStatus(1, asList(phoneNumberBox, confirmPhoneNumber), asList(unmatchingPhone, noPhoneNumberWarning));
                } else if (PhoneNumberFormat.extract(confirmPhoneNumber.getText().toString()).length() > 10) {
                    setStatus(0, asList(phoneNumberBox, confirmPhoneNumber), Collections.singletonList(unmatchingPhone));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        englishCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
            return true;
        });

        findViewById(R.id.EnglishText).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
            return true;
        });

        spanishCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(frenchCheckbox, englishCheckbox, spanishCheckbox);
            return true;
        });

        findViewById(R.id.SpanishText).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(frenchCheckbox, englishCheckbox, spanishCheckbox);
            return true;
        });

        frenchCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, englishCheckbox, frenchCheckbox);
            return true;
        });

        findViewById(R.id.FrenchText).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, englishCheckbox, frenchCheckbox);
            return true;
        });

        nextBtn.setOnClickListener(v -> {
            // System.out.println(PhoneNumberFormat.extract(phoneNumberBox.getText().toString()));
            if (Account.getCurrentAccount() != null) {
                if (validNumber() && Account.getCurrentAccount().getPhoneNumber().equals(PhoneNumberFormat.extract(phoneNumberBox.getText().toString())) && !expanded) {
                    if (Language.getCurrentLanguage() == 0) {
                        englishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                    } else if (Language.getCurrentLanguage() == 1) {
                        spanishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                    } else if (Language.getCurrentLanguage() == 2) {
                        frenchCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                    }
                    noEmailWarning.setVisibility(View.INVISIBLE);
                    noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                    phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    nextBtn.setEnabled(false);
                    Intent intent = new Intent(MainActivity.this, LoggedIn.class);
                    // Intent intent = new Intent(MainActivity.this, OrderEntry.class);
                    startActivity(intent);
                } else {
                    if (!expanded && !accountExists.getValue()) {
                        setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                    }
                    if (!expanded && !validNumber() || !Account.getCurrentAccount().getPhoneNumber().equals(PhoneNumberFormat.extract(phoneNumberBox.getText().toString()))) {
                        setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                    }
                    if (expanded && !validEmail()) {
                        setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                    }
                    if (expanded && !validNumber()) {
                        setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(phoneNumberBox));
                    }
                    if (expanded && !doesEmailMatch()) {
                        setStatus(0, asList(emailAddressBox, confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                    }
                    if (expanded && !doesPhoneMatch()) {
                        setStatus(0, asList(phoneNumberBox, confirmPhoneNumber), Collections.singletonList(unmatchingPhone));
                    }
                    if (validEmail() && validNumber() && doesEmailMatch() && doesPhoneMatch()) {
                        if (Language.getCurrentLanguage() == 0) {
                            englishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                        } else if (Language.getCurrentLanguage() == 1) {
                            spanishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                        } else if (Language.getCurrentLanguage() == 2) {
                            frenchCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
                        }
                        nextBtn.setEnabled(false);
                        setStatus(1, asList(phoneNumberBox, confirmPhoneNumber), asList(noEmailWarning, noPhoneNumberWarning, unmatchingEmail, unmatchingPhone));
                        Intent createAccountIntent = new Intent(MainActivity.this, CreateAccount.class);
                        createAccountIntent.putExtra("Email Address", emailAddressBox.getText().toString());
                        createAccountIntent.putExtra("Phone Number", PhoneNumberFormat.extract(phoneNumberBox.getText().toString()));
                        startActivity(createAccountIntent);
                    }
                }
            }
        });

    }

    private void setStatus(int status, List<EditText> editTexts, List<TextView> textViews) {
        for (int i = 0; i < editTexts.size(); i++) {
            if (status == 1) {
                editTexts.get(i).getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
            } else if (status == 0){
                editTexts.get(i).getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
            } else if (status == -1){
                editTexts.get(i).getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            }
        }
        for (int i = 0; i < textViews.size(); i++) {
            if (status == 1) {
                textViews.get(i).setVisibility(View.INVISIBLE);
            } else if (status == 0) {
                textViews.get(i).setVisibility(View.VISIBLE);
            } else if (status == -1){
                textViews.get(i).setVisibility(View.INVISIBLE);
            }
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
        if (checkBox[checkBox.length-1] == englishCheckbox) {
            Language.setCurrentLanguage(0);
        } else if (checkBox[checkBox.length-1] == spanishCheckbox) {
            Language.setCurrentLanguage(1);
        } else if (checkBox[checkBox.length-1] == frenchCheckbox) {
            Language.setCurrentLanguage(2);
        }
        changeLanguage(Language.getCurrentLanguage());
    }

    private void animation(Object obj, String propertyName, float value) {
        ObjectAnimator animationEditText = ObjectAnimator.ofFloat(obj, propertyName, value);
        animationEditText.setDuration(1000);
        animationEditText.start();
    }

    public void newAccountExpand() {
        confirmEmailAddress.requestFocus();
        if (!validEmail() && !emailAddressBox.getText().toString().equals("")) {
            setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
        } else if (!emailAddressBox.getText().toString().equals("")){
            setStatus(-1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
            animation(phoneNumberBox, "translationY", 165f);
            animation(noPhoneNumberWarning, "translationY", 165f);
            animation(confirmPhoneNumber, "translationY", 330f);
            animation(nextBtn, "translationY", 320f);
            confirmPhoneNumber.setVisibility(View.VISIBLE);
            confirmEmailAddress.setVisibility(View.VISIBLE);
            expanded = true;
        } else {
            setStatus(1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
        }
    }

    private boolean doesEmailMatch() {
        return emailAddressBox.getText().toString().toLowerCase().equals(confirmEmailAddress.getText().toString().toLowerCase());
    }

    private boolean doesPhoneMatch() {
        return PhoneNumberFormat.extract(phoneNumberBox.getText().toString()).equals(PhoneNumberFormat.extract(confirmPhoneNumber.getText().toString()));
    }

    private boolean validEmail() {
        String email = emailAddressBox.getText().toString().toLowerCase();
        if (email.contains("@")) {
            List<String> extensions = asList(getResources().getStringArray(R.array.extensions));
            for (int i = 0; i < extensions.size(); i++) {
                if (email.endsWith(extensions.get(i).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validNumber() {
        String number = PhoneNumberFormat.extract(phoneNumberBox.getText().toString());
        return (number.length() == 10);
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, SHOW_IMPLICIT);
            }
        }
    }

    private void setup() {
        try {
            PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView versionText = findViewById(R.id.VersionText);
        versionText.setText("Version: " + version);
        emailAddressBox = findViewById(R.id.EmailAddressBox);
        phoneNumberBox = findViewById(R.id.PhoneNumberBox);
        confirmEmailAddress = findViewById(R.id.ConfirmEmailAddress);
        confirmPhoneNumber = findViewById(R.id.ConfirmPhoneNumber);
        appointmentText = findViewById(R.id.AppointmentText);
        nextBtn = findViewById(R.id.NextBtn);
        noEmailWarning = findViewById(R.id.NoEmailWarning);
        noPhoneNumberWarning = findViewById(R.id.NoPhoneNumberWarning);
        unmatchingEmail = findViewById(R.id.UnmatchingEmail);
        unmatchingPhone = findViewById(R.id.UnmatchingPhone);

        englishCheckbox = findViewById(R.id.EnglishCheckbox);
        spanishCheckbox = findViewById(R.id.SpanishCheckbox);
        frenchCheckbox = findViewById(R.id.FrenchCheckbox);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        spanishCheckbox.setPressed(false);
        frenchCheckbox.setPressed(false);
        englishCheckbox.setPressed(true);
        States.setSates(MainActivity.this);

        setStatus(1, Collections.emptyList(), asList(noEmailWarning, noPhoneNumberWarning, unmatchingEmail, unmatchingPhone, confirmEmailAddress, confirmPhoneNumber));

        showSoftKeyboard(emailAddressBox);
    }

    private void changeLanguage(int val) {
        emailAddressBox.setHintTextColor(getResources().getColor(R.color.dark_gray));
        phoneNumberBox.setHintTextColor(getResources().getColor(R.color.dark_gray));
        switch(val) {
            case 0:
                // English
                emailAddressBox.setHint("Email address");
                phoneNumberBox.setHint("Phone number");
                confirmEmailAddress.setHint("Confirm email address");
                confirmPhoneNumber.setHint("Confirm phone number");
                unmatchingEmail.setText(R.string.email_doesnt_match_eng);
                unmatchingPhone.setText(R.string.phone_doesnt_match_eng);
                noEmailWarning.setText(R.string.invalid_email_eng);
                noPhoneNumberWarning.setText(R.string.invalid_phone_eng);
                appointmentText.setText(R.string.appt_required_eng);
                // welcomeText.setText("Welcome to D'Arrigo\nCalifornia");
                // loginText.setText("Log-in");
                nextBtn.setText(R.string.next_eng);

                emailAddressBox.setEms(10);
                phoneNumberBox.setEms(10);
                confirmEmailAddress.setEms(10);
                confirmPhoneNumber.setEms(10);
                break;
            case 1:
                // Spanish
                emailAddressBox.setHint("Dirección de correo electrónico");
                phoneNumberBox.setHint("Número de teléfono");
                confirmEmailAddress.setHint("Confirmar el correo");
                confirmPhoneNumber.setHint("Confirmar número de teléfono");
                unmatchingEmail.setText(R.string.email_doesnt_match_sp);
                unmatchingPhone.setText(R.string.phone_doesnt_match_sp);
                noEmailWarning.setText(R.string.invalid_email_sp);
                noPhoneNumberWarning.setText(R.string.invalid_phone_sp);
                appointmentText.setText(R.string.appt_required_sp);
                // welcomeText.setText("Bienvenido a D'Arrigo\nCalifornia");
                // loginText.setText("Iniciar sesión");
                nextBtn.setText(R.string.next_sp);

                emailAddressBox.setEms(12);
                phoneNumberBox.setEms(12);
                confirmEmailAddress.setEms(12);
                confirmPhoneNumber.setEms(12);
                break;
            case 2:
                // French
                emailAddressBox.setHint("Adresse électronique");
                phoneNumberBox.setHint("Numéro de téléphone");
                confirmEmailAddress.setHint("Confirmez votre adresse email");
                confirmPhoneNumber.setHint("Confirmer le numéro de téléphone");
                unmatchingEmail.setText(R.string.email_doesnt_match_fr);
                unmatchingPhone.setText(R.string.phone_doesnt_match_fr);
                noEmailWarning.setText(R.string.invalid_email_fr);
                noPhoneNumberWarning.setText(R.string.invalid_phone_fr);
                appointmentText.setText(R.string.appt_required_fr);
                // welcomeText.setText("Bienvenue à D'Arrigo\nCalifornia");
                // loginText.setText("S'identifier");
                nextBtn.setText(R.string.next_fr);
                emailAddressBox.setEms(12);
                phoneNumberBox.setEms(12);
                confirmEmailAddress.setEms(12);
                confirmPhoneNumber.setEms(13);
                break;
        }
    }
}