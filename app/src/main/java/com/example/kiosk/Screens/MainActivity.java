package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.kiosk.Account;
import com.example.kiosk.Dialogs.HelpDialog;
import com.example.kiosk.Helpers.KeyboardListener;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.PhoneNumberFormat;
import com.example.kiosk.Helpers.States;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import com.example.kiosk.Webservices.GetShippingTruckDriverThread;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;
import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    private EditText emailAddressBox;
    private EditText phoneNumberBox;
    private EditText confirmEmailAddress;
    private EditText confirmPhoneNumber;
    private TextView appointmentText;
    private Button nextBtn, backBtn;
    private TextView noEmailWarning;
    private TextView noPhoneNumberWarning;
    private TextView unmatchingEmail;
    private TextView unmatchingPhone;
    private TextView accountAlreadyExists;
    public ProgressBar progressBar;
    private boolean newAccount;

    public static MutableLiveData<Boolean> accountExists;

    private View englishCheckbox, spanishCheckbox, frenchCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String accountStatus = getIntent().getExtras().getString("accountStatus");
        System.out.println(accountStatus);
        setup();

        if (accountStatus.equals("new")) {
            newAccount = true;
            newAccountExpand();
        } else if (accountStatus.equals("exists")) {
            newAccount = false;
        }


/*
        accountExists = new MutableLiveData<>();

        accountExists.observe(MainActivity.this, accountExists -> {
            if (!accountExists && !expanded) {
                // confirmEmailAddress.requestFocus();
                // newAccountExpand();
                // confirmEmailAddress.requestFocus();
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
                    phoneNumberBox.requestFocus();
                } else {
                    setStatus(1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                    phoneNumberBox.requestFocus();
                }
            }
        });
*/
        confirmPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        emailAddressBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (emailAddressBox.length() != 0) {
                    if (validEmail() && validNumber() && !newAccount) {
                        nextBtn.setEnabled(true);
                    } else if (!newAccount) {
                        nextBtn.setEnabled(false);
                    }
                }
                if (validEmail()) {
                    setStatus(-1, Collections.singletonList(emailAddressBox), asList(noEmailWarning, accountAlreadyExists));
                }
                if (newAccount) {
                    if (emailAddressBox.length() != 0 && confirmEmailAddress.length() != 0 && phoneNumberBox.length() != 0
                            && confirmPhoneNumber.length() != 0 && validEmail() && validNumber()) {
                        nextBtn.setEnabled(true);
                    }
                    if (doesEmailMatch()) {
                        setStatus(-1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailAddressBox.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!validEmail() && emailAddressBox.length() > 0) {
                    setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                }
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
                if (phoneNumberBox.length() > 13) {
                    setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                } else if (validEmail() && !newAccount){
                    setStatus(-1, asList(emailAddressBox, phoneNumberBox), asList(noEmailWarning, noPhoneNumberWarning));
                }
                if (validEmail() && validNumber() && !newAccount) {
                    nextBtn.setEnabled(true);
                } else {
                    nextBtn.setEnabled(false);
                }
                if (newAccount) {
                    if (emailAddressBox.length() != 0 && confirmEmailAddress.length() != 0 && phoneNumberBox.length() != 0
                            && confirmPhoneNumber.length() != 0 && validEmail() && validNumber()) {
                        nextBtn.setEnabled(true);
                    }
                }
                if (validNumber()) {
                    setStatus(-1, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumberBox.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (newAccount) {
                    if (!validNumber() && phoneNumberBox.length() > 0) {
                        setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                    }
                } else {
                    if (phoneNumberBox.length() > 0 && phoneNumberBox.length() != 13) {
                        setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                    }
                }
            }
        });

        confirmEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (validEmail() && doesEmailMatch()) {
                    setStatus(-1, asList(emailAddressBox, confirmEmailAddress), asList(noEmailWarning, unmatchingEmail));
                }
                if (doesEmailMatch()) {
                    setStatus(-1, Collections.singletonList(confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                }
                if (newAccount) {
                    if (emailAddressBox.length() != 0 && confirmEmailAddress.length() != 0 && phoneNumberBox.length() != 0
                            && confirmPhoneNumber.length() != 0 && validEmail() && validNumber()) {
                        nextBtn.setEnabled(true);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmEmailAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!doesEmailMatch()) {
                    setStatus(0, asList(emailAddressBox, confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                }
            }
        });

        confirmPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (validNumber() && doesPhoneMatch()) {
                    setStatus(-1, asList(phoneNumberBox, confirmPhoneNumber), asList(noPhoneNumberWarning, unmatchingPhone));
                }
                if (newAccount) {
                    if (emailAddressBox.length() != 0 && confirmEmailAddress.length() != 0 && phoneNumberBox.length() != 0
                            && confirmPhoneNumber.length() != 0 && validEmail() && validNumber()) {
                        nextBtn.setEnabled(true);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!doesPhoneMatch()) {
                    setStatus(0, Collections.singletonList(confirmPhoneNumber), Collections.singletonList(unmatchingPhone));
                }
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

        backBtn.setOnClickListener(v -> {
            if (Language.getCurrentLanguage() == 0) {
                englishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            } else if (Language.getCurrentLanguage() == 1) {
                spanishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            } else if (Language.getCurrentLanguage() == 2) {
                frenchCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            }
            startActivity(new Intent(MainActivity.this, FirstScreen.class));
        });

        nextBtn.setOnClickListener(v -> {
            nextBtn.setEnabled(false);
            backBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            if (!newAccount) {
                AtomicBoolean existingAccount = new AtomicBoolean(false);
                Thread thread = new Thread(() -> existingAccount.set(new GetShippingTruckDriverThread(MainActivity.this, emailAddressBox.getText().toString().toLowerCase(), PhoneNumberFormat.extract(phoneNumberBox.getText().toString())).call()));
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Here's our AtomicBoolean: " + existingAccount.get());
                if (existingAccount.get()) {
                    setStatus(1, asList(emailAddressBox, phoneNumberBox), asList(noEmailWarning, noPhoneNumberWarning));
                    startActivity(new Intent(MainActivity.this, LoggedIn.class));
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (!existingAccount.get()){
                    progressBar.setVisibility(View.INVISIBLE);
                    setStatus(0, asList(emailAddressBox, phoneNumberBox), asList(noEmailWarning, noPhoneNumberWarning));
                }
            } else if (newAccount) {
                if (validNumber() && validEmail() && doesEmailMatch() && doesPhoneMatch()) {
                    AtomicBoolean existingAccount = new AtomicBoolean(false);
                    Thread thread = new Thread(() -> existingAccount.set(new GetShippingTruckDriverThread(MainActivity.this, emailAddressBox.getText().toString().toLowerCase(), PhoneNumberFormat.extract(phoneNumberBox.getText().toString())).call()));
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Here's our AtomicBoolean: " + existingAccount.get());
                    if (existingAccount.get()) {
                        // Account already exists, if this is you please go back and log in.
                        System.out.println("Account already exists, if this is you please go back and log in.");
                        setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(accountAlreadyExists));
                        confirmEmailAddress.setText("");
                        confirmPhoneNumber.setText("");
                        emailAddressBox.requestFocus();
                        progressBar.setVisibility(View.INVISIBLE);
                    } else if (!existingAccount.get()){
                        nextBtn.setEnabled(false);
                        setStatus(1, asList(emailAddressBox, confirmEmailAddress, phoneNumberBox, confirmPhoneNumber), asList(noEmailWarning, noPhoneNumberWarning, unmatchingEmail, unmatchingPhone));
                        Intent createAccountIntent = new Intent(MainActivity.this, CreateAccount.class);
                        createAccountIntent.putExtra("Email Address", emailAddressBox.getText().toString());
                        createAccountIntent.putExtra("Phone Number", PhoneNumberFormat.extract(phoneNumberBox.getText().toString()));
                        startActivity(createAccountIntent);
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (!validEmail()){
                        setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                    } else {
                        setStatus(-1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                    }
                    if (!validNumber()) {
                        setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                    } else {
                        setStatus(-1, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                    }
                    if (!doesEmailMatch()) {
                        setStatus(0, asList(emailAddressBox, confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                    } else {
                        setStatus(-1, asList(emailAddressBox, confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                    }
                    if (!doesPhoneMatch()) {
                        setStatus(0, asList(phoneNumberBox, confirmPhoneNumber), Collections.singletonList(unmatchingPhone));
                    } else {
                        setStatus(-1, asList(phoneNumberBox, confirmPhoneNumber), Collections.singletonList(unmatchingPhone));
                    }
                }
            }
            nextBtn.setEnabled(true);
            backBtn.setEnabled(true);
        });

/*
        nextBtn.setOnClickListener(v -> {
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
                        setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                    }
                    if (expanded && !doesEmailMatch()) {
                        setStatus(0, asList(emailAddressBox, confirmEmailAddress), Collections.singletonList(unmatchingEmail));
                    }
                    if (expanded && !doesPhoneMatch()) {
                        setStatus(0, asList(phoneNumberBox, confirmPhoneNumber), Collections.singletonList(unmatchingPhone));
                        if (phoneNumberBox.length() == 13) {
                            setStatus(-1, Collections.emptyList(), Collections.singletonList(noPhoneNumberWarning));
                        }
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
*/
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
        // was 1000
        animationEditText.setDuration(1);
        animationEditText.start();
    }

    public void newAccountExpand() {
        // confirmEmailAddress.requestFocus();
        setStatus(-1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
        animation(phoneNumberBox, "translationY", 165f);
        animation(noPhoneNumberWarning, "translationY", 165f);
        animation(confirmPhoneNumber, "translationY", 330f);
        animation(nextBtn, "translationY", 320f);
        animation(backBtn, "translationY", 320f);
        AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
        // was 1000
        fade.setDuration(1);
        confirmPhoneNumber.startAnimation(fade);
        confirmEmailAddress.startAnimation(fade);
        confirmPhoneNumber.setVisibility(View.VISIBLE);
        confirmEmailAddress.setVisibility(View.VISIBLE);
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

        emailAddressBox = findViewById(R.id.EmailAddressBox);
        phoneNumberBox = findViewById(R.id.PhoneNumberBox);
        confirmEmailAddress = findViewById(R.id.ConfirmEmailAddress);
        confirmPhoneNumber = findViewById(R.id.ConfirmPhoneNumber);
        appointmentText = findViewById(R.id.AppointmentText);
        nextBtn = findViewById(R.id.NextBtn);
        backBtn = findViewById(R.id.BackBtn);
        noEmailWarning = findViewById(R.id.NoEmailWarning);
        noPhoneNumberWarning = findViewById(R.id.NoPhoneNumberWarning);
        unmatchingEmail = findViewById(R.id.UnmatchingEmail);
        unmatchingPhone = findViewById(R.id.UnmatchingPhone);
        accountAlreadyExists = findViewById(R.id.AccountAlreadyExists);

        englishCheckbox = findViewById(R.id.EnglishCheckbox);
        spanishCheckbox = findViewById(R.id.SpanishCheckbox);
        frenchCheckbox = findViewById(R.id.FrenchCheckbox);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        nextBtn.setEnabled(false);

        spanishCheckbox.setPressed(false);
        frenchCheckbox.setPressed(false);
        englishCheckbox.setPressed(true);
        States.setSates(MainActivity.this);

        setStatus(1, Collections.emptyList(), asList(noEmailWarning, noPhoneNumberWarning, unmatchingEmail, unmatchingPhone, confirmEmailAddress, confirmPhoneNumber, accountAlreadyExists));

        showSoftKeyboard(emailAddressBox);
        changeLanguage(Language.getCurrentLanguage());
        if (Language.getCurrentLanguage() == 0) {
            setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
        } else if (Language.getCurrentLanguage() == 1) {
            setChecked(englishCheckbox, frenchCheckbox, spanishCheckbox);
        } else if (Language.getCurrentLanguage() == 2) {
            setChecked(spanishCheckbox, englishCheckbox, frenchCheckbox);
        }
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
                backBtn.setText(R.string.back_eng);
                accountAlreadyExists.setText(R.string.account_already_exists_eng);
                // loginBtn.setText(R.string.log_in_eng);
                // createAccountBtn.setText(R.string.create_account_eng);
                /*
                emailAddressBox.setEms(10);
                phoneNumberBox.setEms(10);
                confirmEmailAddress.setEms(10);
                confirmPhoneNumber.setEms(10);
                 */
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
                nextBtn.setText("Siguente");
                backBtn.setText(R.string.back_sp);
                accountAlreadyExists.setText(R.string.account_already_exists_sp);
                // loginBtn.setText(R.string.log_in_sp);
                // createAccountBtn.setText(R.string.create_account_sp);
                /*
                emailAddressBox.setEms(12);
                phoneNumberBox.setEms(12);
                confirmEmailAddress.setEms(12);
                confirmPhoneNumber.setEms(12);
                 */
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
                backBtn.setText(R.string.back_fr);
                accountAlreadyExists.setText(R.string.account_already_exists_fr);
                // loginBtn.setText(R.string.log_in_fr);
                // createAccountBtn.setText(R.string.create_account_fr);
                /*
                emailAddressBox.setEms(12);
                phoneNumberBox.setEms(12);
                confirmEmailAddress.setEms(12);
                confirmPhoneNumber.setEms(13);
                 */
                break;
        }
    }
}