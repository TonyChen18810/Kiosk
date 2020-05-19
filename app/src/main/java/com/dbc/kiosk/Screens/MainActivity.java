package com.dbc.kiosk.Screens;

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
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Helpers.EmailSuggestionAdapter;
import com.dbc.kiosk.Helpers.KeyboardListener;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.Helpers.PhoneNumberFormat;
import com.dbc.kiosk.Helpers.States;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Report;
import com.dbc.kiosk.Settings;
import com.dbc.kiosk.Webservices.CheckForExistingAccount;
import com.dbc.kiosk.Webservices.GetShippingTruckDriver;
import java.util.Collections;
import java.util.List;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;
import static java.util.Arrays.asList;
/**
 * MainActivity.java
 *
 * This activity is used for logging in or creating account
 *
 * If "Yes" is pressed on the FirstScreen activity, only the Email Address
 * and Phone Number fields will show in this activity. If "No" is pressed,
 * it will also show the confirm fields for email & phone number
 *
 * Calls the GetShippingTruckDriver web service when "Next" is pressed
 * to check for existing account
 */
public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView emailAddressBox;

    private EditText emailAddressBox_OLD, phoneNumberBox, confirmEmailAddress, confirmPhoneNumber;
    private TextView appointmentText, noEmailWarning, noPhoneNumberWarning, unmatchingEmail, unmatchingPhone, accountAlreadyExists, phoneAlreadyExists;
    private Button nextBtn, backBtn;
    public ProgressBar progressBar;
    private boolean newAccount;
    public static MutableLiveData<Boolean> accountExists;
    private CheckBox englishCheckbox, spanishCheckbox, frenchCheckbox;
    public static MutableLiveData<Boolean> emailListener;
    public static MutableLiveData<Boolean> phoneListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Crashlytics, set tags
        Report report = new Report(MainActivity.this);
        setContentView(R.layout.activity_main);
        // Property added to new Intent() in FirstScreen.java
        // aka did the user click yes or no
        String accountStatus = getIntent().getExtras().getString("accountStatus");

        // ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.autofill_layout, Account.getEMAIL_LIST());
        emailAddressBox = findViewById(R.id.AutoCompleteEmail);
        EmailSuggestionAdapter adapter = new EmailSuggestionAdapter(this, R.layout.custom_suggestion_row, Account.getEMAIL_LIST(), emailAddressBox);
        emailAddressBox.setAdapter(adapter);

        setup();

        System.out.println("Mode: " + Settings.getDbcUrl());

        // user clicked no
        if (accountStatus.equals("new")) {
            emailAddressBox.setThreshold(50);
            newAccount = true;
            newAccountExpand();
            // user clicked yes
        } else if (accountStatus.equals("exists")) {
            newAccount = false;
            emailAddressBox.setThreshold(1);
        }

        // Listens for response from GetShippingTruckDriver.java web service
        // to know if the entered account info exists or not
        accountExists = new MutableLiveData<>();
        accountExists.observe(MainActivity.this, accountExists -> {
            if (!newAccount) {
                if (!accountExists) {
                    System.out.println("Account does not exist!");
                    setStatus(0, asList(emailAddressBox, phoneNumberBox), asList(noEmailWarning, noPhoneNumberWarning));
                    enableObjects(nextBtn, backBtn, emailAddressBox, phoneNumberBox, englishCheckbox, spanishCheckbox, frenchCheckbox);
                    showSoftKeyboard(emailAddressBox);
                    progressBar.setVisibility(View.GONE);
                } else if (!PhoneNumberFormat.extract(phoneNumberBox.getText().toString()).equals(Account.getCurrentAccount().getPhoneNumber())){
                    setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                    enableObjects(nextBtn, backBtn, emailAddressBox, phoneNumberBox, englishCheckbox, spanishCheckbox, frenchCheckbox);
                    showSoftKeyboard(phoneNumberBox);
                    progressBar.setVisibility(View.GONE);
                } else {
                    System.out.println("Account exists!");
                    setStatus(1, asList(emailAddressBox, phoneNumberBox), asList(noEmailWarning, noPhoneNumberWarning));
                    startActivity(new Intent(MainActivity.this, LoggedIn.class));
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            } else if (newAccount) {
                if (accountExists) {
                    System.out.println("Account already exists, if this is you please go back and log in.");
                    setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(accountAlreadyExists));
                    confirmEmailAddress.setText("");
                    confirmPhoneNumber.setText("");
                    emailAddressBox.requestFocus();
                    nextBtn.setEnabled(true);
                    backBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                } else if (!accountExists){
                    setStatus(1, asList(emailAddressBox, confirmEmailAddress, phoneNumberBox, confirmPhoneNumber), asList(noEmailWarning, noPhoneNumberWarning, unmatchingEmail, unmatchingPhone));
                    Intent createAccountIntent = new Intent(MainActivity.this, CreateAccount.class);
                    createAccountIntent.putExtra("Email Address", emailAddressBox.getText().toString());
                    createAccountIntent.putExtra("Phone Number", PhoneNumberFormat.extract(phoneNumberBox.getText().toString()));
                    startActivity(createAccountIntent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            }
        });

        // avoids letting user press the 'return' or green arrow key on
        // standard keyboard when focus is on confirmPhoneNumber (if they could
        // use this it would close the keyboard)
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
                        setStatus(-1, asList(emailAddressBox, confirmEmailAddress), asList(noEmailWarning, unmatchingEmail));
                    }
                    setStatus(-1, Collections.singletonList(emailAddressBox), Collections.singletonList(accountAlreadyExists));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0 && s.charAt(s.length() - 1) == ' '){
                    s.replace(s.length() - 1, s.length(), "");
                }
            }
        });

        emailAddressBox.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!validEmail() && emailAddressBox.length() > 0) {
                    setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                }
            }
        });

        // format the phone numbers for (xxx)xxx-xxxx
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
                            && confirmPhoneNumber.length() != 0 && validEmail() && validNumber() && doesPhoneMatch() && doesEmailMatch()) {
                        nextBtn.setEnabled(true);
                    }
                    if (doesPhoneMatch() && validNumber()) {
                        setStatus(-1, asList(phoneNumberBox, confirmPhoneNumber), asList(noPhoneNumberWarning, unmatchingPhone));
                    }
                    setStatus(-1, Collections.singletonList(phoneNumberBox), Collections.singletonList(phoneAlreadyExists));
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
                if (s != null && s.length() > 0 && s.charAt(s.length() - 1) == ' '){
                    s.replace(s.length() - 1, s.length(), "");
                }
            }
        });

        confirmEmailAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!doesEmailMatch() && confirmEmailAddress.length() != 0) {
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
                            && confirmPhoneNumber.length() != 0 && validEmail() && validNumber() && confirmPhoneNumber.length() == phoneNumberBox.length()) {
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
                if (!doesPhoneMatch() && confirmPhoneNumber.length() != 0) {
                    setStatus(0, Collections.singletonList(confirmPhoneNumber), Collections.singletonList(unmatchingPhone));
                }
            }
        });

        // takes user back to FirstScreen.java
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FirstScreen.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        });

        // Listens for response from CheckForExistingAccount.java web service,
        // checks if the email is already in use with another account
        emailListener = new MutableLiveData<>();
        emailListener.observe(MainActivity.this, available -> {
            if (!available) {
                System.out.println("Email in use...");
                confirmEmailAddress.setText("");
                setStatus(0, Collections.singletonList(emailAddressBox), Collections.emptyList());
                // emailInUseWarning.setVisibility(View.VISIBLE);
                accountAlreadyExists.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                enableObjects(nextBtn, backBtn, emailAddressBox, confirmEmailAddress, phoneNumberBox, confirmPhoneNumber, englishCheckbox, spanishCheckbox, frenchCheckbox);
            } else if (available) {
                // good email
                System.out.println("Good email...");
                accountAlreadyExists.setVisibility(View.INVISIBLE);
                setStatus(-1, Collections.singletonList(emailAddressBox), Collections.emptyList());
                // emailInUseWarning.setVisibility(View.GONE);
            }

            if (phoneListener.getValue() != null && emailListener.getValue() != null) {
                if (emailListener.getValue() && phoneListener.getValue()) {
                    // start activity
                    System.out.println("Start next activity...");
                    Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                    intent.putExtra("Email Address", emailAddressBox.getText().toString());
                    intent.putExtra("Phone Number", phoneNumberBox.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            }
        });

        // Listens for response from CheckForExistingAccount.java web service,
        // checks if the phone number is already in use with another account
        phoneListener = new MutableLiveData<>();
        phoneListener.observe(MainActivity.this, available -> {
            if (!available) {
                // in use
                System.out.println("Phone in use...");
                phoneAlreadyExists.setVisibility(View.VISIBLE);
                confirmPhoneNumber.setText("");
                setStatus(0, Collections.singletonList(phoneNumberBox), Collections.emptyList());
                // phoneInUseWarning.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                enableObjects(nextBtn, backBtn, emailAddressBox, confirmEmailAddress, phoneNumberBox, confirmPhoneNumber, englishCheckbox, spanishCheckbox, frenchCheckbox);
            } else if (available) {
                // good phone
                System.out.println("Good phone...");
                phoneAlreadyExists.setVisibility(View.INVISIBLE);
                setStatus(-1, Collections.singletonList(phoneNumberBox), Collections.emptyList());
                // phoneInUseWarning.setVisibility(View.GONE);
            }

            if (phoneListener.getValue() != null && emailListener.getValue() != null) {
                if (emailListener.getValue() && phoneListener.getValue()) {
                    // start activity
                    System.out.println("Start next activity...");
                    Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                    intent.putExtra("Email Address", emailAddressBox.getText().toString());
                    intent.putExtra("Phone Number", phoneNumberBox.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            }
        });

        nextBtn.setOnClickListener(v -> {
            disableObjects(nextBtn, backBtn, englishCheckbox, spanishCheckbox, frenchCheckbox, emailAddressBox, phoneNumberBox, confirmEmailAddress, confirmPhoneNumber);
            if (!newAccount) {
                if (validEmail() && validNumber()) {
                    progressBar.setVisibility(View.VISIBLE);
                    new GetShippingTruckDriver(MainActivity.this, emailAddressBox.getText().toString().toLowerCase()).execute();
                } else {
                    if (!validEmail()) {
                        setStatus(0, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
                    }
                    if (!validNumber()) {
                        setStatus(0, Collections.singletonList(phoneNumberBox), Collections.singletonList(noPhoneNumberWarning));
                    }
                }
            } else if (newAccount) {
                if (validNumber() && validEmail() && doesEmailMatch() && doesPhoneMatch()) {
                    progressBar.setVisibility(View.VISIBLE);
                    // check if email is already in use
                    new CheckForExistingAccount(MainActivity.this, emailAddressBox.getText().toString().toLowerCase(), 0, newAccount).execute();
                    // check if phone number is already in use
                    new CheckForExistingAccount(MainActivity.this, PhoneNumberFormat.extract(phoneNumberBox.getText().toString()), 1, newAccount).execute();
                } else {
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
                    enableObjects(nextBtn, backBtn, emailAddressBox, phoneNumberBox, confirmEmailAddress, confirmPhoneNumber, englishCheckbox, spanishCheckbox, frenchCheckbox);
                }
            }
        });

        englishCheckbox.setOnClickListener(v -> {
            System.out.println("English checkbox clicked!");
            handleChecks(englishCheckbox);
        });

        spanishCheckbox.setOnClickListener(v -> {
            System.out.println("Spanish checkbox clicked!");
            handleChecks(spanishCheckbox);
        });

        frenchCheckbox.setOnClickListener(v -> {
            System.out.println("French checkbox clicked!");
            handleChecks(frenchCheckbox);
        });
    }

    int b = 0;
    public void handleChecks(CheckBox cb) {
        if ((Language.getCurrentLanguage() == 1) && (cb.getId() == R.id.EnglishCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if (Language.getCurrentLanguage() == 2 && (cb.getId() == R.id.SpanishCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if (Language.getCurrentLanguage() == 3 && (cb.getId() == R.id.FrenchCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        }
        if (cb.getId() == R.id.EnglishCheckbox) {
            textFadeStart();
            englishCheckbox.setClickable(false);
            changeLanguage(1);
            if (spanishCheckbox.isChecked()) {
                spanishCheckbox.toggle();
                spanishCheckbox.setClickable(true);
            }
            if (frenchCheckbox.isChecked()) {
                frenchCheckbox.toggle();
                frenchCheckbox.setClickable(true);
            }
            textFadeEnd();
        }
        if (cb.getId() == R.id.SpanishCheckbox) {
            textFadeStart();
            spanishCheckbox.setClickable(false);
            changeLanguage(2);
            if (englishCheckbox.isChecked()) {
                englishCheckbox.toggle();
                englishCheckbox.setClickable(true);
            }
            if (frenchCheckbox.isChecked()) {
                frenchCheckbox.toggle();
                frenchCheckbox.setClickable(true);
            }
            textFadeEnd();
        }
        if (cb.getId() == R.id.FrenchCheckbox) {
            textFadeStart();
            frenchCheckbox.setClickable(false);
            changeLanguage(3);
            if (englishCheckbox.isChecked()) {
                englishCheckbox.toggle();
                englishCheckbox.setClickable(true);
            }
            if (spanishCheckbox.isChecked()) {
                spanishCheckbox.toggle();
                spanishCheckbox.setClickable(true);
            }
            textFadeEnd();
        }
    }

    public void textFadeStart() {
        TextView[] textArray = {emailAddressBox, phoneNumberBox, confirmEmailAddress, confirmPhoneNumber, unmatchingEmail,
                                unmatchingPhone, noEmailWarning, noPhoneNumberWarning, appointmentText, nextBtn, backBtn,
                                accountAlreadyExists, phoneAlreadyExists};
        AlphaAnimation ani = new AlphaAnimation(1.0f, 0.2f);
        ani.setDuration(500);
        for (TextView text : textArray) {
            if (text.getVisibility() == View.VISIBLE) {
                text.startAnimation(ani);
            }
        }
    }

    public void textFadeEnd() {
        TextView[] textArray = {emailAddressBox, phoneNumberBox, confirmEmailAddress, confirmPhoneNumber, unmatchingEmail,
                unmatchingPhone, noEmailWarning, noPhoneNumberWarning, appointmentText, nextBtn, backBtn,
                accountAlreadyExists, phoneAlreadyExists};
        AlphaAnimation ani = new AlphaAnimation(0.2f, 1.0f);
        ani.setDuration(500);
        for (TextView text : textArray) {
            if (text.getVisibility() == View.VISIBLE) {
                text.startAnimation(ani);
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

    /**
     *
     * @param status
     * @param editTexts
     * @param textViews
     * Hides/shows error messages depending on int status input
     * 1 = green, 0 = red, -1 = black
     * 1 = hide error msg, 2 = show error msg, -1 = hide error msg
     */
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

    /**
     * @param obj object to be moved (Button, EditText, etc.)
     * @param propertyName (ex: "translationY")
     * @param value amount to move
     * Called from newAccountExpand()
     */
    private void animation(Object obj, String propertyName, float value) {
        ObjectAnimator animationEditText = ObjectAnimator.ofFloat(obj, propertyName, value);
        // was 1000
        animationEditText.setDuration(1);
        animationEditText.start();
    }

    /**
     * Opens the "Confirm Email" and "Confirm Phone Number" fields
     * only called if newAccount = true (if "No" is clicked on FirstScreen.java)
     */
    public void newAccountExpand() {
        setStatus(-1, Collections.singletonList(emailAddressBox), Collections.singletonList(noEmailWarning));
        animation(phoneNumberBox, "translationY", 165f);
        animation(noPhoneNumberWarning, "translationY", 165f);
        animation(phoneAlreadyExists, "translationY", 165f);
        animation(confirmPhoneNumber, "translationY", 330f);
        animation(nextBtn, "translationY", 330f);
        animation(backBtn, "translationY", 330f);
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

    /**
     * @param view
     * Shows the soft keyboard, setting focus on "view" param
     */
    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, SHOW_IMPLICIT);
            }
        }
    }
    /**
     * Used to initialize Ui variables, set language and status
     */
    private void setup() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        emailAddressBox_OLD = findViewById(R.id.EmailAddressBox);
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
        phoneAlreadyExists = findViewById(R.id.PhoneAlreadyExists);

        englishCheckbox = findViewById(R.id.EnglishCheckbox);
        spanishCheckbox = findViewById(R.id.SpanishCheckbox);
        frenchCheckbox = findViewById(R.id.FrenchCheckbox);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        nextBtn.setEnabled(false);

        States.setSates(MainActivity.this);

        setStatus(1, Collections.emptyList(), asList(noEmailWarning, noPhoneNumberWarning, unmatchingEmail, unmatchingPhone, confirmEmailAddress, confirmPhoneNumber, accountAlreadyExists, phoneAlreadyExists));

        if (Language.getCurrentLanguage() == 1) {
            englishCheckbox.setChecked(true);
            englishCheckbox.setClickable(false);
            spanishCheckbox.setClickable(true);
            frenchCheckbox.setClickable(true);
        } else if (Language.getCurrentLanguage() == 2) {
            spanishCheckbox.setChecked(true);
            spanishCheckbox.setClickable(false);
            englishCheckbox.setClickable(true);
            frenchCheckbox.setClickable(true);
        } else if (Language.getCurrentLanguage() == 3) {
            frenchCheckbox.setChecked(true);
            frenchCheckbox.setClickable(false);
            spanishCheckbox.setClickable(true);
            englishCheckbox.setClickable(true);
        }

        showSoftKeyboard(emailAddressBox);
        changeLanguage(Language.getCurrentLanguage());
        // emailAddressBox.setVisibility(View.GONE);
        emailAddressBox_OLD.setVisibility(View.GONE);
    }
    /**
     * @param val
     * changes UI text based on current language int
     * 0 = English, 1 = Spanish, 2 = French
     * Called from setChecked()
     */
    private void changeLanguage(int val) {
        Language.setCurrentLanguage(val);
        emailAddressBox.setHintTextColor(getResources().getColor(R.color.dark_gray));
        phoneNumberBox.setHintTextColor(getResources().getColor(R.color.dark_gray));
        // emailAutoComplete.setHintTextColor(getResources().getColor(R.color.dark_gray));
        switch(val) {
            case 1:
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
                nextBtn.setText(R.string.next_eng);
                backBtn.setText(R.string.back_eng);
                accountAlreadyExists.setText(R.string.account_already_exists_eng);
                phoneAlreadyExists.setText("*An account with this phone number already exists");
                break;
            case 2:
                // Spanish
                emailAddressBox.setHint("Dirección de email");
                phoneNumberBox.setHint("N.º de teléfono");
                confirmEmailAddress.setHint("Confirme el email");
                confirmPhoneNumber.setHint("Confirme el número de teléfono");
                unmatchingEmail.setText(R.string.email_doesnt_match_sp);
                unmatchingPhone.setText(R.string.phone_doesnt_match_sp);
                noEmailWarning.setText(R.string.invalid_email_sp);
                noPhoneNumberWarning.setText(R.string.invalid_phone_sp);
                appointmentText.setText(R.string.appt_required_sp);
                nextBtn.setText("Siguente");
                backBtn.setText(R.string.back_sp);
                accountAlreadyExists.setText(R.string.account_already_exists_sp);
                phoneAlreadyExists.setText("*Ya existe una cuenta con este número de teléfono");
                break;
            case 3:
                // French
                emailAddressBox.setHint("Adresse courriel");
                phoneNumberBox.setHint("Numéro de téléphone");
                confirmEmailAddress.setHint("Confirmer l’adresse courriel");
                confirmPhoneNumber.setHint("Confirmer le numéro de téléphone");
                unmatchingEmail.setText(R.string.email_doesnt_match_fr);
                unmatchingPhone.setText(R.string.phone_doesnt_match_fr);
                noEmailWarning.setText(R.string.invalid_email_fr);
                noPhoneNumberWarning.setText(R.string.invalid_phone_fr);
                appointmentText.setText(R.string.appt_required_fr);
                nextBtn.setText(R.string.next_fr);
                backBtn.setText(R.string.back_fr);
                accountAlreadyExists.setText(R.string.account_already_exists_fr);
                phoneAlreadyExists.setText("*Un compte avec ce numéro de téléphone existe déjà");
                break;
        }
    }
}