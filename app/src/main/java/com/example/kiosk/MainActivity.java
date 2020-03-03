package com.example.kiosk;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;


public class MainActivity extends AppCompatActivity {

    private static int currentLanguage = Language.getCurrentLanguage();

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

    private View englishCheckbox, spanishCheckbox, frenchCheckbox;

    private static Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setup();

        confirmPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        Account kyleAccount = new Account("kyle@gmail.com", "8315885534", "Kyle's Truck",
                "57", "5WHA67V", "California", "F3342376",
                "Arizona", "Kyle Gilbert", "4083675954");
        Account testAccount = new Account("bob@gmail.com", "8319345883", "Bob's Truck",
                "57", "5WHA67V", "California", "F3342376",
                "Arizona", "Bob John", "4083675954");
        Account.addAccount(kyleAccount);
        Account.addAccount(testAccount);

        ArrayList<Account> temp;
        temp = Account.getAccounts();
        for (int i = 0; i < temp.size(); i++) {
            System.out.println(temp.get(i).getEmail());
        }

        Spinner languageSpinner = findViewById(R.id.LanguageSpinner);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.language_spinner_text);
        languageSpinner.setPrompt("Language/Idioma/Langue");
        languageSpinner.setAdapter(languageAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentLanguage = parent.getSelectedItemPosition();
                Language.setCurrentLanguage(currentLanguage);
                changeLanguage(currentLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        languageSpinner.setVisibility(View.INVISIBLE);

        confirmEmailAddress.setVisibility(View.INVISIBLE);
        confirmPhoneNumber.setVisibility(View.INVISIBLE);

        emailAddressBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (accountExists()) {
                        noEmailWarning.setVisibility(View.INVISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    } else if (!validEmail() && !emailAddressBox.getText().toString().equals("")) {
                        noEmailWarning.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    } else if (!emailAddressBox.getText().toString().equals("")){
                        noEmailWarning.setVisibility(View.INVISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                        // check here to see if email is in database
                        // if it is in the database, do NOT show the confirm edit text
                        // if it is NOT in the database, show and request the user to confirm their email
                        ObjectAnimator animationEditText = ObjectAnimator.ofFloat(phoneNumberBox, "translationY", 165f);
                        animationEditText.setDuration(1000);
                        animationEditText.start();
                        ObjectAnimator animationWarningText = ObjectAnimator.ofFloat(noPhoneNumberWarning, "translationY", 165f);
                        animationWarningText.setDuration(1000);
                        animationWarningText.start();
                        confirmPhoneNumber.setVisibility(View.VISIBLE);
                        ObjectAnimator animationConfirmPhone = ObjectAnimator.ofFloat(confirmPhoneNumber, "translationY", 330f);
                        animationConfirmPhone.setDuration(1000);
                        animationConfirmPhone.start();
                        ObjectAnimator animationNext = ObjectAnimator.ofFloat(nextBtn, "translationY", 320f);
                        animationNext.setDuration(1000);
                        animationNext.start();
                        confirmEmailAddress.setVisibility(View.VISIBLE);
                        expanded = true;
                    }
                }
            }
        });

        phoneNumberBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!validNumber() && !phoneNumberBox.getText().toString().equals("")) {
                        noPhoneNumberWarning.setVisibility(View.VISIBLE);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        confirmPhoneNumber.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    } else if (!phoneNumberBox.getText().toString().equals("") && validNumber() && doesPhoneMatch()) {
                        noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                        // check here to see if phone number is in database
                        // if it is in the database, do NOT show the confirm edit text
                        // if it is NOT in the database, show and request the user to confirm their phone number
                    }
                }
            }
        });

        emailAddressBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (accountExists() && expanded) {
                    ObjectAnimator animationEditText = ObjectAnimator.ofFloat(phoneNumberBox, "translationY", -10f);
                    animationEditText.setDuration(1000);
                    animationEditText.start();
                    ObjectAnimator animationWarningText = ObjectAnimator.ofFloat(noPhoneNumberWarning, "translationY", -10f);
                    animationWarningText.setDuration(1000);
                    confirmPhoneNumber.setVisibility(View.INVISIBLE);
                    animationWarningText.start();
                    ObjectAnimator animationConfirmPhone = ObjectAnimator.ofFloat(confirmPhoneNumber, "translationY", -20f);
                    animationConfirmPhone.setDuration(1000);
                    confirmEmailAddress.setVisibility(View.INVISIBLE);
                    animationConfirmPhone.start();
                    ObjectAnimator animationNextBtn = ObjectAnimator.ofFloat(nextBtn, "translationY", -20f);
                    animationNextBtn.setDuration(1000);
                    animationNextBtn.start();
                    expanded = false;

                    noEmailWarning.setVisibility(View.INVISIBLE);
                    noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                    unmatchingEmail.setVisibility(View.INVISIBLE);
                    unmatchingPhone.setVisibility(View.INVISIBLE);
                    emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                } else if (validEmail()) {
                    noEmailWarning.setVisibility(View.INVISIBLE);
                    emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumberBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (doesPhoneMatch() && validNumber()) {
                    phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    confirmPhoneNumber.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    unmatchingPhone.setVisibility(View.INVISIBLE);
                    noPhoneNumberWarning.setVisibility(View.INVISIBLE);
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
                if (emailAddressBox.getText().toString().equals(confirmEmailAddress.getText().toString())) {
                    emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    confirmEmailAddress.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    unmatchingEmail.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmEmailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!emailAddressBox.getText().toString().equals(confirmEmailAddress.getText().toString()) && emailAddressBox.length() != 0 && confirmEmailAddress.length() != 0) {
                        unmatchingEmail.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        confirmEmailAddress.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            }
        });

        confirmPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!phoneNumberBox.getText().toString().equals(confirmPhoneNumber.getText().toString()) && confirmPhoneNumber.length() != 0 && phoneNumberBox.length() != 0) {
                        unmatchingPhone.setVisibility(View.VISIBLE);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        confirmPhoneNumber.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            }
        });

        confirmPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (confirmPhoneNumber.getText().toString().equals(phoneNumberBox.getText().toString())) {
                    phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    confirmPhoneNumber.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    unmatchingPhone.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        englishCheckbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spanishCheckbox.setPressed(false);
                frenchCheckbox.setPressed(false);
                englishCheckbox.setPressed(true);
                Language.setCurrentLanguage(0);
                changeLanguage(Language.getCurrentLanguage());
                return true;
            }
        });

        spanishCheckbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                frenchCheckbox.setPressed(false);
                englishCheckbox.setPressed(false);
                spanishCheckbox.setPressed(true);
                Language.setCurrentLanguage(1);
                changeLanguage(Language.getCurrentLanguage());
                return true;
            }
        });

        frenchCheckbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spanishCheckbox.setPressed(false);
                englishCheckbox.setPressed(false);
                frenchCheckbox.setPressed(true);
                Language.setCurrentLanguage(2);
                changeLanguage(Language.getCurrentLanguage());
                return true;
            }
        });

        findViewById(R.id.EnglishText).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spanishCheckbox.setPressed(false);
                frenchCheckbox.setPressed(false);
                englishCheckbox.setPressed(true);
                Language.setCurrentLanguage(0);
                changeLanguage(Language.getCurrentLanguage());
                return true;
            }
        });

        findViewById(R.id.SpanishText).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                frenchCheckbox.setPressed(false);
                englishCheckbox.setPressed(false);
                spanishCheckbox.setPressed(true);
                Language.setCurrentLanguage(1);
                changeLanguage(Language.getCurrentLanguage());
                return true;
            }
        });

        findViewById(R.id.FrenchText).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spanishCheckbox.setPressed(false);
                englishCheckbox.setPressed(false);
                frenchCheckbox.setPressed(true);
                Language.setCurrentLanguage(2);
                changeLanguage(Language.getCurrentLanguage());
                return true;
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountExists() && validNumber() && !expanded) {
                    noEmailWarning.setVisibility(View.INVISIBLE);
                    noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                    phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    nextBtn.setEnabled(false);
                    Intent intent = new Intent(MainActivity.this, LoggedIn.class);
                    intent.putExtra("Email Address", currentAccount.getEmail());
                    intent.putExtra("Phone Number", currentAccount.getPhoneNumber());
                    intent.putExtra("Truck Name", currentAccount.getTruckName());
                    intent.putExtra("Truck Number", currentAccount.getTruckNumber());
                    intent.putExtra("Trailer License", currentAccount.getTrailerLicense());
                    intent.putExtra("Trailer State", currentAccount.getTrailerState());
                    intent.putExtra("Driver License", currentAccount.getDriverLicense());
                    intent.putExtra("Driver State", currentAccount.getDriverState());
                    intent.putExtra("Driver Name", currentAccount.getDriverName());
                    intent.putExtra("Dispatcher's Phone Number", currentAccount.getDispatcherPhoneNumber());
                    startActivity(intent);
                } else {
                    if (!expanded && !accountExists()) {
                        noEmailWarning.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    }
                    if (!expanded && !validNumber()) {
                        noPhoneNumberWarning.setVisibility(View.VISIBLE);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    }
                    if (expanded && !validEmail()) {
                        noEmailWarning.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    }
                    if (expanded && !validNumber()) {
                        phoneNumberBox.setVisibility(View.VISIBLE);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    }
                    if (expanded && !doesEmailMatch()) {
                        unmatchingEmail.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        confirmEmailAddress.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    }
                    if (expanded && !doesPhoneMatch()) {
                        unmatchingPhone.setVisibility(View.VISIBLE);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        confirmPhoneNumber.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    }
                    if (validEmail() && validNumber() && doesEmailMatch() && doesPhoneMatch()) {
                        nextBtn.setEnabled(false);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                        confirmPhoneNumber.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                        noEmailWarning.setVisibility(View.INVISIBLE);
                        noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                        unmatchingEmail.setVisibility(View.INVISIBLE);
                        unmatchingPhone.setVisibility(View.INVISIBLE);
                        Intent createAccountIntent = new Intent(MainActivity.this, CreateAccount.class);
                        createAccountIntent.putExtra("Email Address", emailAddressBox.getText().toString());
                        createAccountIntent.putExtra("Phone Number", phoneNumberBox.getText().toString());
                        startActivity(createAccountIntent);
                    }
                }
            }
        });

    }

    private boolean doesEmailMatch() {
        return emailAddressBox.getText().toString().equals(confirmEmailAddress.getText().toString());
    }

    private boolean doesPhoneMatch() {
        return phoneNumberBox.getText().toString().equals(confirmPhoneNumber.getText().toString());
    }

    private boolean validEmail() {
        String email = emailAddressBox.getText().toString();
        if (email.contains("@")) {
            List<String> extensions = Arrays.asList(getResources().getStringArray(R.array.extensions));
            for (int i = 0; i < extensions.size(); i++) {
                if (email.endsWith(extensions.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validNumber() {
        String number = phoneNumberBox.getText().toString();
        return (number.length() == 10);
    }

    private boolean accountExists() {
        String email = emailAddressBox.getText().toString();
        ArrayList<Account> temp = Account.getAccounts();
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).getEmail().equals(email)) {
                currentAccount = temp.get(i);
                return true;
            }
        }
        return false;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
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
        Order.clearOrders();
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


        spanishCheckbox.setPressed(false);
        frenchCheckbox.setPressed(false);
        englishCheckbox.setPressed(true);
        // englishCheckbox.performClick();

        noEmailWarning.setVisibility(View.INVISIBLE);
        noPhoneNumberWarning.setVisibility(View.INVISIBLE);
        unmatchingEmail.setVisibility(View.INVISIBLE);
        unmatchingPhone.setVisibility(View.INVISIBLE);

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