package com.example.kiosk;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    private static int currentLanguage = 0;

    private EditText emailAddressBox;
    private EditText phoneNumberBox;
    private EditText confirmEmailAddress;
    private EditText confirmPhoneNumber;
    private TextView appointmentText;
    private TextView welcomeText;
    private TextView loginText;
    private Button nextBtn;
    private TextView noEmailWarning;
    private TextView noPhoneNumberWarning;
    private TextView unmatchingEmail;
    private TextView unmatchingPhone;
    private boolean expanded = false;

    private ArrayList<Account> accounts = new ArrayList<>();
    private static Account currentAccount;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (Build.VERSION.SDK_INT < 16) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getActivity().setContentView(R.layout.activity_main);
        View decorView = getActivity().getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setup();

        confirmPhoneNumber.setOnEditorActionListener(new KeyboardListener());

        Account kyleAccount = new Account("kyle@gmail.com", "8315885534", "Kyle's Truck",
                "57", "5WHA67V", "California", "F3342376",
                "Arizona", "Kyle Gilbert", "4083675954");
        accounts.add(kyleAccount);

        Spinner languageSpinner = getView().findViewById(R.id.LanguageSpinner);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(getContext(), R.array.languages, android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setPrompt("Language/Idioma/Langue");
        languageSpinner.setAdapter(languageAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentLanguage = parent.getSelectedItemPosition();
                changeLanguage(currentLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirmEmailAddress.setVisibility(View.INVISIBLE);
        confirmPhoneNumber.setVisibility(View.INVISIBLE);

        emailAddressBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (accountExists()) {
                        noEmailWarning.setVisibility(View.INVISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    } else if (!validEmail()) {
                        noEmailWarning.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    } else {
                        noEmailWarning.setVisibility(View.INVISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                        // check here to see if email is in database
                        // if it is in the database, do NOT show the confirm edit text
                        // if it is NOT in the database, show and request the user to confirm their email
                        ObjectAnimator animationEditText = ObjectAnimator.ofFloat(phoneNumberBox, "translationY", 120f);
                        animationEditText.setDuration(1000);
                        animationEditText.start();
                        ObjectAnimator animationWarningText = ObjectAnimator.ofFloat(noPhoneNumberWarning, "translationY", 120f);
                        animationWarningText.setDuration(1000);
                        animationWarningText.start();
                        confirmPhoneNumber.setVisibility(View.VISIBLE);
                        ObjectAnimator animationConfirmPhone = ObjectAnimator.ofFloat(confirmPhoneNumber, "translationY", 240f);
                        animationConfirmPhone.setDuration(1000);
                        animationConfirmPhone.start();
                        ObjectAnimator animationNext = ObjectAnimator.ofFloat(nextBtn, "translationY", 200f);
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
                    } else {
                        noPhoneNumberWarning.setVisibility(View.INVISIBLE);
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
                } else if (validEmail()) {
                    noEmailWarning.setVisibility(View.INVISIBLE);
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
                    if (!emailAddressBox.getText().toString().equals(confirmEmailAddress.getText().toString())) {
                        unmatchingEmail.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        confirmEmailAddress.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    } else {
                        unmatchingEmail.setVisibility(View.INVISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                        confirmEmailAddress.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            }
        });

        confirmPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!phoneNumberBox.getText().toString().equals(confirmPhoneNumber.getText().toString())) {
                        unmatchingPhone.setVisibility(View.VISIBLE);
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
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
/**
 if (doesEmailMatch() && doesPhoneMatch() && validEmail() && validNumber()) {
 nextBtn.setEnabled(true);
 }
 */

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountExists() && validNumber()) {
                    noEmailWarning.setVisibility(View.INVISIBLE);
                    noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                    phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                    nextBtn.setEnabled(false);
                    /*
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
                     */
                } else {
                    if (emailAddressBox.getText().toString().equals("") && phoneNumberBox.getText().toString().equals("")) {
                        noEmailWarning.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        noPhoneNumberWarning.setVisibility(View.VISIBLE);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                    } else if (phoneNumberBox.getText().toString().equals("")) {
                        noPhoneNumberWarning.setVisibility(View.VISIBLE);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        noEmailWarning.setVisibility(View.INVISIBLE);
                    } else if (emailAddressBox.getText().toString().equals("")) {
                        noEmailWarning.setVisibility(View.VISIBLE);
                        emailAddressBox.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
                        noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                    } else if (validEmail() && validNumber() && doesEmailMatch() && doesPhoneMatch()) {
                        nextBtn.setEnabled(false);
                        phoneNumberBox.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                        confirmPhoneNumber.getBackground().setColorFilter(getResources().getColor(R.color.okay), PorterDuff.Mode.SRC_ATOP);
                        noEmailWarning.setVisibility(View.INVISIBLE);
                        noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                        unmatchingEmail.setVisibility(View.INVISIBLE);
                        unmatchingPhone.setVisibility(View.INVISIBLE);
                        /*
                        Intent createAccountIntent = new Intent(MainActivity.this, CreateAccount.class);
                        createAccountIntent.putExtra("Email Address", emailAddressBox.getText().toString());
                        createAccountIntent.putExtra("Phone Number", phoneNumberBox.getText().toString());
                        startActivity(createAccountIntent);
                        */
                    } else if (!doesEmailMatch() && !doesPhoneMatch() && validEmail() && validNumber()) {
                        unmatchingEmail.setVisibility(View.VISIBLE);
                        unmatchingPhone.setVisibility(View.VISIBLE);
                    } else if (validEmail() && !validNumber()) {
                        noPhoneNumberWarning.setVisibility(View.VISIBLE);
                        noEmailWarning.setVisibility(View.INVISIBLE);
                    } else if (!validEmail() && validNumber()) {
                        noEmailWarning.setVisibility(View.VISIBLE);
                        noPhoneNumberWarning.setVisibility(View.INVISIBLE);
                    } else if (!doesPhoneMatch() && validNumber()) {
                        unmatchingPhone.setVisibility(View.VISIBLE);
                    } else if (!doesEmailMatch() && validEmail()) {
                        unmatchingEmail.setVisibility(View.VISIBLE);
                    } else {
                        noEmailWarning.setVisibility(View.VISIBLE);
                        noPhoneNumberWarning.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public static int getCurrentLanguage() {
        return currentLanguage;
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
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getEmail().equals(email)) {
                currentAccount = accounts.get(i);
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
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, SHOW_IMPLICIT);
        }
    }

    private void setup() {
        emailAddressBox = getView().findViewById(R.id.EmailAddressBox);
        phoneNumberBox = getView().findViewById(R.id.PhoneNumberBox);
        confirmEmailAddress = getView().findViewById(R.id.ConfirmEmailAddress);
        confirmPhoneNumber = getView().findViewById(R.id.ConfirmPhoneNumber);
        appointmentText = getView().findViewById(R.id.AppointmentText);
        welcomeText = getView().findViewById(R.id.WelcomeText);
        loginText = getView().findViewById(R.id.LoginText);
        nextBtn = getView().findViewById(R.id.NextBtn);
        noEmailWarning = getView().findViewById(R.id.NoEmailWarning);
        noPhoneNumberWarning = getView().findViewById(R.id.NoPhoneNumberWarning);
        unmatchingEmail = getView().findViewById(R.id.UnmatchingEmail);
        unmatchingPhone = getView().findViewById(R.id.UnmatchingPhone);

        noEmailWarning.setVisibility(View.INVISIBLE);
        noPhoneNumberWarning.setVisibility(View.INVISIBLE);
        unmatchingEmail.setVisibility(View.INVISIBLE);
        unmatchingPhone.setVisibility(View.INVISIBLE);

        showSoftKeyboard(emailAddressBox);
    }

    @SuppressLint("SetTextI18n")
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
                unmatchingEmail.setText("*Entered email addresses do not match");
                unmatchingPhone.setText("*Entered phone numbers do not match");
                noEmailWarning.setText("*Invalid email address");
                noPhoneNumberWarning.setText("*Invalid phone number");
                appointmentText.setText("*If your order requires an appointment please call 831-455-4305 to schedule an appointment");
                welcomeText.setText("Welcome to D'Arrigo\nCalifornia");
                loginText.setText("Log-in");
                nextBtn.setText("Next");
                break;
            case 1:
                // Spanish
                emailAddressBox.setHint("Dirección de correo electrónico");
                phoneNumberBox.setHint("Número de teléfono");
                confirmEmailAddress.setHint("Confirmar el correo");
                confirmPhoneNumber.setHint("Confirmar número de teléfono");
                unmatchingEmail.setText("*Las direcciones de correo electrónico ingresadas no coinciden");
                unmatchingPhone.setText("*Los números de teléfono ingresados no coinciden");
                noEmailWarning.setText("*Dirección de correo electrónico no válida");
                noPhoneNumberWarning.setText("*Numero de telefono invalido");
                appointmentText.setText("*Si su pedido requiere una cita, llame al 831-455-4305 para programar una cita");
                welcomeText.setText("Bienvenido a D'Arrigo\nCalifornia");
                loginText.setText("Iniciar sesión");
                nextBtn.setText("Siguiente");
                break;
            case 2:
                // French
                emailAddressBox.setHint("Adresse électronique");
                phoneNumberBox.setHint("Numéro de téléphone");
                confirmEmailAddress.setHint("Confirmez votre adresse email");
                confirmPhoneNumber.setHint("Confirmer le numéro de téléphone");
                unmatchingEmail.setText("*Les adresses e-mail saisies ne correspondent pas");
                unmatchingPhone.setText("*Les numéros de téléphone saisis ne correspondent pas");
                noEmailWarning.setText("*Adresse e-mail invalide");
                noPhoneNumberWarning.setText("*Numéro de téléphone invalide");
                appointmentText.setText("*Si votre commande nécessite un rendez-vous, veuillez appeler le 831-455-4305 pour fixer un rendez-vous");
                welcomeText.setText("Bienvenue à D'Arrigo\nCalifornia");
                loginText.setText("S'identifier");
                nextBtn.setText("Suivant");
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
